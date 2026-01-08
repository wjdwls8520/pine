/**
 * common.js - 사이트 전역 공통 유틸리티
 * 1.토스트메시지 - showToastMsg
 * 2.날짜변환 - timeAgoAjax
 */

// 1. 공통 토스트 메시지
// jsp 하단에(footer 위)<div id="commonToast" class="toastMsg"></div> 태그넣기
// 스크립트에서 showToastMsg("메세지입력") 로 호출
function showToastMsg(message) {
    const toast = document.getElementById("commonToast");
    if (!toast) {
        alert(message); // 토스트 박스가 없으면 alert로 대체
        return;
    }

    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2000);
}
// --------------------------------------------------------------




// 2. 날짜변환
//
function timeAgoAjax(isoString) {
    // ISO 8601 문자열을 Date 객체로 변환
    if (!isoString) return "";

    const inputDate = new Date(isoString);
    const now = new Date();
    const diffMs = now - inputDate; // 밀리초 차이

    const seconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30); // 단순화: 30일 = 1달
    const years = Math.floor(days / 365); // 단순화: 365일 = 1년

    if (seconds <= 60) {
        return `방금 전`;
    } else if (minutes < 60) {
        return `${minutes}분 전`;
    } else if (hours < 24) {
        return `${hours}시간 전`;
    } else if (days < 30) {
        return `${days}일 전`;
    } else if (months < 12) {
        return `${months}달 전`;
    } else {
        return `${years}년 전`;
    }
}

// --------------------------------------------------------------
//링크복사
// https://ko.wikipedia.org/wiki/%EB%B3%B5%EC%82%AC
function copyCurrentPostUrl() {
    const url = window.location.href;

    // 1. 최신 방식 (HTTPS, localhost)
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(url)
            .then(() => {
                showToastMsg("게시글 주소가 복사되었습니다");
            })
            .catch(err => {
                console.error('URL 복사 실패 (최신 방식):', err);
                // 최신 방식 실패 시 레거시 방식으로 재시도
                fallbackCopyTextToClipboard(url);
            });
    } else {
        // 2. 구형 방식 또는 HTTP 환경 (Fallback)
        fallbackCopyTextToClipboard(url);
    }
}

// 혹시 모를 상황을 대비한 레거시 복사 함수
function fallbackCopyTextToClipboard(text) {
    const textArea = document.createElement("textarea");
    textArea.value = text;

    // 화면 밖으로 보내서 안 보이게 처리
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.position = "fixed";

    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        const successful = document.execCommand('copy'); // 예전 명령어
        if (successful) {
            showToastMsg("게시글 주소가 복사되었습니다");
        } else {
            alert("주소 복사에 실패했습니다.");
        }
    } catch (err) {
        console.error('URL 복사 실패 (레거시):', err);
        alert("주소 복사에 실패했습니다.");
    }

    document.body.removeChild(textArea);
}
// --------------------------------------------------------------