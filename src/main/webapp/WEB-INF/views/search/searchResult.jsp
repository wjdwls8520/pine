<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/search.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article search">
        <%-- section page  --%>
        <section class="section section01">
            <div class="pineSearchContainer">

                <div class="pineSearchTabs">
                    <button class="pineTabItem active" data-type="COMMUNITY">커뮤니티</button>
                    <button class="pineTabItem" data-type="SHORTS">쇼츠</button>

                    <c:if test="${not empty myGroups}">
                        <button class="pineTabItem" data-type="GROUP_POST">그룹 포스트</button>
                    </c:if>

                    <button class="pineTabItem" data-type="GROUP">그룹</button>
                    <button class="pineTabItem" data-type="MEMBER">멤버</button>
                </div>

                <div class="pineFilterBar">

                    <select id="pineGroupSelect" class="pineSelectBox pineHidden">
                        <option value="0">전체 그룹</option>
                        <c:forEach var="group" items="${myGroups}">
                            <option value="${group.id}">${group.groupName}</option>
                        </c:forEach>
                    </select>

                    <select id="pineSortSelect" class="pineSelectBox">
                        <option value="relevance">관련성순</option>
                        <option value="likes">좋아요순</option>
                        <option value="replies" class="postOnlyOption">댓글순</option>
                        <option value="members" class="groupOnlyOption pineHidden">멤버수순</option>
                    </select>

                    <select id="pinePeriodSelect" class="pineSelectBox postOnlyOption">
                        <option value="all">전체 기간</option>
                        <option value="today">오늘</option>
                        <option value="month">지난 1개월</option>
                        <option value="year">지난 1년</option>
                    </select>
                </div>

                <div id="pineResultWrapper" class="pineResultWrapper">
                </div>

                <div id="pineLoader" class="pineLoader pineHidden">
                    Loading...
                </div>

                <div id="pineNoResult" class="pineNoResult pineHidden">
                    <p>검색 결과가 없습니다.</p>
                </div>
            </div>

        </section>
    </article>
</div>

<jsp:include page="../include/footer.jsp"></jsp:include>
<script src="/js/search.js"></script>
<script>
    // JSP 변수 JS로 넘기기
    const currentKeyword = "${keyword}";
</script>
</body>
</html>