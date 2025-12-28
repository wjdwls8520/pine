<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal" var="loginUser" />
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

        <script>
            window.groupRole = ${empty isGroupMember ? 0 : isGroupMember.role};
        </script>

        <%-- group detail section --%>
        <section class="section groupMain">
            <div class="contentsWrap groupDetail">

                <section class="groupHero">
                    <div class="groupHeroBlend"></div>
                    <div class="groupHeroInner">
                        <div class="groupHeroArt">
                            <c:choose>
                                <c:when test="${not empty groupDetail.groupImg}">
                                    <img src="${groupDetail.groupImg.path}" alt="${groupDetail.groupName}" />
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
                                    <c:when test="${not empty groupDetail.indate}">
                                        <fmt:formatDate value="${groupDetail.indate}" pattern="yyyy.MM.dd"/>
                                    </c:when>
                                    <c:otherwise>unknown</c:otherwise>
                                </c:choose>
                            </p>
                            <h1 class="groupHeroTitle">${groupDetail.groupName}</h1>
                            <p class="groupHeroDesc">${groupDetail.groupDescription}</p>
                            <div class="groupHeroBadges">
                                <span>전체 조회수 ${groupDetail.allViewCount}</span>
                                <span>멤버 수 ${groupDetail.groupMemberCount}</span>
                                <span>좋아요 ${groupDetail.likeCount}</span>
                                <span>게시물 ${groupDetail.postCount}</span>
                            </div>
                            <div class="groupHeroActions">
                                <c:choose>
                                    <%-- 그룹장 --%>
                                    <c:when test="${not empty isGroupMember and isGroupMember.role == 1}">
                                        <button type="button" class="groupPrimaryBtn" onclick="location.href='/group/gupdate/${groupDetail.id}';">그룹 수정</button>
                                    </c:when>
                                    <%-- 그룹원 --%>
                                    <c:when test="${not empty isGroupMember}">
                                        <button type="button" class="groupPrimaryBtn">그룹 탈퇴</button>
                                    </c:when>
                                    <%-- 비로그인유저 및 비그룹원 --%>
                                    <c:otherwise>
                                        <sec:authorize access="isAuthenticated()">
                                            <button type="button" class="groupPrimaryBtn" onclick="alert('가입 신청 하시겠습니까?')">그룹 가입</button>
                                        </sec:authorize>

                                        <sec:authorize access="isAnonymous()">
                                            <button type="button" class="groupPrimaryBtn" onclick="alert('로그인 이후 이용하실 수 있습니다.'); return location.href='/login';">그룹 가입</button>
                                        </sec:authorize>

                                    </c:otherwise>
                                </c:choose>

                                <button type="button" class="groupGhostBtn share">공유하기</button>
                                <button type="button" class="groupGhostBtn like">좋아요</button>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="groupStats">
                    <div class="groupStatCard">
                        <p class="groupStatLabel">TODAY</p>
                        <strong class="todayViewCount">${groupDetail.todayViewCount}</strong>
                        <span class="groupStatHint">전체 조회수 <span class="groupStatHint dataAllViewCount">${groupDetail.allViewCount}<span></span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">좋아요</p>
                        <strong>${groupDetail.likeCount}</strong>
                        <span class="groupStatHint">계속 증가 중</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">게시물</p>
                        <strong>${groupDetail.postCount}</strong>
                        <span class="groupStatHint">활성 커뮤니티</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">등급</p>
                        <strong>
                            <c:choose>
                                <c:when test="${not empty isGroupMember and isGroupMember.role == 1}">
                                    그룹장
                                </c:when>
                                <c:when test="${not empty isGroupMember and isGroupMember.role == 2}">
                                    그룹 매니저
                                </c:when>
                                <c:when test="${not empty isGroupMember and isGroupMember.role == 3}">
                                    일반 그룹원
                                </c:when>
                                <c:otherwise>그룹 손님</c:otherwise>
                            </c:choose>
                        </strong>
                        <span class="groupStatHint">현재 회원님의 그룹원 등급</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">가입 여부</p>
                        <strong>
                            <c:choose>
                                <c:when test="${groupDetail.joinState == 1}">가입 가능</c:when>
                                <c:otherwise>가입 불가능</c:otherwise>
                            </c:choose>
                        </strong>
                        <span class="groupStatHint">
                            현재 그룹 가입 여부
                        </span>
                    </div>
                    <div class="groupStatCard cursor" onClick="location.href='/group/gdetail/' + ${groupDetail.id} + '/gjoinlist';">
                        <p class="groupStatLabel">가입 방식</p>
                        <strong>
                            <c:choose>
                                <c:when test="${groupDetail.autoJoin == 1}">자동 승인</c:when>
                                <c:otherwise>관리자 승인</c:otherwise>
                            </c:choose>
                        </strong>
                        <span class="groupStatHint">
                            제한 ${groupDetail.userLimit}명 · 현재 ${groupDetail.groupMemberCount}명
                        </span>
                    </div>
                </section>

                <section class="groupLayout">
                    <div class="groupAbout">
                        <h2>그룹 소개</h2>
                        <p>${groupDetail.groupDescription}</p>

                        <div class="groupDetailGrid">
                            <div>
                                <p class="groupDetailLabel">가입 가능 여부</p>
                                <p class="groupDetailValue">
                                    <c:choose>
                                        <c:when test="${groupDetail.joinState == 1}">가능</c:when>
                                        <c:otherwise>불가능</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">자동 승인</p>
                                <p class="groupDetailValue">
                                    <c:choose>
                                        <c:when test="${groupDetail.autoJoin == 1}">ON</c:when>
                                        <c:otherwise>OFF</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">최대 인원</p>
                                <p class="groupDetailValue">${groupDetail.userLimit}명</p>
                            </div>
                            <div>
                                <p class="groupDetailLabel">현재 멤버</p>
                                <p class="groupDetailValue">${groupDetail.groupMemberCount}명</p>
                            </div>
                        </div>

                        <div class="groupCategoryBlock">
                            <p class="groupDetailLabel">카테고리</p>
                            <ul class="groupCategoryList">
                                <c:choose>
                                    <c:when test="${not empty groupDetail.categoryIds}">
                                        <c:forEach var="category" items="${groupDetail.categoryIds}">
                                            <li>#${category.nameKor}</li>
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
                                <strong class="todayViewCount">${groupDetail.todayViewCount}</strong>
                                <span>어제 대비 <span class="dataViewCompareResult"></span></span>
                            </li>
                            <li>
                                <p>좋아요 누적</p>
                                <strong>${groupDetail.likeCount}</strong>
                                <span>팬들의 참여가 활발해요</span>
                            </li>
                            <li>
                                <p>게시글</p>
                                <strong>${groupDetail.postCount}</strong>
                                <span>새로운 콘텐츠가 꾸준해요</span>
                            </li>
                            <li>
                                <p>멤버 수</p>
                                <strong>${groupDetail.groupMemberCount}</strong>
                                <span>한계치 ${groupDetail.userLimit}명</span>
                            </li>
                        </ul>
                    </div>
                </section>

            </div>
        </section>

    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>
<script>
    let targetId = Number("${groupDetail.id}");

    <%-- 현재 조회수 가져오기 --%>
    fetch(`/view/viewcount/` + targetId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ pageType: "GROUP"})
    })
        .then(response => {
            if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
            return response.json(); // 성공하면 JSON 반환
        })
        .then((result) => {
            let allViewCounts = document.querySelectorAll(".dataAllViewCount");
            allViewCounts.forEach((data)=> data.innerText = result.allViewCount);

            let todayViewCounts = document.querySelectorAll(".todayViewCount");
            todayViewCounts.forEach((data)=> data.innerText = result.todayViewCount);
        }).catch(err => console.error(err));


    <%-- 어제 대비 오늘 조회수 가져오기 GROUP 고유 기능 --%>
    fetch(`/view/calculateCompare/` + targetId, {
        method: "GET",
    })
        .then(response => {
            if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
            return response.json(); // 성공하면 JSON 반환
        })
        .then((result) => {
            let dataViewCompareResult = document.querySelectorAll(".dataViewCompareResult");
            dataViewCompareResult.forEach((data)=> data.innerText = result.viewCompareResult.percent + '% ' + result.viewCompareResult.status);
        }).catch(err => console.error(err));
</script>
</body>
</html>

