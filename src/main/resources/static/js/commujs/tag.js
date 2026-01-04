const tagInput = document.getElementById('tagInput');
const tagList = document.getElementById('tagList');
const hiddenTags = document.getElementById('hiddenTags');

let tags = [];

// ========================================================
// 1. [추가] 페이지 로드 시 기존 태그 불러오기 (초기화)
// ========================================================
(function initTags() {
    const initialValue = hiddenTags.value;
    if (initialValue) {
        // 콤마로 쪼개서 배열로 만듦 (빈 값 제거)
        const savedTags = initialValue.split(',').filter(t => t.trim() !== '');

        savedTags.forEach(tag => {
            tags.push(tag);       // 배열에 담고
            createTagElement(tag); // 화면에 그림
        });
    }
})();


// ========================================================
// 2. 이벤트 리스너 (엔터 입력)
// ========================================================
tagInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault(); // 폼 전송 막기
        const value = tagInput.value.trim();

        // 값이 있고, 중복이 아닐 때만 추가
        if (value && !tags.includes(value)) {
            addTag(value);
            tagInput.value = '';
        } else if (tags.includes(value)) {
            alert("이미 등록된 태그입니다.");
            tagInput.value = '';
        }
    }
});


// ========================================================
// 3. 기능 함수들
// ========================================================

// 태그 추가 (데이터 + 화면)
function addTag(name) {
    tags.push(name);
    createTagElement(name);
    updateHiddenInput();
}

// 화면에 태그 UI 생성 (initTags와 addTag에서 공통 사용)
function createTagElement(name) {
    const tagItem = document.createElement('div');
    tagItem.className = 'tagItem';
    // 태그 이름과 삭제 버튼
    tagItem.innerHTML = `<span>#${name}</span> <button type="button" class="removeBtn">x</button>`;

    // 삭제 버튼에 이벤트 직접 연결 (onclick보다 안전)
    tagItem.querySelector('.removeBtn').addEventListener('click', function() {
        removeTag(tagItem, name);
    });

    tagList.appendChild(tagItem);
}

// 태그 삭제
function removeTag(tagElement, name) {
    // 배열에서 삭제
    const index = tags.indexOf(name);
    if (index > -1) {
        tags.splice(index, 1);
    }
    // 화면에서 삭제
    tagElement.remove();
    // input 업데이트
    updateHiddenInput();
}

// Hidden Input 업데이트 (서버 전송용)
function updateHiddenInput() {
    hiddenTags.value = tags.join(',');
}