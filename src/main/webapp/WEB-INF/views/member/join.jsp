<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/login.css">

    <script src="/js/login.js"></script>
    <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
    <div class="wrap">
        <jsp:include page="../include/sideBar.jsp"></jsp:include>

        <div id="overlay"></div>

        <form method="post" action="/insertMember" class="joinMember">
            <h1>로그인</h1><p>* 표시는 필수 항목입니다.</p>
            <div>*아이디</div>
            <input type="text" name="email" value="${sessionScope.userinfo.email}" readonly />
            <div>*이름</div>
            <input type="text" name="name" value="${sessionScope.userinfo.name}" readonly />
            <div>*별명</div>
            <input type="text" name="nickname"  />
            <div>*전화번호</div>
            <input type="text" name="phone" value="${sessionScope.userinfo.phone}"   />
            <div>직업</div>
            <input type="text" name="job"  />
            <div>*주소</div>
            <button type="button" onclick="findPostCode()">주소찾기</button>
            <div>*우편번호</div>
            <input type="text" name="address_code" id="address_code"  readonly/>
            <div>*주소1</div>
            <input type="text" name="address_1" id="address_1" readonly />
            <div>상세주소</div>
            <input type="text" name="address_2" id="address_2"  />

            <div>한줄소개</div>
            <input type="text" name="profile_msg" id="profile_msg"  />

            <button type="button" onclick="beforInsertMember()">회원가입</button>

        </form>

    </div>

</body>
</html>
