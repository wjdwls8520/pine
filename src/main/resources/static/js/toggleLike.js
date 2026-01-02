/**
 * 좋아요 토글 함수
 * @param {string} targetType - "POST" 또는 "REPLY" (엔티티 구분을 위해 추가)
 * @param {number} targetId - 게시글ID 또는 댓글ID
 * @param {HTMLElement} el - 클릭한 좋아요 아이콘 요소 (this)
 */
window.toggleLike = function (targetType, targetId, el) {
    // 1. 로그인 체크
    if (window.isLogin === false) {
        alert("로그인이 필요합니다");
        location.href = "/login";
        return;
    }

    const countSpan = el.nextElementSibling; // 바로 옆에 있는 숫자 태그
    const originCount = parseInt(countSpan.textContent);

    // 2. UI 먼저 반영 (낙관적 업데이트)
    const isActive = el.classList.toggle("active");
    countSpan.textContent = isActive ? originCount + 1 : originCount - 1;

    // 3. 서버 요청
    fetch("/like", {  // url은 RESTful하게 /api/likes 추천 (기존 /like/toggle 도 상관없음)
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            targetType: targetType, // "POST" 인지 "REPLY" 인지 서버에 전달
            targetId: targetId
        })
    })
        .then(res => {
            if (!res.ok) throw new Error();
            return res.json();
        })
        .then(data => {
            // 서버에서 최신 카운트를 돌려준다면 그걸로 덮어쓰기 (안전장치)
            if(data.likeCount !== undefined) {
                countSpan.textContent = data.likeCount;
            }
        })
        .catch(() => {
            // 4. 실패 시 롤백 (원래대로 되돌림)
            console.error("좋아요 실패");
            el.classList.toggle("active");
            countSpan.textContent = originCount;
            alert("처리에 실패했습니다.");
        });
};