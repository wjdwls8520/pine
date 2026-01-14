<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- 쇼츠 공통 푸터 (커뮤니티와 동일한 디자인 클래스 적용) --%>
<footer id="footer" class="subFooter groupFooter">
    <div class="inner">
        <%-- 자바스크립트가 현재 페이지에 맞는 버튼을 이곳에 주입합니다 --%>
        <div class="formActions" id="formActions"></div>
    </div>
</footer>

<%-- 로그인 상태 체크 (스크립트에서 사용) --%>
<sec:authorize access="isAuthenticated()">
    <script>window.isLogin = true;</script>
</sec:authorize>
<sec:authorize access="isAnonymous()">
    <script>window.isLogin = false;</script>
</sec:authorize>

<script>
    // 현재 페이지 경로 확인
    const currentPath = window.location.pathname;

    /**
     * [이동 함수] 쇼츠 업로드 페이지로 이동
     */
    function moveShortsUpload() {
        if (!window.isLogin) {
            alert("로그인 이후 이용하실 수 있습니다.");
            return location.href = '/login';
        }
        location.href = '/shorts/shortsUpload';
    }

    /**
     * [기능 함수] 쇼츠 삭제 (수정 페이지에서 사용)
     * - URL 끝의 ID를 파싱하여 DELETE 요청 전송
     */
    function deleteShortsPost() {
        if (!confirm("정말로 삭제하시겠습니까? 삭제 시 복구할 수 없습니다.")) {
            return;
        }

        // 예: /shorts/update/15 -> 15 추출
        const id = currentPath.split('/').pop();

        fetch('/shorts/' + id, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        })
            .then(res => {
                if (res.ok) {
                    alert("삭제되었습니다.");
                    location.href = '/shorts';
                } else {
                    res.text().then(text => alert("삭제 실패: " + text));
                }
            })
            .catch(err => {
                console.error("Error:", err);
                alert("오류가 발생했습니다.");
            });
    }

    /**
     * [초기화] DOM 로드 완료 시 버튼 렌더링
     */
    document.addEventListener("DOMContentLoaded", () => {
        const formActions = document.getElementById("formActions");
        if (!formActions) return;

        // -------------------------------------------------------
        // CASE 1. 쇼츠 메인 (/shorts)
        // -------------------------------------------------------
        if (currentPath === "/shorts" || currentPath === "/shorts/") {
            formActions.innerHTML = `
                <div class="cancelButton"></div> <%-- 왼쪽 공백 유지 --%>
                <div class="stepButtons">
                    <button type="button" class="btnWH btnSubmit" onclick="moveShortsUpload();">+ 쇼츠 업로드</button>
                </div>
            `;
        }

            // -------------------------------------------------------
            // CASE 2. 쇼츠 업로드 (/shorts/shortsUpload)
        // -------------------------------------------------------
        else if (currentPath.includes("/shorts/shortsUpload")) {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                </div>
                <div class="stepButtons">
                    <button type="button" class="btnWH btnSubmit" id="submitBtn">업로드</button>
                </div>
            `;
        }

            // -------------------------------------------------------
            // CASE 3. 쇼츠 수정 (/shorts/update/{id})
        // -------------------------------------------------------
        else if (currentPath.includes("/shorts/shortsUpdate")) {
            formActions.innerHTML = `
                <div class="cancelButton">
                    <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                </div>
                <div class="stepButtons">
                    <button type="button" class="btnWH btnSubmit" id="submitBtn">수정하기</button>
                </div>
            `;
        }

            // -------------------------------------------------------
            // CASE 4. 쇼츠 상세 보기 (/shorts/view/{id})
        // -------------------------------------------------------
        else if (currentPath.includes("/shorts/view/")) {
            formActions.innerHTML = `
                <div class="cancelButton"></div> <%-- 왼쪽 공백 유지 --%>
                <div class="stepButtons">
                    <button type="button" class="btnWH btnSubmit" onclick="moveShortsUpload();">+ 쇼츠 업로드</button>
                </div>
            `;
        }
    });
</script>