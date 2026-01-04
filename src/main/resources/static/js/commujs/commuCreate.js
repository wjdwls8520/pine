window.addEventListener("load", () => {

    // ==========================================
    // 1. 변수 설정
    // ==========================================
    const mode = document.getElementById('mode').value;
    const postId = document.getElementById('postId').value;
    const postBody = document.getElementById('postBody'); // 숨겨진 textarea

    // 파일 관련 변수
    const fileInput = document.getElementById('mediaUploadInput');
    const mediaSlider = document.getElementById('mediaSlider');
    const dropzoneBtn = document.getElementById('dropzoneSelectBtn');
    const mediaJsonInput = document.getElementById('mediaJsonInput');
    const dropzone = document.getElementById('mediaDropzone');

    // 기존 파일 관리용 변수
    const serverFileInput = document.getElementById('serverFileList');
    let deleteFileIds = [];


    // ==========================================
    // 2. 파일 업로드 및 관리 로직
    // ==========================================

    // 2-1. [초기화] 서버에서 가져온 기존 파일 그리기
    if (serverFileInput && serverFileInput.value) {
        try {
            // JSTL로 만든 JSON 문자열이 가끔 공백 등으로 깨질 수 있어 예외처리
            const jsonStr = serverFileInput.value.trim();
            if(jsonStr) {
                const serverFiles = JSON.parse(jsonStr);
                serverFiles.forEach(file => {
                    renderServerFile(file);
                });
            }
        } catch (e) {
            console.error("파일 JSON 파싱 실패:", e);
        }
    }

    // 서버 파일 렌더링 함수
    function renderServerFile(file) {
        const item = document.createElement('div');
        item.classList.add('mediaItem');
        item.dataset.id = file.id; // 중요: 기존 파일 ID 저장

        // 이미지/비디오 구분
        if (file.type && file.type.startsWith('video')) {
            const video = document.createElement('video');
            video.src = file.path;
            video.controls = true;
            video.classList.add('mediaThumb');
            item.appendChild(video);
        } else {
            const img = document.createElement('img');
            img.src = file.path;
            img.alt = file.name;
            img.classList.add('mediaThumb');
            item.appendChild(img);
        }

        // 삭제 버튼
        const removeBtn = document.createElement('button');
        removeBtn.type = "button";
        removeBtn.textContent = "삭제";
        removeBtn.classList.add('mediaRemoveBtn');

        removeBtn.addEventListener('click', () => {
            // 1. 삭제할 ID 목록에 추가
            deleteFileIds.push(file.id);
            // 2. 화면에서 제거
            item.remove();
        });

        item.appendChild(removeBtn);
        mediaSlider.appendChild(item);
    }

    // 2-2. 새 파일 추가 로직
    if(dropzoneBtn) dropzoneBtn.addEventListener('click', () => fileInput.click());
    if(fileInput) fileInput.addEventListener('change', (e) => handleFiles(Array.from(e.target.files)));

    if(dropzone) {
        dropzone.addEventListener('dragover', (e) => { e.preventDefault(); dropzone.classList.add('dragover'); });
        dropzone.addEventListener('dragleave', (e) => { e.preventDefault(); dropzone.classList.remove('dragover'); });
        dropzone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropzone.classList.remove('dragover');
            handleFiles(Array.from(e.dataTransfer.files));
        });
    }

    function handleFiles(files) {
        if (!files || files.length === 0) return;

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
                img.classList.add('mediaThumb');
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

    // Sortable
    if (typeof Sortable !== 'undefined' && mediaSlider) {
        Sortable.create(mediaSlider, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            onEnd: () => updateFileInputOrder()
        });
    }

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
        // mediaJsonInput 로직은 필요 없다면 삭제해도 됨 (FormData가 아닌 JSON 전송 시에는 파일 전송 불가하므로)
    }


    // ==========================================
    // 3. 버튼 클릭 (저장/수정 요청)
    // ==========================================
    document.addEventListener('click', function(e) {
        if(e.target && e.target.id === 'submitBtn') {

            if (!window.editor) return alert("에디터 로딩 중");

            const content = window.editor.getHTML();
            postBody.value = content;

            if (content.trim() === "<p><br></p>" || content.trim() === "") {
                return alert("내용을 입력해주세요.");
            }

            // 3-1. 수정 모드 (PUT)
            if (mode === 'edit') {

                // 🔥 [수정] JSON 대신 FormData 사용!
                const formData = new FormData();

                // 1. 일반 데이터 append
                formData.append("content", content);
                formData.append("category", document.getElementById("communitySelect").value);
                formData.append("status", document.getElementById("visibilitySelect").value);
                formData.append("tags", document.getElementById("hiddenTags").value);

                // 2. 삭제할 파일 ID들 (리스트로 보냄)
                // Spring에서 List<Long>으로 받으려면 같은 키로 여러 번 append 하면 됨
                if (deleteFileIds.length > 0) {
                    deleteFileIds.forEach(id => {
                        formData.append("deleteFileIds", id);
                    });
                }

                // 3. 🔥 [핵심] 새로 추가한 파일들 append
                // updateFileInputOrder() 함수 덕분에 input[type=file]에 새 파일들이 들어있음
                const fileInput = document.getElementById('mediaUploadInput');
                if (fileInput.files.length > 0) {
                    Array.from(fileInput.files).forEach(file => {
                        formData.append("newFiles", file); // DTO의 필드명(newFiles)과 일치해야 함
                    });
                }

                // 4. 전송 (fetch)
                fetch(`/community/${postId}`, {
                    method: "POST",
                    // 🔥 주의: FormData 전송 시 Content-Type 헤더를 직접 설정하면 안 됨 (브라우저가 알아서 boundary 설정함)
                    body: formData
                })
                    .then(res => {
                        if (res.ok) {
                            alert("수정되었습니다.");
                            location.href = `/community/cdetail/${postId}`;
                        } else {
                            res.text().then(msg => alert("수정 실패: " + msg));
                        }
                    })
                    .catch(err => console.error(err));

            } else {
                // 3-2. 작성 모드 (POST)
                document.getElementById('commuCreateForm').submit();
            }
        }
    });
});