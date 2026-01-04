/**
 * commuDetail.js
 *
 */

let replyPage = 0;
let isLastReplyPage = false;
const PAGE_SIZE = 10;

document.addEventListener("DOMContentLoaded", () => {
    loadMainReplies();

    const btnMainReply = document.getElementById("btnMainReply");
    const mainInput = document.getElementById("mainReplyInput");

    if (btnMainReply && mainInput) {
        btnMainReply.addEventListener("click", () => {
            submitReply(null, mainInput);
        });

        mainInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                submitReply(null, mainInput);
            }
        });
    }
});

/**
 * [R] 메인 댓글 목록 불러오기
 */
function loadMainReplies() {
    if (isLastReplyPage && replyPage > 0) return;

    fetch(`/reply/list?postId=${POST_ID}&page=${replyPage}&size=${PAGE_SIZE}`)
        .then(res => res.json())
        .then(data => {
            const replies = data.content;
            const listArea = document.getElementById("replyListArea");
            const moreBtnWrap = document.getElementById("replyMoreBtnWrap"); // Wrapper 선택

            isLastReplyPage = data.last;

            if (replyPage === 0 && replies.length === 0) {
                // 클래스 적용 (emptyReplyMsg)
                listArea.innerHTML = `<div class="emptyReplyMsg">작성된 댓글이 없습니다.</div>`;
                moreBtnWrap.style.display = "none"; // 표시 제어는 JS가 함 (로직상 필요)
                return;
            }

            replies.forEach(reply => {
                const html = createReplyHtml(reply, false);
                listArea.insertAdjacentHTML("beforeend", html);
            });

            // 더보기 버튼 표시/숨김
            if (isLastReplyPage) {
                moreBtnWrap.style.display = "none";
            } else {
                moreBtnWrap.style.display = "block";
                replyPage++;
            }
        })
        .catch(err => console.error(err));
}

/**
 * [R] 대댓글 불러오기
 */
function loadChildReplies(parentId, childCount) {
    const childArea = document.getElementById(`childArea-${parentId}`);
    const toggleBtn = document.getElementById(`btnToggleChild-${parentId}`);

    // display 제어는 로직의 영역이라 style.display 사용 (초기값은 CSS로 none 처리됨)
    if (childArea.style.display === "block") {
        childArea.style.display = "none";
        toggleBtn.innerText = `── 답글 ${childCount}개 보기`;
        return;
    }

    if (childArea.innerHTML.trim() !== "") {
        childArea.style.display = "block";
        toggleBtn.innerText = `── 답글 숨기기`;
        return;
    }

    fetch(`/reply/${parentId}/children`)
        .then(res => res.json())
        .then(children => {
            let html = "";
            children.forEach(child => {
                html += createReplyHtml(child, true);
            });
            childArea.innerHTML = html;
            childArea.style.display = "block";
            toggleBtn.innerText = `── 답글 숨기기`;
        })
        .catch(err => {
            console.error(err);
            showToastMsg("대댓글 로딩 실패");
        });
}

/**
 * [C] 댓글 등록
 */
function submitReply(parentId, inputEl) {
    if (typeof isLogin !== 'undefined' && isLogin === false) {
        if (confirm("로그인이 필요합니다. 이동하시겠습니까?")) location.href = "/login";
        return;
    }

    const content = inputEl.value.trim();
    if (!content) {
        showToastMsg("내용을 입력해주세요.");
        return;
    }

    const reqDto = { postId: POST_ID, content: content, parentId: parentId };

    fetch("/reply", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reqDto)
    })
        .then(res => {
            if(!res.ok) throw new Error("등록 실패");
            return res.json();
        })
        .then(() => {
            inputEl.value = "";
            showToastMsg("댓글이 등록되었습니다.");

            if (parentId === null) {
                replyPage = 0;
                isLastReplyPage = false;
                document.getElementById("replyListArea").innerHTML = "";
                loadMainReplies();
            } else {
                // 대댓글 등록 시 해당 영역만 갱신
                const childArea = document.getElementById(`childArea-${parentId}`);
                childArea.innerHTML = "";
                childArea.style.display = "none";
                // 강제 갱신을 위해 임의 카운트 전달
                loadChildReplies(parentId, 999);
                document.getElementById(`replyForm-${parentId}`).innerHTML = "";
            }
        })
        .catch(err => {
            console.error(err);
            showToastMsg("등록 실패");
        });
}

/**
 * [D] 댓글 삭제
 */
function deleteReply(replyId) {
    if (!confirm("정말로 삭제하시겠습니까?")) return;

    fetch(`/reply/${replyId}`, { method: "DELETE" })
        .then(res => {
            if (res.ok) {
                showToastMsg("삭제되었습니다.");
                replyPage = 0;
                isLastReplyPage = false;
                document.getElementById("replyListArea").innerHTML = "";
                loadMainReplies();
            } else {
                showToastMsg("삭제 실패");
            }
        })
        .catch(err => console.error(err));
}

/**
 * [Helper] HTML 생성 - 인라인 스타일 제거됨
 */
function createReplyHtml(reply, isChild) {
    const isDeleted = reply.deleteYN === 'Y';
    const indentClass = isChild ? "child" : "";
    const profileSrc = reply.profileImg || "/images/user.png";

    // 1. 내용 (클래스: deletedTxt / replyTxt)
    const contentHtml = isDeleted
        ? `<p class="replyTxt deletedTxt">(삭제된 댓글입니다)</p>`
        : `<p class="replyTxt">${reply.content}</p>`;

    // 2. 버튼 그룹
    let actionButtons = "";
    if (!isDeleted) {
        actionButtons += `
            <div class="replyLikeBox" onclick="toggleLike('REPLY', ${reply.id}, this.querySelector('.ico_like'))">
                <span class="ico ico_like ${reply.liked ? 'active' : ''}"></span>
                <span class="likeCount">${reply.likeCount || 0}</span>
            </div>
        `;

        if (!isChild) {
            actionButtons += `<span class="replyBtnChild" onclick="toggleReplyForm(${reply.id})">답글</span>`;
        }

        if (typeof loginUserId !== 'undefined' && loginUserId == reply.memberId) {
            // 클래스: replyDeleteBtn
            actionButtons += `<span class="replyDeleteBtn" onclick="deleteReply(${reply.id})">삭제</span>`;
        }
    }

    // 3. 대댓글 보기 버튼 (클래스: viewChildWrap, btnViewChild)
    let viewChildBtn = "";
    if (!isChild && reply.childCount > 0) {
        viewChildBtn = `
            <div class="viewChildWrap">
                <span id="btnToggleChild-${reply.id}" class="btnViewChild" 
                      onclick="loadChildReplies(${reply.id}, ${reply.childCount})">
                    ── 답글 ${reply.childCount}개 보기
                </span>
            </div>
        `;
    }

    // 4. 최종 HTML 조립 (클래스: subReplyFormArea, childList)
    return `
        <li class="replyBox ${indentClass}" id="reply-${reply.id}">
            <div class="replyWriterInfo">
                <div class="infoLeft">
                    <div class="profileImgBox">
                        <img src="${profileSrc}" alt="user">
                    </div>
                    <div class="infoContent">
                        <div class="nickNtime">
                            <span class="userNick">${reply.nickname || '(알수없음)'}</span>
                            <span class="replyTime">${timeAgoAjax(reply.writeDate)}</span>
                        </div>
                        
                        <div class="replyContent">
                            ${contentHtml}
                        </div>
                        
                        <div class="replyBottom">
                            ${actionButtons}
                        </div>

                        <div id="replyForm-${reply.id}" class="subReplyFormArea"></div>
                        ${viewChildBtn}
                    </div>
                </div>
            </div>
            
            <ul id="childArea-${reply.id}" class="replyList childList"></ul>
        </li>
    `;
}

/**
 * [Helper] 대댓글 폼 토글 - 인라인 스타일 제거됨
 */
function toggleReplyForm(parentId) {
    const formArea = document.getElementById(`replyForm-${parentId}`);

    if (formArea.innerHTML !== "") {
        formArea.innerHTML = "";
        return;
    }

    document.querySelectorAll('.subReplyFormArea').forEach(el => el.innerHTML = "");

    // subReplyFormArea 내부의 writeBox는 CSS에서 별도 스타일링됨
    formArea.innerHTML = `
        <div class="writeBox">
            <input type="text" id="input-${parentId}" class="replyTextBox" placeholder="답글을 입력하세요">
            <button class="replyBtn" onclick="submitReply(${parentId}, document.getElementById('input-${parentId}'))">등록</button>
        </div>
    `;

    setTimeout(() => document.getElementById(`input-${parentId}`).focus(), 100);
}