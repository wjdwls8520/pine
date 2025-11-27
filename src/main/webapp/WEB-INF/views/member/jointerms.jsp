<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/login.css">

    <script src="/js/login.js"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
    <div class="wrap">
        <jsp:include page="../include/sideBar.jsp"></jsp:include>

        <h2>이용약관</h2>

        <form class="contract" action="/goJoin">
            <h1>입주신청 약관</h1>
            <div class="text">가. 개인정보의 수집 및 이용 목적

                이것은 이용약관입니다

            </div>
            <div>
                <p><input type="checkbox" required> 약관에 동의하면 체크해구리</p>
                <p><input type="submit" value="입주 신청" class="box"><p>
            </div>
        </form>

    </div>

</body>
</html>
