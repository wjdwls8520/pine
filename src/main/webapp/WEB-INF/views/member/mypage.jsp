<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
                <div class="mypage_image"><img src="" /></div>
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
                <h2>MY LIST</h2>
                <div class="mypage_list_mylist_post">
                    <div class="mypage_list_mylist_post_01">
                        포스트1
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        포스트2
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        포스트3
                    </div>
                    <div class="mypage_list_mylist_post_01">
                        포스트4
                    </div>
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
