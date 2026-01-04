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
 *  [핵심] IntersectionObserver (관찰자) 설정
 * - 스크롤에 따라 요소가 화면에 들어오거나 나갈 때 실행됩니다.
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

            // [데이터 렌더링] 받아온 리스트를 HTML로 변환하여 추가
            data.shortsList.map((info) => {
                let videoFile = info.files.find(f => f.contentType.includes("video"));
                let thumbnailFile = info.files.find(f => f.contentType.includes("image"));

                let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(info.writeDate) : info.writeDate;

                let userProfile = info.profileImg ? info.profileImg : '/images/icon_pinedory.png';

                shortsFeedWrap.insertAdjacentHTML("beforeend", `
                   <div class="shortsCard" data-title="${info.title}" data-user="${info.nickname}">
                        <div class="cardInner">
                            <aside class="userPanel">
                                <div class="userWrap">

                                    <div class="userHeader">
                                        <div class="avatar">
                                            <img src="${userProfile}" alt="user">
                                        </div>
                                        <div class="userInfo">
                                            <strong class="nickname">${info.nickname}</strong>
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
                                       poster="${thumbnailFile && thumbnailFile.path ? thumbnailFile.path : ''}"
                                       ontimeupdate="updateProgress(this)">
                                       <source src="${videoFile && videoFile.path ? videoFile.path : ''}">
                                </video>
                                <div class="playOverlay"></div>
                                <div class="timeDisplay">00:00 / 00:00</div>
                                <div class="progressBarContainer">
                                    <div class="progressBarFill"></div>
                                </div>
                            </div>

                            <div class="actionPanel">
                                <button class="actionBtn like"><span>좋아요</span><em>${info.likeCount || 0}</em></button>
                                <button class="actionBtn share"><span>공유</span><em>128</em></button>
                                <button class="actionBtn commentToggle" onclick="openComment(${info.postId}, '${info.title}', '${info.nickname}');">
                                    <span>댓글</span><em>${info.replyCount || 0}</em>
                                </button>
                            </div>
                        </div>
                    </div>
                `);

                const newCard = shortsFeedWrap.lastElementChild;
                const titleEl = newCard.querySelector('.shortsTitle');
                const descEl = newCard.querySelector('.shortsContent');

                // 제목 넘침 검사 (2줄 이상인지)
                if (titleEl.scrollHeight > titleEl.clientHeight) {
                    titleEl.classList.add('expandable'); // CSS 커서 적용
                    titleEl.setAttribute('onclick', 'toggleExpand(this)');
                }

                // 내용 넘침 검사 (3줄 이상인지)
                if (descEl.scrollHeight > descEl.clientHeight) {
                    descEl.classList.add('expandable');
                    descEl.setAttribute('onclick', 'toggleExpand(this)');
                }
                observer.observe(newCard);
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
//  초기 실행 및 이벤트 리스너
// ==========================================
window.addEventListener("load", () => {
    // 1. 페이지 로드 시 첫 번째 데이터 요청
    getData(page);

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

// 댓글창 열기/닫기
function openComment(postId, title, writer) {
    let popupComment = document.getElementById("commentsPanel");
    if(popupComment) popupComment.classList.add("open");
}
function closeComment() {
    let popupComment = document.getElementById("commentsPanel");
    if(popupComment) popupComment.classList.remove("open");
}