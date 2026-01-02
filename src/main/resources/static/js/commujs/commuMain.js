window.addEventListener("load", () => {
    let page = 0;
    let totalPages = 10;
    let loading = false;

    const postWrap = document.getElementById("postList");
    const scrollBox = document.getElementById("commuMainPage");

    // 최초 로딩
    getData(page);

    // 무한 스크롤
    scrollBox.addEventListener("scroll", () => {
        const scrollTop = scrollBox.scrollTop;
        const viewportHeight = scrollBox.clientHeight;
        const totalHeight = scrollBox.scrollHeight;

        if (
            scrollTop + viewportHeight >= totalHeight - 100 &&
            !loading &&
            page < totalPages - 1
        ) {
            page++;
            getData(page);
        }
    });

    // 데이터 요청
    function getData(page) {
        loading = true;

        fetch(`/community/${page}`)
            .then(res => {
                if (!res.ok) throw new Error(res.status);
                return res.json();
            })
            .then(data => {
                console.log(data);

                totalPages = data.totalPage;

                // 첫 페이지 + 글 없음
                if (page === 0 && (!data.postList || data.postList.length === 0)) {
                    postWrap.innerHTML = `
                        <div class="emptyPost">
                            작성된 포스트가 없습니다.
                        </div>
                    `;
                    return;
                }

                data.postList.forEach(info => {

                    const isMyPost = info.owner;

                    // 2. 버튼 HTML 생성
                    let modalHtml = '';

                    if (isMyPost) {
                        // 내 글: 수정, 삭제
                        modalHtml = `
                            <div class="postMoreModal">
                                <button class="menuItem" onclick="goEdit(${info.postId})">수정</button>
                                <button class="menuItem danger" onclick="deletePost(${info.postId})">삭제</button>
                            </div>
                        `;
                    } else {
                        // 남의 글: 신고, 저장
                        modalHtml = `
                            <div class="postMoreModal">
                                <button class="menuItem danger" onclick="doReport(${info.postId})">신고</button>
                                <button class="menuItem" onclick="doSave(${info.postId})">저장</button>
                            </div>
                        `;
                    }

                    postWrap.insertAdjacentHTML("beforeend", `
                        <div class="postBox">
                            <div class="postInner">
                                <div class="postWrap">
                                    <div class="postTop">
                                        <div class="postInfo">
                                            <div class="postProfileImgBox">
                                                <img class="profileImg" src="${info.profileImg || "/images/user.png"}" />
                                            </div>
                                            <div class="userNick">${info.nickname}</div>
                                            <div class="postTime">${timeAgoAjax(info.writeDate)}</div>
                                        </div>
                                        <div class="moreIcon" onclick="toggleMoreModal(event, this)">
                                            <span class="ico ico_more"></span>
                                            ${modalHtml}
                                        </div>
                                    </div>

                                    <div class="postMiddle">
                                        <div class="postContent" onclick="location.href='/community/cdetail/${info.postId}'">
                                            ${info.content}
                                        </div>
                                        <div class="tagZone">
                                            ${ info.tags && info.tags.length > 0 ? info.tags.map(tag => `<span class="tag">#${tag.name}</span>`).join("") : "" }
                                        </div>
                                        ${ info.files && info.files.length > 0 ? `<div class="swiper commuSlide"><div class="swiper-wrapper" id="postImg_${info.postId}"></div><div class="swiper-pagination"></div></div>` : `` }
                                    </div>

                                    <div class="postBottom">
                                        <div class="icoBox postLike">
                                            <span class="ico ico_like ${info.liked ? "active" : ""}" onclick="toggleLike('POST', ${info.postId}, this)"></span>
                                            <span id="likeCount_${info.postId}">${info.likeCount}</span>
                                        </div>
                                        <div class="icoBox postReply">
                                            <span class="ico ico_reply"></span>
                                            <span>${info.replyCount}</span>
                                        </div>
                                        <div class="icoBox postlink">
                                            <span class="ico ico_link"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `);

                    // 이미지 Swiper 초기화
                    if (info.files && info.files.length > 0) {
                        let postImg = document.getElementById(`postImg_${info.postId}`);
                        info.files.forEach(file => {
                            postImg.insertAdjacentHTML("beforeend", `<div class="swiper-slide"><img src="${file.path}" /></div>`);
                        });
                        new Swiper(postImg.closest(".commuSlide"), {
                            speed: 400, loop: false, slidesPerView: 2.3, spaceBetween: 20,
                            pagination: { el: postImg.closest(".commuSlide").querySelector(".swiper-pagination") },
                        });
                    }
                });
            })
            .catch(err => console.error("데이터 로딩 실패:", err))
            .finally(() => loading = false);
    }
});

// ==========================================
// [중요] HTML onclick에서 부르려면 window에 붙여야 함!
// ==========================================

// 1. 더보기 아이콘 클릭 시 모달 토글
window.toggleMoreModal = function(e, iconDiv) {
    e.stopPropagation(); // 이벤트 버블링 방지
    const currentModal = iconDiv.querySelector('.postMoreModal');

    // 다른 열린 모달 닫기
    document.querySelectorAll('.postMoreModal.active').forEach(modal => {
        if (modal !== currentModal) modal.classList.remove('active');
    });

    if (currentModal) currentModal.classList.toggle('active');
};

// 2. 화면 클릭 시 닫기
document.addEventListener('click', function() {
    document.querySelectorAll('.postMoreModal.active').forEach(modal => {
        modal.classList.remove('active');
    });
});

// 기능 함수들
window.goEdit = function(postId) {
    location.href = `/community/edit/${postId}`;
};

window.deletePost = function(postId) {
    if(!confirm("정말로 삭제하시겠습니까?")) return;
    fetch(`/community/${postId}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" }
    }).then(res => {
        if(res.ok) {
            alert("삭제되었습니다.");
            location.reload();
        } else {
            alert("삭제 실패");
        }
    });
};

window.doReport = function(postId) {
    alert(`${postId}번 게시글을 신고합니다.`);
};

window.doSave = function(postId) {
    alert(`${postId}번 게시글을 보관함에 저장했습니다.`);
};