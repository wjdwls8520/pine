/**
 * shorts.js
 * - 쇼츠 목록 무한 스크롤
 * - 비디오 자동 재생/일시정지 (IntersectionObserver)
 * - 커스텀 비디오 컨트롤 (재생바, 시간 표시, 클릭 토글)
 */

let page = 0;
let totalPages = 10;
let loading = false; // 중복 요청 방지용 플래그
let shortsFeedWrap = document.getElementById("shortsFeed");
const scrollBox = document.getElementById("shortsFeed");

/**
 *  IntersectionObserver (관찰자) 설정
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
// 서버 데이터 요청 함수 (Ajax)
// ==========================================
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

// ==========================================
//  [추가] 쇼츠 카드 렌더링 및 단건 조회 함수
// ==========================================

/**
 * [신규] 단건 쇼츠 조회 (공유 링크용)
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

    const html = `
        <div class="shortsCard" data-post-id="${info.postId}" data-title="${info.title}" data-user="${info.nickname}" data-date="${dateStr}">
            <div class="cardInner">
                 <aside class="userPanel">
                    <div class="userWrap">
                        <div class="userHeader">
                            <div class="avatar"><img src="${userProfile}" alt="user"></div>
                            <div class="userInfo">
                                <strong class="nickname">@${info.nickname}</strong>
                                <span class="writedate">· ${dateStr}</span>
                            </div>
                        </div>
                        <div class="userBody">
                            <p class="shortsTitle">${info.title}</p>
                            <p class="shortsContent">${info.content}</p>
                        </div>
                    </div>
                </aside>

                <div class="videoShell" onclick="toggleVideo(this)">
                    <video autoplay muted loop playsinline
                           poster="${thumbnailFile ? thumbnailFile.path : ''}"
                           ontimeupdate="updateProgress(this)">
                           <source src="${videoFile ? videoFile.path : ''}">
                    </video>
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
//  초기 실행 및 이벤트 리스너
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

/**
 * 텍스트 더보기/접기 토글 함수
 */
function toggleExpand(element) {
    element.classList.toggle('expanded');
}

/**
 *  영상 재생/일시정지 토글
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
 *  진행바 및 시간 텍스트 업데이트
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
 *  시간을 '00:00' 형식으로 변환
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

// ==========================================
//  댓글 관련 변수 및 함수
// ==========================================
let currentPostIdForReply = null;
let replyPage = 0;
let isReplyLastPage = false;
let isReplyLoading = false;

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

// 3. 댓글 데이터 불러오기 (AJAX)
function loadReplies(postId, page) {
    if (isReplyLoading || isReplyLastPage) return;

    isReplyLoading = true;

    // 백엔드 API 호출
    fetch(`/reply/list?postId=${postId}&page=${page}`)
        .then(res => {
            if (!res.ok) throw new Error("댓글 조회 실패");
            return res.json();
        })
        .then(data => {
            const replies = data.content; // Page 객체의 content가 리스트
            const listUl = document.getElementById("commentListUl");

            // 마지막 페이지인지 체크
            if (data.last === true) {
                isReplyLastPage = true;
            }

            // [HTML 조립] 닉네임 + 날짜 / 내용 구조
            // html 함수 호출
            let html = "";
            replies.forEach(reply => {
                html += createReplyItemHtml(reply, false);
            });

            listUl.insertAdjacentHTML("beforeend", html);
            replyPage++; // 다음 페이지 준비
        })
        .catch(err => console.error(err))
        .finally(() => {
            isReplyLoading = false;
        });
}

/**
 * 댓글 HTML 생성 함수
 * @param {Object} reply - 댓글 데이터
 * @param {boolean} isSubReply - 대댓글 여부 (true면 답글 버튼 숨김)
 */
function createReplyItemHtml(reply, isSubReply = false) {
    let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(reply.writeDate) : reply.writeDate;

    // 삭제된 댓글 처리
    let isDeleted = reply.deleteYN === 'Y';
    let contentClass = isDeleted ? 'deleted' : '';
    let contentText = isDeleted ? '삭제된 댓글입니다.' : reply.content;
    let nickname = isDeleted ? '(알수없음)' : `@${reply.nickname}`;

    // 답글 버튼 표시 조건 강화
    // 삭제되지 않았고(AND) 대댓글이 아니어야 함(!isSubReply)
    let replyBtnHtml = (!isDeleted && !isSubReply)
        ? `<button class="btnReReply" onclick="toggleReReplyForm(${reply.id})">답글달기</button>`
        : '';

    // 자식 댓글(대댓글) 재귀 생성
    let childrenHtml = "";
    let viewReplyBtn = "";
    // 대댓글이 존재하고(childCount > 0), 현재 렌더링 중인게 대댓글이 아닐 경우(!isSubReply)
    if (!isSubReply && reply.childCount > 0) {
        // (1) 답글 보기/숨기기 버튼 생성
        viewReplyBtn = `
            <button class="btnViewReply" id="btnViewReply-${reply.id}" onclick="loadChildReplies(${reply.id}, ${reply.childCount})">
                ─── 대댓글 ${reply.childCount}개 보기
            </button>
        `;

        // (2) 답글이 들어갈 빈 컨테이너 생성 (초기엔 비어있음)
        childrenHtml = `<ul class="replyList sub-reply-area" id="subReplyArea-${reply.id}" style="display:none;"></ul>`;
    }

    return `
        <li class="replyItem" id="reply-${reply.id}" data-id="${reply.id}">
            <div class="commentTop">
                <b>${nickname}</b>
                <span class="date">${dateStr}</span>
            </div>
            <p class="${contentClass}">${contentText}</p>
            
            <div class="commentAction">
                ${replyBtnHtml} 
            </div>

            <div id="reReplyForm-${reply.id}" class="reReplyFormArea"></div>

            ${viewReplyBtn}
            ${childrenHtml}
        </li>
    `;
}

/**
 * [신규] 답글 입력창 토글 (열기/닫기)
 */
function toggleReReplyForm(parentId) {
    const formArea = document.getElementById(`reReplyForm-${parentId}`);

    // 이미 열려있으면 닫기 (비우기)
    if (formArea.innerHTML !== "") {
        formArea.innerHTML = "";
        return;
    }

    // 다른 열린 창들 다 닫기 (UX 선택사항: 한 번에 하나만 열기)
    document.querySelectorAll('.reReplyFormArea').forEach(el => el.innerHTML = "");

    // 입력창 HTML 주입
    formArea.innerHTML = `
        <div class="reReplyForm">
            <input type="text" id="input-${parentId}" placeholder="답글을 입력하세요..." onkeydown="if(event.key==='Enter') submitSubReply(${parentId})">
            <button onclick="submitSubReply(${parentId})">등록</button>
        </div>
    `;

    // 포커스 주기
    setTimeout(() => document.getElementById(`input-${parentId}`).focus(), 100);
}

/**
 * 대댓글 등록 요청
 */
function submitSubReply(parentId) {
    // 1. 로그인 체크 (강화된 로직)
    const loginCheckInput = document.getElementById("loginCheck");
    // 문자열 "true"인지 확인 (JSP EL 결과는 문자열로 넘어옵니다)
    const isLoggedIn = loginCheckInput && loginCheckInput.value === 'true';

    if (!isLoggedIn) {
        // alert 후 confirm을 하면 팝업이 두 번 뜨므로, confirm 하나로 합치는 것이 UX상 좋습니다.
        if (confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/login";
        }
        return; // 로그인 안 했으면 함수 강제 종료 (fetch 실행 X)
    }

    const inputEl = document.getElementById(`input-${parentId}`);
    const content = inputEl.value.trim();

    if (!content) {
        alert("내용을 입력해주세요.");
        return;
    }

    const reqDto = {
        postId: currentPostIdForReply,
        content: content,
        parentId: parentId // 부모 ID 포함!
    };

    fetch("/reply", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reqDto)
    })
    .then(res => res.json())
    .then(newReply => {
        // [핵심 변경] 전체 리로드(loadReplies)를 하지 않고 DOM에 바로 추가합니다.

        // 1. 입력창 닫기 (폼 비우기)
        const formArea = document.getElementById(`reReplyForm-${parentId}`);
        if(formArea) formArea.innerHTML = "";

        // 2. 대댓글 목록 영역 찾기
        const subReplyArea = document.getElementById(`subReplyArea-${parentId}`);
        const viewBtn = document.getElementById(`btnViewReply-${parentId}`);

        showToastMsg("대댓글 등록이 완료되었습니다.");

        if (subReplyArea) {
            // [Case A] 대댓글 목록이 이미 존재하는 경우 (기존 대댓글이 1개 이상)

            // 1) 새 댓글 HTML 생성 (isSubReply = true)
            // *주의: 작성자 프로필 이미지 등이 newReply에 포함되어 있어야 함
            const html = createReplyItemHtml(newReply, true);

            // 2) 목록의 맨 끝에 추가
            subReplyArea.insertAdjacentHTML("beforeend", html);

            // 3) 만약 닫혀있었다면 강제로 열어서 내가 쓴 글 보여주기
            if (subReplyArea.style.display === "none") {
                subReplyArea.style.display = "block";
                if(viewBtn) viewBtn.innerHTML = "─── 대댓글 숨기기";
            }

            // 4) 긴 글 더보기 버튼 적용
            // (앞서 추가한 checkCommentOverflow 함수가 있다면 호출)
            if (typeof checkCommentOverflow === 'function') {
                checkCommentOverflow();
            }

        } else {
            // [Case B] 대댓글이 처음 달리는 경우 (목록 영역이 아예 없음)
            // 이 경우에는 대댓글 버튼과 영역을 새로 만들어야 하므로, 부득이하게 전체 새로고침을 합니다.
            // (첫 댓글이라 어차피 열려있는 상태가 아니므로 괜찮습니다.)
            replyPage = 0;
            isReplyLastPage = false;
            document.getElementById("commentListUl").innerHTML = "";
            loadReplies(currentPostIdForReply, 0);
        }
    })
    .catch(err => console.error("대댓글 등록 실패", err));
}

/**
 *  대댓글 목록 가져오기 (Lazy Loading)
 * - 버튼 클릭 시 호출됨
 */
function loadChildReplies(parentId, count) {
    const listArea = document.getElementById(`subReplyArea-${parentId}`);
    const btn = document.getElementById(`btnViewReply-${parentId}`);

    // 1. 이미 데이터를 가져온 적이 있다면 -> 토글(보이기/숨기기)만 수행
    if (listArea.innerHTML.trim() !== "") {
        if (listArea.style.display === "none") {
            listArea.style.display = "block";
            btn.innerHTML = `─── 대댓글 숨기기`;
        } else {
            listArea.style.display = "none";
            btn.innerHTML = `─── 대댓글 ${count}개 보기`;
        }
        return;
    }

    // 2. 데이터가 없으면 -> 서버 요청
    fetch(`/reply/${parentId}/children`)
        .then(res => {
            if (!res.ok) throw new Error("답글 조회 실패");
            return res.json();
        })
        .then(data => { // data는 List<ReplyResDto> 형태
            let html = "";

            // 가져온 자식 댓글들을 HTML로 변환 (isSubReply = true 전달)
            data.forEach(child => {
                html += createReplyItemHtml(child, true);
            });

            // 화면에 주입 및 버튼 텍스트 변경
            listArea.innerHTML = html;
            listArea.style.display = "block"; // 숨겨진 영역 보이기
            btn.innerHTML = `─── 대댓글 숨기기`;
        })
        .catch(err => {
            console.error(err);
            alert("답글을 불러오는데 실패했습니다.");
        });
}

// 4. 댓글 등록 (AJAX)
document.getElementById("btnReplyRegist").addEventListener("click", () => {
    const loginCheckInput = document.getElementById("loginCheck");
    // 문자열 "true"인지 확인 (JSP EL 결과는 문자열로 넘어옴)
    const isLoggedIn = loginCheckInput && loginCheckInput.value === 'true';

    if (!isLoggedIn) {
        alert("로그인이 필요한 서비스입니다.");

        // confirm을 써서 선택권을 주는 것이 더 세련된 UX임
        if(confirm("로그인 페이지로 이동하시겠습니까?")) {
             location.href = "/login";
        }
        return; // 함수 강제 종료 (fetch 실행 안 함)
    }

    const contentInput = document.getElementById("replyInput");
    const content = contentInput.value.trim();

    if (!content) {
        alert("내용을 입력해주세요.");
        return;
    }

    // 로그인 체크 등 필요시 추가
    const reqDto = {
        postId: currentPostIdForReply,
        content: content,
        parentId: null
    };

    fetch("/reply", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reqDto)
    })
    .then(res => res.json())
    .then(newReply => {
        // 성공 시 리스트 초기화 후 다시 로드 (가장 간단한 방법)
        replyPage = 0;
        isReplyLastPage = false;
        document.getElementById("commentListUl").innerHTML = "";
        loadReplies(currentPostIdForReply, 0);
        contentInput.value = "";
        showToastMsg("댓글 등록이 완료되었습니다");
    })
    .catch(err => console.error("등록 실패", err));
});

// 5. 댓글 무한 스크롤 이벤트
const commentScrollArea = document.getElementById("commentScrollArea");
if (commentScrollArea) {
    commentScrollArea.addEventListener("scroll", () => {
        const scrollTop = commentScrollArea.scrollTop;
        const clientHeight = commentScrollArea.clientHeight;
        const scrollHeight = commentScrollArea.scrollHeight;

        if (scrollTop + clientHeight >= scrollHeight - 50) {
            loadReplies(currentPostIdForReply, replyPage);
        }
    });
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

// 좋아요 토글 함수 (Ajax)
// shorts.js 파일의 toggleLike 함수 수정

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
            "Content-Type": "application/json" //  JSON 형식임을 명시
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