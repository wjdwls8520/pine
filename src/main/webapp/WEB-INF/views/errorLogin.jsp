<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="./include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/group_common.css">


</head>
<body>
<jsp:include page="./include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="./include/sideBar.jsp"></jsp:include>

    <script>
        var msg = '${msg}';
        if(msg && msg.trim() !== '') {
            alert(msg);
        } else {
            alert("로그인이 필요한 서비스입니다.");
        }

        location.href = "/login"
    </script>

</div>
<jsp:include page="./include/group_footer.jsp"></jsp:include>
