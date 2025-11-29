<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
</head>
<body>

<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article workspace commuCreatePage">
        <form class="createForm" id="commuCreateForm" method="post" action="/community/cCreate" enctype="multipart/form-data">
            <section class="formGrid">
                <div class="formMain">

<%--                카테고리--%>
                    <div class="fieldGroup">
                        <label for="communitySelect" class="fieldLabel">카테고리</label>
                        <select id="communitySelect" class="fieldControl" name="category">
                            <option value="1">General</option>
                            <option value="2">Travel</option>
                            <option value="3">K-POP</option>
                            <option value="4">Trend</option>
                            <option value="5">Game</option>
                            <option value="6">Ask</option>
                        </select>
                    </div>

<%--                    본문    --%>
                    <div class="fieldGroup">
                        <label for="postBody" class="fieldLabel">본문</label>
                        <div id="editor"></div>
                        <textarea id="postBody" name="postBody" hidden></textarea>
                    </div>

<%--                        업로드--%>
                    <section class="mediaManager">
                        <div class="mediaHeader">
                            <div>
                                <p class="fieldLabel">미디어 업로드</p>
                                <p class="mediaDescription">이미지 또는 영상 파일을 추가하면 피드에서 텍스트 아래 갤러리로 표시됩니다.</p>
                            </div>
                            <label class="ghostButton ghostButton--secondary">
                                파일 선택
                                <input type="file" name="files" id="mediaUploadInput" multiple accept="image/*,video/*" hidden />
                            </label>
                        </div>
                        <div id="mediaDropzone" class="mediaDropzone">
                            <p>여기로 파일을 드래그하거나 클릭하여 선택하세요.</p>
                            <span>최대 10개 · 파일당 50MB 권장</span>
                        </div>
                        <ul id="mediaPreviewList" class="mediaPreviewList">
                            <li class="mediaEmpty">첨부된 파일이 없습니다.</li>
                        </ul>
                        <input type="hidden" id="mediaJsonInput" name="mediaJson" />
                    </section>

                    <div class="metaGrid">
                        <div class="fieldGroup">
                            <label class="fieldLabel" for="tagInput">태그</label>
                            <input id="tagInput" type="text" class="fieldControl" name="file" placeholder="#밈 #정보" />
                        </div>
                        <div class="fieldGroup">
                            <label class="fieldLabel" for="visibilitySelect">노출 범위</label>
                            <select id="visibilitySelect" class="fieldControl" name="status">
                                <option value="0">공개</option>
                                <option value="1">비공개</option>

                            </select>
                        </div>
                    </div>

                    <div class="submitRow">
                        <button type="button" class="ghostButton">취소</button>
                        <button type="submit" class="primaryButton">게시하기</button>
                    </div>

                </div>

            </section>
        </form>
    </article>
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script src="/js/toastUI.js"></script>
<script src="/js/commujs/community_common.js"></script>
</body>
</html>

