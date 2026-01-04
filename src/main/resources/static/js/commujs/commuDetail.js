document.addEventListener("DOMContentLoaded", () => {
    const replyBtn = document.querySelector(".replyBtn");
    const replyInput = document.querySelector(".replyTextBox");
    const replyList = document.querySelector(".replyList");

    if (!replyList) {
        console.error("replyList 요소 없음");
        return;
    }

    // =====================
    // 초기 댓글 조회
    // =====================
    loadReplies();

    function loadReplies() {

        fetch(`/reply/list?postId=${POST_ID}`)
            .then(res => {
                if (!res.ok) throw new Error("댓글 조회 실패");
                // 만약 서버에서 응답이 비어있으면(null) 빈 배열로 처리
                return res.json().catch(() => []);
            })
            .then(data => {
                // 🔥 [핵심 수정] data가 null이면 빈 배열([])로 바꿔줌
                const replies = data || [];

                if (!Array.isArray(replies)) {
                    throw new Error("댓글 데이터 형식 오류");
                }

                // 렌더링 함수 호출
                renderReplies(replies);
            })
            .catch(err => {
                console.error("댓글 로딩 에러:", err); // F12 콘솔에서 에러 내용 확인 가능
                replyList.innerHTML = `<li class="errorMsg">댓글을 불러오지 못했습니다.</li>`;
            });
    }

    // =====================
    // 댓글 렌더링 (XSS 안전)
    // =====================
    function renderReplies(replies) {
        replyList.innerHTML = "";

        if (replies.length === 0) {
            replyList.innerHTML = `<li class="emptyReply">등록된 댓글이 없습니다.</li>`;
            return; // 아래 반복문 실행 안 하고 종료
        }

        replies.forEach(reply => {
            const li = document.createElement("li");
            li.appendChild(createReplyBox(reply, false));
            replyList.appendChild(li);

            // 대댓글 렌더링
            if (Array.isArray(reply.children) && reply.children.length > 0) {
                const childUl = document.createElement("ul");
                childUl.className = "replyChildList";

                reply.children.forEach(child => {
                    const childLi = document.createElement("li");
                    childLi.appendChild(createReplyBox(child, true));
                    childUl.appendChild(childLi);
                });

                li.appendChild(childUl);
            }
        });
    }

    // =====================
    // 댓글 DOM 생성 (공통)
    // =====================
    function createReplyBox(reply, isChild) {
        const box = document.createElement("div");
        box.className = isChild ? "replyBox child" : "replyBox";

        const writerInfo = document.createElement("div");
        writerInfo.className = "replyWriterInfo";

        const infoLeft = document.createElement("div");
        infoLeft.className = "infoLeft";

        const imgBox = document.createElement("div");
        imgBox.className = "profileImgBox";

        const img = document.createElement("img");
        img.src = reply.profileImg || "/images/user.png";
        img.alt = "profile";

        imgBox.appendChild(img);

        const infoContent = document.createElement("div");
        infoContent.className = "infoContent";

        const nickNtime = document.createElement("div");
        nickNtime.className = "nickNtime";

        const nick = document.createElement("div");
        nick.className = "userNick";
        nick.textContent = reply.nickname;

        const time = document.createElement("div");
        time.className = "replyTime";
        time.textContent = timeAgoAjax(reply.writeDate);

        nickNtime.append(nick, time);

        const contentWrap = document.createElement("div");
        contentWrap.className = "replyContent";

        const contentText = document.createElement("p");
        contentText.className = "replyTxt";
        contentText.textContent = reply.content;

        contentWrap.appendChild(contentText);

        const bottom = document.createElement("div");
        bottom.className = "replyBottom";

        // 🔥 [수정 포인트 1] 좋아요 아이콘 생성 및 설정
        const likeIcon = document.createElement("span");
        likeIcon.className = "ico ico_like replyLike";

        // 1-1. 내가 좋아요 누른 댓글이면 빨간색(active) 표시
        // (주의: 서버 DTO에서 reply.liked 값을 boolean으로 줘야 함)
        if (reply.liked) {
            likeIcon.classList.add("active");
        }

        // 1-2. 클릭 이벤트 연결 (여기서 'REPLY' 타입 전달!)
        likeIcon.onclick = function() {
            toggleLike('REPLY', reply.id, this);
        };

        const likeCount = document.createElement("span");
        likeCount.className = "likeCount";
        likeCount.textContent = reply.likeCount ?? "0";

        const childReplyBtn = document.createElement("span");
        childReplyBtn.className = "replyBtnChild";
        childReplyBtn.textContent = "답글";
        childReplyBtn.dataset.id = reply.id;

        bottom.append(likeIcon, likeCount, childReplyBtn);

        infoContent.append(nickNtime, contentWrap, bottom);
        infoLeft.append(imgBox, infoContent);

        const moreBtn = document.createElement("div");
        moreBtn.className = "replyMoreBtn";

        const moreIcon = document.createElement("span");
        moreIcon.className = "ico ico_more";

        moreBtn.appendChild(moreIcon);

        writerInfo.appendChild(infoLeft);
        box.appendChild(writerInfo);

        return box;
    }

    // =====================
    // Toast (댓글 등록 알림)
    // =====================
    function showReplyToast() {
        const toast = document.getElementById("replyToast");
        if (!toast) return;

        toast.classList.add("show");
        setTimeout(() => {
            toast.classList.remove("show");
        }, 1500);
    }

    // =====================
    // 댓글 등록
    // =====================
    if (!replyBtn) return;

    replyBtn.addEventListener("click", () => {
        const content = replyInput.value.trim();

        if (!content) {
            alert("댓글을 입력하세요");
            return;
        }

        fetch("/reply", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                targetId: POST_ID,
                content: content,
                parentId: null
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("댓글 등록 실패");
                return res.json();
            })
            .then(() => {
                replyInput.value = "";
                loadReplies();
                showReplyToast();
            })
            .catch(err => {
                console.error(err);
                alert("댓글 등록에 실패했습니다.");
            });
    });
});
