/**
 * reply.js (shorts.js에서 분리된 공용 및 댓글 로직)
 * - 모든 클래스명 및 ID 선택자는 camelCase 준수
 */

// ==========================================
//  1. 전역 변수 및 유틸리티 함수
// ==========================================

const loginUserVal = document.getElementById("loginUser") ? document.getElementById("loginUser").value : null;

function escapeHtml(text) {
    if (!text) return text;
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

/**
 * [공용] 로그인 여부 체크 및 이동 컨펌
 * @returns {boolean} 로그인 상태면 true, 아니면 false
 */
function requireLogin() {
    if (!loginUserVal) {
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

// ==========================================
//  2. 댓글 상태 변수
// ==========================================
let currentPostIdForReply = null;
let replyPage = 0;
let isReplyLastPage = false;
let isReplyLoading = false;


// ==========================================
//  3. 조회 로직 (Read)
// ==========================================

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
 * 대댓글 목록 가져오기 (Lazy Loading)
 * - 버튼 클릭 시 호출됨
 */
function loadChildReplies(parentId, count) {
    // sub-reply-area -> subReplyArea
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


// ==========================================
//  4. 렌더링 로직 (Render)
// ==========================================

/**
 * 댓글 HTML 생성 함수
 */
function createReplyItemHtml(reply, isSubReply = false) {
    let dateStr = typeof timeAgoAjax === 'function' ? timeAgoAjax(reply.writeDate) : reply.writeDate;
    // 수정된 댓글이면 날짜 뒤에 (수정됨) 추가
    if (reply.isEdited) {
        dateStr += ' <span class="editedText">(수정됨)</span>';
    }
    let isDeleted = reply.deleteYN === 'Y';

    // 삭제된 댓글 처리
    let contentClass = isDeleted ? 'deleted' : '';
    let contentText = isDeleted ? '삭제된 댓글입니다.' : escapeHtml(reply.content);
    let nickname = isDeleted ? '(알수없음)' : `@${reply.nickname}`;

    // 프로필 이미지
    let defaultImg = '/images/user.png';
    let profileSrc = (reply.profileImg && !isDeleted) ? reply.profileImg : defaultImg;

    // 1. 드롭다운 메뉴 HTML 생성
    let optionHtml = '';

    // 삭제된 댓글이 아닐 때만 메뉴 표시
    if (!isDeleted) {
        let menuItems = '';

        const isMine = loginUserVal && (String(reply.memberId) === String(loginUserVal));

        if (isMine) {
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
        childrenHtml = `<ul class="replyList subReplyArea" id="subReplyArea-${reply.id}" style="display:none;"></ul>`;
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
                    <span class="date replyDate">${dateStr}</span>
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


// ==========================================
//  5. UI 상호작용 및 헬퍼 함수 (Interaction)
// ==========================================

/**
 * 텍스트 더보기/접기 토글 함수
 */
function toggleExpand(element) {
    element.classList.toggle('expanded');
}

/**
 * 댓글/대댓글 더보기 감지 함수
 */
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


// ==========================================
//  6. 등록/수정/삭제/좋아요 로직 (Action)
// ==========================================

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
 * 댓글 수정 폼 열기
 */
/* 1. 수정 폼 열기 */
function showEditForm(replyId) {
    const replyItem = document.getElementById(`reply-${replyId}`);
    const pTag = replyItem.querySelector('p'); // 본문 텍스트 태그
    const originalContent = pTag.innerText; // 현재 적혀있는 내용 가져오기

    // 이미 수정 창이 열려있다면 중복 실행 방지
    if (replyItem.querySelector('.editFormContainer')) return;

    // 1. 기존 텍스트 숨기기
    pTag.style.display = 'none';

    // 2. 수정 폼 HTML 생성
    const editFormHtml = `
        <div class="editFormContainer" id="edit-form-${replyId}">
            <textarea class="editTextarea" id="edit-textarea-${replyId}" maxlength="500">${originalContent}</textarea>
            <div class="editBtnGroup">
                <button type="button" class="btnCancel" onclick="cancelEdit(event, ${replyId})">취소</button>
                <button type="button" class="btnSave" onclick="saveEdit(${replyId})">저장</button>
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
            const dateSpan = replyItem.querySelector('.replyDate');

            // (1) 날짜 태그가 있고 (2) 아직 "(수정됨)" 표시가 없을 때만 추가
            if (dateSpan && !replyItem.querySelector('.editedText')) {
                dateSpan.insertAdjacentHTML('beforeend', ' <span class="editedText">(수정됨)</span>');
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


// ==========================================
//  7. 이벤트 리스너 (Events)
// ==========================================

// 4. 댓글 등록 (AJAX)
const btnReplyRegist = document.getElementById("btnReplyRegist");
if (btnReplyRegist) {
    btnReplyRegist.addEventListener("click", () => {
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
}

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