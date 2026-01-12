<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <jsp:include page="./include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/swiper-bundle.min.css">
    <script src="/js/home.js"></script>

</head>
<body>
    <jsp:include page="./include/header.jsp"></jsp:include>

    <div class="wrap">
        <jsp:include page="./include/sideBar.jsp"></jsp:include>

        <article class="article home">
        <%-- section page  --%>
            <section class="section section01">
                <h2 class="bigTitle">새로운 소식 <a href="#">더 보기</a> </h2>
                <h3 class="subTitle">숲은 늘 변하고 있어요!</h3>
                <div class="swiper homeSlide">
                    <div class="swiper-wrapper">
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner01.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임</div>
                                </div>
                            </a>
                        </div>
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner02.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임 이쫘나아아아아아아아앙아아앙아아앙앙!!!!!!!</div>
                                </div>
                            </a>
                        </div>
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner03.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임</div>
                                </div>
                            </a>
                        </div>
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner04.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임</div>
                                </div>
                            </a>
                        </div>
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner05.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임</div>
                                </div>
                            </a>
                        </div>
                        <div class="swiper-slide">
                            <a href="#">
                                <div class="imgBox">
                                    <img src="/images/banner03.png" alt="배너이미지" />
                                </div>
                                <div class="infoBox">
                                    <div class="tit">2030 서울 팬클럽 모임</div>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="swiper-pagination"></div>
                </div>

            </section>
            <section class="section section02">
                <h2 class="bigTitle">BEST 컨텐츠 <a href="/community">더 보기</a> </h2>
                <div class="contentsWrap">
                    <div class="postBox">
                        <c:forEach items="${postAll}" var="post">
                            <div class="postList">
                                <div class="postInfo">
                                    <div class="top">
                                        <div class="tit">
                                            ${post.content}
                                        </div>
                                    </div>
                                    <div class="bot">
                                        <span class="userNickname">${post.nickname}</span>
                                        <span class="likeCount">좋아요 ${post.likeCount}</span>
                                    </div>
                                </div>
                                <c:choose>
                                    <c:when test="${not empty post.fileSrc}">
                                        <div class="thumbImg">
                                            <img src="${post.fileSrc}" alt="thumbImg" />
                                        </div>
                                    </c:when>
                                </c:choose>
                                <div class="replyCount">
                                    ${post.replyCount}
                                </div>
                            </div>
                        </c:forEach>

                    </div>
                </div>
            </section>
            <section class="section section03">
                <h2 class="bigTitle">BEST 모임 <a href="/group">더 보기</a> </h2>
                <div class="contentsWrap">
                    <div class="groupBox">
                        <c:forEach items="${groupAll}" var="group">
                            <div class="groupList">
                                <div class="imgBox">
                                    <img src="${group.groupImg.path}" alt="이미지" />
                                </div>
                                <div class="groupInfo">
                                    <div class="top">
<%--                                        <div class="groupImg">--%>
<%--                                            <img src="/images/banner02.png" alt="groupImg" />--%>
<%--                                        </div>--%>
                                        <div class="tit">
                                            <c:out value="${group.groupDescription}" />
                                        </div>
                                    </div>
                                    <div class="mid">
                                        <span class="groupName"><c:out value="${group.groupName}" /></span>
                                    </div>
                                    <div class="bot">
                                        <div class="left">
                                            <span class="viewCount">조회수 ${group.allViewCount}</span>
                                            <span class="likeCount">좋아요 ${group.likeCount}</span>
                                        </div>
                                        <div class="right">

                                            <span class="writeDate"><fmt:formatDate value="${group.indate}" pattern="yyyy-MM-dd"/></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                    </div>
                </div>
            </section>

            <section class="section section05">
                <h2 class="bigTitle"> 요즘 인기 폭발 멤버</h2>
                <div class="contentsWrap">
                    <div class="memberList">
                        <div class="userInfo">
                            <div class="userProfile">
                                <img src="/images/banner05.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userNickname">별난거부기</span>
                                <span class="userMsg">케이드라마가더좋아</span>
                            </div>
                            <button class="btnFollow">
                                Follow
                            </button>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">자유게시판</span>]
                                    </span>
                                    이사람이 가장 조회수랑 좋아요가 많은 글
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner01.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">K-POP</span>]
                                    </span>
                                    개발자라면 몰랐다간 손해 보는 제미나이 CLI 꿀팁 7가지
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner02.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="btnMore">
                                더 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="memberList">
                        <div class="userInfo">
                            <div class="userProfile">
                                <img src="/images/banner04.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userNickname">별난거부기</span>
                                <span class="userMsg">케이드라마가더좋아</span>
                            </div>
                            <button class="btnFollow">
                                Follow
                            </button>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">자유게시판</span>]
                                    </span>
                                    이사람이 가장 조회수랑 좋아요가 많은 글
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner03.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">K-POP</span>]
                                    </span>
                                    신뢰의 상실, 그리고 의심이라는 방패
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner04.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="btnMore">
                                더 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="memberList">
                        <div class="userInfo">
                            <div class="userProfile">
                                <img src="/images/banner03.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userNickname">별난거부기</span>
                                <span class="userMsg">케이드라마가더좋아</span>
                            </div>
                            <button class="btnFollow">
                                Follow
                            </button>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">자유게시판</span>]
                                    </span>
                                    이사람이 가장 조회수랑 좋아요가 많은 글
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner05.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">K-POP</span>]
                                    </span>
                                    나완전히 새됐어 필요하면 내가 너 MySQL이 설치된 경로를 바로 확인할 수 있게 도와줄 수도 있어요.
                                    혹시 MySQL이 어디에 설치되어 있는지 모르면, 아래 명령어 한 번만 실행해줄래요?
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner01.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="btnMore">
                                더 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="memberList">
                        <div class="userInfo">
                            <div class="userProfile">
                                <img src="/images/banner01.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userNickname">별난거부기</span>
                                <span class="userMsg">케이드라마가더좋아 그치만 노래도좋은걸 ㅎㅎㅎ</span>
                            </div>
                            <button class="btnFollow">
                                Follow
                            </button>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">자유게시판</span>]
                                    </span>
                                    이사람이 가장 조회수랑 좋아요가 많은 글
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner02.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="contentsList">
                                <div class="tit">
                                    <span class="headTitle">
                                        [<span class="ht">K-POP</span>]
                                    </span>
                                    세상에 펼쳐지는 지혜로운 사랑과 흔들림 없는 '충실함'
                                </div>
                                <div class="postImg">
                                    <img src="/images/banner03.png" alt="postThumbImg" />
                                </div>
                            </div>
                            <div class="btnMore">
                                더 알아보기
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <section class="section section04">
                <h2 class="bigTitle"> <span><img src="/images/pinedoryC_logo.png" /> 파인도리가 추천하는 K-노래 가사 </span></h2>
                <div class="contentsWrap">
                    <div class="lyrics">
                        " 그렇지만 가끔 미치도록 네가 안고 싶어질 때가 있어 "
                        <em class="info">가을방학 - 가끔 미치도록 네가 안고 싶어질 때가 있어</em>
                    </div>
                    <div class="lyrics">
                        " 우울한 날들에 최선을 다해줘 이 음악이 절대 끝나지 않도록 "
                        <em class="info">안녕하신가영 - 우울한 날들에 최선을 다해줘</em>
                    </div>
                    <div class="lyrics">
                        " 너는 누군가에게 너무 특별해 영원히 잊을 수 없는 사람이 되기도 하고, <br /> 너는 누군가에게 너무 특별해 영원히 잊을 수 없는 사람이 되기도 하고 "
                        <em class="info">김사월 - 누군가에게</em>
                    </div>
                    <div class="lyrics">
                        " 때때로 혼자이면 좀 어떤가요 요즘은 혼자인 게 좀 괜찮네요 "
                        <em class="info">이구이 - 혼자인 게 좀 괜찮군요</em>
                    </div>
                </div>
            </section>

            <section class="section section06">
                <h2 class="bigTitle"> 요즘 인기 문화</h2>
                <div class="contentsWrap">
                    <div class="cultureList">
                        <img src="/images/culture01.jpg" alt="cultureImg" />
                    </div>
                    <div class="cultureList">
                        <img src="/images/culture02.jpg" alt="cultureImg" />
                    </div>
                    <div class="cultureList">
                        <img src="/images/culture03.gif" alt="cultureImg" />
                    </div>
                    <div class="cultureList">
                        <img src="/images/culture04.jpg" alt="cultureImg" />
                    </div>
                </div>
            </section>

            <section class="section advertisement">
                <div class="adBox">
                    <span class="adSimbol">AD</span>
                    <img src="/images/sample.jpg" alt="advertismemnt_Banner" />
                </div>
            </section>

            <section class="section sectionFAQ">
                <h2 class="bigTitle"> FAQ <a href="#">더 보기</a> </h2>
                <div class="contentsWrap">
                    <div class="faqList">
                        <div class="faqInfo">
                            <div class="faqProfile">
                                <img src="/images/icon_pinedory.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userAsk">Q1. 회원가입을 해야만 글을 작성할 수 있나요?</span>
                            </div>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="answer ">
                                    A1. 네, 글 작성과 댓글 작성은 로그인한 회원만 가능합니다.
                                    비회원은 게시글 열람만 가능하며, 간편 로그인(카카오·구글)도 지원합니다.
                                </div>
                            </div>
                            <div class="btnMore">
                                자세히 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="faqList">
                        <div class="faqInfo">
                            <div class="faqProfile">
                                <img src="/images/icon_pinedory.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userAsk">Q2. 작성한 게시글을 수정하거나 삭제할 수 있나요?</span>
                            </div>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="answer ">
                                    A2. 네, 본인이 작성한 글은 게시글 상세 페이지 우측 상단 메뉴(⋮) 를 통해 수정 또는 삭제할 수 있습니다.
                                    단, 삭제된 글은 복구가 불가능하니 신중히 진행해주세요.
                                </div>
                            </div>
                            <div class="btnMore">
                                자세히 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="faqList">
                        <div class="faqInfo">
                            <div class="faqProfile">
                                <img src="/images/icon_pinedory.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userAsk">Q3. 다른 사용자의 부적절한 글이나 댓글은 어떻게 신고하나요?</span>
                            </div>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="answer ">
                                    A3. 각 게시글과 댓글 옆에 있는 ‘신고’ 버튼을 눌러 사유를 선택하면 됩니다.
                                    관리자가 검토 후 커뮤니티 운영 정책에 따라 경고 또는 차단 조치를 취합니다.
                                </div>
                            </div>
                            <div class="btnMore">
                                자세히 알아보기
                            </div>
                        </div>
                    </div>
                    <div class="faqList">
                        <div class="faqInfo">
                            <div class="faqProfile">
                                <img src="/images/icon_pinedory.png" alt="profile" />
                            </div>
                            <div class="userWrap">
                                <span class="userAsk">Q4. 게시글이 갑자기 사라졌어요. 왜 그런가요?</span>
                            </div>
                        </div>
                        <div class="line"></div>
                        <div class="userContents">
                            <div class="contentsList">
                                <div class="answer ">
                                    A4. 이용약관 위반(욕설, 광고, 도배 등)으로 신고가 누적되거나
                                    관리자 검토 결과 부적절한 콘텐츠로 판단된 경우, 사전 안내 없이 삭제될 수 있습니다.
                                </div>
                            </div>
                            <div class="btnMore">
                                자세히 알아보기
                            </div>
                        </div>
                    </div>
                </div>
            </section>

        </article>

    </div>

    <jsp:include page="./include/footer.jsp"></jsp:include>

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
