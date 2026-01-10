/**
 * shorts.js
 * - 쇼츠 목록 무한 스크롤
 * - 비디오 자동 재생/일시정지 (IntersectionObserver)
 * - 커스텀 비디오 컨트롤 (재생바, 시간 표시, 클릭 토글)
 */

// ==========================================
//  1. 전역 변수 및 설정
// ==========================================
let page = 0;
let totalPages = 10;
let loading = false; // 중복 요청 방지용 플래그
let shortsFeedWrap = document.getElementById("shortsFeed");
const scrollBox = document.getElementById("shortsFeed");

/**
 * IntersectionObserver (관찰자) 설정
 * - 스크롤에 따라 요소가 화면에 들어오거나 나갈 때 실행됩니다.
 * - 쇼츠 카드가 화면 뷰포트에 들어오는지 감시
 * - getData() 함수보다 먼저 정의되어야 에러가 발생하지 않습니다.
 */
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        // 관찰 대상(Card) 내부의 video 태그를 찾습니다.
        const video = entry.target.querySelector('video');
        if (!video) return;

        if (entry.isIntersecting) {
            // [CASE 1] 화면에 들어옴 (60% 이상 보임)
            // -> 자동 재생 시작
            video.play().catch(e => console.log("자동 재생 막힘(브라우저 정책):", e));
            // 댓글 동기화 로직
            refreshCommentPanelIfOpen(entry.target);

            //  URL 주소 변경 (새로고침 없이 주소창만 바꿈)
            const currentPostId = entry.target.dataset.postId;
            if (currentPostId) {
                // 예: /shorts/view/15 형태로 주소 변경
                // 페이지를 새로고침하지 않고 주소(URL)만 살짝 바꾼다.(덮어씌움)
                history.replaceState(null, null, `/shorts/view/${currentPostId}`);
            }
        } else {
            // [CASE 2] 화면에서 나감
            // -> 영상 멈춤 및 시간 초기화 (다시 왔을 때 처음부터 나오게)
            video.pause();
            video.currentTime = 0;
        }
    });
}, { threshold: 0.6 }); // 화면의 60%가 보일 때 작동


// ==========================================
//  2. 데이터 조회 (Data Fetching)
// ==========================================

// 서버 데이터 요청 함수 (Ajax)
async function getData(page) {
    console.log(`[getData] 페이지 요청: ${page}`);

    if (loading) return; // 이미 로딩 중이면 중단
    loading = true;

    fetch(`/shorts/${page}`, { method: "GET" })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP 오류: ${response.status}`);
            return response.json();
        })
        .then(data => {
            console.log("서버 응답 데이터:", data);
            totalPages = data.totalPage;

            // [예외 처리] 데이터가 하나도 없을 때 (빈 화면 표시)
            if (page === 0 && (!data.shortsList || data.shortsList.length === 0)) {
                shortsFeedWrap.innerHTML = `
                    <div class="emptyState">
                        <div class="icon">🎬</div>
                        <h3>아직 업로드된 쇼츠가 없어요!</h3>
                        <p>가장 먼저 트렌드를 만들어보세요.</p>
                        <a href="/shorts/shortsUpload" class="btnUploadEmpty">쇼츠 업로드 하러가기</a>
                    </div>
                `;
                loading = false;
                return;
            }

            // [리팩토링] 데이터 렌더링을 renderShortCard 함수에 위임
            data.shortsList.map((info) => {
                // 1. [중복 방지] 이미 화면에 있는 ID(공유로 먼저 뜬 영상)라면 건너뜀
                if (document.querySelector(`.shortsCard[data-post-id="${info.postId}"]`)) {
                    console.log(`[Skip] 중복 영상 건너뜀: ${info.postId}`);
                    return;
                }

                // 2. 카드 생성 (뒤에 추가)
                renderShortCard(info, false); // 없으면 그림
            });

        })
        .catch(err => {
            console.error("데이터 로딩 중 에러 발생:", err);
        })
        .finally(() => {
            loading = false; // 로딩 상태 해제
        });
}

/**
 * 단건 쇼츠 조회 (공유 링크용)
 * - 특정 영상을 가져와서 리스트의 '맨 앞'에 꽂아넣는다.
 */
function getOneShort(targetId) {
    fetch(`/shorts/detail/${targetId}`)
        .then(res => {
            if (!res.ok) throw new Error("쇼츠 단건 조회 실패");
            return res.json();
        })
        .then(data => {
            // 1. 받아온 데이터를 화면에 그린다 (prepend 모드: true)
            renderShortCard(data, true);

            // 2. 그 다음, 나머지 목록(최신순)을 뒤에 로딩한다.
            getData(0);
        })
        .catch(err => {
            console.error("단건 조회 중 에러:", err);
            // 에러 나면 그냥 평소처럼 목록 로딩
            getData(0);
        });
}


// ==========================================
//  3. 렌더링 (Rendering)
// ==========================================

/**
 * [리팩토링] 쇼츠 카드 HTML 생성 및 삽입 함수
 * @param {Object} info - 쇼츠 데이터 객체
 * @param {boolean} isPrepend - true면 맨 앞에 추가, false면 뒤에 추가
 */
function renderShortCard(info, isPrepend = false) {
    let videoFile = info.files.find(f => f.contentType.includes("video"));
    let thumbnailFile = info.files.find(f => f.contentType.includes("image"));
    let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(info.writeDate) : info.writeDate;
    let userProfile = info.profileImg ? info.profileImg : '/images/icon_pinedory.png';
    let likeClass = info.liked ? 'on' : '';

    //  태그 HTML 생성 (데이터가 있을 때만)
    let tagHtml = '';
    if (info.tags && info.tags.length > 0) {
        tagHtml = `<div class="shorts-tags">`;
        info.tags.forEach(tag => {
            // 커뮤니티 스타일 클래스 적용 (예: tag-item)
            tagHtml += `<span class="tag-item">#${tag}</span>`;
        });
        tagHtml += `</div>`;
    }

    // 1. 드롭다운 메뉴 아이템 구성
    // 기본 메뉴: 설명, 신고 (누구나 보임)
    let menuItems = `
        <li><button type="button" class="dropdownItem" onclick="alert('이 쇼츠에 대한 설명입니다:\\n${info.content}')">설명</button></li>
        <li><button type="button" class="dropdownItem" onclick="handleReport('SHORTS', '${info.postId}')">신고</button></li>
    `;

    if (loginUser && String(info.memberId) === String(loginUser)) {
        menuItems = `
            <li><button type="button" class="dropdownItem" onclick="alert('설명:\\n${escapeHtml(info.content)}')">설명</button></li>
            <li><button type="button" class="dropdownItem" onclick="updateShorts('${info.postId}')">수정</button></li>
            <li><button type="button" class="dropdownItem danger" onclick="deleteShorts('${info.postId}')">삭제</button></li>
        `;
    }

    // 2. 드롭다운 HTML 조립 (항상 보임)
    const optionHtml = `
        <div class="commentOption" onclick="event.stopPropagation()">
            <button type="button" class="moreBtn" onclick="toggleCommentMenu(this)">
                <img src="/images/ico_menu.png" alt="더보기">
            </button>
            <ul class="dropdownMenu">
                ${menuItems}
            </ul>
        </div>
    `;

    const html = `
        <div class="shortsCard" data-post-id="${info.postId}" data-title="${info.title}" data-user="${info.nickname}" data-date="${dateStr}">
            <div class="cardInner">
                 <aside class="userPanel">
                    <div class="userWrap">
                        <div class="userHeader">
                            <div class="avatar"><img src="${userProfile}" alt="user"></div>
                            <div class="userInfo">
                                <strong class="nickname">@${escapeHtml(info.nickname)}</strong>
                                <span class="writedate">· ${dateStr}</span>
                            </div>
                        </div>
                        <div class="userBody">
                            <p class="shortsTitle">${escapeHtml(info.title)}</p>
                            <p class="shortsContent">${escapeHtml(info.content)}</p>
                            ${tagHtml}
                        </div>
                    </div>
                </aside>

                <div class="videoShell" onclick="toggleVideo(this)">
                    <video autoplay muted loop playsinline
                           poster="${thumbnailFile ? thumbnailFile.path : ''}"
                           ontimeupdate="updateProgress(this)">
                           <source src="${videoFile ? videoFile.path : ''}">
                    </video>

                    ${optionHtml}

                    <div class="playOverlay"></div>
                    <div class="timeDisplay">00:00 / 00:00</div>
                    <div class="progressBarContainer"><div class="progressBarFill"></div></div>
                </div>

                <div class="actionPanel">
                    <button class="actionBtn like ${likeClass}" onclick="toggleLike(${info.postId}, this)">
                        <span>좋아요</span>
                        <div class="icoBox">
                            <span class="ico"></span>
                            <em id="likeCount-${info.postId}">${info.likeCount}</em>
                        </div>
                    </button>
                    
                    <button class="actionBtn share">
                        <span>링크공유</span>
                        <em class="ico_link"></em>
                    </button>
                    
                    <button class="actionBtn commentToggle"
                        onclick="openComment(${info.postId}, '${info.title}', '${info.nickname}', '${dateStr}');">
                        <span>댓글</span>
                        <div class="icoBox">
                            <span class="ico"></span>
                            <em>${info.replyCount}</em>
                        </div>
                    </button>
                </div>
            </div>
        </div>
    `;

    // 1. DOM에 추가
    if (isPrepend) {
        shortsFeedWrap.insertAdjacentHTML("afterbegin", html);
    } else {
        shortsFeedWrap.insertAdjacentHTML("beforeend", html);
    }

    // 2. 방금 추가된 요소 선택
    const newCard = isPrepend ? shortsFeedWrap.firstElementChild : shortsFeedWrap.lastElementChild;

    // 3. 더보기(Expand) 로직 적용 (기존 로직 이관)
    const titleEl = newCard.querySelector('.shortsTitle');
    const descEl = newCard.querySelector('.shortsContent');

    if (titleEl && titleEl.scrollHeight > titleEl.clientHeight) {
        titleEl.classList.add('expandable');
        titleEl.setAttribute('onclick', 'toggleExpand(this)');
    }

    if (descEl && descEl.scrollHeight > descEl.clientHeight) {
        descEl.classList.add('expandable');
        descEl.setAttribute('onclick', 'toggleExpand(this)');
    }

    // 4. 관찰자(Observer) 등록
    observer.observe(newCard);
}


// ==========================================
//  4. 비디오 및 UI 제어 (Controls)
// ==========================================

/**
 * 영상 재생/일시정지 토글
 * @param {HTMLElement} shellElement - 클릭된 .videoShell 요소
 */
function toggleVideo(shellElement) {
    const video = shellElement.querySelector('video');

    if (video.paused) {
        video.play();
        shellElement.classList.remove('paused'); // 아이콘 숨기기
    } else {
        video.pause();
        shellElement.classList.add('paused');    // 아이콘 보이기 (▶)
    }
}

/**
 * 진행바 및 시간 텍스트 업데이트
 * @param {HTMLVideoElement} videoElement - 현재 재생 중인 비디오 태그
 */
function updateProgress(videoElement) {
    // 영상 길이가 로드되지 않았으면 중단 (NaN 에러 방지)
    if (isNaN(videoElement.duration)) return;

    // 1. 진행바 너비 계산 (%)
    const percent = (videoElement.currentTime / videoElement.duration) * 100;
    const progressBar = videoElement.parentElement.querySelector('.progressBarFill');
    if (progressBar) {
        progressBar.style.width = `${percent}%`;
    }

    // 2. 시간 텍스트 업데이트 (00:05 / 01:00)
    const current = formatTime(videoElement.currentTime);
    const total = formatTime(videoElement.duration);
    const timeDisplay = videoElement.parentElement.querySelector('.timeDisplay');

    if (timeDisplay) {
        timeDisplay.innerText = `${current} / ${total}`;
    }
}

/**
 * 시간을 '00:00' 형식으로 변환
 * @param {number} seconds - 초 단위 시간
 * @returns {string} - '분:초' 문자열
 */
function formatTime(seconds) {
    if (isNaN(seconds)) return "00:00";

    const min = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);

    // 숫자가 10보다 작으면 앞에 '0' 붙여주기 (삼항 연산자)
    const minStr = min < 10 ? `0${min}` : min;
    const secStr = sec < 10 ? `0${sec}` : sec;

    return `${minStr}:${secStr}`;
}

// 1. 댓글창 열기 (쇼츠 리스트에서 버튼 클릭 시 호출)
function openComment(postId, title, writer, date) {
    const panel = document.getElementById("commentsPanel");

    // 상태 초기화
    currentPostIdForReply = postId;
    replyPage = 0;
    isReplyLastPage = false;
    isReplyLoading = false;

    // 헤더 정보 세팅
    document.getElementById("commentPanelTitle").innerText = title;
    document.getElementById("commentPanelUser").innerText = "@" + writer;
    document.getElementById("commentPanelDate").innerText = date || "";

    // 리스트 비우기
    document.getElementById("commentListUl").innerHTML = "";
    document.getElementById("replyInput").value = "";

    // 창 열기
    panel.classList.add("open");

    // 첫 페이지 로드
    loadReplies(postId, 0);
}

// 2. 창 닫기
function closeComment() {
    document.getElementById("commentsPanel").classList.remove("open");
}

/**
 * 스크롤 시 열려있는 댓글창의 내용을 갱신하는 함수
 * @param {HTMLElement} cardElement - 현재 화면에 보이는 쇼츠 카드 요소
 */
function refreshCommentPanelIfOpen(cardElement) {
    const panel = document.getElementById("commentsPanel");

    // 1. 댓글창이 닫혀있으면 아무것도 안 함 (서버 요청 방지)
    if (!panel.classList.contains("open")) return;

    // 2. 카드에서 데이터 추출 (dataset 활용)
    const newPostId = cardElement.dataset.postId;
    const title = cardElement.dataset.title;
    const writer = cardElement.dataset.user;
    const date = cardElement.dataset.date;

    // 3. 같은 게시글이면 굳이 다시 로드 안  (중복 방지)
    // currentPostIdForReply 변수는 기존에 선언된 전역 변수
    if (currentPostIdForReply == newPostId) return;

    console.log(`[Sync] 댓글창 갱신: 게시글 ID ${newPostId}`);

    // 4. 전역 변수 업데이트
    currentPostIdForReply = newPostId;
    replyPage = 0;
    isReplyLastPage = false;

    // 5. 헤더 UI 업데이트
    document.getElementById("commentPanelTitle").innerText = title;
    document.getElementById("commentPanelUser").innerText = "@" + writer;
    document.getElementById("commentPanelDate").innerText = date || "";

    // 6. 리스트 초기화 및 데이터 로드
    document.getElementById("commentListUl").innerHTML = "";
    document.getElementById("replyInput").value = "";
    loadReplies(newPostId, 0);
}


// ==========================================
//  5. 액션 및 이벤트 처리 (Actions & Events)
// ==========================================

// 좋아요 토글 함수 (Ajax)
function toggleLike(postId, btnElement) {
    // 1. 로그인 체크
    const loginCheckInput = document.getElementById("loginCheck");
    if (!loginCheckInput || loginCheckInput.value !== 'true') {
        if(confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/login";
        }
        return;
    }

    // 2. 서버로 보낼 데이터 준비
    const requestData = {
        targetType: "POST",  // LikeReqDto의 targetType
        targetId: postId     // LikeReqDto의 targetId
    };

    // 3. fetch 요청 보내기
    fetch(`/like`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json" //
        },
        body: JSON.stringify(requestData) //  객체를 JSON 문자열로 변환하여 Body에 탑재
    })
        .then(res => {
            if (!res.ok) {
                if (res.status === 401) {
                    alert("로그인이 만료되었습니다.");
                    location.href = "/login";
                    return;
                }
                throw new Error(`통신 실패 상태코드: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            // 4. 응답 데이터 처리
            // data = { liked: true/false, likeCount: 123 }

            const countEm = btnElement.querySelector('em');

            if (data.liked) {
                btnElement.classList.add('on');
                showToastMsg("이 영상을 좋아합니다!");
            } else {
                btnElement.classList.remove('on');
                showToastMsg("좋아요를 취소했습니다.");
            }

            // 숫자 갱신 (LikeResDto의 likeCount 필드 사용)
            countEm.innerText = data.likeCount;
        })
        .catch(err => {
            console.error("좋아요 처리 중 오류 발생:", err);
            alert("좋아요 처리에 실패했습니다. 잠시 후 다시 시도해주세요.");
        });
}

function updateShorts(postId) {
    const newContent = prompt("수정할 설명을 입력하세요.");
    if (newContent) {
        // TODO: 실제 쇼츠 수정 API 호출 로직 구현 필요
        // fetch(`/shorts/${postId}`, { method: 'PUT', body: ... })
        alert("수정 기능은 서버 API 연결이 필요합니다.\n입력내용: " + newContent);
    }
}

function deleteShorts(postId) {
    if (confirm("정말 이 쇼츠를 삭제하시겠습니까?")) {
        // TODO: 실제 쇼츠 삭제 API 호출 로직 구현 필요
        // fetch(`/shorts/${postId}`, { method: 'DELETE' }).then(...)
        alert("삭제 요청이 전송되었습니다. (기능 연결 필요)");
        // location.reload(); // 성공 시 새로고침
    }
}

// 공유 버튼 클릭 이벤트 (이벤트 위임 사용)
shortsFeedWrap.addEventListener('click', (e) => {
    // 클릭한 요소가 공유 버튼(.share)인지 확인
    const shareBtn = e.target.closest('.actionBtn.share');

    if (shareBtn) {
        // 1. 현재 주소창의 URL 가져오기
        const currentUrl = window.location.href;

        // 2. 클립보드에 복사
        navigator.clipboard.writeText(currentUrl)
            .then(() => {
                showToastMsg("링크가 복사되었습니다!");
            })
            .catch(err => {
                console.error("복사 실패:", err);
                prompt("자동 복사에 실패했습니다. 아래 링크를 직접 복사해주세요.", currentUrl);
            });
    }
});

// ==========================================
//  6. 초기 실행 (Initialization)
// ==========================================
window.addEventListener("load", () => {
    // 1. 공유 링크 등으로 들어왔는지 확인 (JSP에서 심어둔 ID 값)
    const targetIdInput = document.getElementById("targetShortsId");
    const targetId = targetIdInput ? targetIdInput.value : null;

    if (targetId) {
        // [CASE A] 특정 쇼츠 지정 접속 (/shorts/view/15)
        console.log(`[DeepLink] 공유된 쇼츠 ID: ${targetId} 로 시작합니다.`);
        getOneShort(targetId);
    } else {
        // [CASE B] 일반 접속 (/shorts)
        getData(page);
    }

    // 2. 무한 스크롤 이벤트 감지
    scrollBox.addEventListener("scroll", () => {
        const scrollTop = scrollBox.scrollTop;          // 현재 스크롤 위치
        const viewportHeight = scrollBox.clientHeight;  // 화면 높이
        const totalHeight = scrollBox.scrollHeight;     // 전체 컨텐츠 높이

        // 바닥에서 100px 남았을 때 + 로딩 중 아닐 때 + 다음 페이지 있을 때
        if (scrollTop + viewportHeight >= totalHeight - 100 && !loading && (page < totalPages - 1)) {
            console.log(" 바닥 감지! 다음 페이지 요청");
            page++;
            getData(page);
        }
    });
});