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
        <section class="section groupMain">
            <h2 class="bigTitle">그룹 만들기</h2>

            <div id="contentsWrap" class="contentsWrap">
                <form>
                    <c:forEach var="item" items="${list}" varStatus="idx">
                        <div>
                            <input type="checkbox"
                                   id="chk_${item.id}"
                                   name="categoryIds"
                                   value="${item.id}" />
                            <label for="chk_${item.id}">${item.nameKor}</label>
                        </div>
                    </c:forEach>
                </form>
            </div>
        </section>



    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>