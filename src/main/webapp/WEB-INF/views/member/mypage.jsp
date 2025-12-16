<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/login.css">
    <link rel="stylesheet" href="/css/mypage.css">

    <script src="/js/login.js"></script>
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <div class="mypage">
        <div class="mypage_info">
            <div class="mypage_profile">
                <c:if test="${member.profileimg}">
                    <div class="mypage_image"><img src="${member.profileimg}" /></div>
                </c:if>
                <div class="mypage_image"><img src="../image/user.png" /></div>
                <div style="margin: 20px 0">${member.name}</div>
                <button>프로필 수정</button>
            </div>
            <div class="mypage_infolist">
                <ul>
                    <li>내 포인트</li>
                    <li>좋아요 한 글</li>
                    <li>내 게시글 / 영상</li>
                    <li>포인트 상점</li>
                </ul>
            </div>
        </div>
        <div class="mypage_list">
            <div class="mypage_list_mylist">
                <h2>MY POST</h2>
                <div class="mypage_list_mylist_post" id="mypageListMylistPost">
                <%-- 여기에 포스트 나옴 --%>
                    <c:if test="${empty post}">
                        <div>작성된 포스트가 없습니다.</div>
                    </c:if>
                    <c:forEach var="post" items="${post}">
                        <div>
                            <a href="/post/detail/${post.id}">
                                <span>${post.content}</span>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="mypage_list_mylist">
                <h2>MY GROUP</h2>
                <div class="mypage_list_mylist_post">
                    <div class="mypage_list_mylist_post_01">
                        그룹1
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        그룹2
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        그룹3
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        그룹4
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

</body>
</html>
