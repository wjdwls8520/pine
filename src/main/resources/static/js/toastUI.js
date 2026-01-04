window.addEventListener("load", () => {
    const Editor = toastui.Editor;

    const editor = new Editor({
        el: document.querySelector('#editor'),
        height: '500px',
        initialEditType: 'wysiwyg',  // WYSIWYG 모드만 사용
        hideModeSwitch: true, //마크다운 WYSIWYG 모드스위치 끔.
        toolbarItems: [
            ['heading', 'bold', 'italic', 'strike'],
            ['hr', 'quote'],
        ],

        usageStatistics: false //익명 통계 수집 비활성화
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
