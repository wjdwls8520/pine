<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<header id="header">
    <div class="inner">
        <h1 class="logo">
            <a href="/">
                <img src="/images/pine_logo.png" alt="로고(pine)" />
            </a>
        </h1>
        <div class="searchWrap">
            <input type="text" id="searchBar" class="searchBar" placeholder="원하는 경험을 검색해보세요." />
            <label class="btnSearch" for="searchBar"><img src="/images/ico_search.png" alt="search" /></label>
        </div>
        <div class="util">
<%--                <a href="#">Logout</a>--%>
<%--                <a href="#">MyPage</a>--%>
            <sec:authorize access="isAuthenticated()">
                <div>${}</div><a href="/">Logout</a>
            </sec:authorize>
            <a href="/login">Login</a>
            <a href="#" class="join">Join</a>
        </div>
    </div>
</header>
