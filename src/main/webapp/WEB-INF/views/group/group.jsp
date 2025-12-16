<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal" var="loginUser" />
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/group_common.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace group">

        <script>
            var msg = '${msg}';
            if(msg && msg.trim() !== '') {
                alert(msg);
            }
        </script>

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

        <%-- section page--%>
        <section class="section groupMain">
            <div class="tabWrap">
                <div class="tabTitle active" onclick="getAllData(this);">메인</div>
                <div class="tabTitle" onclick="getAllMyData(this);">내 그룹</div>
            </div>


            <div class="contentsWrap">
                <div id="groupBox" class="groupBox">
                    <%-- 동적으로 태그 추가 --%>

                </div>
            </div>
        </section>



    </article>

</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>
<script src="/js/timeAgo.js"></script>
<script src="/js/groupjs/groupScroll.js"></script>