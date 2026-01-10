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

        <script>
            var msg = '${msg}';
            if(msg && msg.trim() !== '') {
                alert(msg);
            }
        </script>

        <div class="shorts-upload-page">
            <form class="upload-form" id="shortsUploadForm" method="post" action="/shorts/shortsUpload" enctype="multipart/form-data">
                <h1 class="form-title">쇼츠 업로드</h1>

                <div class="field-group">
                    <label for="videoFile" class="field-label">비디오 파일</label>
                    <div class="video-upload-area" id="uploadArea">
                        <div class="upload-icon">🎬</div>
                        <div class="upload-text">비디오 파일을 선택하거나 드래그하세요</div>
                        <div class="upload-hint">MP4, MOV, AVI 형식 · 최대 10MB</div>
                        <input type="file" id="videoFile" name="videoFile" class="file-input" accept="video/*">
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
                    <label for="tagInput" class="field-label">태그</label>
                    <div class="tag-input-container">
                        <input type="text" id="tagInput" class="field-control" placeholder="태그를 입력하고 Enter를 누르세요 (예: #한국 #여행 #브이로그)">

                        <div class="tag-hint">최대 5개까지 추가 가능합니다</div> <div class="tag-list" id="tagList"></div>

                        <input type="hidden" id="hiddenTags" name="tags" value="">
                    </div>
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
                                <input type="file" id="thumbnailFile" name="thumbnailFile" class="file-input" accept="image/*">
                            </div>
                        </div>
                        <div class="thumbnail-preview-area">
                            <div class="thumbnail-preview" id="thumbnailPreview">
                                <img id="thumbnailPreviewImg" src="" alt="썸네일 미리보기">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="button-group">
                    <button type="button" class="btn btn-cancel" onclick="history.back()">취소</button>
                    <button type="submit" class="btn btn-submit" id="submitBtn">업로드</button>
                </div>
            </form>
        </div>
    </article>
</div>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/commujs/tag.js"></script>
<script src="/js/shortsjs/shortsUpload.js"></script>

</body>
</html>

