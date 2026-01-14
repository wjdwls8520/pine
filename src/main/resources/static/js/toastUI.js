window.addEventListener("load", () => {
    const Editor = toastui.Editor;

    const MAX_LENGTH = 20000; // 최대 글자 수 설정

    const editor = new Editor({
        el: document.querySelector('#editor'),
        height: '500px',
        initialEditType: 'wysiwyg',  // WYSIWYG 모드만 사용
        hideModeSwitch: true, //마크다운 WYSIWYG 모드스위치 끔.
        toolbarItems: [
            ['heading', 'bold', 'italic', 'strike'],
            ['hr', 'quote'],
        ],

        usageStatistics: false, //익명 통계 수집 비활성화

        events: {
            change: function() {
                // 1. 내용 가져오기
                const content = editor.getHTML();
                const len = content.length;

                // ============================================================
                // [에러 원인 해결] 변수를 여기서 확실하게 선언해줘야 합니다!
                // ============================================================
                const currentLenSpan = document.getElementById('currentLen');
                const maxLenSpan = document.getElementById('maxLen');
                const countWrap = document.getElementById('charCountWrap');
                const MAX_LENGTH = 20000; // 혹시 몰라서 여기도 상수로 박아둡니다.

                // 2. 숫자 업데이트
                if(currentLenSpan) {
                    currentLenSpan.innerText = len.toLocaleString();
                }

                // 3. 색상 변경 로직 (부모 div + 숫자 span 둘 다 변경)
                if (countWrap && currentLenSpan && maxLenSpan) {
                    if (len > MAX_LENGTH) {
                        // (빨강) 초과 시
                        countWrap.style.color = 'red';
                        countWrap.style.fontWeight = 'bold';

                        currentLenSpan.style.color = 'red';
                        maxLenSpan.style.color = 'red';
                    } else {
                        // (회색) 정상 시 - 원래대로 복구
                        countWrap.style.color = '#666';
                        countWrap.style.fontWeight = 'normal';

                        currentLenSpan.style.color = '#666';
                        maxLenSpan.style.color = '#666';
                    }
                }
            }
        }

    });

    window.editor = editor;

    // ========================================================
    // 🔥 [추가된 부분] 에디터 생성 직후에 값 넣어주는 코드
    // ========================================================
    // 1. JSP에서 hidden textarea에 넣어둔 값 가져오기
    const savedContent = document.getElementById('postBody').value;

    // 2. 값이 있다면 에디터 화면에 HTML로 그려주기
    if (savedContent && savedContent.trim() !== "") {
        editor.setHTML(savedContent);
    }
    // ========================================================


    // (기존 코드 유지) 폼 제출 시 에디터 내용을 textarea로 옮기기
    // 주의: 이건 '작성(Create)' 모드일 때만 동작합니다.
    const form = document.getElementById('commuCreateForm');
    if (form) {
        form.addEventListener('submit', () => {
            document.getElementById('postBody').value = editor.getHTML();
        });
    }

    // document.getElementById('commuCreateForm').addEventListener('submit', () => {
    //     document.getElementById('postBody').value = editor.getHTML();
    // });
});
