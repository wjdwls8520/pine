<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">


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
<%--                <c:forEach var="post" items="${postList}">--%>
<%--                    <div class="postBox" onclick="location.href=`/community/cdetail/${post.id}`;">--%>
<%--                        <div class="postInner">--%>
<%--                            <div class="postWrap">--%>
<%--                                <div class="postTop">--%>
<%--                                    <div class="postInfo">--%>
<%--                                        <div class="postProfileImgBox">--%>
<%--                                            <img class="profileImg" src="/images/banner02.png" />--%>
<%--                                        </div>--%>
<%--                                        <div class="userNick">둘리둘리짱${post.id}</div>--%>
<%--                                        <div id="timeAgo" class="postTime relative-time"--%>
<%--                                             data-write-date="${post.updateDate}"> --%>
<%--                                            ${post.updateDate}--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="moreIcon">...</div>--%>
<%--                                </div>--%>
<%--                                <div class="postMiddle">--%>
<%--                                    <div class="postContent">--%>
<%--                                        <c:out value="${post.content}" escapeXml="false"/>--%>
<%--                                    </div>--%>
<%--                                    <div class="postHash">#해시태그</div>--%>
<%--                                    <div class="postImgBox">--%>
<%--                                        <div class="postImg">이미지</div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="postBottom">--%>
<%--                                    <div class="postLike">${post.likeCount}</div>--%>
<%--                                    <div class="postReply">${post.replyCount}</div>--%>
<%--                                    <div class="postlink">공유하기</div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </c:forEach>--%>

            </div>
            <!-- 오른쪽 그룹 랭크 섹션 -->
            <div class="groupSection">
                <div class="inner">
                    <div class="bestGroupTitle">BEST GROUP</div>
                    <div class="bestGroupListWrap">
                        <ul class="bestGroupList">
                            <%-- 5개 노출 후 더보기--%>
                            <li>
                                <a>
                                    <div class="bGroupImg">
                                        <img src="/images/banner02.png"/>
                                    </div>
                                    <div class="bGroupInfo">
                                        <span class="bGroupName">방탄팬클럽</span>
                                        <span class="bGroupCount">멤버 2,200명</span>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </section>

    </article
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
<script src="/js/timeAgo.js"></script>
<script src="/js/commujs/commuMain.js"></script>
