<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer id="footer" class="subFooter">
    <div class="inner" id="shortsFooterInner">
        </div>
</footer>

<script>
    window.addEventListener("load", () => {
        const path = window.location.pathname;
        const footerInner = document.getElementById("shortsFooterInner");

        if (!footerInner) return;

        // ==========================================
        // [CASE 1] 업로드 페이지 (/shorts/shortsUpload)
        // ==========================================
        if (path.includes("/shorts/shortsUpload")) {
            footerInner.innerHTML = `
                <div class="formActions">
                    <div class="cancelButton">
                        <button type="button" class="btnWH btnCancel" onclick="history.back()">취소</button>
                    </div>
                    <div class="stepButtons">
                        <button type="button" class="btnWH btnSubmit" onclick="submitShorts()">업로드</button>
                    </div>
                </div>
            `;
        }
        // ==========================================
        // [CASE 2] 그 외 페이지 (메인 등)
        // ==========================================
        else {
            // .inner 바로 아래에 <ul>이 들어가므로 #footer .inner > ul 스타일이 적용됨
            footerInner.innerHTML = `
                <ul>
                    <li><a href="/shorts/shortsUpload">쇼츠업로드</a></li>
                    <li><a href="#" onclick="window.scrollTo({top: 0, behavior: 'smooth'}); return false;">맨위로</a></li>
                </ul>
            `;
        }
    });

</script>