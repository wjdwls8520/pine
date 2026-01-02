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

            <div class="commentsPanel" id="commentsPanel">
                <div class="panel-header">
                    <div class="panel-title">
                        <strong class="comment-header-title">한복 입고 서울 야경 즐기기</strong>
                        <span class="comment-header-user">@pinedory</span>
                    </div>
                    <button class="close-btn" onclick="closeComment()">닫기</button>
                </div>
                <div class="panel-body">
                    <ul class="comment-list">
                        <li>
                            <b>@pine_member</b>
                            <p>영상미 대박... 다음에도 추천 부탁해요!</p>
                        </li>
                        <li>
                            <b>@culture_fan</b>
                            <p>밤에 보기 좋은 코스네요. 지도 공유 가능할까요?</p>
                        </li>
                        <li>
                            <b>@yeoni</b>
                            <p>영상 편집 감성 최고예요.</p>
                        </li>
                    </ul>
                    <div class="comment-input">
                        <input type="text" placeholder="댓글을 남겨보세요">
                        <button type="button">등록</button>
                    </div>
                </div>
            </div>
        </div>

</div>



<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/shortsjs/shorts.js"></script>
