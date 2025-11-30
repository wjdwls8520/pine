<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/shortsUpload.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article workspace shorts">
        <div class="shorts-upload-page">
            <form class="upload-form" id="shortsUploadForm" method="post" action="/shorts/shortsUpload" enctype="multipart/form-data">
                <h1 class="form-title">쇼츠 업로드</h1>

                <div class="field-group">
                    <label for="videoFile" class="field-label">비디오 파일</label>
                    <div class="video-upload-area" id="uploadArea">
                        <div class="upload-icon">🎬</div>
                        <div class="upload-text">비디오 파일을 선택하거나 드래그하세요</div>
                        <div class="upload-hint">MP4, MOV, AVI 형식 · 최대 100MB</div>
                        <input type="file" id="videoFile" name="files" class="file-input" accept="video/*" required>
                    </div>
                    <div class="video-preview" id="videoPreview">
                        <video id="previewVideo" controls></video>
                        <div class="video-info" id="videoInfo"></div>
                    </div>
                </div>

                <div class="field-group">
                    <label for="title" class="field-label">제목</label>
                    <input type="text" id="title" name="title" class="field-control" placeholder="쇼츠 제목을 입력하세요" maxlength="100" required>
                </div>

                <div class="field-group">
                    <label for="content" class="field-label">설명</label>
                    <textarea id="content" name="content" class="field-control" placeholder="쇼츠에 대한 설명을 입력하세요 (최대 200자)" maxlength="200"></textarea>
                </div>

                <div class="field-group">
                    <label class="field-label">썸네일</label>
                    <div class="thumbnail-section">
                        <div class="thumbnail-options">
                            <div class="thumbnail-option">
                                <input type="radio" id="thumbnailAuto" name="thumbnailType" value="auto" checked>
                                <label for="thumbnailAuto">비디오에서 자동 생성</label>
                            </div>
                            <div class="thumbnail-option">
                                <input type="radio" id="thumbnailManual" name="thumbnailType" value="manual">
                                <label for="thumbnailManual">직접 업로드</label>
                            </div>
                            <div class="thumbnail-upload-area" id="thumbnailUploadArea">
                                <div class="thumbnail-upload-icon">🖼️</div>
                                <div class="thumbnail-upload-text">썸네일 이미지를 선택하세요</div>
                                <input type="file" id="thumbnailFile" name="files" class="file-input" accept="image/*">
                            </div>
                        </div>
                        <div class="thumbnail-preview-area">
                            <div class="thumbnail-preview" id="thumbnailPreview">
                                <img id="thumbnailPreviewImg" src="" alt="썸네일 미리보기">
                            </div>
                        </div>
                    </div>
                </div>

<%--                <div class="field-group">--%>
<%--                    <label for="tagInput" class="field-label">태그</label>--%>
<%--                    <div class="tag-input-container">--%>
<%--                        <input type="text" id="tagInput" class="field-control" placeholder="태그를 입력하고 Enter를 누르세요 (예: #한국 #여행 #브이로그)">--%>
<%--                        <div class="tag-hint">최대 10개까지 추가 가능합니다</div>--%>
<%--                        <div class="tag-list" id="tagList"></div>--%>
<%--                        <input type="hidden" id="tags" name="tags" value="">--%>
<%--                    </div>--%>
<%--                </div>--%>

                <div class="button-group">
                    <button type="button" class="btn btn-cancel" onclick="history.back()">취소</button>
                    <button type="submit" class="btn btn-submit" id="submitBtn">업로드</button>
                </div>
            </form>
        </div>
    </article>
</div>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<%--<script src="/js/shorts/shortsUpload.js"></script>--%>

<script>
    const uploadArea = document.getElementById('uploadArea');
    const videoFileInput = document.getElementById('videoFile');
    const videoPreview = document.getElementById('videoPreview');
    const previewVideo = document.getElementById('previewVideo');
    const videoInfo = document.getElementById('videoInfo');
    const submitBtn = document.getElementById('submitBtn');
    const form = document.getElementById('shortsUploadForm');

    // 썸네일 관련
    const thumbnailAuto = document.getElementById('thumbnailAuto');
    const thumbnailManual = document.getElementById('thumbnailManual');
    const thumbnailUploadArea = document.getElementById('thumbnailUploadArea');
    const thumbnailFileInput = document.getElementById('thumbnailFile');
    const thumbnailPreview = document.getElementById('thumbnailPreview');
    const thumbnailPreviewImg = document.getElementById('thumbnailPreviewImg');

    // 태그 관련
    const tagInput = document.getElementById('tagInput');
    const tagList = document.getElementById('tagList');
    const tagsHidden = document.getElementById('tags');
    let tags = [];

    // 업로드 영역 클릭
    uploadArea.addEventListener('click', () => {
        videoFileInput.click();
    });

    // 파일 선택 시
    videoFileInput.addEventListener('change', (e) => {
        handleFileSelect(e.target.files[0]);
    });

    // 드래그 앤 드롭
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file && file.type.startsWith('video/')) {
            videoFileInput.files = e.dataTransfer.files;
            handleFileSelect(file);
        } else {
            alert('비디오 파일만 업로드 가능합니다.');
        }
    });

    // 파일 처리 함수
    function handleFileSelect(file) {
        if (!file) return;

        // 파일 크기 체크 (100MB)
        const maxSize = 100 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('파일 크기는 100MB를 초과할 수 없습니다.');
            videoFileInput.value = '';
            return;
        }

        // 비디오 미리보기
        const url = URL.createObjectURL(file);
        previewVideo.src = url;
        videoPreview.classList.add('active');

        // 파일 정보 표시
        const fileSize = (file.size / (1024 * 1024)).toFixed(2);
        videoInfo.innerHTML = `
            <strong>파일명:</strong> ${file.name}<br>
            <strong>크기:</strong> ${fileSize} MB<br>
            <strong>형식:</strong> ${file.type}
        `;

        // 자동 썸네일 생성 (비디오의 첫 프레임)
        if (thumbnailAuto.checked) {
            generateThumbnailFromVideo(url);
        }

        // 업로드 버튼 활성화
        submitBtn.disabled = false;
    }

    // 비디오에서 썸네일 자동 생성
    function generateThumbnailFromVideo(videoUrl) {
        const video = document.createElement('video');
        video.src = videoUrl;
        video.currentTime = 1;

        video.addEventListener('loadeddata', () => {
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            const ctx = canvas.getContext('2d');

            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

            // ✅ 1. 미리보기용
            thumbnailPreviewImg.src = canvas.toDataURL('image/jpeg');
            thumbnailPreview.classList.add('active');

            // ✅ 2. 서버 전송용 (진짜 이미지 파일로 변환)
            canvas.toBlob((blob) => {
                const thumbnailFile = new File([blob], "thumbnail.jpg", {
                    type: "image/jpeg"
                });

                // ✅ 여기서 FormData에 강제로 썸네일 주입
                const formData = new FormData(document.getElementById("shortsUploadForm"));
                formData.set("files", thumbnailFile);

                window.generatedThumbnailFile = thumbnailFile; // 전역 보관 (선택)
            }, "image/jpeg");
        });
    }


    // 썸네일 타입 변경
    thumbnailAuto.addEventListener('change', () => {
        if (thumbnailAuto.checked) {
            thumbnailUploadArea.classList.remove('active');
            thumbnailFileInput.value = '';
            // 비디오가 있으면 자동 썸네일 생성
            if (previewVideo.src) {
                generateThumbnailFromVideo(previewVideo.src);
            }
        }
    });

    thumbnailManual.addEventListener('change', () => {
        if (thumbnailManual.checked) {
            thumbnailUploadArea.classList.add('active');
            thumbnailPreview.classList.remove('active');
        }
    });

    // 썸네일 업로드 영역 클릭
    thumbnailUploadArea.addEventListener('click', () => {
        if (thumbnailManual.checked) {
            thumbnailFileInput.click();
        }
    });

    // 썸네일 파일 선택
    thumbnailFileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            // 파일 크기 체크 (5MB)
            const maxSize = 5 * 1024 * 1024;
            if (file.size > maxSize) {
                alert('썸네일 이미지 크기는 5MB를 초과할 수 없습니다.');
                thumbnailFileInput.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = (event) => {
                thumbnailPreviewImg.src = event.target.result;
                thumbnailPreview.classList.add('active');
            };
            reader.readAsDataURL(file);
        }
    });

    // 태그 추가
    tagInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            addTag();
        }
    });

    function addTag() {
        const tagValue = tagInput.value.trim();

        if (!tagValue) return;

        // # 제거하고 다시 추가
        let tag = tagValue.replace(/^#+/, '');
        if (!tag) return;

        tag = '#' + tag;

        // 중복 체크
        if (tags.includes(tag)) {
            alert('이미 추가된 태그입니다.');
            tagInput.value = '';
            return;
        }

        // 최대 10개 제한
        if (tags.length >= 10) {
            alert('태그는 최대 10개까지 추가할 수 있습니다.');
            return;
        }

        tags.push(tag);
        updateTagList();
        tagInput.value = '';
    }

    function removeTag(index) {
        tags.splice(index, 1);
        updateTagList();
    }

    // 전역 함수로 노출 (onclick에서 사용)
    window.removeTag = removeTag;

    function updateTagList() {
        tagList.innerHTML = '';
        tags.forEach((tag, index) => {
            const tagItem = document.createElement('div');
            tagItem.className = 'tag-item';
            tagItem.innerHTML = `
                <span>${tag}</span>
                <span class="tag-remove" onclick="removeTag(${index})">×</span>
            `;
            tagList.appendChild(tagItem);
        });
        tagsHidden.value = tags.join(',');
    }

    // 폼 제출 전 검증
    form.addEventListener('submit', (e) => {
        if (!videoFileInput.files || videoFileInput.files.length === 0) {
            e.preventDefault();
            alert('비디오 파일을 선택해주세요.');
            return;
        }

        const title = document.getElementById('title').value.trim();
        if (!title) {
            e.preventDefault();
            alert('제목을 입력해주세요.');
            return;
        }

        // 수동 썸네일 선택 시 파일 체크
        if (thumbnailManual.checked && (!thumbnailFileInput.files || thumbnailFileInput.files.length === 0)) {
            e.preventDefault();
            alert('썸네일 이미지를 선택해주세요.');
            return;
        }

        submitBtn.disabled = true;
        submitBtn.textContent = '업로드 중...';
    });
</script>
</body>
</html>

