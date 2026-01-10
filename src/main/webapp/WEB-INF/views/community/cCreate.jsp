<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">
    <link rel="stylesheet" href="/css/toastui_bundle.css" />
</head>
<body>

<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>

    <article class="article workspace commuCreatePage">
        <%--이 페이지에서 작성,수정 둘다 함--%>
        <h2>${not empty post ? '커뮤니티 포스트 수정' : '커뮤니티 포스트 작성'}</h2>
        <form class="createForm" id="commuCreateForm" method="post" action="/community/cCreate" enctype="multipart/form-data">

            <%-- JS에서 수정/작성 구분하기 위한 히든 값 추가 --%>
            <input type="hidden" id="mode" value="${not empty post ? 'edit' : 'create'}">
            <input type="hidden" id="postId" value="${post.id}">

            <section class="formGrid">
                <div class="formMain">

<%--                카테고리--%>
                    <div class="fieldGroup">
                        <label for="communitySelect" class="fieldLabel">카테고리</label>
                        <select id="communitySelect" class="fieldControl" name="category">
                            <option value="1" ${post.category == 1 ? 'selected' : ''}>General</option>
                            <option value="2" ${post.category == 2 ? 'selected' : ''}>Travel</option>
                            <option value="3" ${post.category == 3 ? 'selected' : ''}>K-POP</option>
                            <option value="4" ${post.category == 4 ? 'selected' : ''}>Trend</option>
                            <option value="5" ${post.category == 5 ? 'selected' : ''}>Game</option>
                            <option value="6" ${post.category == 6 ? 'selected' : ''}>Ask</option>
                        </select>
                    </div>

<%--                    본문    --%>
                    <div class="fieldGroup">
                        <label for="postBody" class="fieldLabel">본문</label>
                        <div id="editor"></div>
                        <textarea id="postBody" name="postBody" hidden>${post.content}</textarea>
                    </div>

                    <div class="fieldGroup tagInputWrapper">
                        <label class="fieldLabel" for="tagInput">태그</label>
                        <input type="text" id="tagInput" placeholder="태그를 입력하고 Enter" />
                        <div id="tagList" class="tagList"></div>
                        <%-- [수정] value에 기존 태그들을 콤마로 연결해서 넣어둠 --%>
                        <input type="hidden" name="tags" id="hiddenTags" value="<c:if test="${not empty post.tags}">
                            <c:forEach items="${post.tags}" var="tag" varStatus="status">${tag}${!status.last ? ',' : ''}
                            </c:forEach>
                        </c:if>" />
                    </div>

<%--                        업로드--%>
                    <section class="mediaManager">
                        <div class="mediaHeader">
                            <div>
                                <p class="fieldLabel">미디어 업로드</p>
                                <p class="mediaDescription">이미지 또는 영상 파일을 추가하면 피드에서 텍스트 아래 갤러리로 표시됩니다.</p>
                            </div>
                        </div>

                        <div id="mediaDropzone" class="mediaDropzone">
                            <p>여기로 파일을 드래그하거나</p>
                            <button type="button" id="dropzoneSelectBtn">파일 선택</button>
                            <div id="mediaSlider" class="mediaSlider"></div>
                            <span>파일 업로드 제한 최대 10개 · 최대 크기 50MB</span>
                        </div>

                        <%-- 1.새 파일 업로드용 input --%>
                        <input type="file" name="files" id="mediaUploadInput" multiple accept="image/*,video/*" hidden />
                        <input type="hidden" id="mediaJsonInput" name="mediaJson" />

                        <%-- 2.삭제할 기존 파일 ID들을 담을 곳 (서버 전송용) --%>
                        <input type="hidden" id="deleteFileIds" />

                        <%-- 3.서버에서 가져온 기존 파일 리스트를 JSON 문자열로 저장 --%>
                        <%-- JSTL을 이용해 JSON 배열 문자열을 직접 만듭니다 --%>
                        <input type="hidden" id="serverFileList"
                               value='[
                           <c:if test="${not empty post.files}">
                               <c:forEach items="${post.files}" var="f" varStatus="st">
                                   {
                                       "id": "${f.id}",
                                       "path": "${f.path}",
                                       "name": "${f.originalname}",
                                       "type": "${f.contentType}"
                                   }${!st.last ? "," : ""}
                               </c:forEach>
                           </c:if>
                           ]'
                        />

                    </section>

                    <div class="metaGrid">
                        <div class="fieldGroup">
                            <label class="fieldLabel" for="visibilitySelect">노출 범위</label>
                            <select id="visibilitySelect" class="fieldControl" name="status">
                                <option value="0" ${post.status == 0 ? 'selected' : ''}>공개</option>
                                <option value="1" ${post.status == 1 ? 'selected' : ''}>비공개</option>
                            </select>
                        </div>
                    </div>
                </div>

            </section>
        </form>
    </article>
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
<script src="/js/toastui_bundle.js"></script>
<script src="/js/toastUI.js"></script>
<script src="/js/sortable.min.js"></script>
<script src="/js/commujs/commuCreate.js"></script>
<script src="/js/commujs/tag.js"></script>
</body>
</html>

