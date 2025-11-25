<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="./include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/login.css">

    <script src="/js/login.js"></script>
</head>
<body>
    <jsp:include page="./include/header.jsp"></jsp:include>
    <div class="wrap">
        <jsp:include page="./include/sideBar.jsp"></jsp:include>
        <article class="article_login">
            <div>뒤로가기</div>

            <div class="login_container">
                <div>PINE TREE</div>

                <div class="login_input">
                    <button onclick="googleLogin()">구글로 로그인</button>
                    <button onclick="naverLogin()">네이버로 로그인</button>
                    <button onclick="kakaoLogin()">카카오로 로그인</button>
                </div>

            </div>


        </article>
    </div>

</body>
</html>
