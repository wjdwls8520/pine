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

    if (files.size > 10000) {return alert("파일 용량이 1MB를 초과했습니다");}

    console.log(files);
    const mediaPreviewList = document.getElementById('mediaPreviewList');
    
   // if (!files || files.length === 0) return;
    
    // 기존 "첨부된 파일이 없습니다" 메시지 제거
    const emptyMsg = mediaPreviewList.querySelector('.mediaEmpty');
    if (emptyMsg) {
        emptyMsg.remove();
    }
    
    // 선택된 파일들을 미리보기로 추가
    Array.from(files).forEach((file, idx) => {
        
        if (file.size > 1_000_000) { // 1MB 초과
            alert(`파일 "${file.name}" 용량이 1MB를 초과했습니다.`);
            return; // 이 파일은 미리보기에 추가하지 않음
        }

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




//드래그엔드롭으로 파일첨부

const dropzone = document.getElementById('mediaDropzone');
const fileInput = document.getElementById('mediaUploadInput');
const previewList = document.getElementById('mediaPreviewList');
const mediaJsonInput = document.getElementById('mediaJsonInput');

dropzone.addEventListener('dragover', (e) => {
    e.preventDefault(); // 브라우저 기본 동작(링크 열기 등) 막기
    dropzone.classList.add('dragover'); // CSS로 강조 표시
});

dropzone.addEventListener('dragleave', (e) => {
    e.preventDefault();
    dropzone.classList.remove('dragover');
});

dropzone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropzone.classList.remove('dragover');

    const files = Array.from(e.dataTransfer.files); // 드롭된 파일 가져오기
    handleFiles(files);
});

function handleFiles(files) {
    files.forEach(file => {
        if (file.size > 1_000_000) { // 1MB 초과
            alert(`파일 "${file.name}" 용량이 1MB를 초과했습니다.`);
            return; // 이 파일은 미리보기에 추가하지 않음
        }

        if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
            alert('이미지나 영상만 업로드 가능합니다.');
            return;
        }

        // 미리보기 만들기
        const li = document.createElement('li');
        const fileURL = URL.createObjectURL(file);

        if (file.type.startsWith('image/')) {
            li.innerHTML = `<img src="${fileURL}" alt="${file.name}" />`;
        } else {
            li.innerHTML = `<video src="${fileURL}" controls></video>`;
        }

        previewList.appendChild(li);
    });

    // 실제 form 전송용 JSON 처리
    mediaJsonInput.value = JSON.stringify(files.map(f => ({name: f.name, type: f.type, size: f.size})));

    // 선택한 파일을 숨은 input에 넣기
    const dataTransfer = new DataTransfer();
    files.forEach(f => dataTransfer.items.add(f));
    fileInput.files = dataTransfer.files;
}






