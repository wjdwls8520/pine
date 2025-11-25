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
        return location.href='/group/gcreate';
    }

    function createGroup() {
        alert("해당 내용으로 그룹을 만드시겠습니까?");
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
                        <button type="submit" class="btnWH btnSubmit hidden" id="submitBtn" onclick="createGroup();">그룹 만들기</button>
                    </div>
                `;

                if (typeof window.handleGroupButtonsReady === "function") {
                    window.handleGroupButtonsReady();
                }
            }
        } else if (path === "/group/gupdate") {
            if (formActions) {
                formActions.innerHTML = `
                    <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnPrev" id="prevStepBtn" disabled>이전</button>
                        <button type="button" class="btnWH btnNext" id="nextStepBtn">다음</button>
                        <button type="submit" class="btnWH btnSubmit hidden" id="submitBtn" onclick="createGroup();">그룹 수정하기</button>
                    </div>
                `;

                if (typeof window.handleGroupButtonsReady === "function") {
                    window.handleGroupButtonsReady();
                }
            }
        } else if (path === "/group/detail") {
            if (formActions) {
                formActions.innerHTML = `
                    <div class="cancelButton"><button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button></div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnNext" id="" onclick="moveGcreate();">수정하기</button>
                    </div>
                `;
            }
        }
    });
</script>

