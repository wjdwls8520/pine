document.addEventListener("DOMContentLoaded", () => {
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
        console.log('handleFileSelect called', file);


        // 파일 타입 체크
        const isVideo =
            (file.type && file.type.startsWith('video/')) ||
            /\.(mp4|mov|avi|webm)$/i.test(file.name);

        if (!isVideo) {
            alert('비디오 파일만 업로드 가능합니다.');
            videoFileInput.value = '';
            return;
        }

        // 파일 크기 체크 (10MB)
        const maxSize = 10 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('파일 크기는 10MB를 초과할 수 없습니다.');
            videoFileInput.value = '';
            return;
        }

        // 비디오 미리보기
        const url = URL.createObjectURL(file);
        previewVideo.src = url;
        videoPreview.classList.add('active');

        // 파일 정보 표시
        const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2);
        videoInfo.innerHTML =
            '<strong>파일명:</strong> ' + file.name + '<br>' +
            '<strong>크기:</strong> ' + fileSizeMB + ' MB<br>' +
            '<strong>형식:</strong> ' + file.type;

        // 자동 썸네일 생성 (비디오의 첫 프레임)
        // 서버 업로드용 아님 (미리보기 전용)
        if (thumbnailAuto.checked) {
            generateThumbnailFromVideo(url);
        }
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

            //  미리보기용
            thumbnailPreviewImg.src = canvas.toDataURL('image/jpeg');
            thumbnailPreview.classList.add('active');
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
            thumbnailPreviewImg.src = '';
        }
    });

    // 비디오 변경 시 수동 썸네일 초기화
    videoFileInput.addEventListener('change', () => {
        if (thumbnailManual.checked) {
            thumbnailFileInput.value = '';
            thumbnailPreview.classList.remove('active');
            thumbnailPreviewImg.src = '';
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
        if (!file) return;

        // 타입 검사
        if (!file.type.startsWith('image/')) {
            alert('썸네일은 이미지 파일만 업로드 가능합니다.');

            // 상태 초기화
            thumbnailFileInput.value = '';
            thumbnailPreviewImg.src = '';
            thumbnailPreview.classList.remove('active');
            return;
        }

        // 파일 크기 체크 (5MB)
        const maxSize = 5 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('썸네일 이미지 크기는 5MB를 초과할 수 없습니다.');
            thumbnailFileInput.value = '';
            return;
        }

        // 정상이미지일 때만 미리보기
        const reader = new FileReader();
        reader.onload = (event) => {
            thumbnailPreviewImg.src = event.target.result;
            thumbnailPreview.classList.add('active');
        };
        reader.readAsDataURL(file);

    });

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
    });
});