<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/shortsUpload.css">
    <style>
        /* 수정 페이지 전용 추가 스타일 */
        .info-box {
            padding: 20px;
            background-color: #fff8e1; /* 연한 노란색 배경 */
            border: 1px solid #ffe0b2;
            border-radius: 8px;
            color: #f57f17;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 10px;
        }
        .info-box .icon { font-size: 20px; }

        .current-thumbnail {
            margin-bottom: 10px;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 8px;
            text-align: center;
        }
        .current-thumbnail img {
            max-height: 150px;
            border-radius: 4px;
        }
        .current-thumbnail p {
            margin-bottom: 5px;
            font-weight: 600;
            color: #555;
        }
    </style>
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article workspace shorts">
        <div class="shorts-upload-page">
            <form class="upload-form" id="updateForm">
                <h1 class="form-title">쇼츠 게시물 수정</h1>

                <input type="hidden" id="postId" name="postId" value="${shorts.postId}">

                <div class="field-group">
                    <label class="field-label">비디오 파일</label>
                    <div class="info-box">
                        <span class="icon">⚠️</span>
                        <div>
                            <strong>동영상 파일은 수정할 수 없습니다.</strong><br>
                            영상을 변경하려면 현재 게시글을 삭제한 후 다시 업로드해주세요.
                        </div>
                    </div>
                </div>

                <div class="field-group">
                    <label for="title" class="field-label">제목 수정</label>
                    <input type="text" id="title" name="title" class="field-control"
                           placeholder="쇼츠 제목을 입력하세요" maxlength="100"
                           value="${shorts.title}" required>
                </div>

                <div class="field-group">
                    <label for="content" class="field-label">설명 수정</label>
                    <textarea id="content" name="content" class="field-control"
                              placeholder="쇼츠에 대한 설명을 입력하세요 (최대 200자)"
                              maxlength="200">${shorts.content}</textarea>
                </div>

                <div class="field-group">
                    <label for="tagInput" class="field-label">태그 수정</label>
                    <div class="tag-input-container">
                        <input type="text" id="tagInput" class="field-control"
                               placeholder="태그를 입력하고 Enter를 누르세요 (예: #한국 #여행)">
                        <div class="tag-hint">최대 5개까지 추가 가능합니다</div>

                        <div class="tag-list" id="tagList"></div>
                    </div>
                </div>

                <div class="field-group">
                    <label class="field-label">썸네일 변경 (선택)</label>
                    <div class="thumbnail-section">
                        <c:if test="${not empty shorts.files}">
                            <c:forEach var="file" items="${shorts.files}">
                                <c:if test="${file.contentType.startsWith('image/')}">
                                    <div class="current-thumbnail">
                                        <p>현재 썸네일</p>
                                        <img src="${file.path}" alt="Current Thumbnail">
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:if>

                        <div class="thumbnail-upload-area" id="thumbnailUploadArea" style="margin-top: 10px;">
                            <div class="thumbnail-upload-icon">🖼️</div>
                            <div class="thumbnail-upload-text">새로운 썸네일 이미지를 선택하세요 (변경 시)</div>
                            <input type="file" id="thumbnailFile" name="thumbnailFile" class="file-input" accept="image/*">
                        </div>

                        <div class="thumbnail-preview-area">
                            <div class="thumbnail-preview" id="thumbnailPreview">
                                <img id="thumbnailPreviewImg" src="" alt="새 썸네일 미리보기">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="button-group">
                    <button type="button" class="btn btn-cancel" onclick="history.back()">취소</button>
                    <button type="button" class="btn btn-submit" id="submitBtn" onclick="submitEdit()">수정 완료</button>
                </div>
            </form>
        </div>
    </article>
</div>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/commujs/tag.js"></script>
<script src="/js/shortsjs/shortsUpload.js"></script>
<script src="/js/shortsjs/shorts.js"></script>

<script>
    // ==========================================
    //  1. 태그 초기화 및 관리 로직
    // ==========================================
    const tagInput = document.getElementById('tagInput');
    const tagList = document.getElementById('tagList');
    // Set을 사용하여 중복 방지
    let tags = new Set();

    // 서버에서 받아온 기존 태그 리스트 초기화 (JSTL -> JS Array)
    <c:forEach var="tag" items="${shorts.tags}">
    tags.add("${tag}");
    </c:forEach>

    // 초기 태그 렌더링
    renderTags();

    // 태그 입력 이벤트 (Enter 키)
    tagInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault(); // 폼 제출 방지
            const value = e.target.value.trim().replace(/^#/, ''); // # 제거

            if (value && tags.size < 5) {
                tags.add(value);
                renderTags();
                e.target.value = '';
            } else if (tags.size >= 5) {
                alert("태그는 최대 5개까지만 가능합니다.");
            }
        }
    });

    // 태그 렌더링 함수
    function renderTags() {
        tagList.innerHTML = '';
        tags.forEach(tag => {
            const tagEl = document.createElement('div');
            tagEl.className = 'tag-item';
            tagEl.innerHTML = `
                <span class="tag-text">#\${tag}</span>
                <button type="button" class="btn-remove" onclick="removeTag('\${tag}')">×</button>
            `;
            tagList.appendChild(tagEl);
        });
    }

    // 전역 함수로 등록 (onclick에서 호출 위해)
    window.removeTag = function(tag) {
        tags.delete(tag);
        renderTags();
    }

    // ==========================================
    //  2. 썸네일 미리보기 로직
    // ==========================================
    const thumbnailInput = document.getElementById('thumbnailFile');
    const thumbnailPreview = document.getElementById('thumbnailPreview');
    const thumbnailPreviewImg = document.getElementById('thumbnailPreviewImg');

    thumbnailInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                thumbnailPreviewImg.src = e.target.result;
                thumbnailPreview.classList.add('active'); // CSS display 처리 확인 필요
                thumbnailPreview.style.display = 'block';
            }
            reader.readAsDataURL(file);
        } else {
            thumbnailPreviewImg.src = '';
            thumbnailPreview.style.display = 'none';
        }
    });

    // ==========================================
    //  3. 수정 제출 로직 (AJAX Fetch)
    // ==========================================
    function submitEdit() {
        const submitBtn = document.getElementById('submitBtn');
        submitBtn.disabled = true;
        submitBtn.innerText = "수정 중...";

        const postId = document.getElementById("postId").value;
        const title = document.getElementById("title").value;
        const content = document.getElementById("content").value;
        const thumbnailFile = document.getElementById("thumbnailFile").files[0];

        // 유효성 검사
        if (!title.trim() || !content.trim()) {
            alert("제목과 내용은 필수입니다.");
            submitBtn.disabled = false;
            submitBtn.innerText = "수정 완료";
            return;
        }

        // FormData 생성 (파일 + 텍스트 전송용)
        const formData = new FormData();
        formData.append("postId", postId);
        formData.append("title", title);
        formData.append("content", content);

        // 태그 배열 추가 (Controller의 List<String> tags에 매핑됨)
        tags.forEach(tag => {
            formData.append("tags", tag);
        });

        // 썸네일 파일이 있는 경우에만 추가
        if (thumbnailFile) {
            formData.append("thumbnailFile", thumbnailFile);
            formData.append("thumbnailType", "manual"); // 수동 변경 명시
        }

        // Fetch API 전송
        fetch('/shorts/shortsUpdate', {
            method: 'POST',
            // headers: Content-Type은 FormData가 알아서 'multipart/form-data'로 설정하므로 생략!
            body: formData
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.msg);
                    location.href = `/shorts/view/\${postId}`; // 상세 페이지로 이동
                    // 또는 location.href = '/shorts'; // 목록으로 이동
                } else {
                    alert(data.msg);
                    submitBtn.disabled = false;
                    submitBtn.innerText = "수정 완료";
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert("시스템 오류가 발생했습니다.");
                submitBtn.disabled = false;
                submitBtn.innerText = "수정 완료";
            });
    }
</script>

</body>
</html>

