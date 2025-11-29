window.addEventListener("load", () => {
    let page = 0;
    let totalPages = 10;
    let loading = false; // 요청 중인지 확인
    //  데이터 추가할 섹션
    let postWrap = document.getElementById("postList");

    const scrollBox = document.querySelector(".article.workspace.commu");


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
        fetch(`/community/${page}`, { method: "GET" })
            .then(response => {
                    if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                    return response.json(); // 성공하면 JSON 반환
            })
            .then(data => {
                    // 성공 시 처리
                    console.log(data); // 서버에서 받은 값 콘솔

                    // 토탈페이지 변경
                    totalPages = data.post.totalPages;

                    // js로 동적 태그 생성
                    data.post.content.map(info => {
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
                                            <div class="postImgBox" onclick="location.href='/community/cdetail/${info.id}'">
                                                <div class="postImg">${info.image || "이미지"}</div>
                                            </div>
                                        </div>
                                        <div class="postBottom">
                                            <div class="postLike">${info.likeCount}</div>
                                            <div class="postReply">${info.replyCount}</div>
                                            <div class="postlink">공유하기</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `);
                    });
            })
            .catch(err => {
                // 실패 시 처리
                console.error("데이터 로딩 실패:", err);
            })
            .finally(() => {
                loading = false; // 요청 종료
            });

















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
    }
});
