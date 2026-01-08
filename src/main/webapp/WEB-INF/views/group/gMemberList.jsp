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
    <article class="article workspace groupMemberListWrap">

        <script>
            window.groupId = ${groupId};

            window.groupRole = ${empty isGroupMember ? 0 : isGroupMember.role};
        </script>

        <%-- section page--%>
        <section class="section groupMemberList">
            <h2 class="bigTitle">그룹 멤버 리스트</h2>

            <div id="contentsWrap" class="contentsWrap groupJoinListWrap">
                <div class="groupJoinListHeader">
                    <p class="groupJoinListDesc">그룹 멤버 리스트</p>
                </div>
                
                <div id="groupMemberListContent" class="groupJoinListWrap">
                <%-- ajax 데이터 바인딩 --%>

                </div>
            </div>
        </section>
        
    </article>
</div>

<div id="levelChangeModal" class="groupJoinModal">
    <div class="groupJoinModalContent">
        <div class="groupJoinModalHeader">
            <h3 class="groupJoinModalTitle">회원 등급 변경</h3>
            <button type="button" class="groupJoinModalClose" onclick="closeLevelModal()">
                &#10005;
            </button>
        </div>

        <div class="groupJoinModalBody">
            <p class="groupJoinModalDesc">해당 회원의 변경할 등급을 선택해주세요.</p>

            <div class="formInputWrapper">
                <select id="modalLevelSelect" class="formInput" style="width: 100%; border-radius: 6px;">
                    <option value="3">그룹 일반</option>
                    <option value="2">그룹 매니저</option>
                    <c:if test="${not empty isGroupMember and isGroupMember.role == 1}">
                        <option value="1">그룹장</option>
                    </c:if>
                </select>
            </div>
        </div>

        <div class="groupJoinModalFooter">
            <button type="button" class="groupJoinBtn groupJoinBtnCancel" onclick="closeLevelModal()">취소</button>
            <button type="button" class="groupJoinBtn groupJoinBtnSubmit" onclick="confirmLevelChange()">변경하기</button>
        </div>
    </div>
</div>

<script>
    function gmemberOut(target, groupId, memberId) {
        if(!confirm("해당 그룹멤버를 추방하시겠습니까?")) return;

        fetch(`/group/gjoinreqapprej`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({groupId: groupId, memberId: memberId})})
            .then(response => {
                if (response.ok) {
                    alert("처리되었습니다.");
                    // location.reload(); // 성공 시 새로고침
                    target.parentElement.parentElement.remove();
                } else {
                    // [여기가 핵심] 서버가 에러(400, 500)를 던지면 프론트가 페이지를 이동시킴
                    console.log(response);
                    return response.text().then(msg => {
                        alert(msg); // "잘못된 접근입니다" 메시지 출력
                        location.href = "/error"; // 에러 페이지로 강제 이동!
                    });
                }
            }).catch(err => console.error(err));
    }

    // 전역 변수로 선택된 타겟 정보 저장
    let targetElement = null;
    let targetGroupId = null;
    let targetMemberId = null;

    /* * 1. 등급 변경 버튼 클릭 시 모달 오픈 * 기존 함수명 유지 (JSP/JS 연결 호환성 위함) */
    function gmemberChangeLevel(target, groupId, memberId) {
        // 타겟 정보 저장
        targetElement = target;
        targetGroupId = groupId;
        targetMemberId = memberId;

        // 현재 회원의 등급을 가져와서 select box에 기본값으로 세팅하고 싶다면,
        // target(버튼)의 형제 요소나 data-속성 등을 통해 현재 등급 정보를 가져오는 로직 추가 가능
        // 예: document.getElementById('modalLevelSelect').value = currentLevel;

        // 모달 활성화 (group_common.css .groupJoinModal.active { display: flex; })
        document.getElementById('levelChangeModal').classList.add('active');
    }

    /* * 2. 모달 닫기 */
    function closeLevelModal() {
        document.getElementById('levelChangeModal').classList.remove('active');

        // 데이터 초기화 (선택사항)
        targetElement = null;
        targetGroupId = null;
        targetMemberId = null;
    }

    /* * 3. 변경사항 서버 전송 (AJAX) */
    function confirmLevelChange() {
        const selectedLevel = document.getElementById('modalLevelSelect').value;

        if (!targetGroupId || !targetMemberId) {
            alert("잘못된 접근입니다.");
            closeLevelModal();
            return;
        }

        let answer = "notOk";
        if(Number(selectedLevel) === 1) {
            if(confirm("그룹장으로의 변경은 나의 그룹장 권한을 위임하게 됩니다. 정말로 위임하시겠습니까?")) {
                answer = prompt(" 그룹장 위임을 원할시 'ok'를 입력해주세요.");
                if (answer !== "ok") {
                    alert("그룹장 위임이 취소되었습니다.");
                    return location.reload();
                }
            } else {
                return;
            }
        }

        fetch(`/group/glevelchange`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                groupId: targetGroupId,
                memberId: targetMemberId,
                role: selectedLevel, // DTO 필드명에 맞춰 수정 (role 또는 level)
                answer: answer
            })
        })
            .then(response => {
                if (response.ok) {
                    alert("등급이 변경되었습니다.");

                    // UI 새로고침 (간편하게 reload 하거나, DOM만 업데이트)
                    location.reload();

                    // 만약 DOM만 업데이트 하려면:
                    // updateMemberBadge(targetElement, selectedLevel); // 별도 UI 업데이트 함수 필요
                } else {
                    console.log(response);
                    return response.text().then(msg => {
                        alert(msg);
                        location.href = "/error";
                    });
                }
            })
            .catch(err => console.error(err))
            .finally(() => {
                closeLevelModal();
            });
    }
</script>
<script src="/js/groupjs/groupMemberScroll.js"></script>
<jsp:include page="../include/group_footer.jsp"></jsp:include>