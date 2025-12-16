window.addEventListener("load", () => {
    const fileInput = document.getElementById('mediaUploadInput');
    const mediaSlider = document.getElementById('mediaSlider');
    const dropzoneBtn = document.getElementById('dropzoneSelectBtn');
    const mediaJsonInput = document.getElementById('mediaJsonInput');

    // 드래그존 클릭 -> 파일 선택
    dropzoneBtn.addEventListener('click', () => fileInput.click());

    // 파일 input change
    fileInput.addEventListener('change', (e) => handleFiles(Array.from(e.target.files)));

    // 파일 드래그 앤 드롭
    const dropzone = document.getElementById('mediaDropzone');
    dropzone.addEventListener('dragover', (e) => { e.preventDefault(); dropzone.classList.add('dragover'); });
    dropzone.addEventListener('dragleave', (e) => { e.preventDefault(); dropzone.classList.remove('dragover'); });
    dropzone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropzone.classList.remove('dragover');
        handleFiles(Array.from(e.dataTransfer.files));
    });

    function handleFiles(files) {
        if (!files || files.length === 0) return;

        const mediaSlider = document.getElementById('mediaSlider');
        const currentCount = mediaSlider.querySelectorAll('.mediaItem').length;

        if (currentCount + files.length > 10) {
            alert("최대 10개의 파일만 업로드할 수 있습니다.");
            return;
        }

        files.forEach(file => {
            if (file.size > 50_000_000) return alert(`파일 "${file.name}" 용량이 50MB를 초과했습니다.`);
            if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) return alert('이미지나 영상만 업로드 가능합니다.');

            const item = document.createElement('div');
            item.classList.add('mediaItem');
            item.fileRef = file;

            if (file.type.startsWith('image/')) {
                const img = document.createElement('img');
                img.src = URL.createObjectURL(file);
                img.alt = file.name;
                img.classList.add('mediaThumb'); // CSS에서 크기, object-fit 처리
                item.appendChild(img);
            } else {
                const video = document.createElement('video');
                video.src = URL.createObjectURL(file);
                video.controls = true;
                video.classList.add('mediaThumb');
                item.appendChild(video);
            }

            const removeBtn = document.createElement('button');
            removeBtn.type = "button";
            removeBtn.textContent = "삭제";
            removeBtn.classList.add('mediaRemoveBtn');
            removeBtn.addEventListener('click', () => {
                item.remove();
                updateFileInputOrder();
            });

            item.appendChild(removeBtn);
            mediaSlider.appendChild(item);
        });

        updateFileInputOrder();
    }

    // Sortable 초기화
    Sortable.create(mediaSlider, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        onEnd: () => updateFileInputOrder()
    });

    function updateFileInputOrder() {
        const dataTransfer = new DataTransfer();
        const files = [];

        mediaSlider.querySelectorAll('.mediaItem').forEach(item => {
            if (item.fileRef) {
                files.push(item.fileRef);
                dataTransfer.items.add(item.fileRef);
            }
        });

        fileInput.files = dataTransfer.files;
        mediaJsonInput.value = JSON.stringify(files.map(f => ({
            name: f.name,
            size: f.size,
            type: f.type
        })));
    }
});
