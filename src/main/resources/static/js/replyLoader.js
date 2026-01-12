/**
 * replyLoader.js
 * - 커뮤니티 상세 페이지에서 reply.js(쇼츠 공통)를 사용하기 위한 초기화 스크립트
 * 쇼츠는 댓글창을 버튼 눌러서 열지만, 커뮤니티는 페이지 로딩되자마자 댓글을 불러와야함.
 */

document.addEventListener("DOMContentLoaded", () => {
    // 1. JSP에서 심어둔 게시글 ID 가져오기
    // (cDetail.jsp의 <input type="hidden" id="targetPostId"> 값)
    const targetPostIdInput = document.getElementById("targetPostId");
    if (targetPostIdInput) {
        // reply.js의 전역 변수 세팅
        currentPostIdForReply = targetPostIdInput.value;

        // 2. 초기 댓글 목록 로딩 (reply.js 함수 호출)
        loadReplies(currentPostIdForReply, 0);
    }

    // 3. 댓글 입력창 엔터키 이벤트 연결
    // (reply.js의 btnReplyRegist 클릭 이벤트는 이미 reply.js 안에 있음)
    // 하지만 엔터키 입력 시 등록 기능은 쇼츠엔 없고 커뮤니티에만 있을 수 있으므로 추가
    const replyInput = document.getElementById("replyInput");
    const btnReplyRegist = document.getElementById("btnReplyRegist");

    if (replyInput && btnReplyRegist) {
        replyInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                btnReplyRegist.click(); // 등록 버튼 클릭 트리거
            }
        });
    }

    // 4. 무한 스크롤 (Observer) 설정
    // 쇼츠는 스크롤 박스에 이벤트를 걸었지만, 커뮤니티는 페이지 전체 스크롤을 쓰거나
    // 하단 감지 요소를 쓰는 게 좋습니다.
    const scrollTrigger = document.getElementById("scrollTrigger");

    if (scrollTrigger) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                // 화면 하단 센서가 보이고 + 로딩 중 아니고 + 마지막 페이지가 아니면
                if (entry.isIntersecting && !isReplyLoading && !isReplyLastPage) {
                    console.log("[CommuDetail] 댓글 더보기 로딩...");
                    loadReplies(currentPostIdForReply, replyPage);
                }
            });
        }, { threshold: 0.1 });

        observer.observe(scrollTrigger);
    }
});