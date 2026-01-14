<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
            <form class="uploadForm" id="updateForm">
                <h1 class="formTitle">쇼츠 게시물 수정</h1>

                <input type="hidden" id="postId" name="postId" value="${shorts.postId}">

                <div class="fieldGroup">
                    <label class="fieldLabel">비디오 파일</label>
                    <div class="infoBox">
                        <span class="icon">⚠️</span>
                        <div>
                            <strong>동영상 파일은 수정할 수 없습니다.</strong><br>
                            영상을 변경하려면 현재 게시글을 삭제한 후 다시 업로드해주세요.
                        </div>
                    </div>
                </div>

                <div class="fieldGroup">
                    <label for="title" class="fieldLabel">제목 수정</label>
                    <input type="text" id="title" name="title" class="fieldControl"
                           placeholder="쇼츠 제목을 입력하세요" maxlength="100"
                           value="${shorts.title}" required>
                </div>

                <div class="fieldGroup">
                    <label for="content" class="fieldLabel">설명 수정</label>
                    <textarea id="content" name="content" class="fieldControl"
                              placeholder="쇼츠에 대한 설명을 입력하세요 (최대 200자)"
                              maxlength="200">${shorts.content}</textarea>
                </div>

                <div class="fieldGroup">
                    <label for="tagInput" class="fieldLabel">태그 수정</label>
                    <div class="tagInputContainer">
                        <input type="text" id="tagInput" class="fieldControl"
                               placeholder="태그를 입력하고 Enter를 누르세요 (예: #한국 #여행)">
                        <div class="tagHint">최대 5개까지 추가 가능합니다</div>

                        <input type="hidden" id="hiddenTags" name="tags"
                               value="<c:forEach var="tag" items="${shorts.tags}" varStatus="status">${tag}<c:if test="${!status.last}">,</c:if></c:forEach>">

                        <div class="tagList" id="tagList"></div>
                    </div>
                </div>

                <div class="fieldGroup">
                    <label class="fieldLabel">썸네일 변경 (선택)</label>
                    <div class="thumbnailSection">
                        <c:if test="${not empty shorts.files}">
                            <c:forEach var="file" items="${shorts.files}">
                                <c:if test="${file.contentType.startsWith('image/')}">
                                    <div class="currentThumbnail">
                                        <p>현재 썸네일</p>
                                        <img src="${file.path}" alt="Current Thumbnail">
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:if>

                        <div class="thumbnailUploadArea active thumbnailUpdateArea" id="thumbnailUploadArea"
                             onclick="document.getElementById('thumbnailFile').click()">

                            <div class="thumbnailUploadAreaContent">
                                <div class="thumbnailUploadIcon">🖼️</div>
                                <div class="thumbnailUploadText">새로운 썸네일 이미지를 선택하세요 (변경 시)</div>
                            </div>
                            <input type="file" id="thumbnailFile" name="thumbnailFile" class="fileInput" accept="image/*">
                        </div>

                        <div class="thumbnailPreviewArea">
                            <div class="thumbnailPreview" id="thumbnailPreview">
                                <img id="thumbnailPreviewImg" src="" alt="새 썸네일 미리보기">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="buttonGroup">
                    <button type="button" class="btn btnCancel" onclick="history.back()">취소</button>
                    <button type="button" class="btn btnSubmit" id="submitBtn" onclick="submitEdit()">수정 완료</button>
                </div>
            </form>
        </div>
    </article>
</div>

<jsp:include page="../include/shorts_footer.jsp"></jsp:include>
<script src="/js/shortsjs/shortsUpload.js"></script>
<script src="/js/commujs/tag.js"></script>

</body>
</html>