<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">
    <link rel="stylesheet" href="/css/swiper-bundle.min.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article id="commuMainPage" class="article workspace commu">

        <script> window.groupId = ${groupId} </script>

        <%-- section page--%>
        <h2 class="pageTitle cursor" onclick="location.href=`/group/gdetail/${groupId}`;">${groupName} Track</h2>
        <section class="section section01 commuSection">

            <%--  왼쪽 포스트 섹션--%>
            <div class="postSection" id="postList">

                 <%--ajax로 동적 태그 생성--%>
                    <%--<script src="/js/commujs/commuMain.js"></script>--%>

            </div>
            <!-- 오른쪽 그룹 랭크 섹션 -->
            <div class="groupSection">
                <div class="inner">
                    <div class="bestGroupTitle">BEST GROUP</div>
                    <div class="bestGroupListWrap">
                        <ul class="bestGroupList">
                            <%-- 5개 노출 후 더보기--%>
                            <c:forEach items="${groupAll}" var="group">
                                <li>
                                    <a>
                                        <div class="bGroupImg">
                                            <img src="${group.groupImg.path}"/>
                                        </div>
                                        <div class="bGroupInfo">
                                            <span class="bGroupName">${group.groupName}</span>
                                            <span class="bGroupCount">멤버 ${group.groupMemberCount}</span>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </section>

    </article
</div>
<div id="commonToast" class="toastMsg"></div>
<jsp:include page="../include/groupPost_footer.jsp"></jsp:include>
<script src="/js/swiper-bundle.min.js"></script>
<script src="/js/toggleLike.js"></script>
<script src="/js/groupjs/groupCommon.js"></script>
<script src="/js/groupjs/groupMain.js"></script>

