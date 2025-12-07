let page = 0;
let totalPages = 10;
let loading = false; // 요청 중인지 확인
//  데이터 추가할 섹션
let shortsFeedWrap = document.getElementById("shortsFeed");

const scrollBox = document.getElementById("shortsFeed");


// 에이잭스요청
async function getData(page) {
    console.log("🔥 getData 호출, page =", page);
    if (loading) return;
    loading = true; // 요청 시작
    fetch(`/shorts/${page}`, { method: "GET" })
        .then(response => {
            if (!response.ok) throw new Error(`상태 코드: ${response.status}`);
            return response.json(); // 성공하면 JSON 반환
        })
        .then(data => {
            // 성공 시 처리
            console.log("서버 응답:", data); // 서버에서 받은 값 콘솔

            // 토탈페이지 변경
            totalPages = data.totalPage;

            // @@@@  js로 동적 태그 생성  @@@@@
            data.shortsList.map((info) => {

                // File 엔티티에서 영상 / 썸네일 분리
                let videoFile = info.files.find(f => f.contentType.includes("video"));
                let thumbnailFile = info.files.find(f => f.contentType.includes("image"));

                // 포스트 반복문으로 생성
                shortsFeedWrap.insertAdjacentHTML("beforeend", `
                       <div class="shortsCard" data-title="한복 입고 서울 야경 즐기기" data-user="pinedory">
                            <div class="cardInner">
                                <aside class="userPanel">
                                    <div class="userWrap">
                                        <div class="avatar">
                                            <img src="/images/icon_pinedory.png" alt="pinedory">
                                        </div>
                                        <div class="userMeta">
                                            <strong>${info.title}</strong>
                                            <p>${info.content}</p>
                                        </div>
                                    </div>
                                </aside>
        
                                <div class="videoShell">
                                    <video autoplay muted loop playsinline poster="${thumbnailFile ? thumbnailFile.path : ''}">
                                        <source src="${videoFile ? videoFile.path : ''}">
                                    </video>
                                </div>
        
                                <div class="actionPanel">
                                    <button class="actionBtn like">
                                        <span>좋아요</span>
                                        <em>2.1K</em>
                                    </button>
                                    <button class="actionBtn share">
                                        <span>공유</span>
                                        <em>128</em>
                                    </button>
                                    <button class="actionBtn commentToggle" data-target="commentsPanel" onclick="openComment();">
                                        <span>댓글</span>
                                        <em>356</em>
                                    </button>
                                </div>
                            </div>
                        </div>
                        
                `);

                // // 포스트안에있는 이미지를 반복분으로 생성
                // let postImg = document.getElementById(`postImg_${info.id}`);
                // info.file.map((a, idx)=> {
                //     postImg.insertAdjacentHTML("beforeend", `
                //
                //     `);
                // })

            });
            // @@@@@@@@@@@@@@@@@@@@@

        })
        .catch(err => {
            // 실패 시 처리
            console.error("데이터 로딩 실패:", err);
        })
        .finally(() => {
            loading = false; // 요청 종료
        });
}


window.addEventListener("load", () => {

    // 1. 로딩후 바로 ajax 요청
    getData(page);

    scrollBox.addEventListener("scroll", () => {
        const scrollTop = scrollBox.scrollTop;          // 현재 스크롤 위치
        const viewportHeight = scrollBox.clientHeight;  // 보이는 높이
        const totalHeight = scrollBox.scrollHeight;     // 전체 내용 높이

        if (scrollTop + viewportHeight >= totalHeight - 100 && !loading && (page < totalPages - 1)) {
            console.log(" 하단 감지됨! ");
            console.log("하단 감지됨! 현재 page =", page);  //  증가 전

            page = page +1;
            console.log("page 증가 후 =", page);
            //// 2.. 스크롤할때마다 바로 ajax 요청
            getData(page);
        }
    });
});





function openComment() {
    let popupComment = document.getElementById("commentsPanel");
    popupComment.classList.add("open");
}

function closeComment() {
    let popupComment = document.getElementById("commentsPanel");
    popupComment.classList.remove("open");
}