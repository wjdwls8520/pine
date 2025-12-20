<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/login.css">
    <link rel="stylesheet" href="/css/swiper-bundle.min.css">

    <script src="/js/home.js"></script>
    <script src="/js/login.js"></script>
    <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <div id="adSerchOverlay"></div>

    <article class="article join">
        <form method="post" action="/insertMember" class="joinMember">
            <div class="form_title">회원 정보 수정</div>
            <p>* 표시는 필수 항목입니다.</p>
            <div class="form_list">*아이디</div>
            <input type="text" name="email" value="${sessionScope.userinfo.email}" readonly /> <p>${emailError}</p>
            <div class="form_list">*이름</div>
            <input type="text" name="name" value="${sessionScope.userinfo.name}" readonly />
            <div class="form_list">*별명</div>
            <input type="text" name="nickname"  /> <p style="color: red;">${nicknameError}</p>
            <div class="form_list">*전화번호</div>
            <input type="text" name="phone" value="${sessionScope.userinfo.phone}"   />
            <div class="form_list">직업</div>
            <input type="text" name="job"  />
            <div class="join_findAd">
                <div class="form_list">*주소</div>
                <button class="join_btn" type="button" onclick="findPostCode()">주소찾기</button>
            </div>
            <div class="form_list">*국적</div>
            <input type="text" name="country" id="country" onchange="searchContry()" />
            <ul id="country_result" class="country_result_list"></ul>
            <div class="form_list">*우편번호</div>
            <input type="text" name="address_code" id="address_code"  readonly/>
            <div class="form_list">*주소1</div>
            <input type="text" name="address_1" id="address_1" readonly />
            <div class="form_list">상세주소</div>
            <input type="text" name="address_2" id="address_2"  />

            <div class="form_list">한줄소개</div>
            <input type="text" name="profile_msg" id="profile_msg"  />

            <div class="join_submit">
                <button class="join_submit_btn" type="button" onclick="beforInsertMember()">회원가입</button>
            </div>

        </form>
    </article>
</div>

<jsp:include page="../include/footer.jsp"></jsp:include>

<script src="/js/swiper-bundle.min.js"></script>
<script>
    const swiper = new Swiper('.homeSlide', {
        speed: 400,
        direction: 'horizontal',
        loop: true,
        // autoplay: {
        //     delay: 2200,
        // },
        slidesPerView: 4.5,
        spaceBetween: 30,
        //breakpoints: {
        // when window width is >= 320px
        // 320: {
        //     slidesPerView: 2,
        //     spaceBetween: 20
        // },
        // // when window width is >= 480px
        // 480: {
        //     slidesPerView: 3,
        //     spaceBetween: 30
        // },
        // // when window width is >= 640px
        // 640: {
        //     slidesPerView: 4,
        //     spaceBetween: 40
        // }
        //}

        // If we need pagination
        pagination: {
            el: '.swiper-pagination',
        },
    });

</script>


</body>
</html>
