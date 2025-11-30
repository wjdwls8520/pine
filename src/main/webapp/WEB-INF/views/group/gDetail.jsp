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

                <section class="groupHero">
                    <div class="groupHeroBlend"></div>
                    <div class="groupHeroInner">
                        <div class="groupHeroArt">
                            <c:choose>
                                <c:when test="${not empty group.groupImg}">
                                    <img src="${group.groupImg.path}" alt="${group.groupName}" />
                                </c:when>
                                <c:otherwise>
                                    <div class="groupHeroFallback">NO IMAGE</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="groupHeroCopy">
                            <p class="groupHeroLabel">
                                GROUP · since
                                <c:choose>
                                    <c:when test="${not empty group.indate}">
                                        <fmt:formatDate value="${group.indate}" pattern="yyyy.MM.dd"/>
                                    </c:when>
                                    <c:otherwise>unknown</c:otherwise>
                                </c:choose>
                            </p>
                            <h1 class="groupHeroTitle">${group.groupName}</h1>
                            <p class="groupHeroDesc">${group.groupDescription}</p>
                            <div class="groupHeroBadges">
                                <span>멤버 ${group.groupMemberCount}</span>
                                <span>게시글 ${group.postCount}</span>
                                <span>좋아요 ${group.likeCount}</span>
                            </div>
                            <div class="groupHeroActions">
                                <button type="button" class="groupPrimaryBtn">그룹 가입</button>
                                <button type="button" class="groupGhostBtn">공유하기</button>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="groupStats">
                    <div class="groupStatCard">
                        <p class="groupStatLabel">전체 조회수</p>
                        <strong>${group.allViewCount}</strong>
                        <span class="groupStatHint">Today ${group.todayViewCount}</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">좋아요</p>
                        <strong>${group.likeCount}</strong>
                        <span class="groupStatHint">계속 증가 중</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">게시물</p>
                        <strong>${group.postCount}</strong>
                        <span class="groupStatHint">활성 커뮤니티</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">가입 방식</p>
                        <strong>
                            <c:choose>
                                <c:when test="${group.autoJoin == 1}">자동 승인</c:when>
                                <c:otherwise>관리자 승인</c:otherwise>
                            </c:choose>
                        </strong>
                        <span class="groupStatHint">
                            제한 ${group.userLimit}명 · 현재 ${group.groupMemberCount}명
                        </span>
                    </div>
                </section>

                <section class="groupLayout">
                    <div class="groupAbout">
                        <h2>그룹 소개</h2>
                        <p>${group.groupDescription}</p>

                        <div class="groupDetailGrid">
                            <div>
                                <p class="groupDetailLabel">가입 방식</p>
                                <p class="groupDetailValue">
                                    <c:choose>
                                        <c:when test="${group.joinState == 1}">승인 필요</c:when>
                                        <c:otherwise>누구나 가입</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">자동 승인</p>
                                <p class="groupDetailValue">
                                    <c:choose>
                                        <c:when test="${group.autoJoin == 1}">ON</c:when>
                                        <c:otherwise>OFF</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">최대 인원</p>
                                <p class="groupDetailValue">${group.userLimit}명</p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">현재 멤버</p>
                                <p class="groupDetailValue">${group.groupMemberCount}명</p>
                            </div>
                        </div>

                        <div class="groupCategoryBlock">
                            <p class="groupDetailLabel">카테고리</p>
                            <ul class="groupCategoryList">
                                <c:choose>
                                    <c:when test="${not empty group.categoryIds}">
                                        <c:forEach var="categoryId" items="${group.categoryIds}">
                                            <li>#카테고리${categoryId}</li>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="groupCategoryEmpty">카테고리가 아직 없습니다.</li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>
                    </div>

                    <div class="groupActivity">
                        <h2>최근 활동 지표</h2>
                        <ul class="groupActivityList">
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

