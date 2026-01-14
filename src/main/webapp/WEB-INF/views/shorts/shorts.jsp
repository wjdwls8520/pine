<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal" var="loginUser" />
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/shorts.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <div id="shortsFeed" class="shortsFeed">
        <sec:authorize access="isAuthenticated()">
            <input type="hidden" id="loginCheck" value="true">
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
            <input type="hidden" id="loginCheck" value="false">
        </sec:authorize>
        <input type="hidden" id="targetShortsId" value="${targetPostId}">

        <sec:authorize access="isAuthenticated()">
            <input type="hidden" id="loginUser" value="${loginUser.id}">
        </sec:authorize>

        <div class="commentsPanel" id="commentsPanel">
            <div class="panelHeader">
                <div class="panelTitle">
                    <div class="authorInfo">
                        <div class="authorAvatar">
                            <img id="commentPanelProfileImg" src="/images/user.png" alt="프로필">
                        </div>
                        <div class="authorDetails">
                            <span class="authorName" id="commentPanelUser">@작성자</span>
                            <span class="authorDate" id="commentPanelDate">날짜</span>
                        </div>
                    </div>
                    <strong id="commentPanelTitle">제목</strong>
                </div>
                <button class="closeBtn" onclick="closeComment()">닫기</button>
            </div>
            <div class="panelBody" id="commentScrollArea">
                <ul class="commentList" id="commentListUl">
                    </ul>
                <div class="commentInput">
                    <input type="text" id="replyInput" placeholder="댓글을 남겨보세요">
                    <button type="button" id="btnReplyRegist">등록</button>
                </div>
            </div>
        </div>

        <div class="descriptionPanel" id="descriptionPanel">
            <div class="panelHeader">
                <div class="panelTitle">
                    <div class="authorInfo">
                        <div class="authorAvatar">
                            <img id="descPanelProfileImg" src="/images/user.png" alt="프로필">
                        </div>
                        <div class="authorDetails">
                            <span class="authorName" id="descPanelUser">@작성자</span>
                            <span class="authorDate" id="descPanelDate">날짜</span>
                        </div>
                    </div>
                    <strong id="descPanelTitle">제목</strong>
                </div>
                <button class="closeBtn" onclick="closeDescription()">닫기</button>
            </div>
            <div class="panelBody">
                <div class="descContent" id="descPanelContent">
                    내용이 표시됩니다.
                </div>
                <div class="descTags" id="descPanelTags">
                    <!-- 해시태그가 동적으로 추가됩니다 -->
                </div>
                <div class="descStats">
                    <div class="statItem">
                        <span class="statIcon like"></span>
                        <span class="statLabel">좋아요</span>
                        <span class="statValue" id="descPanelLikeCount">0</span>
                    </div>
                    <div class="statItem">
                        <span class="statIcon view"></span>
                        <span class="statLabel">조회수</span>
                        <span class="statValue" id="descPanelViewCount">0</span>
                    </div>
                    <div class="statItem">
                        <span class="statIcon reply"></span>
                        <span class="statLabel">댓글</span>
                        <span class="statValue" id="descPanelReplyCount">0</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>


<div id="commonToast" class="toastMsg"></div>
<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/reply.js"></script>
<script src="/js/shortsjs/shorts.js"></script>
