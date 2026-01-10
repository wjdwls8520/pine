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
            <sec:authorize access="isAuthenticated()">
                window.isLogin = true;
            </sec:authorize>

            <sec:authorize access="isAnonymous()">
                window.isLogin = false;
            </sec:authorize>

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
                            <h1 class="groupHeroTitle"><c:out value="${groupDetail.groupName}" /></h1>
                            <p class="groupHeroDesc"><c:out value="${groupDetail.groupDescription}" /></p>
                            <div class="groupHeroBadges">
                                <span>전체 조회수 ${groupDetail.allViewCount}</span>
                                <span class="groupMember">멤버 수 ${groupDetail.groupMemberCount}</span>
                                <span class="like">좋아요 ${groupDetail.likeCount}</span>
                                <span class="post">게시물 ${groupDetail.postCount}</span>
                            </div>
                            <div class="groupHeroActions">
                                <c:choose>
                                    <%-- 그룹장 --%>
                                    <c:when test="${not empty isGroupMember and isGroupMember.role == 1}">
                                        <button type="button" class="groupPrimaryBtn" onclick="location.href='/group/gupdate/${groupDetail.id}';">그룹 수정</button>
                                    </c:when>
                                    <%-- 그룹원 --%>
                                    <c:when test="${not empty isGroupMember}">
                                        <button type="button" class="groupPrimaryBtn" onclick="groupOut();">그룹 탈퇴</button>
                                    </c:when>
                                    <%-- 비로그인유저 및 비그룹원 --%>
                                    <c:otherwise>
                                        <sec:authorize access="isAuthenticated()">
                                            <c:choose>
                                                <%-- 그룹 가입 신청 대기중인 유저 일 때, --%>
                                                <c:when test="${isGroupJoinState}">
                                                    <button type="button" class="groupPrimaryBtn notClick">가입 신청 대기중</button>
                                                </c:when>
                                                <c:when test="${groupDetail.joinState == 1}">  <%-- 가입 가능 옵션 상태 --%>
                                                    <button type="button" class="groupPrimaryBtn" onclick="openJoinModal()">그룹 가입</button>
                                                </c:when>
                                                <c:otherwise> <%-- 가입 불가능 옵션 상태 --%>
                                                    <button type="button" class="groupPrimaryBtn notClick">그룹 가입</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </sec:authorize>

                                        <sec:authorize access="isAnonymous()">
                                            <button type="button" class="groupPrimaryBtn" onclick="alert('로그인 이후 이용하실 수 있습니다.'); return location.href='/login';">그룹 가입</button>
                                        </sec:authorize>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <%-- 그룹장 --%>
                                    <c:when test="${not empty isGroupMember and isGroupMember.role == 1}">
                                        <button type="button" class="groupGhostBtn joinList" onClick="location.href='/group/gdetail/' + ${groupDetail.id} + '/gjoinlist';">가입신청 리스트</button>
                                    </c:when>
                                </c:choose>
                                <button type="button" class="groupGhostBtn share" onclick="copyCurrentPostUrl()">공유하기</button>
                                <button type="button" class="groupGhostBtn groupMember" onclick="location.href='/group/detail/${groupDetail.id}/gmemberlist';">멤버 리스트</button>
                                <button type="button" class="groupGhostBtn like ${isLike ? "active" : ""}" onclick="toggleLike('GROUP', ${groupDetail.id}, this)">좋아요</button>
                            </div>

                        </div>
                    </div>
                </section>

                <section class="groupStats">
                    <div class="groupStatCard">
                        <p class="groupStatLabel">TODAY</p>
                        <strong class="todayViewCount">${groupDetail.todayViewCount}</strong>
                        <span class="groupStatHint">전체 조회수 <span class="groupStatHint dataAllViewCount">${groupDetail.allViewCount}</span></span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">좋아요</p>
                        <strong class="like">${groupDetail.likeCount}</strong>
                        <span class="groupStatHint">인기를 모아봐요!</span>
                    </div>
                    <div class="groupStatCard">
                        <p class="groupStatLabel">게시물</p>
                        <strong>${groupDetail.postCount}</strong>
                        <span class="groupStatHint">게시글로 소통해요!</span>
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
                    <div class="groupStatCard">
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
                        <p><c:out value="${groupDetail.groupDescription}" /></p>

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


                <%-- [NEW] Group Tracklist Section (Apple Music Style) --%>
                <section class="groupTrackList">
                    <div class="trackListHeader">
                            <h3>New Post</h3>
                        <%-- '더보기' 클릭 시 전체 게시글 목록 페이지로 이동 --%>
                        <a class="btnTrackMore" onclick="
                            <c:choose>
                                <c:when test="${not empty isGroupMember and isGroupMember.role > 0}">
                                    location.href = '/group/${groupDetail.id}/post/main'
                                </c:when>
                                <c:otherwise>
                                    alert('그룹멤버만 이용 가능합니다.');
                                </c:otherwise>
                            </c:choose>
                        ">
                            더 보기
                        </a>
                    </div>

                    <ul class="trackList">
                        <%-- 예시 데이터: 실제 개발 시 c:forEach로 대체 --%>
                        <c:forEach var="i" begin="1" end="5" step="1">
                            <li class="trackItem" onclick="
                                <c:choose>
                                    <c:when test="${not empty isGroupMember and isGroupMember.role > 0}">
                                            location.href = '/group/${groupDetail.id}/post/main'
                                    </c:when>
                                    <c:otherwise>
                                            alert('그룹멤버만 이용 가능합니다.');
                                    </c:otherwise>
                                </c:choose>
                            ">
                                <div class="trackIndexWrap">
                                    <span class="trackIndex">${i}</span>
                                    <img src="/images/icon_pinedory.png" alt="play" class="trackPlayIcon">
                                </div>
                                <div class="trackInfo">
                                    <p class="trackTitle">
                                        <c:choose>
                                            <c:when test="${i == 1}">[공지] 이번 앨범 활동 관련 필독 사항입니다.</c:when>
                                            <c:when test="${i == 2}">오늘자 무대 직캠 공유합니다 (화질 좋음)</c:when>
                                            <c:otherwise>그룹 활동 게시글 제목 예시입니다 ${i}</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <span class="trackArtist">
                                        <c:choose>
                                            <c:when test="${i == 1}">관리자</c:when>
                                            <c:otherwise>팬덤명${i}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="trackMeta">
                                    <span class="trackDate">2026.01.08</span>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </section>

            </div>
        </section>

    </article>
</div>
<%-- ... 기존 HTML 끝 ... --%>

<div id="groupJoinModal" class="groupJoinModal">
    <div class="groupJoinModalContent">
        <div class="groupJoinModalHeader">
            <h3 class="groupJoinModalTitle">그룹 가입 신청</h3>
            <button type="button" class="groupJoinModalClose" onclick="closeJoinModal()">&times;</button>
        </div>
        <div class="groupJoinModalBody">
            <p class="groupJoinModalDesc">
                그룹장에게 전송될 간단한 자기소개를 입력해주세요.<br>
                승인이 완료되면 그룹 활동을 시작할 수 있습니다.
            </p>
            <textarea id="joinIntroduction" class="formTextarea" maxlength="200" placeholder="안녕하세요! 이 그룹의 활동에 관심이 있어 신청합니다.">안녕하세요! 이 그룹의 활동에 관심이 있어 신청합니다.</textarea>
        </div>
        <div class="groupJoinModalFooter">
            <button type="button" class="groupJoinBtn groupJoinBtnCancel" onclick="closeJoinModal()">취소</button>
            <button type="button" class="groupJoinBtn groupJoinBtnSubmit" onclick="submitGroupJoin()">신청하기</button>
        </div>
    </div>
</div>
<div id="commonToast" class="toastMsg"></div>

<script src="/js/common.js"></script>
<script>
    let targetId = ${groupId};

    <%-- 현재 조회수 가져오기 --%>
    fetch(`/view/groupviewcount/` + targetId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
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
    fetch(`/view/groupcalculateCompare/` + targetId, {
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


    // 팝업 열기
    function openJoinModal() {
        if(confirm("그룹 가입을 신청하시겠습니까?")) {
            document.getElementById('groupJoinModal').classList.add('active');
            // 스크롤 방지 (선택사항)
            document.body.style.overflow = 'hidden';
        }
    }

    // 팝업 닫기
    function closeJoinModal() {
        document.getElementById('groupJoinModal').classList.remove('active');
        document.getElementById('joinIntroduction').value = ''; // 입력값 초기화
        document.body.style.overflow = '';
    }

    // 팝업 외부 클릭 시 닫기
    document.getElementById('groupJoinModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeJoinModal();
        }
    });

    // 가입 신청 전송 (AJAX)
    function submitGroupJoin() {
        const introduction = document.getElementById('joinIntroduction').value;

        // 유효성 검사
        if (!introduction.trim()) {
            alert("가입 인사말을 입력해주세요.");
            return;
        }

        const joinData = {
            groupId: targetId, // 상단 스크립트 변수 사용
            introduction: introduction
        };

        fetch('/group/gjoin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(joinData)
        })
            .then(response => {
                if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                return response.json(); // 성공하면 JSON 반환
            })
            .then((result) => {
                alert(result.msg);
                closeJoinModal();
                location.reload(); // 상태 반영을 위해 새로고침
            })
            .catch(err => {
                console.error(err);
                alert(err.message);
            });
    }

    // 그룹탈퇴
    function groupOut() {
        if(groupRole === 1) {
            alert("그룹장은 그룹탈퇴가 불가능 합니다. 다른 멤버에게 그룹장을 위임하거나, 그룹을 삭제해주세요.");
            return;
        }
        if(confirm("정말로 탈퇴 하시겠습니까?")) {
            fetch(`/group/groupout`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({groupId: targetId})})
                .then(response => {
                    if (response.ok) {
                        alert("처리되었습니다.");
                        location.href='/group';
                    } else {
                    // [여기가 핵심] 서버가 에러(400, 500)를 던지면 프론트가 페이지를 이동시킴
                        console.log(response);
                        return response.text().then(msg => {
                            alert(msg); // "잘못된 접근입니다" 메시지 출력
                            location.href = "/error"; // 에러 페이지로 강제 이동!
                        });
                    }
                }).catch(err => {
                console.error(err);
                alert(err.message);
            });
        }
    }

    // 좋아요
    function toggleLike(targetType, targetId, elTag) {
        if(!isLogin) {
            alert("로그인이 필요한 기능입니다");
            return;
        }

        fetch("/like", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                targetType: targetType,
                targetId: targetId
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("서버 통신 실패");
                return res.json();
            })
            .then(data => {
                // 🕵️‍♂️ [디버깅] 여기서 서버가 무슨 값을 주는지 콘솔(F12)에서 꼭 확인하세요!
                console.log("서버가 보낸 최신 좋아요 수:", data);
                let isLike = data.liked;
                if(isLike) {
                    elTag.classList.add("active");
                } else {
                    elTag.classList.remove("active");
                }
                document.querySelector(".groupStatCard .like").textContent = data.likeCount;
                document.querySelector(".groupHeroBadges .like").textContent = "좋아요 " + data.likeCount;
            })
            .catch((err) => {
                console.error("좋아요 에러 발생:", err);
                alert("처리에 실패했습니다.");
            });
    }
</script>

<jsp:include page="../include/group_footer.jsp"></jsp:include>
</body>
</html>

