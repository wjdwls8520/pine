<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/shorts.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article id="shortsMain" class="article workspace shorts">
        <section class="section section01">
            <div id="shorts-feed" class="shorts-feed">
                <div class="shorts-card" data-title="한복 입고 서울 야경 즐기기" data-user="pinedory">
                    <div class="card-inner">
                        <aside class="user-panel">
                            <div class="avatar">
                                <img src="/images/icon_pinedory.png" alt="pinedory">
                            </div>
                            <div class="user-meta">
                                <strong>파인데도리</strong>
                                <p>역사와 문화를 즐기는 짧은 브이로그</p>
                            </div>
                        </aside>

                        <div class="video-shell">
                            <video autoplay muted loop playsinline poster="/images/culture01.jpg">
                                <source src="/shorts/sample1.mp4" type="video/mp4">
                            </video>
                        </div>

                        <div class="action-panel">
                            <button class="action-btn like">
                                <span>좋아요</span>
                                <em>2.1K</em>
                            </button>
                            <button class="action-btn share">
                                <span>공유</span>
                                <em>128</em>
                            </button>
                            <button class="action-btn comment-toggle" data-target="commentsPanel">
                                <span>댓글</span>
                                <em>356</em>
                            </button>
                        </div>
                    </div>
                </div>

                <div class="shorts-card" data-title="세종대왕릉 산책 루틴" data-user="jang_g">
                    <div class="card-inner">
                        <aside class="user-panel">
                            <div class="avatar">
                                <img src="/images/king-sejong.png" alt="jang_g">
                            </div>
                            <div class="user-meta">
                                <strong>장군</strong>
                                <span>@jang_g</span>
                                <p>도심 속 힐링 스팟을 소개합니다</p>
                            </div>
                        </aside>

                        <div class="video-shell">
                            <video autoplay muted loop playsinline poster="/images/culture02.jpg">
                                <source src="/shorts/sample2.mp4" type="video/mp4">
                            </video>
                        </div>

                        <div class="action-panel">
                            <button class="action-btn like">
                                <span>좋아요</span>
                                <em>987</em>
                            </button>
                            <button class="action-btn share">
                                <span>공유</span>
                                <em>64</em>
                            </button>
                            <button class="action-btn comment-toggle" data-target="commentsPanel">
                                <span>댓글</span>
                                <em>98</em>
                            </button>
                        </div>
                    </div>
                </div>

                <div class="shorts-card" data-title="한옥마을 새벽 풍경" data-user="yeoni">
                    <div class="card-inner">
                        <aside class="user-panel">
                            <div class="avatar">
                                <img src="/images/sample.jpg" alt="yeoni">
                            </div>
                            <div class="user-meta">
                                <strong>여니</strong>
                                <span>@yeoni</span>
                                <p>감성 카메라로 담아낸 한국의 아침</p>
                            </div>
                        </aside>

                        <div class="video-shell">
                            <video autoplay muted loop playsinline poster="/images/culture03.jpg">
                                <source src="/shorts/sample3.mp4" type="video/mp4">
                            </video>
                        </div>

                        <div class="action-panel">
                            <button class="action-btn like">
                                <span>좋아요</span>
                                <em>1.4K</em>
                            </button>
                            <button class="action-btn share">
                                <span>공유</span>
                                <em>201</em>
                            </button>
                            <button class="action-btn comment-toggle" data-target="commentsPanel">
                                <span>댓글</span>
                                <em>512</em>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="comments-panel" id="commentsPanel">
                <div class="panel-header">
                    <div class="panel-title">
                        <strong class="comment-header-title">한복 입고 서울 야경 즐기기</strong>
                        <span class="comment-header-user">@pinedory</span>
                    </div>
                    <button class="close-btn" onclick="closeComments()">닫기</button>
                </div>
                <div class="panel-body">
                    <ul class="comment-list">
                        <li>
                            <b>@pine_member</b>
                            <p>영상미 대박... 다음에도 추천 부탁해요!</p>
                        </li>
                        <li>
                            <b>@culture_fan</b>
                            <p>밤에 보기 좋은 코스네요. 지도 공유 가능할까요?</p>
                        </li>
                        <li>
                            <b>@yeoni</b>
                            <p>영상 편집 감성 최고예요.</p>
                        </li>
                    </ul>
                    <div class="comment-input">
                        <input type="text" placeholder="댓글을 남겨보세요">
                        <button type="button">등록</button>
                    </div>
                </div>
            </div>
        </section>
    </article>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const commentButtons = document.querySelectorAll(".comment-toggle");
        const commentsPanel = document.getElementById("commentsPanel");
        const titleEl = commentsPanel.querySelector(".comment-header-title");
        const userEl = commentsPanel.querySelector(".comment-header-user");
        const shortsFeed = document.querySelector(".shorts-feed");
        const shortsCards = document.querySelectorAll(".shorts-card");

        // 댓글 토글 기능
        commentButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                const card = btn.closest(".shorts-card");
                titleEl.textContent = card?.dataset?.title || "쇼츠";
                userEl.textContent = card?.dataset?.user ? "@" + card.dataset.user : "";
                commentsPanel.classList.add("open");
            });
        });

        // 스크롤 스냅 기능 강화
        if (shortsFeed && shortsCards.length > 0) {
            let isScrolling = false;
            let scrollTimeout = null;

            // 휠 이벤트로 스냅 제어
            shortsFeed.addEventListener("wheel", function (e) {
                if (isScrolling) {
                    e.preventDefault();
                    return;
                }

                const delta = e.deltaY;
                const currentScrollTop = shortsFeed.scrollTop;
                const cardHeight = shortsFeed.clientHeight;
                const currentIndex = Math.round(currentScrollTop / cardHeight);

                let targetIndex = currentIndex;

                if (delta > 0) {
                    // 아래로 스크롤
                    targetIndex = Math.min(currentIndex + 1, shortsCards.length - 1);
                } else {
                    // 위로 스크롤
                    targetIndex = Math.max(currentIndex - 1, 0);
                }

                if (targetIndex !== currentIndex) {
                    e.preventDefault();
                    isScrolling = true;

                    const targetCard = shortsCards[targetIndex];
                    if (targetCard) {
                        targetCard.scrollIntoView({
                            behavior: "smooth",
                            block: "start"
                        });

                        setTimeout(function () {
                            isScrolling = false;
                        }, 500);
                    }
                }
            }, { passive: false });

            // 스크롤 이벤트로 자동 스냅 조정
            shortsFeed.addEventListener("scroll", function () {
                clearTimeout(scrollTimeout);

                scrollTimeout = setTimeout(function () {
                    if (!isScrolling) {
                        const scrollTop = shortsFeed.scrollTop;
                        const cardHeight = shortsFeed.clientHeight;
                        const currentIndex = Math.round(scrollTop / cardHeight);
                        const targetCard = shortsCards[currentIndex];

                        if (targetCard) {
                            const targetScrollTop = currentIndex * cardHeight;
                            const scrollDiff = Math.abs(scrollTop - targetScrollTop);

                            // 스냅 위치에서 벗어났을 경우 자동 조정
                            if (scrollDiff > 50) {
                                isScrolling = true;
                                targetCard.scrollIntoView({
                                    behavior: "smooth",
                                    block: "start"
                                });

                                setTimeout(function () {
                                    isScrolling = false;
                                }, 500);
                            }
                        }
                    }
                }, 100);
            });

            // 터치 이벤트 지원 (모바일)
            let touchStartY = 0;
            let touchEndY = 0;

            shortsFeed.addEventListener("touchstart", function (e) {
                touchStartY = e.touches[0].clientY;
            }, { passive: true });

            shortsFeed.addEventListener("touchend", function (e) {
                touchEndY = e.changedTouches[0].clientY;
                handleTouchSwipe();
            }, { passive: true });

            function handleTouchSwipe() {
                const swipeThreshold = 50;
                const diff = touchStartY - touchEndY;

                if (Math.abs(diff) > swipeThreshold && !isScrolling) {
                    const currentScrollTop = shortsFeed.scrollTop;
                    const cardHeight = shortsFeed.clientHeight;
                    const currentIndex = Math.round(currentScrollTop / cardHeight);

                    let targetIndex = currentIndex;

                    if (diff > 0) {
                        // 위로 스와이프 (다음 카드)
                        targetIndex = Math.min(currentIndex + 1, shortsCards.length - 1);
                    } else {
                        // 아래로 스와이프 (이전 카드)
                        targetIndex = Math.max(currentIndex - 1, 0);
                    }

                    if (targetIndex !== currentIndex) {
                        isScrolling = true;
                        const targetCard = shortsCards[targetIndex];

                        if (targetCard) {
                            targetCard.scrollIntoView({
                                behavior: "smooth",
                                block: "start"
                            });

                            setTimeout(function () {
                                isScrolling = false;
                            }, 500);
                        }
                    }
                }
            }
        }
    });

    function closeComments() {
        document.getElementById("commentsPanel").classList.remove("open");
    }

    document.body.addEventListener("click", function (e) {
        const commentsPanel = document.getElementById("commentsPanel");

        // 패널이 없거나 열려있지 않으면 무시
        if (!commentsPanel || !commentsPanel.classList.contains("open")) return;

        // 패널 외부 + 댓글 버튼이 아닐 때만 닫기
        if (
            !commentsPanel.contains(e.target) &&
            !e.target.closest(".comment-toggle")
        ) {
            // 부드럽게 닫히도록 살짝 딜레이
            setTimeout(() => {
                commentsPanel.classList.remove("open");
            }, 50);
        }
    });
</script>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/shortsjs/shorts.js"></script>
