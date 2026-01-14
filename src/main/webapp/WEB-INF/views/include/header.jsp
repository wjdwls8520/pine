<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal" var="loginUser" />
<header id="header">
    <div class="inner">
        <h1 class="logo">
            <a href="/">
                <img src="/images/pine_logo.png" alt="로고(pine)" />
            </a>
        </h1>
        <div class="searchWrap">
            <input type="text" id="searchBar" class="searchBar" placeholder="원하는 경험을 검색해보세요." onkeyup="enterSearch(event)"/>
            <label class="btnSearch" for="searchBar" onclick="doSearch()"><img src="/images/ico_search.png" alt="search" /></label>
        </div>
        <script>
            // [추가됨] 페이지 로드 시 URL 파라미터에서 keyword를 꺼내 검색창에 유지
            (function() {
                const urlParams = new URLSearchParams(window.location.search);
                const keyword = urlParams.get('keyword');
                if (keyword) {
                    document.getElementById('searchBar').value = keyword;
                }
            })();

            function enterSearch(e) {
                if (e.key === "Enter") { // 엔터키를 눌렀을 때
                    doSearch();
                }
            }

            function doSearch() {
                const keyword = document.getElementById("searchBar").value.trim();
                if (keyword.length < 1) {
                    alert("검색어를 입력해주세요.");
                    return;
                }
                // 검색 페이지로 이동
                location.href = "/search?keyword=" + encodeURIComponent(keyword);
            }
        </script>

        <div class="util">
<%--                <a href="#">Logout</a>--%>
<%--                <a href="#">MyPage</a>--%>
            <sec:authorize access="hasRole('ROLE_USER')">
                <p>${loginUser.nickname} 님 반가워요!</p>
                <form method="post" action="/logout">
                    <button type="submit">Logout</button>
                </form>
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_OAUTH')">
                <a href="/CheckCours">회원가입을 진행해주세요.</a>
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_USER')">
                <a href="/GoMypage">MyPage</a>
            </sec:authorize>
            <sec:authorize access="!isAuthenticated()">
                <a href="/login">Login / join</a>
            </sec:authorize>

        </div>
    </div>
</header>


