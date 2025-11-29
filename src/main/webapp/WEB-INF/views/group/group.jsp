<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/group_common.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace group">

        <c:if test="${not empty msg}">
            <script>
                alert('${msg}');
            </script>
        </c:if>

        <%-- section page--%>
        <section class="section groupMain">
            <div class="tabWrap">
                <div class="tabTitle active">메인</div>
                <div class="tabTitle">내 그룹</div>
            </div>


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



    </article
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>