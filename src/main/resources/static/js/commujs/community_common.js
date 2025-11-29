window.addEventListener("load", () => {
    const mediaUploadInput = document.getElementById('mediaUploadInput');
    const mediaPreviewList = document.getElementById('mediaPreviewList');
    const mediaDropzone = document.getElementById('mediaDropzone');
    
    if (mediaUploadInput) {
        mediaUploadInput.addEventListener('change', fileUpload);
    }
    
    // 드롭존 클릭 시 파일 선택
    if (mediaDropzone) {
        mediaDropzone.addEventListener('click', () => {
            mediaUploadInput.click();
        });
    }
});

function fileUpload(e) {
    const files = e.target.files;
    console.log(files);
    const mediaPreviewList = document.getElementById('mediaPreviewList');
    
    if (!files || files.length === 0) return;
    
    // 기존 "첨부된 파일이 없습니다" 메시지 제거
    const emptyMsg = mediaPreviewList.querySelector('.mediaEmpty');
    if (emptyMsg) {
        emptyMsg.remove();
    }
    
    // 선택된 파일들을 미리보기로 추가
    Array.from(files).forEach((file, idx) => {
        const li = document.createElement('li');

        const reader = new FileReader();
        reader.onload = (e) => {
            li.innerHTML = `
                <div class="mediaInfo">
                    <div class="mediaThumb" style="background: #f1f5f9; padding: 0; overflow: hidden;">
                        <img src="${e.target.result}" alt="${file.name}" style="width: 100%; height: 100%; object-fit: cover;" />
                    </div>
                    <div class="mediaMeta">
                        <div class="mediaName">${file.name}</div>
                        <div class="mediaSize">${formatFileSize(file.size)}</div>
                    </div>
                </div>
                <button type="button" onclick="removeFile(this, ${idx})">삭제</button>
            `;
            mediaPreviewList.appendChild(li);
        };
        reader.readAsDataURL(file);
    });
}





function formatFileSize(bytes) {
    const kb = 1024;
    const mb = kb * 1024;

    if (bytes >= mb) {
        return (bytes / mb).toFixed(2) + " MB"; // MB 단위 (소수점 2자리)
    } else {
        return (bytes / kb).toFixed(2) + " KB"; // KB 단위
    }
}





