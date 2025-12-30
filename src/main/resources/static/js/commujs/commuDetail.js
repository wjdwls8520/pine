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
        fetch(`/reply/list?targetId=${POST_ID}`)
            .then(res => {
                if (!res.ok) throw new Error("댓글 조회 실패");
                return res.json();
            })
            .then(data => {
                if (!Array.isArray(data)) {
                    throw new Error("댓글 데이터 형식 오류");
                }
                renderReplies(data);
            })
            .catch(err => {
                console.error(err);
                replyList.textContent = "댓글을 불러오지 못했습니다.";
            });
    }

    // =====================
    // 댓글 렌더링 (XSS 안전)
    // =====================
    function renderReplies(replies) {
        replyList.innerHTML = "";

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
        contentText.textContent = reply.content; // 🔐 XSS 차단

        contentWrap.appendChild(contentText);

        const bottom = document.createElement("div");
        bottom.className = "replyBottom";

        const likeIcon = document.createElement("span");
        likeIcon.className = "ico ico_like replyLike";

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
