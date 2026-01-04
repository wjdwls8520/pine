// ==========================================
// 더보기 모달 및 버튼 (수정 삭제 신고 저장) 기능
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
    location.href = `/community/edit/${postId}`;
}

function deletePost(postId) {
    if(!confirm("정말로 삭제하시겠습니까? 복구할 수 없습니다.")) return;

    fetch(`/community/${postId}`,{
        method: "DELETE",
    }).then(res => {
        if(res.ok) {
            alert("삭제되었습니다.");
            location.href = '/community'; // 삭제 후 목록으로 이동
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
function copyPostUrl(postId) {
    // 1. 복사할 전체 주소 만들기
    // window.location.origin : http://localhost:8080 같은 도메인 앞부분
    const url = `${window.location.origin}/community/cdetail/${postId}`;

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
