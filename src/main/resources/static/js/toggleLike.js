window.toggleLike = function ({  targetId, el }) {
    if (window.isLogin === false) {
        alert("로그인이 필요합니다");
        location.href = "/login";
        return;
    }

    const countSpan = el.nextElementSibling;
    const originCount = parseInt(countSpan.textContent);

    // UI 먼저 반영
    const isActive = el.classList.toggle("active");
    countSpan.textContent = isActive ? originCount + 1 : originCount - 1;

    fetch("/like/toggle", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            targetId
        })
    })
        .then(res => {
            if (!res.ok) throw new Error();
            return res.json();
        })
        .then(data => {
            countSpan.textContent = data.likeCount;
        })
        .catch(() => {
            // 실패 시 롤백
            el.classList.toggle("active");
            countSpan.textContent = originCount;
        });
};
