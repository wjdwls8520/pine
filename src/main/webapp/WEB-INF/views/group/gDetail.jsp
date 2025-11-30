<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
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
        <section class="section groupDetail">
            <h2 class="bigTitle"></h2>

            <div class="contentsWrap">

            </div>

        </section>

    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>
