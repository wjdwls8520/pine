<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        <%-- section page--%>
        <section class="section groupMain">
            <h2 class="bigTitle">그룹 만들기</h2>

            <div class="contentsWrap">

            </div>
        </section>



    </article>
    <script src="/js/groupjs/group_common.js"></script>
    <script>
        // 카테고리가져오는 에이잭스
        getCategory();
    </script>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>