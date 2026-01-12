/**
 * shorts.js
 * - 쇼츠 목록 무한 스크롤
 * - 비디오 자동 재생/일시정지 (IntersectionObserver)
 * - 커스텀 비디오 컨트롤 (재생바, 시간 표시, 클릭 토글)
 */

// ==========================================
//  0. 유틸리티 함수
// ==========================================

/**
 * HTML 이스케이프 함수 (XSS 방지)
 */
if (typeof escapeHtml === 'undefined') {
    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return String(text).replace(/[&<>"']/g, m => map[m]);
    }
}

/**
 * 숫자 포맷팅 (예: 1234 -> 1.2K)
 */
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

/**
 * 시간을 '00:00' 형식으로 변환
 */
function formatTime(seconds) {
    if (isNaN(seconds)) return "00:00";

    const min = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);

    // 숫자가 10보다 작으면 앞에 '0' 붙여주기
    const minStr = min < 10 ? `0${min}` : min;
    const secStr = sec < 10 ? `0${sec}` : sec;

    return `${minStr}:${secStr}`;
}


// ==========================================
//  1. 전역 변수 및 설정
// ==========================================
let page = 0;
let totalPages = 10;
let loading = false; // 중복 요청 방지용 플래그
let shortsFeedWrap = document.getElementById("shortsFeed");
const scrollBox = document.getElementById("shortsFeed");

// 설명창 중복 갱신 방지용 변수
let currentPostIdForDescription = null;
// (참고: 댓글창용 currentPostIdForReply는 openComment 등에서 사용됨)

/**
 * IntersectionObserver (관찰자) 설정
 * - 스크롤에 따라 요소가 화면에 들어오거나 나갈 때 실행됩니다.
 * - 쇼츠 카드가 화면 뷰포트에 들어오는지 감시
 */
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        // 관찰 대상(Card) 내부의 video 태그를 찾습니다.
        const video = entry.target.querySelector('video');
        if (!video) return;

        const currentPostId = entry.target.dataset.postId;

        if (entry.isIntersecting) {
            // [CASE 1] 화면에 들어옴 (60% 이상 보임)
            // -> 자동 재생 시작
            video.play().catch(e => console.log("자동 재생 막힘(브라우저 정책):", e));

            // 댓글 동기화 로직
            refreshCommentPanelIfOpen(entry.target);
            // 설명창 동기화 로직
            refreshDescriptionPanelIfOpen(entry.target);

            //  URL 주소 변경 (새로고침 없이 주소창만 바꿈)
            if (currentPostId) {
                history.replaceState(null, null, `/shorts/view/${currentPostId}`);
            }

            // 타이머 시작 (3초 시청 체크)
            if (entry.target.viewTimer) {
                clearTimeout(entry.target.viewTimer);
            }

            // 3초 뒤에 실행될 예약 걸기
            entry.target.viewTimer = setTimeout(() => {
                console.log(`[View] 3초 시청 완료! 조회수 증가 요청: ${currentPostId}`);
                increaseViewCount(currentPostId);
                entry.target.viewTimer = null;
            }, 3000);

        } else {
            // [CASE 2] 화면에서 나감
            // -> 영상 멈춤 및 시간 초기화
            video.pause();
            video.currentTime = 0;

            // 타이머 취소
            if (entry.target.viewTimer) {
                console.log(`[View] 3초 미만 시청. 집계 취소: ${currentPostId}`);
                clearTimeout(entry.target.viewTimer);
                entry.target.viewTimer = null;
            }
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

            // [예외 처리] 데이터가 하나도 없을 때
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

            // 데이터 렌더링
            data.shortsList.map((info) => {
                // 1. [중복 방지]
                if (document.querySelector(`.shortsCard[data-post-id="${info.postId}"]`)) {
                    console.log(`[Skip] 중복 영상 건너뜀: ${info.postId}`);
                    return;
                }
                // 2. 카드 생성
                renderShortCard(info, false);
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
 */
function getOneShort(targetId) {
    fetch(`/shorts/detail/${targetId}`)
        .then(res => {
            if (!res.ok) throw new Error("쇼츠 단건 조회 실패");
            return res.json();
        })
        .then(data => {
            renderShortCard(data, true); // prepend
            getData(0); // 나머지 목록 로딩
        })
        .catch(err => {
            console.error("단건 조회 중 에러:", err);
            getData(0);
        });
}


// ==========================================
//  3. 렌더링 (Rendering)
// ==========================================

/**
 * 쇼츠 카드 HTML 생성 및 삽입 함수
 */
function renderShortCard(info, isPrepend = false) {
    let videoFile = info.files.find(f => f.contentType.includes("video"));
    let thumbnailFile = info.files.find(f => f.contentType.includes("image"));
    let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(info.writeDate) : info.writeDate;
    let userProfile = info.profileImg ? info.profileImg : '/images/icon_pinedory.png';
    let likeClass = info.liked ? 'on' : '';

    // 태그 HTML 생성
    let tagHtml = '';
    if (info.tags && info.tags.length > 0) {
        tagHtml = `<div class="shorts-tags">`;
        info.tags.forEach(tag => {
            tagHtml += `<span class="tag-item">#${tag}</span>`;
        });
        tagHtml += `</div>`;
    }

    // 1. 게시물 데이터를 JSON 문자열로 변환 (설명 패널용)
    const postDataJson = JSON.stringify({
        postId: info.postId,
        title: info.title,
        nickname: info.nickname,
        writeDate: dateStr,
        content: info.content,
        tags: info.tags || [],
        likeCount: info.likeCount,
        viewCount: info.viewCount || 0,
        profileImg: userProfile
    }).replace(/"/g, '&quot;');

    // 2. 드롭다운 메뉴 아이템 구성
    let menuItems = `
        <li><button type="button" class="dropdownItem" onclick='openDescriptionFromMenu(this)'>설명</button></li>
        <li><button type="button" class="dropdownItem" onclick="handleReport('SHORTS', '${info.postId}')">신고</button></li>
    `;

    if (loginUser && String(info.memberId) === String(loginUser)) {
        menuItems = `
            <li><button type="button" class="dropdownItem" onclick='openDescriptionFromMenu(this)'>설명</button></li>
            <li><button type="button" class="dropdownItem" onclick="updateShorts('${info.postId}')">수정</button></li>
            <li><button type="button" class="dropdownItem danger" onclick="deleteShorts('${info.postId}')">삭제</button></li>
        `;
    }

    // 3. 드롭다운 HTML 조립
    const optionHtml = `
        <div class="commentOption" onclick="event.stopPropagation()" data-post-info="${postDataJson}">
            <button type="button" class="moreBtn" onclick="toggleCommentMenu(this)">
                <img src="/images/ico_menu.png" alt="더보기">
            </button>
            <ul class="dropdownMenu">
                ${menuItems}
            </ul>
        </div>
    `;

    const html = `
        <div class="shortsCard" data-post-id="${info.postId}" data-title="${info.title}" data-user="${info.nickname}" data-date="${dateStr}" data-post-info="${postDataJson}">
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
                            <p class="shortsTitle" onclick="openDescriptionFromTitle(this)" style="cursor: pointer;">${escapeHtml(info.title)}</p>
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

    // DOM 추가
    if (isPrepend) {
        shortsFeedWrap.insertAdjacentHTML("afterbegin", html);
    } else {
        shortsFeedWrap.insertAdjacentHTML("beforeend", html);
    }

    // 방금 추가된 요소 선택 및 옵저버 등록
    const newCard = isPrepend ? shortsFeedWrap.firstElementChild : shortsFeedWrap.lastElementChild;

    // 더보기 로직
    const descEl = newCard.querySelector('.shortsContent');
    if (descEl && descEl.scrollHeight > descEl.clientHeight) {
        descEl.classList.add('expandable');
        descEl.setAttribute('onclick', 'toggleExpand(this)');
    }

    observer.observe(newCard);
}


// ==========================================
//  4. 비디오 및 UI 제어 (Controls)
// ==========================================

/**
 * 영상 재생/일시정지 토글
 */
function toggleVideo(shellElement) {
    const video = shellElement.querySelector('video');

    if (video.paused) {
        video.play();
        shellElement.classList.remove('paused');
    } else {
        video.pause();
        shellElement.classList.add('paused');
    }
}

/**
 * 진행바 및 시간 텍스트 업데이트
 */
function updateProgress(videoElement) {
    if (isNaN(videoElement.duration)) return;

    // 진행바 업데이트
    const percent = (videoElement.currentTime / videoElement.duration) * 100;
    const progressBar = videoElement.parentElement.querySelector('.progressBarFill');
    if (progressBar) {
        progressBar.style.width = `${percent}%`;
    }

    // 시간 텍스트 업데이트
    const current = formatTime(videoElement.currentTime);
    const total = formatTime(videoElement.duration);
    const timeDisplay = videoElement.parentElement.querySelector('.timeDisplay');

    if (timeDisplay) {
        timeDisplay.innerText = `${current} / ${total}`;
    }
}


// ==========================================
//  5. 패널 관리 (설명창 & 댓글창)
// ==========================================

// ---------------------------
//  5-1. 설명 패널 (Description)
// ---------------------------

/**
 * 설명 패널 열기 (UI 렌더링)
 */
function openDescription(postData) {
    const panel = document.getElementById("descriptionPanel");

    if (!panel) {
        console.error("[설명 패널] 패널 요소를 찾을 수 없습니다.");
        return;
    }

    // 헤더 정보
    document.getElementById("descPanelTitle").innerText = postData.title || "제목 없음";

    const profileImg = document.getElementById("descPanelProfileImg");
    if (profileImg) {
        profileImg.src = postData.profileImg || '/images/icon_pinedory.png';
    }

    const nickname = postData.nickname ? postData.nickname : "알 수 없음";
    document.getElementById("descPanelUser").innerText = "@" + nickname;

    // 날짜
    if (typeof timeAgoAjax === 'function' && postData.writeDate) {
        document.getElementById("descPanelDate").innerText = timeAgoAjax(postData.writeDate);
    }

    // 본문 (줄바꿈 처리)
    const contentEl = document.getElementById("descPanelContent");
    if (contentEl) {
        const formattedContent = escapeHtml(postData.content || "내용 없음").replace(/\n/g, '<br>');
        contentEl.innerHTML = formattedContent;
    }

    // 해시태그
    const tagsContainer = document.getElementById("descPanelTags");
    if (tagsContainer) {
        if (postData.tags && postData.tags.length > 0) {
            let tagsHtml = '';
            postData.tags.forEach(tag => {
                tagsHtml += `<span class="tag-item">#${escapeHtml(tag)}</span>`;
            });
            tagsContainer.innerHTML = tagsHtml;
            tagsContainer.style.display = 'flex';
        } else {
            tagsContainer.innerHTML = '';
            tagsContainer.style.display = 'none';
        }
    }

    // 통계 정보
    document.getElementById("descPanelLikeCount").innerText = formatNumber(postData.likeCount || 0);
    document.getElementById("descPanelViewCount").innerText = formatNumber(postData.viewCount || 0);
    document.getElementById("descPanelReplyCount").innerText = formatNumber(postData.replyCount || 0);

    // 패널 열기
    panel.classList.add("open");
}

/**
 * 설명 패널 닫기
 */
function closeDescription() {
    const panel = document.getElementById("descriptionPanel");
    if (panel) {
        panel.classList.remove("open");
    }
}

/**
 * 제목 클릭 시 설명 패널 열기 (서버 조회)
 */
function openDescriptionFromTitle(titleElement) {
    const card = titleElement.closest('.shortsCard');
    if (!card) return;

    const postId = card.dataset.postId;
    if (!postId) {
        console.error("[설명 패널] 게시물 ID를 찾을 수 없습니다.");
        return;
    }

    fetch(`/shorts/detail/${postId}`)
        .then(res => {
            if (!res.ok) throw new Error("서버 통신 실패");
            return res.json();
        })
        .then(data => {
            openDescription(data);
        })
        .catch(err => {
            console.error("[설명 패널] 최신 데이터 불러오기 실패:", err);
        });
}

/**
 * 메뉴에서 설명 패널 열기 (서버 조회)
 */
function openDescriptionFromMenu(menuButton) {
    const card = menuButton.closest('.shortsCard');
    if (!card) return;

    const postId = card.dataset.postId;

    fetch(`/shorts/detail/${postId}`)
        .then(res => res.json())
        .then(data => {
            openDescription(data);

            const dropdown = menuButton.closest('.dropdownMenu');
            if (dropdown) {
                dropdown.classList.remove('active');
            }
        })
        .catch(err => console.error("데이터 로드 에러:", err));
}

/**
 * 스크롤 시 설명창 자동 갱신 (Observer에서 호출)
 */
function refreshDescriptionPanelIfOpen(cardElement) {
    const panel = document.getElementById("descriptionPanel");

    // 패널이 닫혀있으면 중단
    if (!panel || !panel.classList.contains("open")) return;

    const newPostId = cardElement.dataset.postId;

    // 중복 갱신 방지
    if (currentPostIdForDescription === newPostId) return;

    console.log(`[Sync] 설명창 갱신: 게시글 ID ${newPostId}`);
    currentPostIdForDescription = newPostId;

    // 서버 최신 데이터 조회
    fetch(`/shorts/detail/${newPostId}`)
        .then(res => {
            if (!res.ok) throw new Error("상세 정보 조회 실패");
            return res.json();
        })
        .then(data => {
            openDescription(data);
        })
        .catch(err => console.error("설명창 갱신 실패:", err));
}

// ---------------------------
//  5-2. 댓글 패널 (Comments)
// ---------------------------

/**
 * 댓글창 열기
 */
function openComment(postId, title, writer, date) {
    const panel = document.getElementById("commentsPanel");

    currentPostIdForReply = postId;
    replyPage = 0;
    isReplyLastPage = false;
    isReplyLoading = false;

    document.getElementById("commentPanelTitle").innerText = title;
    document.getElementById("commentPanelUser").innerText = "@" + writer;
    document.getElementById("commentPanelDate").innerText = date || "";

    // 프로필 이미지 설정
    const card = document.querySelector(`.shortsCard[data-post-id="${postId}"]`);
    if (card) {
        const postInfoStr = card.getAttribute('data-post-info');
        if (postInfoStr) {
            try {
                const postInfo = JSON.parse(postInfoStr.replace(/&quot;/g, '"'));
                const profileImg = document.getElementById("commentPanelProfileImg");
                if (profileImg) {
                    profileImg.src = postInfo.profileImg || '/images/icon_pinedory.png';
                }
            } catch (e) {
                console.error("프로필 이미지 설정 중 오류:", e);
            }
        }
    }

    document.getElementById("commentListUl").innerHTML = "";
    document.getElementById("replyInput").value = "";

    panel.classList.add("open");

    loadReplies(postId, 0);
}

/**
 * 댓글창 닫기
 */
function closeComment() {
    document.getElementById("commentsPanel").classList.remove("open");
}

/**
 * 스크롤 시 댓글창 자동 갱신
 */
function refreshCommentPanelIfOpen(cardElement) {
    const panel = document.getElementById("commentsPanel");

    if (!panel.classList.contains("open")) return;

    const newPostId = cardElement.dataset.postId;
    const title = cardElement.dataset.title;
    const writer = cardElement.dataset.user;
    const date = cardElement.dataset.date;

    if (currentPostIdForReply == newPostId) return;

    console.log(`[Sync] 댓글창 갱신: 게시글 ID ${newPostId}`);

    currentPostIdForReply = newPostId;
    replyPage = 0;
    isReplyLastPage = false;

    document.getElementById("commentPanelTitle").innerText = title;
    document.getElementById("commentPanelUser").innerText = "@" + writer;
    document.getElementById("commentPanelDate").innerText = date || "";

    // 프로필 이미지 설정
    const postInfoStr = cardElement.getAttribute('data-post-info');
    if (postInfoStr) {
        try {
            const postInfo = JSON.parse(postInfoStr.replace(/&quot;/g, '"'));
            const profileImg = document.getElementById("commentPanelProfileImg");
            if (profileImg) {
                profileImg.src = postInfo.profileImg || '/images/icon_pinedory.png';
            }
        } catch (e) {
            console.error("프로필 이미지 설정 중 오류:", e);
        }
    }

    document.getElementById("commentListUl").innerHTML = "";
    document.getElementById("replyInput").value = "";
    loadReplies(newPostId, 0);
}


// ==========================================
//  6. 액션 및 이벤트 처리 (Actions & Events)
// ==========================================

// 좋아요 토글
function toggleLike(postId, btnElement) {
    const loginCheckInput = document.getElementById("loginCheck");
    if (!loginCheckInput || loginCheckInput.value !== 'true') {
        if(confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/login";
        }
        return;
    }

    const requestData = {
        targetType: "POST",
        targetId: postId
    };

    fetch(`/like`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
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
            const countEm = btnElement.querySelector('em');

            if (data.liked) {
                btnElement.classList.add('on');
                showToastMsg("이 영상을 좋아합니다!");
            } else {
                btnElement.classList.remove('on');
                showToastMsg("좋아요를 취소했습니다.");
            }
            countEm.innerText = data.likeCount;
        })
        .catch(err => {
            console.error("좋아요 처리 중 오류 발생:", err);
            alert("좋아요 처리에 실패했습니다. 잠시 후 다시 시도해주세요.");
        });
}

// 조회수 증가 API 호출
function increaseViewCount(postId) {
    if (!postId) return;

    fetch(`/view/shorts/${postId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(res => {
            if (res.ok) {
                console.log(`[View] 조회수 집계 성공 (ID: ${postId})`);
            } else {
                console.warn(`[View] 조회수 집계 실패 상태코드: ${res.status}`);
            }
        })
        .catch(err => console.error("[View] 통신 오류:", err));
}

// DOM 내 조회수 업데이트
function updateViewCountInDOM(postId) {
    const card = document.querySelector(`.shortsCard[data-post-id="${postId}"]`);
    if (!card) return;

    let infoStr = card.getAttribute('data-post-info');
    if (infoStr) {
        try {
            let info = JSON.parse(infoStr.replace(/&quot;/g, '"'));
            info.viewCount = (info.viewCount || 0) + 1;

            const newInfoStr = JSON.stringify(info).replace(/"/g, '&quot;');
            card.setAttribute('data-post-info', newInfoStr);

            const panel = document.getElementById("descriptionPanel");
            if (panel && panel.classList.contains("open")) {
                const panelTitle = document.getElementById("descPanelTitle").innerText;
                if (panelTitle === info.title) {
                    document.getElementById("descPanelViewCount").innerText = formatNumber(info.viewCount);
                }
            }
        } catch (e) {
            console.error("조회수 업데이트 중 JSON 파싱 에러:", e);
        }
    }
}

// 쇼츠 수정
function updateShorts(postId) {
    location.href = `/shorts/shortsUpdate/${postId}`;
}

// 쇼츠 삭제
function deleteShorts(postId) {
    if (!confirm("정말 이 쇼츠를 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.")) {
        return;
    }

    fetch(`/shorts/delete/${postId}`, {
        method: 'DELETE',
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert(data.msg);
            location.href = '/shorts';
        } else {
            alert(data.msg);
        }
    })
    .catch(err => {
        console.error("삭제 실패:", err);
        alert("시스템 오류가 발생했습니다.");
    });
}

// 링크 복사 (공유 버튼 이벤트 위임)
shortsFeedWrap.addEventListener('click', (e) => {
    const shareBtn = e.target.closest('.actionBtn.share');

    if (shareBtn) {
        const currentUrl = window.location.href;
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
//  7. 초기 실행 (Initialization)
// ==========================================
window.addEventListener("load", () => {
    // 1. 공유 링크 등으로 들어왔는지 확인
    const targetIdInput = document.getElementById("targetShortsId");
    const targetId = targetIdInput ? targetIdInput.value : null;

    if (targetId) {
        console.log(`[DeepLink] 공유된 쇼츠 ID: ${targetId} 로 시작합니다.`);
        getOneShort(targetId);
    } else {
        getData(page);
    }

    // 2. 무한 스크롤 이벤트 감지
    scrollBox.addEventListener("scroll", () => {
        const scrollTop = scrollBox.scrollTop;
        const viewportHeight = scrollBox.clientHeight;
        const totalHeight = scrollBox.scrollHeight;

        if (scrollTop + viewportHeight >= totalHeight - 100 && !loading && (page < totalPages - 1)) {
            console.log(" 바닥 감지! 다음 페이지 요청");
            page++;
            getData(page);
        }
    });
});