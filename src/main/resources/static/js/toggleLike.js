window.toggleLike = function (targetType, targetId, el) {
    // 1. 로그인 체크 (isLogin이 정의 안 되어 있을 경우 대비)
    if (typeof window.isLogin !== 'undefined' && window.isLogin === false) {
        alert("로그인이 필요합니다");
        location.href = "/login";
        return;
    }

    const countSpan = el.nextElementSibling;
    // 숫자가 아닌 경우(빈칸 등) 0으로 처리하는 안전장치 추가 (|| 0)
    let originCount = parseInt(countSpan.textContent) || 0;

    // 2. UI 먼저 반영 (낙관적 업데이트)
    const isActive = el.classList.toggle("active");

    // 🔥 [수정] 0 밑으로 내려가지 않게 막음 (Math.max 사용)
    // 활성화되면 +1, 비활성화되면 -1 (단, 0보다 작아질 수 없음)
    let newCount = isActive ? originCount + 1 : Math.max(0, originCount - 1);
    countSpan.textContent = newCount;

    // 3. 서버 요청
    fetch("/like", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            targetType: targetType,
            targetId: targetId
        })
    })
        .then(res => {
            if (!res.ok) throw new Error("서버 통신 실패");
            return res.json();
        })
        .then(data => {
            // 🕵️‍♂️ [디버깅] 여기서 서버가 무슨 값을 주는지 콘솔(F12)에서 꼭 확인하세요!
            console.log("서버가 보낸 최신 좋아요 수:", data);

            // 서버에서 likeCount를 제대로 줬다면 덮어쓰기
            if (data.likeCount !== undefined && data.likeCount !== null) {
                countSpan.textContent = data.likeCount;
            } else {
                console.warn("서버 응답에 likeCount가 없습니다!", data);
            }
        })
        .catch((err) => {
            console.error("좋아요 에러 발생:", err);
            // 4. 실패 시 롤백 (원래대로 되돌림)
            el.classList.toggle("active");
            countSpan.textContent = originCount;
            alert("처리에 실패했습니다.");
        });
};