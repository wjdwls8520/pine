<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/shorts.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace shorts">

        <%-- section page--%>
        <section class="section section01">
            <div class="shorts-page">
                <div class="shorts-video">
                    <div class="comment-btn" onclick="openComments()">댓글</div>
                </div>
            </div>

            <div class="comments-panel" id="commentsPanel">
                <div class="close-btn" onclick="closeComments()">닫기</div>
                <h2>댓글 패널</h2>
                <p>여기에 댓글 목록이 들어갑니다.</p>
            </div>

        </section>

    </article>

</div>

<script>
    function openComments() {
        document.getElementById("commentsPanel").classList.add("open");
    }

    function closeComments() {
        document.getElementById("commentsPanel").classList.remove("open");
    }
</script>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>