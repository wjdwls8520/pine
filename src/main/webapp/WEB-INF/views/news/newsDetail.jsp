<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--
    [샘플 데이터 설정]
    실제로는 Controller에서 DB 데이터를 model에 담아 보내지만,
    화면 확인용으로 JSP 내부에서 변수를 세팅하는 로직입니다.
    param.id 값이 없으면 기본값 1로 설정합니다.
--%>
<c:set var="reqId" value="${paramId.id}" />
<c:if test="${empty reqId}">
    <c:set var="reqId" value="1" />
</c:if>

<c:choose>
    <%-- Case 1: 수험생 이벤트 --%>
    <c:when test="${reqId == 1}">
        <c:set var="nTitle" value="2026년 대한민국 수험생 경품 이벤트" />
        <c:set var="nImg" value="/images/banner01.png" />
        <c:set var="nDate" value="2026.01.13" />
        <c:set var="nView" value="2,451" />
        <c:set var="nBody">
            <p>
                안녕하세요, 숲(Pine) 관리자입니다.<br>
                2026년 대입 수험생 여러분을 위한 특별한 이벤트를 준비했습니다.
            </p>
            <p>
                긴 시간 동안 꿈을 위해 달려온 여러분, 정말 고생 많으셨습니다.<br>
                이번 이벤트는 수험생 인증을 완료한 모든 회원님들을 대상으로 진행되며,
                참여해주신 분들 중 추첨을 통해 최신형 태블릿, 백화점 상품권 등 다양한 경품을 드립니다.
            </p>
            <p>
                숲은 여러분의 새로운 시작을 언제나 응원합니다.<br>
                자세한 참여 방법은 하단 버튼을 통해 확인해주세요. 감사합니다.
            </p>
        </c:set>
    </c:when>

    <%-- Case 2: 박보검 생일 --%>
    <c:when test="${reqId == 2}">
        <c:set var="nTitle" value="박보검 생일 축하 배너 오픈 안내" />
        <c:set var="nImg" value="/images/banner02.png" />
        <c:set var="nDate" value="2026.06.16" />
        <c:set var="nView" value="5,102" />
        <c:set var="nBody">
            <p>
                6월 16일, 사랑스러운 배우 박보검님의 생일을 진심으로 축하합니다!
            </p>
            <p>
                팬 여러분의 따뜻한 마음을 모아 제작된 생일 축하 배너가<br>
                오늘부터 일주일간 메인 페이지와 커뮤니티 상단에 게시됩니다.
            </p>
            <p>
                "선한 영향력"이라는 말이 가장 잘 어울리는 배우,<br>
                올해도 행복만 가득하시길 숲(Pine)이 함께 기원합니다.
            </p>
        </c:set>
    </c:when>

    <%-- Case 3: 웹툰 캐릭터 찾기 --%>
    <c:when test="${reqId == 3}">
        <c:set var="nTitle" value="인기 아이돌 닮은 웹툰 캐릭터를 찾아라!" />
        <c:set var="nImg" value="/images/banner03.png" />
        <c:set var="nDate" value="2026.01.20" />
        <c:set var="nView" value="1,893" />
        <c:set var="nBody">
            <p>
                "어? 이 캐릭터 완전 내 최애랑 판박이인데?"<br>
                웹툰을 보다가 무릎을 탁 친 적이 있으신가요?
            </p>
            <p>
                싱크로율 100%를 자랑하는 아이돌과 웹툰 캐릭터 매칭 이벤트를 시작합니다.<br>
                가장 많은 공감을 받은 매칭을 찾아주신 분께는 해당 웹툰 단행본 세트를 드려요!
            </p>
            <p>
                지금 바로 커뮤니티 [이벤트] 게시판에 제보해주세요.<br>
                팬 여러분의 매의 눈을 기다립니다.
            </p>
        </c:set>
    </c:when>

    <%-- Case 4: K-문화 --%>
    <c:when test="${reqId == 4}">
        <c:set var="nTitle" value="K-문화를 찾아: 숨겨진 명소 탐방기" />
        <c:set var="nImg" value="/images/banner04.png" />
        <c:set var="nDate" value="2026.02.05" />
        <c:set var="nView" value="3,211" />
        <c:set var="nBody">
            <p>
                서울의 화려함 뒤에 숨겨진 고즈넉한 아름다움을 찾아서.<br>
                이번 주말에는 북촌 한옥마을의 숨겨진 골목길 투어 어떠세요?
            </p>
            <p>
                전통 매듭 만들기 체험부터 고궁 야간 개장 꿀팁까지,<br>
                외국인 친구들에게 소개하기 딱 좋은 K-문화 명소 5곳을 엄선했습니다.
            </p>
            <p>
                익숙하지만 낯선, 우리 문화의 새로운 매력에 빠져보세요.
            </p>
        </c:set>
    </c:when>

    <%-- Case 5: 팬클럽 모임 --%>
    <c:when test="${reqId == 5}">
        <c:set var="nTitle" value="2030 서울 팬클럽 연합 정기 모임" />
        <c:set var="nImg" value="/images/banner05.png" />
        <c:set var="nDate" value="2026.03.01" />
        <c:set var="nView" value="4,567" />
        <c:set var="nBody">
            <p>
                덕질은 함께해야 제맛!<br>
                서울 지역 2030 팬클럽 연합 정기 모임이 개최됩니다.
            </p>
            <p>
                일시: 2026년 3월 15일 (일) 오후 2시<br>
                장소: 강남구 잼투고 2층 전체 대관<br>
                대상: K-POP을 사랑하는 2030 직장인 및 대학생 누구나
            </p>
            <p>
                랜덤 플레이 댄스부터 최애 자랑 타임까지,<br>
                같은 취미를 가진 친구들을 사귈 수 있는 절호의 기회를 놓치지 마세요!
            </p>
        </c:set>
    </c:when>
</c:choose>

<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/news_common.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article newsDetail">
        <section class="section">

            <%-- 1. 헤더 영역 (동적 데이터 바인딩) --%>
            <div class="newsHeader">
                <span class="newsCategory">News</span>
                <h2 class="newsTitle"><c:out value="${nTitle}" /></h2>
                <div class="newsMeta">
                    <span class="newsDate"><c:out value="${nDate}" /></span>
                    <span class="newsView">조회수 <c:out value="${nView}" /></span>
                </div>
            </div>

            <%-- 2. 본문 영역 --%>
            <div class="newsContent">
                <div class="newsHeroImg">
                    <%-- 이미지 경로 바인딩 --%>
                    <img src="${nImg}" alt="newsHero" />
                </div>

                <div class="newsBody">
                    <%-- 본문 내용은 HTML 태그가 포함되어 있으므로 escapeXml="false" --%>
                    <c:out value="${nBody}" escapeXml="false" />
                </div>
            </div>

            <%-- 3. 버튼 영역 --%>
            <div class="btnWrap">
                <button type="button" class="btnBack" onclick="history.back()">목록으로</button>
            </div>

        </section>
    </article>
</div>

<jsp:include page="../include/footer.jsp"></jsp:include>
</body>
</html>