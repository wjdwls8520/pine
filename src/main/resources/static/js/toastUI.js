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

    document.getElementById('commuCreateForm').addEventListener('submit', () => {
        document.getElementById('postBody').value = editor.getHTML();
    });
});
