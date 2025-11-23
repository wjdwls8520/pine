<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer id="footer" class="subFooter">
    <div class="inner">
        <ul id="bindingTag">
        </ul>
    </div>
</footer>

<script>
    // 현재 경로 가져오기 → "/test/create"
    let path = window.location.pathname;
    let query = window.location.search;

    function testFnc() {
        console.log(query);

    }

    window.addEventListener("load", () => {

        // 메뉴가 들어갈 div
        const menu = document.getElementById("bindingTag");

        // 1. 조건 비교
        if (path === "/group") {
            menu.innerHTML = `
                <li><a href="/group/create">글쓰기</a></li>
                <li><a>맨위로</a></li>
            `;
        } else if (path === "/group/gcreate") {
            menu.innerHTML = `
                <li><a onclick="testFnc(); return false;">등록하기</a></li>
                <li><a href="/group">취소</a></li>
            `;
        } else if (path === "/group/gupdate") {
            menu.innerHTML = `
                <li><a onclick="testFnc(); return false;">수정 완료</a></li>
                <li><a onclick="testFnc(); return false">취소</a></li>
                <li><a onclick="testFnc(); return false;">삭제</a></li>
            `;
        } else if (path === "/group/detail") {
            menu.innerHTML = `
                <li><a onclick="testFnc(); return false">수정 하기</a></li>
                <li><a href="/group">취소</a></li>
            `;
        }
    });
</script>

