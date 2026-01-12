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

        <%-- section page--%>
        <h2 class="pageTitle">community main</h2>
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
                            <%-- [변경] 데이터가 있을 때만 반복문 실행 --%>
                                <c:choose>
                                    <c:when test="${not empty bestGroups}">
                                        <c:forEach var="group" items="${bestGroups}">
                                            <li>
                                                <a href="/group/detail/${group.id}">
                                                    <div class="bGroupImg">
                                                        <img src="${not empty group.groupImg ? group.groupImg.path : '/images/icon_pindory.png'}"
                                                             alt="group_img"/>
                                                    </div>
                                                    <div class="bGroupInfo">
                                                        <span class="bGroupName">${group.groupName}</span>
                                                        <span class="bGroupCount">멤버 ${group.groupMemberCount}명</span>
                                                    </div>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <li style="text-align: center; padding: 20px; color: #999;">
                                            아직 생성된 그룹이 없습니다.
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                        </ul>
                    </div>
                </div>
            </div>
        </section>

    </article
</div>
<div id="commonToast" class="toastMsg"></div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
<script src="/js/swiper-bundle.min.js"></script>
<script src="/js/toggleLike.js"></script>
<script src="/js/commujs/commuCommon.js"></script>
<script src="/js/commujs/commuMain.js"></script>

