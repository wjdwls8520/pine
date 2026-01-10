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