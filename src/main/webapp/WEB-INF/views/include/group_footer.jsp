<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer id="footer" class="subFooter groupFooter">
    <div class="inner">
        <div class="formActions" id="formActions"></div>
    </div>
</footer>

<script>
    // 현재 경로 가져오기 → "/test/create"
    let path = window.location.pathname;
    let query = window.location.search;

    function testFnc() {
        console.log(query);

    }

    function moveGcreate() {
        if(!window.isLogin) { alert("로그인 이후 이용하실 수 있습니다."); return location.href='/login'; }
        return location.href='/group/gcreate';
    }

    function createGroup() {
        if(!window.isLogin) { alert("로그인 이후 이용하실 수 있습니다."); return location.href='/login'; }

        const checked = document.querySelectorAll('.categoryInput:checked').length;
        if (checked < 1 || checked > 5) {
            alert("카테고리는 최소 1개 이상 최대 5개 이하로 선택 해주셔야 합니다.");
            return;
        }

        const prevImg = document.getElementById("iconPreviewImg").getAttribute("src");
        if(!prevImg) {
            alert("그룹 대표이미지를 설정해주세요.");
            return;
        }

        if(confirm("해당 내용으로 그룹을 만드시겠습니까?")) {
            document.getElementById("groupCreateForm").submit();
        }
    }

    function updateGroup() {
        if(!window.isLogin) { alert("로그인 이후 이용하실 수 있습니다."); return location.href='/login'; }

        const checked = document.querySelectorAll('.categoryInput:checked').length;
        if (checked < 1 || checked > 5) {
            alert("카테고리는 최소 1개 이상 최대 5개 이하로 선택 해주셔야 합니다.");
            return;
        }

        if(confirm("해당 내용으로 그룹을 정보를 변경 하시겠습니까?")) {
            document.getElementById("groupCreateForm").submit();
        }

    }

    window.addEventListener("load", () => {

        // 메뉴가 들어갈 div
        const formActions = document.getElementById("formActions");

        // 1. 조건 비교
        if (path === "/group") {
            if (formActions) {
                formActions.innerHTML = `
                    <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnSubmit" id="" onclick="moveGcreate();">그룹 만들기</button>
                    </div>
                `;
            }
        } else if (path === "/group/gcreate") {
            if (formActions) {
                formActions.innerHTML = `
                    <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnPrev" id="prevStepBtn" disabled>이전</button>
                        <button type="button" class="btnWH btnNext" id="nextStepBtn">다음</button>
                        <button type="button" class="btnWH btnSubmit hidden" id="submitBtn" onclick="createGroup();">그룹 만들기</button>
                    </div>
                `;

                if (typeof window.handleGroupButtonsReady === "function") {
                    window.handleGroupButtonsReady();
                }
            }
        } else if (path.startsWith("/group/gupdate")) {
            if (formActions) {
                formActions.innerHTML = `
                    <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnPrev" id="prevStepBtn" disabled>이전</button>
                        <button type="button" class="btnWH btnNext" id="nextStepBtn">다음</button>
                        <button type="submit" class="btnWH btnSubmit hidden" id="submitBtn" onclick="updateGroup();">그룹 수정하기</button>
                    </div>
                `;

                if (typeof window.handleGroupButtonsReady === "function") {
                    window.handleGroupButtonsReady();
                }
            }
        } else if (path.startsWith("/group/gdetail")) {
            if (formActions) {
                if(window.groupRole && window.groupRole > 0) {
                    formActions.innerHTML = `
                        <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                        <div class="stepButtons">
                            <button type="button" class="btnWH btnNext" id="" onclick="">그룹 포스트 작성</button>
                        </div>
                    `;
                } else {
                    formActions.innerHTML = `
                        <div></div>
                        <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    `;
                }


                if (typeof window.handleGroupButtonsReady === "function") {
                    window.handleGroupButtonsReady();
                }
            }
        }
    });
</script>

