let page = 0;
let totalPages = 10;
let loading = false; // 요청 중인지 확인
//  데이터 추가할 섹션
let shortsFeedWrap = document.getElementById("shorts-feed");

const scrollBox = document.getElementById("shortsMain");


// 에이잭스요청
async function getData(page) {
    loading = true; // 요청 시작
    fetch(`/shorts/${page}`, { method: "GET" })
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
                shortsFeedWrap.insertAdjacentHTML("beforeend", `
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
                `);

                // 포스트안에있는 이미지를 반복분으로 생성
                let postImg = document.getElementById(`postImg_${info.id}`);
                info.file.map((a, idx)=> {
                    postImg.insertAdjacentHTML("beforeend", `
                                
                    `);
                })

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
            console.log("🔥 하단 감지됨! 🔥");

            page = page +1;

            //// 2.. 스크롤할때마다 바로 ajax 요청
            getData(page);
        }
    });
});