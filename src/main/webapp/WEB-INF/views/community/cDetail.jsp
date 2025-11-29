<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">
</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>
<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace commuCreatePage">
        <form class="createForm" id="commuCreateForm" method="post" action="/community/cCreate">
            <section class="formGrid">
                <div class="formMain">
                    <div class="fieldGroup">
                        <label for="communitySelect" class="fieldLabel">커뮤니티</label>
                        <select id="communitySelect" class="fieldControl" name="category">
                            <option value="1">r/pinecommunity</option>
                            <option value="2">r/kculturetalk</option>
                            <option value="3">r/kpopdaily</option>
                        </select>
                    </div>

<%--                    <div class="fieldGroup">--%>
<%--                        <label for="postTitle" class="fieldLabel">제목</label>--%>
<%--                        <input id="postTitle" type="text" class="fieldControl" placeholder="제목을 입력하세요" />--%>
<%--                    </div>--%>

                    <div class="fieldGroup">
                        <label for="postBody" class="fieldLabel">본문</label>
                        <textarea id="postBody" class="fieldControl" name="bodyHtml" rows="12"
                                  placeholder="본문을 입력하세요."></textarea>
                    </div>

                    <section class="mediaManager">
                        <div class="mediaHeader">
                            <div>
                                <p class="fieldLabel">미디어 업로드</p>
                                <p class="mediaDescription">이미지 또는 영상 파일을 추가하면 피드에서 텍스트 아래 갤러리로 표시됩니다.</p>
                            </div>
                            <label class="ghostButton ghostButton--secondary">
                                파일 선택
                                <input type="file" name="file" id="mediaUploadInput" multiple accept="image/*,video/*" hidden />
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

<%--                    <div class="toggleRow">--%>
<%--                        <label class="toggleItem">--%>
<%--                            <input type="checkbox" checked /> 댓글 허용--%>
<%--                        </label>--%>
<%--                        <label class="toggleItem">--%>
<%--                            <input type="checkbox" /> 스포일러--%>
<%--                        </label>--%>
<%--                        <label class="toggleItem">--%>
<%--                            <input type="checkbox" /> NSFW--%>
<%--                        </label>--%>
<%--                    </div>--%>

                    <div class="submitRow">
                        <button type="button" class="ghostButton">취소</button>
                        <button type="submit" class="primaryButton">게시하기</button>
                    </div>
                </div>

<%--                <aside class="formSidebar">--%>
<%--                    <div class="guideCard">--%>
<%--                        <h3>커뮤니티 가이드</h3>--%>
<%--                        <ul>--%>
<%--                            <li>커뮤니티 주제와 맞는 내용을 올려주세요.</li>--%>
<%--                            <li>근거 없는 주장보다 팩트·출처를 명시하면 업보트 상승!</li>--%>
<%--                            <li>slash 명령으로 블록을 빠르게 바꿔보세요.</li>--%>
<%--                        </ul>--%>
<%--                    </div>--%>

<%--                    <div class="previewCard">--%>
<%--                        <div class="previewHeader">--%>
<%--                            <span class="badge">미리보기</span>--%>
<%--                            <span>r/pinecommunity</span>--%>
<%--                        </div>--%>
<%--                        <p class="previewHint">작성 중인 내용이 여기에 표시됩니다.</p>--%>
<%--                        <div class="previewMeta">--%>
<%--                            <span>⬆ 0</span>--%>
<%--                            <span>💬 0</span>--%>
<%--                            <span>저장 0</span>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </aside>--%>

            </section>
        </form>
    </article>
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>
</body>
</html>

