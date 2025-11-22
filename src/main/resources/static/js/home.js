// document.addEventListener('DOMContentLoaded', function () {
//     let lastScrollTop = 0; // 마지막 스크롤 위치 기억용
//
//     window.addEventListener('scroll', function () {
//         const currentScroll = window.scrollY || window.pageYOffset;
//
//         let botHeader =  document.querySelector('.botHeader');
//
//         if (currentScroll > lastScrollTop) {
//             //  스크롤 내림
//             botHeader.classList.add('hide');
//         } else {
//             //  스크롤 올림
//             botHeader.classList.remove('hide');
//         }
//
//         lastScrollTop = currentScroll <= 0 ? 0 : currentScroll; // 음수 방지
//     });
//
// });
//
