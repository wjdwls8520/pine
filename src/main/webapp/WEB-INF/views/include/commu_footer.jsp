<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- 그룹 푸터와 동일한 클래스 구조 사용 --%>
<footer id="footer" class="subFooter groupFooter">
    <div class="inner">
        <%-- 여기에 자바스크립트로 버튼이 주입됩니다 --%>
        <div class="formActions" id="formActions"></div>
    </div>
</footer>

<%--이미지 원본보기 모달--%>
<div id="imageModal" class="imageModalOverlay">
    <span class="closeBtn" onclick="closeImageModal()">&times;</span>
    <div class="swiper modalSwiper">
        <div class="swiper-wrapper" id="modalWrapper">
        </div>
        <div class="swiper-button-next"></div>
        <div class="swiper-button-prev"></div>
        <div class="swiper-pagination modal-pagination"></div>
    </div>
</div>

<%-- 로그인 상태 체크 --%>
<sec:authorize access="isAuthenticated()">
    <script>window.isLogin = true;</script>
</sec:authorize>
<sec:authorize access="isAnonymous()">
    <script>window.isLogin = false;</script>
</sec:authorize>

<script>
    // 1. 공통 변수 및 이동 함수 정의
    const path = window.location.pathname; // 예: /community, /community/edit/5

    // 글쓰기 페이지로 이동
    function moveCreate() {
        if (!window.isLogin) {
            alert("로그인 이후 이용하실 수 있습니다.");
            return location.href = '/login';
        }
        location.href = '/community/ccreate';
    }

    // 게시글 삭제 (수정 페이지에서 사용)
    function deletePost() {
        if (!confirm("정말로 삭제하시겠습니까? 삭제 시 복구할 수 없습니다.")) {
            return;
        }

        // 현재 URL에서 ID 추출 (/community/edit/15 -> 15)
        const id = path.split('/').pop();

        fetch(`/community/${'${id}'}`, { // EL표현식 충돌 방지 위해 '${id}' 사용
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        })
            .then(res => {
                if (res.ok) {
                    alert("삭제되었습니다.");
                    location.href = '/community';
                } else {
                    res.text().then(text => alert("삭제 실패: " + text));
                }
            })
            .catch(err => console.error(err));
    }

    // 2. 페이지 로드 시 URL에 따라 버튼 그리기
    window.addEventListener("load", () => {
        const formActions = document.getElementById("formActions");
        if (!formActions) return;

        // (1) 커뮤니티 목록 페이지 (/community)
        // [글쓰기] 버튼
        if (path === "/community") {
            formActions.innerHTML = `
                <div class="cancelButton"></div> <%-- 왼쪽 공백 유지 --%>
                <div class="stepButtons">
                    <button type="button" class="btnWH btnSubmit" onclick="moveCreate();">글쓰기</button>
                </div>
            `;
        }

            // (2) 글 작성 페이지 (/community/ccreate)
        // [취소] [작성하기] 버튼
        else if (path === "/community/ccreate") {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                </div>
                <div class="stepButtons">
                    <%-- commuCreate.js의 이벤트 리스너가 이 ID(submitBtn)를 찾습니다 --%>
                    <button type="button" class="btnWH btnSubmit" id="submitBtn">게시하기</button>
                </div>
            `;
        }

            // (3) 글 수정 페이지 (/community/edit/숫자)
        // [취소] [삭제] [수정하기] 버튼
        else if (path.startsWith("/community/edit/")) {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                    <button type="button" class="btnWH btnCancel" onclick="deletePost()">삭제</button>
                </div>
                <div class="stepButtons">
                    <%-- commuCreate.js가 mode='edit'인 걸 알고 PUT 요청을 보냅니다 --%>
                    <button type="button" class="btnWH btnSubmit" id="submitBtn">수정하기</button>
                </div>
            `;
        }

        // (4) 상세 페이지 (/community/cdetail/숫자) - 필요하다면 추가
        else if (path.startsWith("/community/cdetail/")) {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="location.href='/community'">목록</button>
                </div>
             `;
        }
    });
</script>