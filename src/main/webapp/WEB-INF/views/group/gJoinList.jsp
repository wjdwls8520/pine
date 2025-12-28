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
    <article class="article workspace group">

        <sec:authorize access="isAuthenticated()">
            <script>
                window.isLogin = true;
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
                
                <div class="groupJoinListContent">
                    <div class="groupJoinListItem">
                        <div class="groupJoinListItemAvatar">
                            <div class="groupJoinListItemAvatarImg">김</div>
                        </div>
                        <div class="groupJoinListItemInfo">
                            <div class="groupJoinListItemTop">
                                <h3 class="groupJoinListItemName">김민수</h3>
                                <span class="groupJoinListItemBadge">신규 신청</span>
                            </div>
                            <p class="groupJoinListItemDesc">안녕하세요! 이 그룹에 관심이 많아 가입 신청합니다.</p>
                            <div class="groupJoinListItemMeta">
                                <span class="groupJoinListItemDate">2시간 전</span>
                                <span class="groupJoinListItemId">@kimminsoo</span>
                            </div>
                        </div>
                        <div class="groupJoinListItemActions">
                            <button type="button" class="groupJoinListBtn groupJoinListBtnApprove">승인</button>
                            <button type="button" class="groupJoinListBtn groupJoinListBtnReject">거절</button>
                        </div>
                    </div>

                    <div class="groupJoinListItem">
                        <div class="groupJoinListItemAvatar">
                            <div class="groupJoinListItemAvatarImg">이</div>
                        </div>
                        <div class="groupJoinListItemInfo">
                            <div class="groupJoinListItemTop">
                                <h3 class="groupJoinListItemName">이지은</h3>
                                <span class="groupJoinListItemBadge">신규 신청</span>
                            </div>
                            <p class="groupJoinListItemDesc">커뮤니티 활동에 적극적으로 참여하고 싶습니다.</p>
                            <div class="groupJoinListItemMeta">
                                <span class="groupJoinListItemDate">5시간 전</span>
                                <span class="groupJoinListItemId">@leeji</span>
                            </div>
                        </div>
                        <div class="groupJoinListItemActions">
                            <button type="button" class="groupJoinListBtn groupJoinListBtnApprove">승인</button>
                            <button type="button" class="groupJoinListBtn groupJoinListBtnReject">거절</button>
                        </div>
                    </div>

                    <div class="groupJoinListItem">
                        <div class="groupJoinListItemAvatar">
                            <div class="groupJoinListItemAvatarImg">박</div>
                        </div>
                        <div class="groupJoinListItemInfo">
                            <div class="groupJoinListItemTop">
                                <h3 class="groupJoinListItemName">박준호</h3>
                                <span class="groupJoinListItemBadge groupJoinListItemBadgePending">대기 중</span>
                            </div>
                            <p class="groupJoinListItemDesc">관리자 검토 중입니다. 잠시만 기다려주세요.</p>
                            <div class="groupJoinListItemMeta">
                                <span class="groupJoinListItemDate">1일 전</span>
                                <span class="groupJoinListItemId">@parkjun</span>
                            </div>
                        </div>
                        <div class="groupJoinListItemActions">
                            <button type="button" class="groupJoinListBtn groupJoinListBtnApprove">승인</button>
                            <button type="button" class="groupJoinListBtn groupJoinListBtnReject">거절</button>
                        </div>
                    </div>

                    <div class="groupJoinListItem">
                        <div class="groupJoinListItemAvatar">
                            <div class="groupJoinListItemAvatarImg">최</div>
                        </div>
                        <div class="groupJoinListItemInfo">
                            <div class="groupJoinListItemTop">
                                <h3 class="groupJoinListItemName">최수진</h3>
                                <span class="groupJoinListItemBadge">신규 신청</span>
                            </div>
                            <p class="groupJoinListItemDesc">이 그룹의 활동을 지켜보며 관심을 갖게 되었습니다.</p>
                            <div class="groupJoinListItemMeta">
                                <span class="groupJoinListItemDate">3일 전</span>
                                <span class="groupJoinListItemId">@choisujin</span>
                            </div>
                        </div>
                        <div class="groupJoinListItemActions">
                            <button type="button" class="groupJoinListBtn groupJoinListBtnApprove">승인</button>
                            <button type="button" class="groupJoinListBtn groupJoinListBtnReject">거절</button>
                        </div>
                    </div>

                    <div class="groupJoinListItem">
                        <div class="groupJoinListItemAvatar">
                            <div class="groupJoinListItemAvatarImg">정</div>
                        </div>
                        <div class="groupJoinListItemInfo">
                            <div class="groupJoinListItemTop">
                                <h3 class="groupJoinListItemName">정다은</h3>
                                <span class="groupJoinListItemBadge">신규 신청</span>
                            </div>
                            <p class="groupJoinListItemDesc">친구 추천으로 알게 되어 가입 신청드립니다.</p>
                            <div class="groupJoinListItemMeta">
                                <span class="groupJoinListItemDate">1주 전</span>
                                <span class="groupJoinListItemId">@jungdaeun</span>
                            </div>
                        </div>
                        <div class="groupJoinListItemActions">
                            <button type="button" class="groupJoinListBtn groupJoinListBtnApprove">승인</button>
                            <button type="button" class="groupJoinListBtn groupJoinListBtnReject">거절</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        
    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>