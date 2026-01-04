<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
            <div class="postSection" id="postList">
                <div class="postBox">
                    <div class="postInner">
                        <div class="postWrap">
                            <div class="postTop">
                                <div class="postInfo">
                                    <div class="postProfileImgBox">
                                        <img class="profileImg"
                                             src="${not empty post.profile_img ? post.profile_img : '/images/user.png'}" />
                                    </div>
                                    <div class="userNick">${post.nickname}</div>
                                    <div class="postTime">
                                        <fmt:formatDate value="${post.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </div>
                                </div>

                                <div class="moreIcon" onclick="toggleMoreModal(event, this)">
                                    <span class="ico ico_more"></span>

                                    <div class="postMoreModal">
                                        <c:choose>
                                            <%-- loginUserId는 이제 컨트롤러에서 넘어온 값입니다 --%>
                                            <c:when test="${not empty loginUserId and loginUserId == post.memberId}">
                                                <%-- 내 글 --%>
                                                <button class="menuItem" onclick="goEdit(${post.id})">수정</button>
                                                <button class="menuItem danger" onclick="deletePost(${post.id})">삭제</button>
                                            </c:when>
                                            <c:otherwise>
                                                <%-- 남의 글 --%>
                                                <button class="menuItem danger" onclick="doReport(${post.id})">신고</button>
                                                <button class="menuItem" onclick="doSave(${post.id})">저장</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                            </div>

                            <div class="postMiddle">
                                <div class="postContent">${post.content}</div>

                                <c:if test="${not empty post.tags}">
                                    <div class="tagZone">
                                        <c:forEach var="tag" items="${post.tags}">
                                            <span class="tag">#${tag.name}</span>
                                        </c:forEach>
                                    </div>
                                </c:if>

                                <c:if test="${not empty post.files}">
                                    <div class="swiper commuSlide">
                                        <div class="swiper-wrapper">
                                            <c:forEach var="file" items="${post.files}">
                                                <div class="swiper-slide">
                                                    <img src="${file.path}" alt="image"/>
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <div class="swiper-pagination"></div>
                                    </div>
                                </c:if>

                            </div>

                            <div class="postBottom">
                                <div class="icoBox postLike">
                                    <span class="ico ico_like ${post.liked ? "active" : ""}"
                                          onclick="toggleLike('POST', ${post.id}, this)"></span>
                                    <span id="likeCount_${post.id}">${post.likeCount}</span>
                                </div>
                                <div class="icoBox postReply">
                                    <span class="ico ico_reply"></span>
                                    <span>${post.replyCount}</span>
                                </div>
                                <div class="icoBox postlink" onclick="copyPostUrl(${post.id})" style="cursor: pointer;">
                                    <span class="ico ico_link"></span>
                                </div>
                            </div>

                            <%-- 댓글영역 --%>
                            <div class="replyWrap">
                                <div class="writeBox">
                                    <input type="text" id="mainReplyInput" class="replyTextBox" placeholder="댓글을 남겨보세요">
                                    <button id="btnMainReply" class="replyBtn">등록</button>
                                </div>

                                <ul class="replyList" id="replyListArea">
                                </ul>

                                <div id="replyMoreBtnWrap" class="replyMoreBtnWrap">
                                    <button class="btnMoreReply" onclick="loadMainReplies()">
                                        댓글 더보기 +
                                    </button>
                                </div>
                            </div>
                            <%-- 댓글영역 끝 --%>

                        </div>
                    </div>
                </div>
            </div>

            <div class="groupSection">
                <div class="inner">
                    <div class="bestGroupTitle">BEST GROUP</div>
                    <div class="bestGroupListWrap">
                        <ul class="bestGroupList">
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
<<div id="commonToast" class="toastMsg"></div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>

<script>
    const POST_ID = ${post.id};
</script>
<script src="/js/toggleLike.js"></script>
<script src="/js/commujs/commuCommon.js"></script>
<script src="/js/commujs/commuDetail.js"></script>
<script src="/js/swiper-bundle.min.js"></script>

<script>
    window.addEventListener("load", () => {
        // 공통 함수 호출 (이 한 줄이면 끝!)
        initPostSliders();
    });
</script>
</body>
</html>