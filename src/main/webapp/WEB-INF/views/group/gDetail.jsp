<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
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

        <%-- group detail section --%>
        <section class="section groupMain">
            <div class="contentsWrap groupDetail">

                <section class="testHero">
                    <div class="testHeroBlend"></div>
                    <div class="testHeroInner">
                        <div class="testHeroArt">
                            <c:choose>
                                <c:when test="${not empty group.groupImg}">
                                    <img src="${group.groupImg.path}" alt="${group.groupName}" />
                                </c:when>
                                <c:otherwise>
                                    <div class="testHeroFallback">NO IMAGE</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="testHeroCopy">
                            <p class="testHeroLabel">
                                GROUP · since
                                <c:choose>
                                    <c:when test="${not empty group.indate}">
                                        <fmt:formatDate value="${group.indate}" pattern="yyyy.MM.dd"/>
                                    </c:when>
                                    <c:otherwise>unknown</c:otherwise>
                                </c:choose>
                            </p>
                            <h1 class="testHeroTitle">${group.groupName}</h1>
                            <p class="testHeroDesc">${group.groupDescription}</p>
                            <div class="testHeroBadges">
                                <span>멤버 ${group.groupMemberCount}</span>
                                <span>게시글 ${group.postCount}</span>
                                <span>좋아요 ${group.likeCount}</span>
                            </div>
                            <div class="testHeroActions">
                                <button type="button" class="testPrimaryBtn">그룹 가입</button>
                                <button type="button" class="testGhostBtn">공유하기</button>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="testStats">
                    <div class="testStatCard">
                        <p class="testStatLabel">전체 조회수</p>
                        <strong>${group.allViewCount}</strong>
                        <span class="testStatHint">Today ${group.todayViewCount}</span>
                    </div>
                    <div class="testStatCard">
                        <p class="testStatLabel">좋아요</p>
                        <strong>${group.likeCount}</strong>
                        <span class="testStatHint">계속 증가 중</span>
                    </div>
                    <div class="testStatCard">
                        <p class="testStatLabel">게시물</p>
                        <strong>${group.postCount}</strong>
                        <span class="testStatHint">활성 커뮤니티</span>
                    </div>
                    <div class="testStatCard">
                        <p class="testStatLabel">가입 방식</p>
                        <strong>
                            <c:choose>
                                <c:when test="${group.autoJoin == 1}">자동 승인</c:when>
                                <c:otherwise>관리자 승인</c:otherwise>
                            </c:choose>
                        </strong>
                        <span class="testStatHint">
                            제한 ${group.userLimit}명 · 현재 ${group.groupMemberCount}명
                        </span>
                    </div>
                </section>

                <section class="testLayout">
                    <div class="testAbout">
                        <h2>그룹 소개</h2>
                        <p>${group.groupDescription}</p>

                        <div class="testDetailGrid">
                            <div>
                                <p class="testDetailLabel">가입 방식</p>
                                <p class="testDetailValue">
                                    <c:choose>
                                        <c:when test="${group.joinState == 1}">승인 필요</c:when>
                                        <c:otherwise>누구나 가입</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="testDetailLabel">자동 승인</p>
                                <p class="testDetailValue">
                                    <c:choose>
                                        <c:when test="${group.autoJoin == 1}">ON</c:when>
                                        <c:otherwise>OFF</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="testDetailLabel">최대 인원</p>
                                <p class="testDetailValue">${group.userLimit}명</p>
                            </div>
                            <div>
                                <p class="testDetailLabel">현재 멤버</p>
                                <p class="testDetailValue">${group.groupMemberCount}명</p>
                            </div>
                        </div>

                        <div class="testCategoryBlock">
                            <p class="testDetailLabel">카테고리</p>
                            <ul class="testCategoryList">
                                <c:choose>
                                    <c:when test="${not empty group.categoryIds}">
                                        <c:forEach var="categoryId" items="${group.categoryIds}">
                                            <li>#카테고리${categoryId}</li>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="testCategoryEmpty">카테고리가 아직 없습니다.</li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>
                    </div>

                    <div class="testActivity">
                        <h2>최근 활동 지표</h2>
                        <ul class="testActivityList">
                            <li>
                                <p>하루 조회수</p>
                                <strong>${group.todayViewCount}</strong>
                                <span>어제 대비 +18%</span>
                            </li>
                            <li>
                                <p>좋아요 누적</p>
                                <strong>${group.likeCount}</strong>
                                <span>팬들의 참여가 활발해요</span>
                            </li>
                            <li>
                                <p>게시글</p>
                                <strong>${group.postCount}</strong>
                                <span>새로운 콘텐츠가 꾸준해요</span>
                            </li>
                            <li>
                                <p>멤버 수</p>
                                <strong>${group.groupMemberCount}</strong>
                                <span>한계치 ${group.userLimit}명</span>
                            </li>
                        </ul>
                    </div>
                </section>

            </div>
        </section>

    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>
</body>
</html>

