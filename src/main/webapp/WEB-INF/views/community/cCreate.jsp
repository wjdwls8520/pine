<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>
    <link rel="stylesheet" href="/css/post.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace commu">


        <%-- section page--%>
        <h2 class="pageTitle">community write</h2>
        <section class="section section01 commuCreate">
            <div>
                <select class="pCategorySelect">
                    <option>자유게시판</option>
                    <option>kpop</option>
                    <option>트랜드</option>
                </select>
            </div>
            <div class="pMideaUploadBox">미디어 업로드 섹션</div>
            <div class="pTxtBox">
                <textarea placeholder="본문 텍스트" ></textarea>
            </div>
            <button class="btnPost">게시하기</button>

        </section>



    </article
</div>
<jsp:include page="../include/commu_footer.jsp"></jsp:include>