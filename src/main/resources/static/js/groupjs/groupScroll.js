let page = 0;
let totalPages = 10;
let loading = false; // 요청 중인지 확인

const scrollBox = document.querySelector(".article.workspace.group");

//  데이터 추가할 섹션
let groupWrap = document.getElementById("groupBox");

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

                // 그룹이 비어있을시
                if(data.resDto.msg) {
                    return groupWrap.insertAdjacentHTML("beforeend", `
                        <span>그룹이 비어있습니다.</span>
                    `);
                }

                // 토탈페이지 변경
                totalPages = data.resDto.totalPage;

                // js로 동적 태그 생성
                data.resDto.groupList.forEach(info => {
                renderGroup(info);
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

function renderGroup(info) {
    const groupList = document.createElement("div");
    groupList.className = "groupList";

    // imgBox
    const imgBox = document.createElement("div");
    imgBox.className = "imgBox cursor";
    imgBox.addEventListener("click", () => {
        location.href = `/group/gdetail/${info.id}`;
    });

    const img = document.createElement("img");
    img.alt = "이미지";
    img.src = info.groupImg?.path || "/img/default.png";
    imgBox.appendChild(img);

    // groupInfo
    const groupInfo = document.createElement("div");
    groupInfo.className = "groupInfo";

    // top
    const top = document.createElement("div");
    top.className = "top";

    const groupName = document.createElement("div");
    groupName.className = "groupName cursor";
    groupName.textContent = info.groupName;
    groupName.addEventListener("click", () => {
        location.href = `/group/gdetail/${info.id}`;
    });

    top.appendChild(groupName);

    // mid
    const mid = document.createElement("div");
    mid.className = "mid";

    const description = document.createElement("span");
    description.className = "description";
    description.textContent = info.groupDescription;
    mid.appendChild(description);

    // bot
    const bot = document.createElement("div");
    bot.className = "bot";

    const left = document.createElement("div");
    left.className = "left";

    const viewCount = document.createElement("span");
    viewCount.className = "viewCount";
    viewCount.textContent = `조회수 ${info.allViewCount}`;

    const likeCount = document.createElement("span");
    likeCount.className = "likeCount";
    likeCount.textContent = `좋아요 ${info.likeCount}`;

    const replyCount = document.createElement("span");
    replyCount.className = "replyCount";
    replyCount.textContent = `그룹멤버 ${info.groupMemberCount}`;

    left.append(viewCount, likeCount, replyCount);

    const right = document.createElement("div");
    right.className = "right";

    const writeDate = document.createElement("span");
    writeDate.className = "writeDate";
    writeDate.textContent = timeAgoAjax(info.indate);

    right.appendChild(writeDate);

    bot.append(left, right);
    groupInfo.append(top, mid, bot);

    groupList.append(imgBox, groupInfo);
    groupWrap.appendChild(groupList);
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
    