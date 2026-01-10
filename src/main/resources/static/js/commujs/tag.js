window.addEventListener("load", () => {
    const tagInput = document.getElementById("tagInput");
    const tagList = document.getElementById("tagList");
    const hiddenTags = document.getElementById("hiddenTags");

    // 1. 태그를 관리할 Set (중복 방지)
    let tags = new Set();

    // ============================================================
    // 🔥 [핵심 수정] 페이지 로드 시, 기존 태그 값을 읽어서 Set에 넣기
    // ============================================================
    if (hiddenTags && hiddenTags.value) {
        const existingTags = hiddenTags.value.split(",");
        existingTags.forEach(tag => {
            const trimmedTag = tag.trim();
            if (trimmedTag !== "") {
                tags.add(trimmedTag); // Set에 저장
                addTagElement(trimmedTag); // 화면에 그리기
            }
        });
    }
    // ============================================================

    // 2. 태그 입력 이벤트 (Enter 키)
    if (tagInput) {
        tagInput.addEventListener("keydown", (e) => {

            // 한글 조합(IME) 중일 때 중복 입력 방지
            // e.isComposing: 현재 글자가 조합 중이면 true (예: '한'을 치고 엔터를 누르는 순간)
            if (e.isComposing) return;

            if (e.key === "Enter") {
                e.preventDefault(); // 폼 제출 방지
                const value = e.target.value.trim();

                if (value && !tags.has(value)) {
                    // 태그 갯수 제한 (선택사항)
                    if (tags.size >= 5) {
                        alert("태그는 최대 5개까지 입력 가능합니다.");
                        return;
                    }

                    tags.add(value);      // Set에 추가
                    addTagElement(value); // 화면에 추가
                    updateHiddenInput();  // Hidden Input 업데이트
                    e.target.value = "";  // 입력창 초기화
                } else if (tags.has(value)) {
                    alert("이미 입력된 태그입니다.");
                    e.target.value = "";
                }
            }
        });
    }

    // 3. 태그 화면 생성 함수
    function addTagElement(text) {
        const tagItem = document.createElement("div");
        tagItem.classList.add("tagItem");
        tagItem.innerHTML = `
            <span>#${text}</span>
            <button type="button" class="delBtn">x</button>
        `;

        // 삭제 버튼 이벤트
        tagItem.querySelector(".delBtn").addEventListener("click", () => {
            tags.delete(text);     // Set에서 삭제
            tagItem.remove();      // 화면에서 삭제
            updateHiddenInput();   // Hidden Input 업데이트
        });

        tagList.appendChild(tagItem);
    }

    // 4. Hidden Input 값 업데이트 (서버 전송용)
    function updateHiddenInput() {
        // Set을 배열로 바꾸고 콤마로 합침
        hiddenTags.value = Array.from(tags).join(",");
    }
});