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

    function moveGroup() {
        location.href = '/group/gdetail/${groupId}'
    }

    // 글쓰기 페이지로 이동
    function moveCreate() {
        if (!window.isLogin) {
            alert("로그인 이후 이용하실 수 있습니다.");
            return location.href = '/login';
        }
        location.href = '/group/${groupId}/post/create';
    }

    // 게시글 삭제 (수정 페이지에서 사용)
    function deletePost() {
        if (!confirm("정말로 삭제하시겠습니까? 삭제 시 복구할 수 없습니다.")) {
            return;
        }

        // 현재 URL에서 ID 추출 (/community/edit/15 -> 15)
        const id = path.split('/').pop();

        fetch(`/group/${groupId}/post/${post.id}/delete`, { // EL표현식 충돌 방지 위해 '${id}' 사용
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        })
            .then(res => {
                if (res.ok) {
                    alert("삭제되었습니다.");
                    location.href = '/group/${groupId}/post/main';
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

        // 1. 현재 경로
        const path = window.location.pathname;

        // 2. URL에서 ID값만 단순 추출 (URL 쪼개기)
        // 예: /group/4/post/detail/12 -> '4'는 인덱스 2
        const pathSegments = path.split('/');
        const urlGroupId = pathSegments[2];

        // 3. 조건문: 정규식 대신 .includes() 사용 (무조건 작동함)
        if (path.includes('/post/detail/')) {
            formActions.innerHTML = `
            <div class="cancelButton">
                <button type="button" class="btnWH btnCancel"
                        onclick="location.href='/group/' + groupId + '/post/main'">목록</button>
            </div>
        `;
        }
        else if (path.includes('/post/main')) {
            formActions.innerHTML = `
            <div class="cancelButton">
                <button type="button" class="btnWH btnCancel" onclick="moveGroup();">그룹으로</button>
            </div>
            <div class="stepButtons">
                <button type="button" class="btnWH btnSubmit" onclick="moveCreate();">글쓰기</button>
            </div>
        `;
        }
        else if (path.includes('/post/create')) {
            formActions.innerHTML = `
            <div class="cancelButton">
                <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
            </div>
            <div class="stepButtons">
                <button type="button" class="btnWH btnSubmit" id="submitBtn">게시하기</button>
            </div>
        `;
        }
        else if (path.endsWith('/edit')) {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                    <button type="button" class="btnWH btnCancel" onclick="deletePost()">삭제</button>
                </div>
                <div class="stepButtons">
                    <%-- commuCreate.js가 mode='edit'인 걸 감지하고 PUT 요청 보냄 --%>
                    <button type="button" class="btnWH btnSubmit" id="submitBtn">수정하기</button>
                </div>
            `;
        }
    });
</script>