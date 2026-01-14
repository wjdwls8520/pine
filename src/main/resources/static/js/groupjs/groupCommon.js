// ==========================================
// 더보기 모달 및 버튼 (수정 삭제 신고 저장) 기능,
// 이미지 슬라이더 & 라이트박스 (공통)
// ==========================================

// 1. 더보기 아이콘 클릭 시 모달 토글
function toggleMoreModal(e, iconDiv) {
    e.stopPropagation(); // 부모 클릭 방지
    const currentModal = iconDiv.querySelector('.postMoreModal');

    // 혹시 열려있는 다른 모달이 있다면 닫기
    document.querySelectorAll('.postMoreModal.active').forEach(modal => {
        if (modal !== currentModal) modal.classList.remove('active');
    });

    if (currentModal) {
        currentModal.classList.toggle('active');
    }
}

// 2. 화면 아무 곳이나 클릭 시 모달 닫기
document.addEventListener('click', function() {
    document.querySelectorAll('.postMoreModal.active').forEach(modal => {
        modal.classList.remove('active');
    });
});

// 3. 기능 함수들 (수정, 삭제, 신고, 저장)
function goEdit(postId) {
    // 수정 페이지로 이동
    location.href = `/group/${groupId}/post/${postId}/edit`;
}

function deletePost(postId) {
    if(!confirm("정말로 삭제하시겠습니까? 복구할 수 없습니다.")) return;

    fetch(`/group/${groupId}/post/${postId}/delete`,{
        method: "DELETE",
    }).then(res => {
        if(res.ok) {
            alert("삭제되었습니다.");
            location.href = `/group/${groupId}/post/main`; // 삭제 후 목록으로 이동
        } else {
            alert("삭제 실패");
        }
    });
}

function doReport(postId) {
    alert(postId + "번 게시글을 신고합니다.");
}

function doSave(postId) {
    alert(postId + "번 게시글을 보관함에 저장했습니다.");
}


//링크복사
// https://ko.wikipedia.org/wiki/%EB%B3%B5%EC%82%AC
function copyPostUrl(groupId, postId) {
    // 1. 복사할 전체 주소 만들기
    // window.location.origin : http://localhost:8080 같은 도메인 앞부분
    const url = `${window.location.origin}/group/${groupId}/post/detail/${postId}`;

    // 2. 클립보드에 쓰기
    navigator.clipboard.writeText(url)
        .then(() => {
            // 성공 시 알림 (alert 대신 기존에 있던 toast를 쓰면 더 예쁨)
            showToastMsg("게시글 주소가 복사되었습니다");
        })
        .catch(err => {
            console.error('URL 복사 실패:', err);
            alert("주소 복사에 실패했습니다.");
        });
}



// ==========================================
// 5. 이미지 슬라이더 & 라이트박스 (공통)
// ==========================================

let modalSwiper = null;

/**
 * [초기화] 페이지에 있는 모든 작은 슬라이더를 찾아서 Swiper를 적용하고,
 * 클릭 시 모달을 띄우는 이벤트를 연결합니다.
 * 사용처: cDetail.jsp (window.onload), commuMain.js (fetch 이후)
 */
function initPostSliders() {
    // 1. 아직 초기화되지 않은(.swiper-initialized가 없는) 슬라이더만 찾음
    const sliders = document.querySelectorAll(".commuSlide:not(.swiper-initialized)");

    sliders.forEach(sliderEl => {
        // 1-1. 작은 슬라이더 생성
        new Swiper(sliderEl, {
            speed: 400,
            loop: false,
            slidesPerView: 2.3,
            spaceBetween: 20,
            pagination: {
                el: sliderEl.querySelector(".swiper-pagination"),
            },
        });

        // 1-2. 이미지 클릭 이벤트 연결 (동적 데이터 수집)
        const images = sliderEl.querySelectorAll(".swiper-slide img");
        // 해당 슬라이더(게시글)에 포함된 모든 이미지 URL을 배열로 만듦
        const imageUrls = Array.from(images).map(img => img.src);

        images.forEach((img, index) => {
            img.style.cursor = "pointer";
            img.addEventListener("click", () => {
                // 클릭 시: (이미지주소배열, 클릭한순서)를 가지고 모달 열기
                openDynamicModal(imageUrls, index);
            });
        });
    });
}

/**
 * [모달 열기] 클릭한 게시글의 이미지들로 모달 내용을 갈아끼우고 엽니다.
 */
function openDynamicModal(imageUrls, startIndex) {
    const modal = document.getElementById("imageModal");
    const wrapper = document.getElementById("modalWrapper");

    if (!modal || !wrapper) return;

    // 1. 기존 슬라이드 비우기
    wrapper.innerHTML = "";

    // 2. 새로운 이미지 슬라이드 채워넣기
    imageUrls.forEach(url => {
        const slideDiv = document.createElement("div");
        slideDiv.className = "swiper-slide modalSlideItem";
        slideDiv.innerHTML = `<img src="${url}" alt="Large Image" />`;
        wrapper.appendChild(slideDiv);
    });

    // 3. 모달 보여주기
    modal.style.display = "block";

    // 4. 모달 Swiper 초기화 (없으면 생성, 있으면 업데이트)
    if (!modalSwiper) {
        modalSwiper = new Swiper(".modalSwiper", {
            initialSlide: startIndex,
            spaceBetween: 10,
            observer: true,
            observeParents: true,
            navigation: {
                nextEl: ".swiper-button-next",
                prevEl: ".swiper-button-prev",
            },
            pagination: {
                el: ".modal-pagination", // footer에 지정한 클래스
                type: "fraction",
            },
        });
    } else {
        modalSwiper.update(); // 슬라이드 개수가 바뀌었으니 업데이트 필수
        modalSwiper.slideTo(startIndex, 0); // 클릭한 사진 위치로 이동
    }
}

/**
 * [모달 닫기]
 */
function closeImageModal() {
    const modal = document.getElementById("imageModal");
    if (modal) modal.style.display = "none";
}

// ESC 키 닫기
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeImageModal();
});
