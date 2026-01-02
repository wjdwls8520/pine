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

                    postWrap.insertAdjacentHTML("beforeend", `
                        <div class="postBox">
                            <div class="postInner">
                                <div class="postWrap">

                                    <div class="postTop">
                                        <div class="postInfo">
                                            <div class="postProfileImgBox">
                                                <img class="profileImg"
                                                     src="${info.profileImg || "/images/user.png"}" />
                                            </div>
                                            <div class="userNick">${info.nickname}</div>
                                            <div class="postTime">
                                                ${timeAgoAjax(info.writeDate)}
                                            </div>
                                        </div>
                                        <div class="moreIcon"><span class="ico ico_more"></span></div>
                                    </div>

                                    <div class="postMiddle">
                                        <div class="postContent"
                                             onclick="location.href='/community/cdetail/${info.postId}'">
                                            ${info.content}
                                        </div>
                                        <div class="tagZone">
                                            ${
                                                info.tags && info.tags.length > 0
                                                    ? info.tags.map(tag =>
                                                        `<span class="tag">#${tag.name}</span>`
                                                    ).join("")
                                                    : ""
                                            }
                                        </div>
                                        ${
                                            info.files && info.files.length > 0 
                                            ? `<div class="swiper commuSlide">
                                                    <div class="swiper-wrapper" id="postImg_${info.postId}"></div>
                                                    <div class="swiper-pagination"></div>
                                                </div>` 
                                            : ``
                                        }
                                        
                                    </div>

                                    <div class="postBottom">
                                        <div class="icoBox postLike">
                                            <span class="ico ico_like ${info.liked ? "active" : ""}" onclick="toggleLike('POST', ${info.postId}, this)"></span>
                                            <span id="likeCount_${info.postId}">
                                                ${info.likeCount}
                                            </span>
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

                    // 🔥 이미지가 있을 때만 실행
                    if (info.files && info.files.length > 0) {

                        // 1️⃣ swiper-wrapper 찾기
                        let postImg = document.getElementById(`postImg_${info.postId}`);

                        // 2️⃣ 이미지 하나씩 추가
                        info.files.forEach(file => {
                            postImg.insertAdjacentHTML("beforeend", `
                                <div class="swiper-slide">
                                    <img src="${file.path}" />
                                </div>
                            `);
                        });

                        // 🔥 여기서 초기화 (이 포스트의 swiper만)
                        new Swiper(postImg.closest(".commuSlide"), {
                            speed: 400,
                            loop: false,
                            slidesPerView: 2.3,
                            spaceBetween: 20,
                            pagination: {
                                el: postImg.closest(".commuSlide").querySelector(".swiper-pagination"),
                            },
                        });

                    }

                });

            })
            .catch(err => {
                console.error("데이터 로딩 실패:", err);
            })
            .finally(() => {
                loading = false;
            });
    }
});
