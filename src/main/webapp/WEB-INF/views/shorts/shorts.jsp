<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
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
        <input type="hidden" id="loginCheck" value="${not empty pageContext.request.userPrincipal}">
        <input type="hidden" id="targetShortsId" value="${targetPostId}">
        <div class="commentsPanel" id="commentsPanel">
            <div class="panelHeader">
                <div class="panelTitle">
                    <strong class="commentHeaderTitle" id="commentPanelTitle">제목</strong>
                    <span class="commentHeaderUser" id="commentPanelUser">@작성자</span>
                    <span id="commentPanelDate">날짜</span>
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
    </div>

</div>


<div id="commonToast" class="toastMsg"></div>
<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/shortsjs/shorts.js"></script>
