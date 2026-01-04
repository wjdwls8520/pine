<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <article class="article workspace groupJoinListWrap">

        <sec:authorize access="isAuthenticated()">
            <script>
                window.isLogin = true;
                window.groupId = ${groupId};

                window.groupRole = ${empty isGroupMember ? 0 : isGroupMember.role};
                if(groupRole != 1) {
                    alert("그룹장만 입장 가능합니다.");
                    history.back();
                }
            </script>
        </sec:authorize>

        <sec:authorize access="isAnonymous()">
            <script>
                window.isLogin = false;
            </script>
        </sec:authorize>

        <%-- section page--%>
        <section class="section groupJoinList">
            <h2 class="bigTitle">가입 신청 리스트</h2>

            <div id="contentsWrap" class="contentsWrap groupJoinListWrap">
                <div class="groupJoinListHeader">
                    <p class="groupJoinListDesc">그룹 가입을 신청한 멤버들을 확인하고 관리할 수 있습니다.</p>
                </div>
                
                <div id="groupJoinListContent" class="groupJoinListContent">
                <%-- ajax 데이터 바인딩 --%>

                </div>
            </div>
        </section>
        
    </article>
</div>
<script>
    function groupJoinReq(target, status, groupId, memberId) {
        console.log(target)
        if(status == "APPROVE") {
            if(!confirm("가입신청을 승인 하시겠습니까?")) return;
        } else {
            if(!confirm("가입신청을 거절 하시겠습니까?")) return;
        }

        fetch(`/group/gjoinreq/`, {method: "POST", body: JSON.stringify({status: status, groupId: groupId, memberId: memberId})})
            .then(response => {
                if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                return response.json(); // 성공하면 JSON 반환
            })
            .then((result) => {
                alert("정상적으로 처리되었습니다.");
            }).catch(err => console.error(err));
    }
</script>
<script src="/js/groupjs/groupJoinRequestScroll.js"></script>
<script src="/js/timeAgo.js"></script>
<jsp:include page="../include/group_footer.jsp"></jsp:include>