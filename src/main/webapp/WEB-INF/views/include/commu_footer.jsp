<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<footer id="footer" class="subFooter">
    <div class="inner">
        <ul id="bindingTag">
        </ul>
    </div>
</footer>

<!-- 로그인 상태를 JS 변수로 세팅 -->
<sec:authorize access="isAuthenticated()">
    <script>
        window.isLogin = true;
    </script>
</sec:authorize>

<sec:authorize access="isAnonymous()">
    <script>
        window.isLogin = false;
    </script>
</sec:authorize>

<script>
    window.addEventListener("load", () => {
        // 현재 경로 가져오기 → "/community" 등
        let path = window.location.pathname;
        let query = window.location.search;

        // 메뉴 ul
        const menu = document.getElementById("bindingTag");

        if (path === "/community") {
            menu.innerHTML = `
            <li><a id="writeBtn" href="#">글쓰기</a></li>
            <li><a>맨위로</a></li>
        `;

            // 글쓰기 버튼 클릭 이벤트
            document.getElementById("writeBtn").addEventListener("click", (e) => {
                e.preventDefault(); // a 태그 기본 이동 막기

                if(window.isLogin){
                    // 로그인 상태면 글쓰기 페이지 이동
                    window.location.href = "/community/ccreate";
                } else {
                    // 미로그인 시 경고 + 로그인 페이지 이동
                    alert("로그인이 필요합니다.");
                    window.location.href = "/login";
                }
            });

        } else if (path === "/community/ccreate") {
            menu.innerHTML = `
            <li><a href="/community">목록으로</a></li>
            <li><a>맨위로</a></li>
        `;
        }
    });
</script>
