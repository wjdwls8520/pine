/**
 * shortsUpload.js
 * - 쇼츠 업로드(작성) 및 수정 페이지 공통 스크립트
 */

document.addEventListener("DOMContentLoaded", () => {
    // 현재 페이지가 무엇인지 폼 ID로 구분
    const uploadForm = document.getElementById('shortsUploadForm'); // 업로드 페이지 폼
    const updateForm = document.getElementById('updateForm');       // 수정 페이지 폼

    // 버튼이 나중에 생겨도 클릭 이벤트를 잡을 수 있도록 body에 이벤트를 겁니다.
    document.body.addEventListener('click', (e) => {
        // 클릭된 요소가 'submitBtn'인지 확인
        if (e.target && e.target.id === 'submitBtn') {
            if (uploadForm) {
                // 업로드 폼이면 강제로 submit 이벤트 발생
                uploadForm.dispatchEvent(new Event('submit'));
            } else if (updateForm) {
                // 수정 폼이면 수정 함수 실행
                submitEdit();
            }
        }
    });

    // ============================================================
    //  CASE 1: 쇼츠 업로드(작성) 페이지 로직
    // ============================================================
    if (uploadForm) {
        const uploadArea = document.getElementById('uploadArea');
        const videoFileInput = document.getElementById('videoFile');
        const videoPreview = document.getElementById('videoPreview');
        const previewVideo = document.getElementById('previewVideo');
        const videoInfo = document.getElementById('videoInfo');
        const submitBtn = document.getElementById('submitBtn');

        // 썸네일 관련 요소
        const thumbnailAuto = document.getElementById('thumbnailAuto');
        const thumbnailManual = document.getElementById('thumbnailManual');
        const thumbnailUploadArea = document.getElementById('thumbnailUploadArea');
        const thumbnailFileInput = document.getElementById('thumbnailFile');
        const thumbnailPreview = document.getElementById('thumbnailPreview');
        const thumbnailPreviewImg = document.getElementById('thumbnailPreviewImg');

        // 1. 비디오 업로드 영역 클릭
        if (uploadArea) {
            uploadArea.addEventListener('click', () => {
                videoFileInput.click();
            });

            // 3. 드래그 앤 드롭
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
                    handleVideoSelect(file, videoFileInput, previewVideo, videoPreview, videoInfo, thumbnailAuto, thumbnailPreviewImg, thumbnailPreview);
                } else {
                    alert('비디오 파일만 업로드 가능합니다.');
                }
            });
        }

        // 2. 비디오 파일 선택 시
        if (videoFileInput) {
            videoFileInput.addEventListener('change', (e) => {
                handleVideoSelect(e.target.files[0], videoFileInput, previewVideo, videoPreview, videoInfo, thumbnailAuto, thumbnailPreviewImg, thumbnailPreview);

                // 비디오 변경 시 수동 썸네일 초기화
                if (thumbnailManual && thumbnailManual.checked) {
                    if(thumbnailFileInput) thumbnailFileInput.value = '';
                    if(thumbnailPreview) thumbnailPreview.classList.remove('active');
                    if(thumbnailPreviewImg) thumbnailPreviewImg.src = '';
                }
            });
        }

        // 4. 썸네일 타입 변경 (자동/수동)
        if (thumbnailAuto) {
            thumbnailAuto.addEventListener('change', () => {
                if (thumbnailAuto.checked) {
                    if(thumbnailUploadArea) thumbnailUploadArea.classList.remove('active');
                    if(thumbnailFileInput) thumbnailFileInput.value = '';
                    // 비디오가 있으면 자동 썸네일 생성
                    if (previewVideo.src) {
                        generateThumbnailFromVideo(previewVideo.src, thumbnailPreviewImg, thumbnailPreview);
                    }
                }
            });
        }

        if (thumbnailManual) {
            thumbnailManual.addEventListener('change', () => {
                if (thumbnailManual.checked) {
                    if(thumbnailUploadArea) thumbnailUploadArea.classList.add('active');
                    if(thumbnailPreview) thumbnailPreview.classList.remove('active');
                    if(thumbnailPreviewImg) thumbnailPreviewImg.src = '';
                }
            });
        }

        // 썸네일 업로드 영역 클릭
        if (thumbnailUploadArea) {
            thumbnailUploadArea.addEventListener('click', () => {
                if (thumbnailManual && thumbnailManual.checked) {
                    thumbnailFileInput.click();
                }
            });
        }

        // 5. 수동 썸네일 파일 선택
        if (thumbnailFileInput) {
            thumbnailFileInput.addEventListener('change', (e) => {
                handleThumbnailSelect(e.target.files[0], thumbnailFileInput, thumbnailPreviewImg, thumbnailPreview);
            });
        }

        // 6. 폼 제출 (업로드용 - Fetch 적용)
        uploadForm.addEventListener('submit', (e) => {
            e.preventDefault(); // 기본 제출 방지

            // 유효성 검사
            if (!videoFileInput.files || videoFileInput.files.length === 0) {
                alert('비디오 파일을 선택해주세요.');
                return;
            }
            const title = document.getElementById('title').value.trim();
            if (!title) {
                alert('제목을 입력해주세요.');
                return;
            }
            if (thumbnailManual.checked && (!thumbnailFileInput.files || thumbnailFileInput.files.length === 0)) {
                alert('썸네일 이미지를 선택해주세요.');
                return;
            }

            const formData = new FormData(uploadForm);

            // 태그 값 추가
            const hiddenTags = document.getElementById("hiddenTags");
            if (hiddenTags && hiddenTags.value) {
                formData.set("tags", hiddenTags.value);
            }

            submitBtn.disabled = true;
            submitBtn.innerText = "업로드 중...";

            fetch('/shorts/shortsUpload', {
                method: 'POST',
                body: formData
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert(data.msg); // ★ 성공 알림
                        location.href = '/shorts';
                    } else {
                        alert(data.msg); // 실패 알림
                        submitBtn.disabled = false;
                        submitBtn.innerText = "업로드";
                    }
                })
                .catch(err => {
                    console.error('Error:', err);
                    alert("업로드 중 오류가 발생했습니다.");
                    submitBtn.disabled = false;
                    submitBtn.innerText = "업로드";
                });
        });
    }

    // ============================================================
    //  CASE 2: 쇼츠 수정 페이지 로직
    // ============================================================
    if (updateForm) {
        // 수정 페이지용 썸네일 요소
        const thumbnailInput = document.getElementById('thumbnailFile');
        const thumbnailPreview = document.getElementById('thumbnailPreview');
        const thumbnailPreviewImg = document.getElementById('thumbnailPreviewImg');

        // 수정 페이지에서 썸네일 변경 시 미리보기
        if (thumbnailInput) {
            thumbnailInput.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    // 공통 파일 처리 함수 재사용 가능하지만, 요청하신 로직 그대로 적용
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        if(thumbnailPreviewImg) thumbnailPreviewImg.src = e.target.result;
                        if(thumbnailPreview) {
                            thumbnailPreview.classList.add('active');
                            thumbnailPreview.style.display = 'block';
                        }
                    }
                    reader.readAsDataURL(file);
                } else {
                    if(thumbnailPreviewImg) thumbnailPreviewImg.src = '';
                    if(thumbnailPreview) thumbnailPreview.style.display = 'none';
                }
            });
        }
    }
});

// ============================================================
//  3. 공통 및 헬퍼 함수들
// ============================================================

/**
 * 수정 완료 버튼 클릭 시 호출 (AJAX)
 */
function submitEdit() {
    const submitBtn = document.getElementById('submitBtn');
    if(submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerText = "수정 중...";
    }

    const postId = document.getElementById("postId").value;
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const thumbnailFile = document.getElementById("thumbnailFile") ? document.getElementById("thumbnailFile").files[0] : null;

    // 유효성 검사
    if (!title.trim() || !content.trim()) {
        alert("제목과 내용은 필수입니다.");
        if(submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerText = "수정 완료";
        }
        return;
    }

    const formData = new FormData();
    formData.append("postId", postId);
    formData.append("title", title);
    formData.append("content", content);

    // tag.js가 관리하는 hidden input 값 가져오기
    const hiddenTags = document.getElementById("hiddenTags");
    if (hiddenTags && hiddenTags.value) {
        formData.append("tags", hiddenTags.value);
    }

    // 썸네일 파일 (있는 경우에만)
    if (thumbnailFile) {
        formData.append("thumbnailFile", thumbnailFile);
        formData.append("thumbnailType", "manual");
    }

    // 전송
    fetch('/shorts/shortsUpdate', {
        method: 'POST',
        body: formData
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert(data.msg);
                location.href = `/shorts/view/${postId}`;
            } else {
                alert(data.msg);
                if(submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = "수정 완료";
                }
            }
        })
        .catch(err => {
            console.error('Error:', err);
            alert("시스템 오류가 발생했습니다.");
            if(submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerText = "수정 완료";
            }
        });
}

/**
 * [업로드 페이지] 비디오 선택 처리 핸들러
 */
function handleVideoSelect(file, input, previewVideo, videoPreview, videoInfo, thumbnailAuto, thumbnailPreviewImg, thumbnailPreview) {
    if (!file) return;

    // 파일 타입 및 크기 체크
    const isVideo = (file.type && file.type.startsWith('video/')) || /\.(mp4|mov|avi|webm)$/i.test(file.name);
    if (!isVideo) {
        alert('비디오 파일만 업로드 가능합니다.');
        input.value = '';
        return;
    }

    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
        alert('파일 크기는 10MB를 초과할 수 없습니다.');
        input.value = '';
        return;
    }

    // 미리보기 설정
    const url = URL.createObjectURL(file);
    if(previewVideo) previewVideo.src = url;
    if(videoPreview) videoPreview.classList.add('active');

    const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2);
    if(videoInfo) {
        videoInfo.innerHTML =
            '<strong>파일명:</strong> ' + file.name + '<br>' +
            '<strong>크기:</strong> ' + fileSizeMB + ' MB<br>' +
            '<strong>형식:</strong> ' + file.type;
    }

    // 자동 썸네일 생성
    if (thumbnailAuto && thumbnailAuto.checked) {
        generateThumbnailFromVideo(url, thumbnailPreviewImg, thumbnailPreview);
    }
}

/**
 * [업로드 페이지] 비디오에서 썸네일 추출
 */
function generateThumbnailFromVideo(videoUrl, imgElement, previewContainer) {
    const video = document.createElement('video');
    video.src = videoUrl;
    video.currentTime = 1; // 1초 지점 캡처

    video.addEventListener('loadeddata', () => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');

        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        if(imgElement) imgElement.src = canvas.toDataURL('image/jpeg');
        if (previewContainer) previewContainer.classList.add('active');
    });
}

/**
 * [업로드 페이지] 썸네일 파일 선택 처리
 */
function handleThumbnailSelect(file, input, imgElement, previewContainer) {
    if (!file) return;

    if (!file.type.startsWith('image/')) {
        alert('썸네일은 이미지 파일만 업로드 가능합니다.');
        input.value = '';
        if(imgElement) imgElement.src = '';
        if (previewContainer) previewContainer.classList.remove('active');
        return;
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
        alert('썸네일 이미지 크기는 5MB를 초과할 수 없습니다.');
        input.value = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = (event) => {
        if(imgElement) imgElement.src = event.target.result;
        if (previewContainer) {
            previewContainer.classList.add('active');
            previewContainer.style.display = 'block';
        }
    };
    reader.readAsDataURL(file);
}