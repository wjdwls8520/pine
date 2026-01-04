let page = 0;
let totalPages = 10;
let loading = false; // 요청 중인지 확인

const scrollBox = document.querySelector(".article.workspace.groupJoinListWrap");

//  데이터 추가할 섹션
let groupWrap = document.getElementById("groupJoinListContent");

// 에이잭스요청
async function getData(page) {
    loading = true; // 요청 시작
    fetch(`/group/gdetail/${groupId}/gjoinlist/${page}`, { method: "GET" })
        .then(response => {
                if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
                return response.json(); // 성공하면 JSON 반환
        })
        .then(data => {
                // 성공 시 처리
                console.log(data); // 서버에서 받은 값 콘솔

                // 그룹이 비어있을시
                if(data.content.length < 1) {
                    return groupWrap.insertAdjacentHTML("beforeend", `
                        <span>가입신청 요청이 없습니다.</span>
                    `);
                }

                // 토탈페이지 변경
                totalPages = data.totalPages;

                // js로 동적 태그 생성
                data.content.map((info, idx) => {
                    if(info.status == 0) {
                        groupWrap.insertAdjacentHTML("beforeend", `
                            <div class="groupJoinListItem">
                                <div class="groupJoinListItemAvatar">
                                    <div class="groupJoinListItemAvatarImg">
                                        ${ info.profileImg ?
                                            `<img src="${info.profileImg}" alt="프로필" style="width:100%; height:100%; border-radius:50%; object-fit: cover";>`:
                                            info.nickName.charAt(0)
                                        }
                                    </div>
                                </div>
                                <div class="groupJoinListItemInfo">
                                    <div class="groupJoinListItemTop">
                                        <h3 class="groupJoinListItemName">${info.nickName}</h3>
                                        <span class="groupJoinListItemBadge">
                                            신규 신청
                                        </span>
                                    </div>
                                    <p class="groupJoinListItemDesc">${info.introduction}</p>
                                    <div class="groupJoinListItemMeta">
                                        <span class="groupJoinListItemDate">${timeAgoAjax(info.requestDate)}</span>
                                        <span class="groupJoinListItemId">@${info.userId}</span>
                                    </div>
                                </div>
                                <div class="groupJoinListItemActions">
                                    <button type="button" class="groupJoinListBtn groupJoinListBtnApprove" onclick="groupJoinReq(this, 'APPROVE', ${info.groupId}, ${info.memberId});">승인</button>
                                    <button type="button" class="groupJoinListBtn groupJoinListBtnReject" onclick="groupJoinReq(this, 'REJECT', ${info.groupId}, ${info.memberId});">거절</button>
                                </div>
                            </div>
                        `);
                    }
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

// getAllData
function getAllData(targetTag) {
    groupWrap.innerHTML = ``;
    page = 0;
    getData(page);

    targetTag.parentElement.querySelectorAll(".tabTitle").forEach((tab)=> {tab.classList.remove("active");})
    targetTag.classList.add("active");
}

// getMyData
function getAllMyData(targetTag) {
    groupWrap.innerHTML = ``;
    page = 0;
    getData(page);

    targetTag.parentElement.querySelectorAll(".tabTitle").forEach((tab)=> {tab.classList.remove("active");})
    targetTag.classList.add("active");
}



window.addEventListener("load", () => {
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
});
    