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