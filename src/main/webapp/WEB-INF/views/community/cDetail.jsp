<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">
    <link rel="stylesheet" href="/css/swiper-bundle.min.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article id="commuDtailPage" class="article workspace commu">

        <h2 class="pageTitle">community detail</h2>
        <section class="section section01 commuSection">
            <%--  왼쪽 포스트 섹션--%>
            <div class="postSection" id="postList">
                <div class="postBox">
                    <div class="postInner">
                        <div class="postWrap">
                            <div class="postTop">
                                <div class="postInfo">
                                    <div class="postProfileImgBox">
                                        <img class="profileImg" src="/images/banner02.png" />
                                    </div>
        <%--                            <div class="userNick">${post.userNick} ${post.id}</div>--%>
                                    <div class="postTime relative-time">
                                        ${post.updateDate}
                                    </div>
                                </div>
                                <div class="moreIcon">...</div>
                            </div>
                            <div class="postMiddle">
                                <div class="postContent">${post.content}</div>
        <%--                        <div class="postHash">${post.hashTag || ""}</div>--%>

                                <div class="swiper commuSlide">
                                    <div class="swiper-wrapper" id="postImg_${post.id}">
                                        <c:forEach var="file" items="${post.file}">
                                            <div class="swiper-slide">
                                                <img src="${file.path}" alt="image"/>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="swiper-pagination"></div>
                                </div>

                            </div>
                            <div class="postBottom">
                                <div class="icoBox postLike">
                                    <span class="ico ico_like" onclick="toggleLike(${post.id}, this)"></span>
                                    <span id="likeCount_${post.id}">${post.likeCount}</span>
                                </div>
                                <div class="icoBox postReply">
                                    <span class="ico ico_reply"></span>
                                    <span>${post.replyCount}</span>
                                </div>
                                <div class="icoBox postlink">
                                    <span class="ico ico_link"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 오른쪽 그룹 랭크 섹션 -->
            <div class="groupSection">
                <div class="inner">
                    <div class="bestGroupTitle">BEST GROUP</div>
                    <div class="bestGroupListWrap">
                        <ul class="bestGroupList">
                            <%-- 5개 노출 후 더보기--%>
                            <li>
                                <a>
                                    <div class="bGroupImg">
                                        <img src="/images/banner02.png"/>
                                    </div>
                                    <div class="bGroupInfo">
                                        <span class="bGroupName">방탄팬클럽</span>
                                        <span class="bGroupCount">멤버 2,200명</span>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

        </section>
    </article>
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
<script src="/js/swiper-bundle.min.js"></script>
<script src="/js/commujs/commuMain.js"></script>
</body>
</html>

