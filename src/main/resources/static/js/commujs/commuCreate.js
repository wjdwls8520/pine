window.addEventListener("load", () => {

    // ==========================================
    // 1. 변수 설정
    // ==========================================
    const mode = document.getElementById('mode').value;
    const postId = document.getElementById('postId').value;
    const postBody = document.getElementById('postBody');

    // 파일 관련 변수
    const fileInput = document.getElementById('mediaUploadInput'); // 전송용 (데이터 저장소)
    const tempInput = document.getElementById('tempFileInput');   // [수정] 파일 선택창용 (트리거)

    const mediaSlider = document.getElementById('mediaSlider');
    const dropzoneBtn = document.getElementById('dropzoneSelectBtn');
    const dropzone = document.getElementById('mediaDropzone');

    // 기존 파일 관리용 변수
    const serverFileInput = document.getElementById('serverFileList');
    let deleteFileIds = [];


    // ==========================================
    // 2. 파일 업로드 및 관리 로직
    // ==========================================

    // 2-1. [초기화] 서버에서 가져온 기존 파일 그리기 (수정 모드)
    if (serverFileInput && serverFileInput.value) {
        try {
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
            deleteFileIds.push(file.id);
            item.remove();
        });

        item.appendChild(removeBtn);
        mediaSlider.appendChild(item);
    }

    // 2-2. 새 파일 추가 로직 (드래그앤드롭 & 버튼 선택)

    // [수정] 버튼 클릭 시 '전송용 input'이 아니라 '임시 input'을 클릭
    if(dropzoneBtn) {
        dropzoneBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if(tempInput) tempInput.click();
        });
    }

    // [수정] 임시 input에 파일이 들어오면 처리하고 값을 비움 (취소 이슈 해결)
    if(tempInput) {
        tempInput.addEventListener('change', (e) => {
            handleFiles(Array.from(e.target.files));
            // 중요: 같은 파일을 다시 선택할 수 있게, 그리고 취소 시 영향 없게 초기화
            e.target.value = '';
        });
    }

    // [참고] fileInput(mediaUploadInput)에는 change 이벤트를 걸지 않습니다.
    // 사용자가 직접 건드리는 게 아니라 코드로만 값을 넣기 때문입니다.

    if(dropzone) {
        dropzone.addEventListener('dragover', (e) => { e.preventDefault(); dropzone.classList.add('dragover'); });
        dropzone.addEventListener('dragleave', (e) => { e.preventDefault(); dropzone.classList.remove('dragover'); });
        dropzone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropzone.classList.remove('dragover');
            handleFiles(Array.from(e.dataTransfer.files));
        });
    }

    // 파일 처리 공통 함수
    function handleFiles(files) {
        if (!files || files.length === 0) return;

        const currentCount = mediaSlider.querySelectorAll('.mediaItem').length;
        if (currentCount + files.length > 10) {
            alert("최대 10개의 파일만 업로드할 수 있습니다.");
            return;
        }

        files.forEach(file => {
            // 유효성 검사
            if (file.size > 50_000_000) return alert(`파일 "${file.name}" 용량이 50MB를 초과했습니다.`);
            if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) return alert('이미지나 영상만 업로드 가능합니다.');

            const item = document.createElement('div');
            item.classList.add('mediaItem');

            // [핵심] DOM 요소에 실제 File 객체를 붙여둡니다.
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
                // 삭제 후 반드시 input 동기화!
                updateFileInputOrder();
            });

            item.appendChild(removeBtn);
            mediaSlider.appendChild(item);
        });

        // [핵심] 파일 추가가 끝나면 input 태그를 최신 상태로 갱신합니다.
        updateFileInputOrder();
    }

    // Sortable (순서 변경 시에도 input 동기화)
    if (typeof Sortable !== 'undefined' && mediaSlider) {
        Sortable.create(mediaSlider, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            onEnd: () => updateFileInputOrder()
        });
    }

    // [핵심 함수] 화면에 있는 '새 파일'들을 긁어모아 전송용 input에 꽂아넣는 함수
    function updateFileInputOrder() {
        const dataTransfer = new DataTransfer(); // 가상의 파일 컨테이너 생성

        // mediaSlider 안의 모든 아이템을 순서대로 탐색
        mediaSlider.querySelectorAll('.mediaItem').forEach(item => {
            // item.fileRef가 있다는 건 '새로 추가된 파일'이라는 뜻
            if (item.fileRef) {
                dataTransfer.items.add(item.fileRef);
            }
        });

        // 전송용 fileInput의 내용을 우리가 만든 리스트로 교체!
        fileInput.files = dataTransfer.files;

        // console.log("현재 전송될 새 파일 개수:", fileInput.files.length);
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

            // 3-1. 수정 모드 (Ajax - PUT/POST)
            if (mode === 'edit') {
                const formData = new FormData();
                formData.append("content", content);
                formData.append("category", document.getElementById("communitySelect").value);
                formData.append("status", document.getElementById("visibilitySelect").value);
                formData.append("tags", document.getElementById("hiddenTags").value);

                // 삭제할 기존 파일 ID들
                if (deleteFileIds.length > 0) {
                    deleteFileIds.forEach(id => {
                        formData.append("deleteFileIds", id);
                    });
                }

                // 새로 추가한 파일들 (input.files에 있는 것들 그대로 전송)
                if (fileInput.files.length > 0) {
                    Array.from(fileInput.files).forEach(file => {
                        formData.append("newFiles", file);
                    });
                }

                fetch(`/community/${postId}`, {
                    method: "POST",
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
                // 3-2. 작성 모드 (Form Submit)
                // updateFileInputOrder() 덕분에 fileInput.files에 파일이 잘 들어있음
                document.getElementById('commuCreateForm').submit();
            }
        }
    });
});