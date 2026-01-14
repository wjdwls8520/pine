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

        <div class="shortsUploadPage">
            <form class="uploadForm" id="shortsUploadForm" method="post" action="/shorts/shortsUpload" enctype="multipart/form-data">
                <h1 class="formTitle">쇼츠 업로드</h1>

                <div class="fieldGroup">
                    <label for="videoFile" class="fieldLabel">비디오 파일</label>
                    <div class="videoUploadArea" id="uploadArea">
                        <div class="uploadIcon">🎬</div>
                        <div class="uploadText">비디오 파일을 선택하거나 드래그하세요</div>
                        <div class="uploadHint">MP4, MOV, AVI 형식 · 최대 10MB</div>
                        <input type="file" id="videoFile" name="videoFile" class="fileInput" accept="video/*">
                    </div>
                    <div class="videoPreview" id="videoPreview">
                        <video id="previewVideo" controls></video>
                        <div class="videoInfo" id="videoInfo"></div>
                    </div>
                </div>

                <div class="fieldGroup">
                    <label for="title" class="fieldLabel">제목</label>
                    <input type="text" id="title" name="title" class="fieldControl" placeholder="쇼츠 제목을 입력하세요" maxlength="100" required>
                </div>

                <div class="fieldGroup">
                    <label for="content" class="fieldLabel">설명</label>
                    <textarea id="content" name="content" class="fieldControl" placeholder="쇼츠에 대한 설명을 입력하세요 (최대 200자)" maxlength="200"></textarea>
                </div>

                <div class="fieldGroup">
                    <label for="tagInput" class="fieldLabel">태그</label>
                    <div class="tagInputContainer">
                        <input type="text" id="tagInput" class="fieldControl" placeholder="태그를 입력하고 Enter를 누르세요 (예: #한국 #여행 #브이로그)">

                        <div class="tagHint">최대 5개까지 추가 가능합니다</div>
                        <div class="tagList" id="tagList"></div>

                        <input type="hidden" id="hiddenTags" name="tags" value="">
                    </div>
                </div>

                <div class="fieldGroup">
                    <label class="fieldLabel">썸네일</label>
                    <div class="thumbnailSection">
                        <div class="thumbnailOptions">
                            <div class="thumbnailOption">
                                <input type="radio" id="thumbnailAuto" name="thumbnailType" value="auto" checked>
                                <label for="thumbnailAuto">비디오에서 자동 생성</label>
                            </div>
                            <div class="thumbnailOption">
                                <input type="radio" id="thumbnailManual" name="thumbnailType" value="manual">
                                <label for="thumbnailManual">직접 업로드</label>
                            </div>
                            <div class="thumbnailUploadArea" id="thumbnailUploadArea">
                                <div class="thumbnailUploadIcon">🖼️</div>
                                <div class="thumbnailUploadText">썸네일 이미지를 선택하세요</div>
                                <input type="file" id="thumbnailFile" name="thumbnailFile" class="fileInput" accept="image/*">
                            </div>
                        </div>
                        <div class="thumbnailPreviewArea">
                            <div class="thumbnailPreview" id="thumbnailPreview">
                                <img id="thumbnailPreviewImg" src="" alt="썸네일 미리보기">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="buttonGroup">
                    <button type="button" class="btn btnCancel" onclick="history.back()">취소</button>
                    <button type="submit" class="btn btnSubmit" id="submitBtn">업로드</button>
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