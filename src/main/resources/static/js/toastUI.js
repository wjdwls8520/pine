
document.addEventListener('DOMContentLoaded', () => {
    const { Editor } = toastui;
  
    const editor = new Editor({
      el: document.querySelector('#editor'),
      height: '500px',
      initialEditType: 'wysiwyg',        // 필요하면 markdown 유지
      previewStyle: 'vertical',
      hideModeSwitch: true,              // 아래쪽 Markdown/WYSIWYG 탭 숨김
      // toolbarItems: [
      //   // 원하는 버튼만 나열 (예: 이미지/동영상 등은 제외)
      //   ['heading', 'bold', 'italic', 'strike'],
      //   ['hr', 'quote'],
      //   // ['ul', 'ol', 'task'],
      //   //['code', 'codeblock'],
      //   // ['table'],
      //   ['link'],
      //   ['undo', 'redo']
      // ],
      useCommandShortcut: true, //커멘트 단축키사용가능
      usageStatistics: false //사용통계스크립트 차단 개인정보/보안 지킬수있음
    });
  
    document.getElementById('commuCreateForm').addEventListener('submit', () => {
    document.getElementById('postBody').value = editor.getHTML();
    });
  });