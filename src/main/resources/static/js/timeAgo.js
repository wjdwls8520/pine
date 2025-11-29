// function timeAgo(dateString) {
//     const target = new Date(dateString);
//     const now = new Date();
//     const diffMs = now - target;
//     const diffSec = Math.floor(diffMs / 1000);
//     const diffMin = Math.floor(diffSec / 60);
//     const diffHour = Math.floor(diffMin / 60);
//     const diffDay = Math.floor(diffHour / 24);
//     const diffWeek = Math.floor(diffDay / 7);
//     const diffMonth = Math.floor(diffDay / 30);
//     const diffYear = Math.floor(diffDay / 365);
//
//     if (diffSec < 60) return '방금 전';
//     if (diffMin < 60) return `${diffMin}분 전`;
//     if (diffHour < 24) return `${diffHour}시간 전`;
//     if (diffDay < 7) return `${diffDay}일 전`;
//     if (diffWeek < 5) return `${diffWeek}주 전`;
//     if (diffMonth < 12) return `${diffMonth}개월 전`;
//     return `${diffYear}년 전`;
//   }
//
//   document.addEventListener('DOMContentLoaded', () => {
//     document.querySelectorAll('#timeAgo').forEach((el) => {
//       const writeDate = el.dataset.writeDate;
//       if (writeDate) {
//         el.textContent = timeAgo(writeDate);
//       }
//     });
//   });

function timeAgoAjax(isoString) {
    // ISO 8601 문자열을 Date 객체로 변환
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