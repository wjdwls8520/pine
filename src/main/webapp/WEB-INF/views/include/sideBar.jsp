<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="lnb">
    <ul class="menu">

        <li>
            <a href="/">
                <div class="iconBox"><img src="/images/icon_pinedory.png" alt="home" /></div>
                <span class="tit">Home</span>
            </a>
        </li>
        <li>
            <a href="/shorts">
                <div class="iconBox"><img src="/images/kpop.png" alt="Shorts" /></div>
                <span class="tit">Shorts</span>
            </a>
        </li>

        <li class="line"></li>

        <%--<li>
            <a href="#">
                <div class="iconBox"><img src="/images/ico_news.png" alt="새소식" /></div>
                <span class="tit">News</span>
            </a>
        </li>--%>

        <li>
            <a href="/group">
                <div class="iconBox"><img src="/images/ico_group.png" alt="그룹" /></div>
                <span class="tit">Group</span>
            </a>
        </li>

        <li class="hasSub ${not empty param.category ? 'active open' : ''}" id="communityMenu">

            <a href="#" onclick="toggleSubMenu(this); return false;">
                <div class="iconBox"><img src="/images/ico_chat.png" alt="커뮤니티" /></div>
                <span class="tit">Community</span>
                <span class="arrow"></span>
            </a>

            <ul class="subMenu">
                <li><a href="/community" class="${param.category == null ? 'current' : ''}">All</a></li>
                <li><a href="/community?category=1" class="${param.category == '1' ? 'current' : ''}">General</a></li>
                <li><a href="/community?category=2" class="${param.category == '2' ? 'current' : ''}">Travel</a></li>
                <li><a href="/community?category=3" class="${param.category == '3' ? 'current' : ''}">K-POP</a></li>
                <li><a href="/community?category=4" class="${param.category == '4' ? 'current' : ''}">Trend</a></li>
                <li><a href="/community?category=5" class="${param.category == '5' ? 'current' : ''}">Game</a></li>
                <li><a href="/community?category=6" class="${param.category == '6' ? 'current' : ''}">Ask</a></li>
            </ul>
        </li>

        <li>
            <a href="#">
                <div class="iconBox"><img src="/images/ico_god.png" alt="문화" /></div>
                <span class="tit">Culture</span>
            </a>
        </li>
        <li>
            <a href="#">
                <div class="iconBox"><img src="/images/ico_talk.png" alt="PineTalk" /></div>
                <span class="tit">PineTalk</span>
            </a>
        </li>

    </ul>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        // 1. 현재 URL 감지하여 일반 메뉴 Active (이건 유지)
        const currentPath = window.location.pathname;
        const menuLinks = document.querySelectorAll('.menu > li > a');

        menuLinks.forEach(link => {
            const linkPath = link.getAttribute('href');
            if (linkPath !== '#' && linkPath !== null) {
                if (linkPath === '/' && currentPath === '/') {
                    link.parentElement.classList.add('active');
                } else if (linkPath !== '/' && currentPath.startsWith(linkPath)) {
                    link.parentElement.classList.add('active');
                }
            }
        });

        // [삭제함] 2. 커뮤니티 메뉴 강제 오픈 로직
        // JSP에서 이미 open 클래스를 줬기 때문에, 자바스크립트에서 또 열라고 하면
        // 오히려 애니메이션이 꼬일 수 있으므로 이 부분은 지워주세요.
    });

    // 3. 토글 함수 (유지)
    function toggleSubMenu(element) {
        const parentLi = element.parentElement;
        parentLi.classList.toggle('open');
    }
</script>
