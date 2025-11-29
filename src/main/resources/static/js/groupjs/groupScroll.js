window.addEventListener("load", () => {
    let page = 0;
    let totalPages = 10;
    let loading = false; // 요청 중인지 확인
    //  데이터 추가할 섹션
    let groupWrap = document.getElementById("groupBox");

    const scrollBox = document.querySelector(".article.workspace.group");


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
        fetch(`/group/${page}`, { method: "GET" })
            .then(response => {
                    if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                    return response.json(); // 성공하면 JSON 반환
            })
            .then(data => {
                    // 성공 시 처리
                    console.log(data); // 서버에서 받은 값 콘솔

                    // 토탈페이지 변경
                    totalPages = data.resDto.totalPage.totalPages;

                    // js로 동적 태그 생성
                    data.resDto.groupList.map((info, idx) => {
                        groupWrap.insertAdjacentHTML("beforeend", `
                            <div class="groupList">
                                <div class="imgBox">
                                    <img src="${info.groupImg.path}" alt="이미지" />
                                </div>
                                <div class="groupInfo">
                                    <div class="top">
                                        <div class="tit">
                                            ${info.groupDescription}
                                        </div>
                                    </div>
                                    <div class="mid">
                                        <span class="groupName">${info.groupName}</span>
                                    </div>
                                    <div class="bot">
                                        <div class="left">
                                            <span class="viewCount">조회수 ${info.allViewCount}</span>
                                            <span class="likeCount">좋아요 ${info.likeCount}</span>
                                            <span class="replyCount">그룹멤버 ${info.groupMemberCount}</span>
                                        </div>
                                        <div class="right">
                                            
                                            
                                            <span class="writeDate">${timeAgoAjax(info.indate)}</span>
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
    }
});
