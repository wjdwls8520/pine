/**
 * shorts.js
 * - 쇼츠 목록 무한 스크롤
 * - 비디오 자동 재생/일시정지 (IntersectionObserver)
 * - 커스텀 비디오 컨트롤 (
 * 재생바, 시간 표시, 클릭 토글)
 */
function escapeHtml(text) {
    if (!text) return text;
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

let page = 0;
let totalPages = 10;
let loading = false; // 중복 요청 방지용 플래그
let shortsFeedWrap = document.getElementById("shortsFeed");
const scrollBox = document.getElementById("shortsFeed");
const loginUser = document.getElementById("loginUser") ? document.getElementById("loginUser").value : null;

/**
 * [공용] 로그인 여부 체크 및 이동 컨펌
 * @returns {boolean} 로그인 상태면 true, 아니면 false
 */
function requireLogin() {
    if (!loginUser) {
        if (confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/login";
        }
        return false;
    }
    return true;
}

/**
 * [공용] 신고 기능 (쇼츠/댓글 공용)
 * @param {string} type - 'SHORTS' 또는 'REPLY'
 * @param {string|number} id - 대상 ID
 */
function handleReport(type, id) {
    // 1. 클릭 시점에 로그인 체크 수행
    if (!requireLogin()) return;

    // 2. 신고 로직 (공용)
    if (confirm(`정말 이 ${type === 'SHORTS' ? '게시물을' : '댓글을'} 신고하시겠습니까?`)) {
        // 실제 API 호출 로직 (예시)
        // fetch('/report', { body: JSON.stringify({ type, id }) ... })

        alert("신고가 정상적으로 접수되었습니다.");

        // 열려있는 드롭다운 닫기
        document.querySelectorAll('.dropdownMenu.active').forEach(m => m.classList.remove('active'));
    }
}

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
// 쇼츠 카드 렌더링 및 단건 조회 함수
// ==========================================

/**
 *  단건 쇼츠 조회 (공유 링크용)
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
    // page가 0이면(새로고침) LastPage 플래그를 초기화해줘야 다시 로딩이 됩니다.
    if (page === 0) {
        isReplyLastPage = false;
    }

    if (isReplyLoading || isReplyLastPage) return;

    isReplyLoading = true;

    fetch(`/reply/list?postId=${postId}&page=${page}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        cache: "no-store"
    })
    .then(res => {
        if (!res.ok) throw new Error("댓글 조회 실패");
        return res.json();
    })
    .then(data => {
        const replies = data.content;
        const listUl = document.getElementById("commentListUl");

        // 첫 페이지(0)를 부를 땐, 기존 목록을 싹 비워야 합니다. (중복 방지)
        if (page === 0) {
            listUl.innerHTML = "";
        }

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

        checkCommentOverflow();
        replyPage++; // 다음 페이지 준비
    })
    .catch(err => console.error(err))
    .finally(() => {
        isReplyLoading = false;
    });
}

/**
 * 댓글 HTML 생성 함수
 */
function createReplyItemHtml(reply, isSubReply = false) {
    let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(reply.writeDate) : reply.writeDate;
    // 수정된 댓글이면 날짜 뒤에 (수정됨) 추가
    if (reply.isEdited) {
        dateStr += ' <span class="edited-text">(수정됨)</span>';
    }
    let isDeleted = reply.deleteYN === 'Y';

    // 삭제된 댓글 처리
    let contentClass = isDeleted ? 'deleted' : '';
    let contentText = isDeleted ? '삭제된 댓글입니다.' : escapeHtml(reply.content);
    let nickname = isDeleted ? '(알수없음)' : `@${reply.nickname}`;

    // 프로필 이미지
    let defaultImg = '/images/icon_pinedory.png';
    let profileSrc = (reply.profileImg && !isDeleted) ? reply.profileImg : defaultImg;

    // 1. 드롭다운 메뉴 HTML 생성
    let optionHtml = '';

    // 삭제된 댓글이 아닐 때만 메뉴 표시
    if (!isDeleted) {
        let menuItems = '';

        // 로그인 여부와 관계없이 일단 메뉴 버튼 구조는 준비할 수 있지만,
        // 댓글의 경우 보통 '신고' 외에 '설명' 같은 게 없으므로
        // 비로그인 상태면 아예 메뉴를 안 보여주는 게 나을 수도 있습니다.
        // 하지만 "신고 시 로그인 유도"를 원하시므로 메뉴를 보여줍니다.

        const isMine = loginUser && (String(reply.memberId) === String(loginUser));

        if (isMine) {
            // 내 댓글: 수정/삭제
            menuItems = `
                <li><button type="button" class="dropdownItem" onclick="showEditForm('${reply.id}')">수정</button></li>
                <li><button type="button" class="dropdownItem danger" onclick="deleteComment('${reply.id}')">삭제</button></li>
            `;
        } else {
            // 남의 댓글 or 비로그인: 신고
            // handleReport 함수가 클릭 시 로그인 체크를 수행합니다.
            menuItems = `
                <li><button type="button" class="dropdownItem" onclick="handleReport('REPLY', '${reply.id}')">신고</button></li>
            `;
        }

        optionHtml = `
            <div class="commentOption">
                <button type="button" class="moreBtn" onclick="toggleCommentMenu(this)">
                    <img src="/images/ico_menu.png" alt="더보기">
                </button>
                <ul class="dropdownMenu">
                    ${menuItems}
                </ul>
            </div>`;
    }

    // 2. 답글 버튼 (삭제 안 됨 && 대댓글 아님)
    let replyBtnHtml = (!isDeleted && !isSubReply)
        ? `<button class="btnReReply" onclick="toggleReReplyForm(${reply.id})">답글달기</button>`
        : '';

    // 3. 좋아요 버튼
    let likeClass = reply.liked ? 'on' : '';
    let likeCount = reply.likeCount != null ? reply.likeCount : 0;
    let likeBtnHtml = !isDeleted ? `
        <button class="btnCommentLike ${likeClass}" onclick="toggleCommentLike(${reply.id}, this)">
            <span class="ico"></span>
            <em>${likeCount}</em>
        </button>
    ` : '';

    // 4. 대댓글 보기 버튼 및 영역
    let childrenHtml = "";
    let viewReplyBtn = "";
    if (!isSubReply && reply.childCount > 0) {
        viewReplyBtn = `
            <button class="btnViewReply" id="btnViewReply-${reply.id}" onclick="loadChildReplies(${reply.id}, ${reply.childCount})">
                ─── 대댓글 ${reply.childCount}개 보기
            </button>
        `;
        childrenHtml = `<ul class="replyList sub-reply-area" id="subReplyArea-${reply.id}" style="display:none;"></ul>`;
    }

    // 최종 HTML 반환
    return `
        <li class="replyItem" id="reply-${reply.id}" data-id="${reply.id}">
            <div class="replyProfileBox">
                <img src="${profileSrc}" alt="프로필" onerror="this.src='${defaultImg}'">
            </div>
            <div class="replyContentBox">
                <div class="commentTop">
                    <b>${nickname}</b>
                    <span class="date reply-date">${dateStr}</span>
                    ${optionHtml}
                </div>

                <p class="${contentClass}">${contentText}</p>

                <div class="commentAction">
                    ${likeBtnHtml}
                    ${replyBtnHtml}
                </div>

                <div id="reReplyForm-${reply.id}" class="reReplyFormArea"></div>

                ${viewReplyBtn}
                ${childrenHtml}
            </div>
        </li>
    `;
}

/**
 * 답글 입력창 토글 (열기/닫기)
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
 *  댓글 패널 외부 클릭 시 닫기
 * - 패널이 열려있을 때, 패널 밖이나 여는 버튼이 아닌 곳을 누르면 닫습니다.
 */
document.addEventListener('click', function(e) {
    const panel = document.getElementById("commentsPanel");

    // 1. 패널이 없거나 닫혀있으면 로직 수행 안 함
    if (!panel || !panel.classList.contains('open')) return;

    // 2. 클릭한 요소가 '패널 내부'라면 닫지 않음
    if (panel.contains(e.target)) return;

    // 3. 클릭한 요소가 '댓글 열기 버튼(.commentToggle)'이라면 닫지 않음
    // (버튼을 누르면 openComment 함수가 실행되어야 하는데, 여기서 닫아버리면 충돌남)
    if (e.target.closest('.commentToggle')) return;

    // 4. 그 외 영역(영상, 빈 공간 등)을 클릭했다면 패널 닫기
    closeComment();
});

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
        parentId: parentId
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
            const html = createReplyItemHtml(newReply, true);

            // 2) 목록의 맨 끝에 추가
            subReplyArea.insertAdjacentHTML("beforeend", html);

            // 3) 만약 닫혀있었다면 강제로 열어서 내가 쓴 글 보여주기
            if (subReplyArea.style.display === "none") {
                subReplyArea.style.display = "block";
                if(viewBtn) viewBtn.innerHTML = "─── 대댓글 숨기기";
            }

            // 4) 긴 글 더보기 버튼 적용
            if (typeof checkCommentOverflow === 'function') {
                checkCommentOverflow();
            }

        } else {
            // [Case B] 대댓글이 처음 달리는 경우 (목록 영역이 아예 없음)
            // 이 경우에는 대댓글 버튼과 영역을 새로 만들어야 하므로, 부득이하게 전체 새로고침을 합니다.
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
            checkCommentOverflow();
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
        listArea.style.display = "block";
        btn.innerHTML = `─── 대댓글 숨기기`;
        checkCommentOverflow();
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
        return;
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
        // 성공 시 리스트 초기화 후 다시 로드
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
const commentListUl = document.getElementById("commentListUl");
if (commentListUl) {
    commentListUl.addEventListener("scroll", () => {
        const scrollTop = commentListUl.scrollTop;
        const clientHeight = commentListUl.clientHeight;
        const scrollHeight = commentListUl.scrollHeight;

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

// 댓글 좋아요 토글 함수
function toggleCommentLike(replyId, btnElement) {
    // 1. 로그인 체크
    const loginCheckInput = document.getElementById("loginCheck");
    // (loginCheckInput이 없으면 false 처리, 있으면 값 확인)
    const isLoggedIn = loginCheckInput && loginCheckInput.value === 'true';

    if (!isLoggedIn) {
        if(confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/login";
        }
        return;
    }

    // 2. 서버 요청 데이터 (targetType을 'REPLY'로 가정)
    const requestData = {
        targetType: "REPLY",
        targetId: replyId
    };

    fetch(`/like`, { // 기존 좋아요 API 재사용 (백엔드에서 타입 분기 처리 필요)
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
    })
    .then(res => {
        if (!res.ok) throw new Error("좋아요 처리 실패");
        return res.json();
    })
    .then(data => {
        // data = { liked: true/false, likeCount: 123 }
        const em = btnElement.querySelector('em');

        if (data.liked) {
            btnElement.classList.add('on');
            showToastMsg("좋아요가 완료되었습니다");
        } else {
            btnElement.classList.remove('on');
            showToastMsg("좋아요가 취소되었습니다");
        }
        em.innerText = data.likeCount;
    })
    .catch(err => {
        console.error(err);
        // alert("오류가 발생했습니다.");
    });
}

// 댓글/대댓글 더보기 감지 함수
function checkCommentOverflow() {
    // .commentList 내부의 모든 p 태그 중, 아직 검사 안 된(.expandable 없는) 항목 선택
    // 대댓글도 .commentList 안에 포함되므로 한 번에 처리 가능합니다.
    const comments = document.querySelectorAll('.commentList p:not(.expandable)');

    comments.forEach(p => {
        // scrollHeight(실제 높이)가 clientHeight(화면에 보이는 높이)보다 크면 넘친 것임
        if (p.scrollHeight > p.clientHeight) {
            p.classList.add('expandable');
            p.setAttribute('onclick', 'toggleExpand(this)'); // 기존 토글 함수 재사용
        }
    });
}

/**
 * 드롭다운 메뉴 토글 (열기/닫기)
 */
function toggleCommentMenu(btn) {
    // 1. 현재 버튼의 바로 다음 형제인 ul(메뉴) 찾기
    const menu = btn.nextElementSibling;
    if (!menu) return;

    // 2. 현재 상태가 열려있는지 확인
    const isOpen = menu.classList.contains('active');

    // 3. 다른 모든 열려있는 메뉴 닫기 (하나만 열리도록)
    document.querySelectorAll('.dropdownMenu.active').forEach(item => {
        item.classList.remove('active');
    });

    // 4. 아까 안 열려 있었으면 열기 (토글)
    if (!isOpen) {
        menu.classList.add('active');
    }

    // 5. 클릭 이벤트 전파 방지 (바로 닫히는 것 방지)
    event.stopPropagation();
}

/**
 * 화면의 빈 곳을 클릭하면 열려있는 모든 드롭다운 닫기
 */
document.addEventListener('click', function(e) {
    // 클릭한 곳이 '.commentOption' 내부가 아니라면 닫음
    if (!e.target.closest('.commentOption')) {
        document.querySelectorAll('.dropdownMenu.active').forEach(menu => {
            menu.classList.remove('active');
        });
    }
});

/**
 * 댓글 수정
 */
/* 1. 수정 폼 열기 */
function showEditForm(replyId) {
    const replyItem = document.getElementById(`reply-${replyId}`);
    const pTag = replyItem.querySelector('p'); // 본문 텍스트 태그
    const originalContent = pTag.innerText; // 현재 적혀있는 내용 가져오기

    // 이미 수정 창이 열려있다면 중복 실행 방지
    if (replyItem.querySelector('.edit-form-container')) return;

    // 1. 기존 텍스트 숨기기
    pTag.style.display = 'none';

    // 2. 수정 폼 HTML 생성 (백틱 `` 사용)
    const editFormHtml = `
        <div class="edit-form-container" id="edit-form-${replyId}">
            <textarea class="edit-textarea" id="edit-textarea-${replyId}" maxlength="500">${originalContent}</textarea>
            <div class="edit-btn-group">
                <button type="button" class="btn-cancel" onclick="cancelEdit(event, ${replyId})">취소</button>
                <button type="button" class="btn-save" onclick="saveEdit(${replyId})">저장</button>
            </div>
        </div>
    `;

    // 3. p태그 바로 뒤에 폼 삽입
    pTag.insertAdjacentHTML('afterend', editFormHtml);

    // (선택사항) 드롭다운 메뉴 닫기
    document.querySelectorAll('.dropdownMenu.active').forEach(m => m.classList.remove('active'));
}

/* 2. 수정 취소 */
function cancelEdit(e, replyId) {
    // 클릭이 댓글 패널이나 배경으로 퍼지지 않아 패널이 닫히지않음
    if (e && typeof e.stopPropagation === 'function') {
        e.stopPropagation();
    }
    const replyItem = document.getElementById(`reply-${replyId}`);
    const pTag = replyItem.querySelector('p');
    const editForm = document.getElementById(`edit-form-${replyId}`);

    // 폼 제거
    if (editForm) {
        editForm.remove();
    }
    // 텍스트 다시 보이기
    if (pTag) {
        pTag.style.display = 'block'; // or 'flex' 등 원래 display 속성
    }
}

/* 3. 댓글 수정  */
function saveEdit(replyId) {
    const textarea = document.getElementById(`edit-textarea-${replyId}`);
    const newContent = textarea.value;

    // 유효성 검사
    if (newContent.trim() === "") {
        alert("내용을 입력해주세요.");
        return;
    }

    if (newContent.length > 500) {
        alert("댓글은 500자까지만 입력 가능합니다.");
        return;
    }

    fetch(`/reply`, {
        method: 'PUT',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ replyId: replyId, content: newContent })
    })
    .then(async res => {
        const msg = await res.text();

        if (res.status === 401) {
            alert(msg);
            window.location.href = "/login";
            return;
        }
        if (!res.ok) {
            alert(msg);
            return;
        }

        const replyItem = document.getElementById(`reply-${replyId}`);
        const pTag = replyItem.querySelector('p');

        // 1. 텍스트 내용 변경
        pTag.innerText = newContent;

        // 2. (수정됨) 표시 즉시 붙이기
        const dateSpan = replyItem.querySelector('.reply-date');

        // (1) 날짜 태그가 있고 (2) 아직 "(수정됨)" 표시가 없을 때만 추가
        if (dateSpan && !replyItem.querySelector('.edited-text')) {
            dateSpan.insertAdjacentHTML('beforeend', ' <span class="edited-text">(수정됨)</span>');
        }

        // 2. 폼 닫기 (취소 함수 재사용하면 됨)
        cancelEdit(null, replyId);

        showToastMsg("댓글이 수정되었습니다.");
    })
    .catch(err => console.error("수정 오류:", err));
}

/**
 * 댓글 삭제
 */
function deleteComment(replyId) {
    if (!confirm("정말 댓글을 삭제하시겠습니까?")) return;

    fetch(`/reply/${replyId}`, {
        method: 'DELETE',
        headers: { "Content-Type": "application/json" }
    })
        .then(async res => {
            const msg = await res.text();

            // 2. [401 Unauthorized] 로그인이 필요한 경우
            if (res.status === 401) {
                alert(msg);
                window.location.href = "/login";
                return;
            }

            // 3. [400 Bad Request / 403 Forbidden] 그 외 에러 (권한 없음, 이미 삭제됨 등)
            if (!res.ok) {
                alert(msg);
                return;
            }

            // 4. [200 OK] 성공
            showToastMsg(msg);

            // 목록 새로고침 (가장 깔끔한 방법)
            // 전역변수 currentPostIdForReply를 사용하여 현재 보고 있는 댓글창을 갱신합니다.
            loadReplies(currentPostIdForReply, 0);
        })
        .catch(err => console.error("통신 에러:", err));
}

















/* ===========================
   [게시글 전용] 수정/삭제 기능
   =========================== */

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