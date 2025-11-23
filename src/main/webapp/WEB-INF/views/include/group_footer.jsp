<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer id="footer" class="subFooter">
    <div class="inner">
        <ul id="bindingTag">
            <li><a href="/group/create">글쓰기</a></li>
            <li><a href="#">맨위로</a></li>
        </ul>
    </div>
</footer>

<script>
    window.addEventListener("load", () => {
        // 현재 경로 가져오기 → "/test/create"
        let path = window.location.pathname;
        const query = window.location.search;

        // 메뉴가 들어갈 div
        const menu = document.getElementById("bindingTag");

        // 1. 조건 비교
        if (path === "/group") {
            menu.innerHTML = `
                <li><a href="/group/create">글쓰기</a></li>
                <li><a href="#">맨위로</a></li>
            `;
        } else if (path === "/group/create") {
            menu.innerHTML = `
                <li><a href="#" onclick="testFnc(query)">등록하기</a></li>
                <li><a href="/group">취소</a></li>
            `;
        } else if (path === "/group/update") {
            menu.innerHTML = `
                <li><a href="#" onclick="testFnc(query)">수정 완료</a></li>
                <li><a href="/group/detail${query}">취소</a></li>
                <li><a href="#" onclick="testFnc(query)">삭제</a></li>
            `;
        } else if (path === "/group/detail") {
            menu.innerHTML = `
                <li><a href="/group/update${query}">수정 하기</a></li>
                <li><a href="/group">취소</a></li>
            `;
        }
    });
</script>

