let page = 0;
let totalPages = 10;
let loading = false; // 요청 중인지 확인

const scrollBox = document.querySelector(".article.workspace.groupMemberListWrap");

//  데이터 추가할 섹션
let groupWrap = document.getElementById("groupMemberListContent");

// 에이잭스요청
async function getData(page) {
    loading = true; // 요청 시작
    fetch(`/group/gdetail/${groupId}/gmemberlist/${page}`, { method: "GET" })
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
                        <span>멤버가 없습니다.</span>
                    `);
                }

                // 토탈페이지 변경
                totalPages = data.totalPages;

                // js로 동적 태그 생성
                data.content.forEach(info => {

                    const item = document.createElement("div");
                    item.className = "groupJoinListItem";

                    /* avatar */
                    const avatarWrap = document.createElement("div");
                    avatarWrap.className = "groupJoinListItemAvatar";

                    const avatarImgWrap = document.createElement("div");
                    avatarImgWrap.className = "groupJoinListItemAvatarImg";

                    if (info.profileImg) {
                        const img = document.createElement("img");
                        img.src = info.profileImg;   // src는 JS 할당이라 XSS 안전
                        img.alt = "프로필";
                        img.style.cssText = "width:100%;height:100%;border-radius:50%;object-fit:cover;";
                        avatarImgWrap.appendChild(img);
                    } else {
                        avatarImgWrap.textContent = info.nickname?.charAt(0) ?? "";
                    }

                    avatarWrap.appendChild(avatarImgWrap);

                    /* info */
                    const infoWrap = document.createElement("div");
                    infoWrap.className = "groupJoinListItemInfo";

                    const top = document.createElement("div");
                    top.className = "groupJoinListItemTop";

                    const name = document.createElement("h3");
                    name.className = "groupJoinListItemName";
                    name.textContent = info.nickname; // 🔐 XSS 차단

                    const badge = document.createElement("span");
                    badge.className = "groupJoinListItemBadge";
                    badge.textContent =
                        info.role === 1 ? "그룹장" :
                            info.role === 2 ? "그룹매니저" :
                                info.role === 3 ? "그룹일반" : "알 수 없음";

                    top.append(name, badge);

                    const desc = document.createElement("p");
                    desc.className = "groupJoinListItemDesc";
                    desc.textContent = info.profileMsg; // 🔐 핵심

                    const meta = document.createElement("div");
                    meta.className = "groupJoinListItemMeta";

                    const date = document.createElement("span");
                    date.className = "groupJoinListItemDate";
                    date.textContent = timeAgoAjax(info.joinDate);

                    meta.appendChild(date);
                    infoWrap.append(top, desc, meta);

                    if(groupRole === 1) {
                        /* actions */
                        const actions = document.createElement("div");
                        actions.className = "groupJoinListItemActions";

                        const approveBtn = document.createElement("button");
                        approveBtn.className = "groupJoinListBtn groupJoinListBtnApprove";
                        approveBtn.textContent = "등급 변경";
                        approveBtn.onclick = () =>
                            gmemberChangeLevel(approveBtn, info.groupId, info.memberId);

                        const rejectBtn = document.createElement("button");
                        rejectBtn.className = "groupJoinListBtn groupJoinListBtnReject";
                        rejectBtn.textContent = "그룹 추방";
                        rejectBtn.onclick = () =>
                            gmemberOut(rejectBtn, info.groupId, info.memberId);
                        actions.append(approveBtn, rejectBtn);
                        item.append(avatarWrap, infoWrap, actions);
                    } else {
                        item.append(avatarWrap, infoWrap);
                    }

                    groupWrap.appendChild(item);
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
    