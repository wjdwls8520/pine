<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/mypage.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article workspace">
        <div class="mpContainer">

            <header class="mpHeader">
                <div class="mpProfileImgWrapper">
                    <c:choose>
                        <c:when test="${not empty member.profileimg}">
                            <img src="${member.profileimg}" alt="${member.nickname}" class="mpProfileImg" />
                        </c:when>
                        <c:otherwise>
                            <img src="/images/user.png" alt="Profile" class="mpProfileImg" onerror="this.src='/images/user.png'"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="mpProfileInfo">
                    <span class="mpRoleBadge">Profile</span>
                    <h1 class="mpNickname">${member.nickname}</h1>
                    <p class="mpEmail">${member.email}</p>
                </div>
            </header>

            <nav class="mpTabNav">
                <button class="mpTabBtn active" data-tab="posts">작성 글</button>
                <button class="mpTabBtn" data-tab="comments">댓글 단 글</button>
                <button class="mpTabBtn" data-tab="likes">좋아요 한 글</button>
                <button class="mpTabBtn" data-tab="groups">내 그룹</button>
            </nav>

            <div class="mpSubNav hidden" id="postSubTabs">
                <button class="mpSubBtn active" data-subtab="all">전체</button>
                <button class="mpSubBtn" data-subtab="COMMUNITY">커뮤니티</button>
                <button class="mpSubBtn" data-subtab="SHORTS">쇼츠</button>
                <button class="mpSubBtn" data-subtab="GROUP">그룹</button>
            </div>

            <div class="mpGrid" id="mpContentArea">
            </div>

            <div class="mpLoader hidden" id="mpLoader"><div class="mpSpinner"></div></div>
            <div class="mpEmpty hidden" id="mpEmptyState">콘텐츠가 없습니다.</div>

            <div class="mpPagination hidden" id="mpPagination">
                <button class="mpPageBtn" id="prevBtn">이전</button>
                <span style="font-size: 14px; color: #86868b; align-self: center;" id="pageInfo"></span>
                <button class="mpPageBtn" id="nextBtn">다음</button>
            </div>

        </div>
    </article>
</div>
<jsp:include page="../include/footer.jsp"></jsp:include>
<script src="/js/mypage.js"></script>
</body>
</html>