window.addEventListener("load", () => {
    let page = 0;
    let totalPages = 10;
    let loading = false; // 요청 중인지 확인
    //  데이터 추가할 섹션
    let postWrap = document.getElementById("postList");

    const scrollBox = document.getElementById("commuMainPage");


    // 1. 로딩후 바로 ajax 요청
    getData(page);

    scrollBox.addEventListener("scroll", () => {
        const scrollTop = scrollBox.scrollTop;          // 현재 스크롤 위치
        const viewportHeight = scrollBox.clientHeight;  // 보이는 높이
        const totalHeight = scrollBox.scrollHeight;     // 전체 내용 높이

        if (scrollTop + viewportHeight >= totalHeight - 100 && !loading && (page < totalPages - 1)) {
            console.log("🔥 하단 감지됨! 🔥");

            page = page +1;

            //// 2.. 스크롤할때마다 바로 ajax 요청
            getData(page);
        }
    });


    // 에이잭스요청
    async function getData(page) {
        loading = true; // 요청 시작
        fetch(`/community/${page}`, {method: "GET"})
            .then(response => {
                if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                return response.json(); // 성공하면 JSON 반환
            })
            .then(data => {
                // 성공 시 처리
                console.log(data); // 서버에서 받은 값 콘솔

                // 토탈페이지 변경
                totalPages = data.post.totalPage;

                // @@@@  js로 동적 태그 생성  @@@@@
                data.post.postList.map((info) => {

                    // 포스트 반복문으로 생성
                    postWrap.insertAdjacentHTML("beforeend", `
                            <div class="postBox">
                                <div class="postInner">
                                    <div class="postWrap">
                                        <div class="postTop">
                                            <div class="postInfo">
                                                <div class="postProfileImgBox">
                                                    <img class="profileImg" src="/images/banner02.png" />
                                                </div>
                                                <div class="userNick">${info.userNick} ${info.id}</div>
                                                <div class="postTime relative-time">
                                                    ${timeAgoAjax(info.updateDate)}
                                                </div>
                                            </div>
                                            <div class="moreIcon">...</div>
                                        </div>
                                        <div class="postMiddle">
                                            <div class="postContent" onclick="location.href='/community/cdetail/${info.id}'">${info.content}</div>
                                            <div class="postHash">${info.hashTag || ""}</div>
                                                                                      
                                            <div class="swiper commuSlide">
                                                <div class="swiper-wrapper" id="postImg_${info.id}">

                                                </div>
                                                <div class="swiper-pagination"></div>
                                            </div>
                                            
                                        </div>
                                        <div class="postBottom">
                                            <div class="icoBox postLike">
                                                <span class="ico ico_like" onclick="toggleLike(${info.id}, this)"></span>
                                                <span id="likeCount_${info.id}">${info.likeCount}</span>
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

                    // 포스트안에있는 이미지를 반복문으로 생성
                    let postImg = document.getElementById(`postImg_${info.id}`);
                    info.file.map((a, idx) => {
                        postImg.insertAdjacentHTML("beforeend", `
                            <div class="swiper-slide"><img src="${a.path}"/></div>
                        `);
                    })

                });

                const swiper = new Swiper('.commuSlide', {
                    speed: 400,
                    direction: 'horizontal',
                    loop: false,
                    slidesPerView: 2.3,
                    spaceBetween: 20,

                    pagination: {
                        el: '.swiper-pagination',
                    },
                });

                // swiper.update();

            })
            .catch(err => {
                // 실패 시 처리
                console.error("데이터 로딩 실패:", err);
            })

            .finally(() => {
                loading = false; // 요청 종료
            });

        }
});

//@@@@@좋아요
function toggleLike(postId, el) {
    const countSpan = document.getElementById(`likeCount_${postId}`);
    let count = parseInt(countSpan.textContent);

    // 1. 토글 클래스
    const isActive = el.classList.toggle('active');

    // 2. UI 즉시 업데이트
    countSpan.textContent = isActive ? count + 1 : count - 1;

    // 3. 서버 요청
    fetch(`/community/likeCount/${postId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ like: isActive })
    })
        .then(res => res.json())
        .then(data => {
            // 서버에서 실제 좋아요 수 갱신
            countSpan.textContent = data.likeCount;
        })
        .catch(err => {
            console.error(err);
            // 실패 시 UI 되돌리기
            el.classList.toggle('active');
            countSpan.textContent = count;
        });
}










































        // try {
        //     const res = await fetch(`/community/${page}`); // 쿼리스트링으로 page 전송
        //     if (!res.ok) throw new Error(res.status);
        //
        //     const data = await res.json();
        //     // 내가 서버에서 가져온 데이터들
        //     console.log(data);
        //
        //     // 토탈페이지 수정
        //     totalPages = data.post.totalPages;
        //     console.log("토탈페이지 : "  + totalPages)
        //
        //     // 동적으로 태그 생성
        //     data.post.content.map((info, idx)=> {
        //         postWrap.insertAdjacentHTML("beforeend", `
        //             <div class="postBox" onclick="location.href='/community/cdetail/${info.id}'">
        //                 <div class="postInner">
        //                     <div class="postWrap">
        //                         <div class="postTop">
        //                             <div class="postInfo">
        //                                 <div class="postProfileImgBox">
        //                                     <img class="profileImg" src="/images/banner02.png" />
        //                                 </div>
        //                                 <div class="userNick">${info.userNick} ${info.id}</div>
        //                                 <div class="postTime relative-time">
        //                                     ${timeAgoAjax(info.updateDate)}
        //                                 </div>
        //                             </div>
        //                             <div class="moreIcon">...</div>
        //                         </div>
        //                         <div class="postMiddle">
        //                             <div class="postContent">${info.content}</div>
        //                             <div class="postHash">${info.hashTag || ""}</div>
        //                             <div class="postImgBox">
        //                                 <div class="postImg">${info.image || "이미지"}</div>
        //                             </div>
        //                         </div>
        //                         <div class="postBottom">
        //                             <div class="postLike">${info.likeCount}</div>
        //                             <div class="postReply">${info.replyCount}</div>
        //                             <div class="postlink">공유하기</div>
        //                         </div>
        //                     </div>
        //                 </div>
        //             </div>
        //         `);
        //
        //     })
        //
        // } catch (err) {
        //     console.error("데이터 로딩 실패:", err);
        // } finally {
        //     loading = false; // 요청 종료
        // }

