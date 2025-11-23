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
        if (path === "/community") {
            menu.innerHTML = `
                <li><a href="/community/ccreate">글쓰기</a></li>
                <li><a>맨위로</a></li>
            `;
        } else if (path === "/community/ccreate") {
            menu.innerHTML = `
                <li><a href="/community">목록으로</a></li>
                <li><a>맨위로</a></li>
            `;
        }
    });
</script>

