<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
                <h2 class="bigTitle">새로운 소식</h2>
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
<%--            <section class="section advertisement">--%>
<%--                <img />--%>
<%--            </section>--%>
            <section class="section section02">
                <h2 class="bigTitle">BEST 컨텐츠</h2>
                <div class="contentsWrap">
                    <div class="postBox">
                        <div class="postList">
                            <div class="postInfo">
                                <div class="top">
                                    <div class="profileImg">
                                        <img src="/images/banner01.png" alt="profileImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <div class="left">
                                        <span class="userNickname">별난거북이</span>
                                    </div>
                                    <div class="right">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="postList">
                            <div class="postInfo">
                                <div class="top">
                                    <div class="profileImg">
                                        <img src="/images/banner01.png" alt="profileImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="userNickname">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="postList">
                            <div class="postInfo">
                                <div class="top">
                                    <div class="profileImg">
                                        <img src="/images/banner01.png" alt="profileImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="userNickname">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="postList">
                            <div class="postInfo">
                                <div class="top">
                                    <div class="profileImg">
                                        <img src="/images/banner01.png" alt="profileImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="userNickname">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <section class="section section03">
                <h2 class="bigTitle">BEST 모임</h2>
                <div class="contentsWrap">
                    <div class="groupBox">
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner01.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner02.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner02.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner03.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner03.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner04.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner04.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner05.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner05.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner01.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner02.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner03.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner03.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner04.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="groupList">
                            <div class="imgBox">
                                <img src="/images/banner04.png" alt="이미지" />
                            </div>
                            <div class="groupInfo">
                                <div class="top">
                                    <div class="groupImg">
                                        <img src="/images/banner05.png" alt="groupImg" />
                                    </div>
                                    <div class="tit">
                                        2024 엑소 으르렁 무비 봄? ㅋㅋㅋ 와 이번에 진짜 대박이야 좀 멋있었어.
                                    </div>
                                </div>
                                <div class="mid">
                                    <span class="groupName">별난거북이</span>
                                </div>
                                <div class="bot">
                                    <div class="left">
                                        <span class="viewCount">조회수 321</span>
                                        <span class="writeDate">3개월 전</span>
                                    </div>
                                    <div class="right">
                                        <span class="likeCount">좋아요 88</span>
                                        <span class="replyCount">댓글 122</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </section>


            <div class="floatTab">
                <div class="li active">글로벌 게시글</div>
                <div class="li">베스트 게시글</div>
            </div>
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
