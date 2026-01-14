document.addEventListener('DOMContentLoaded', function() {

    // 상태 변수
    let currentPage = 0;
    let currentType = "COMMUNITY"; // 초기값
    let isLoading = false;
    let isLastPage = false;

    // DOM 요소 캐싱
    const resultWrapper = document.getElementById('pineResultWrapper');
    const loader = document.getElementById('pineLoader');
    const noResult = document.getElementById('pineNoResult');

    const sortSelect = document.getElementById('pineSortSelect');
    const periodSelect = document.getElementById('pinePeriodSelect');
    const groupSelect = document.getElementById('pineGroupSelect'); // 그룹 필터 박스

    const tabItems = document.querySelectorAll('.pineTabItem');

    // 초기 로드
    loadSearchResults(true);

    // 1. 탭 클릭 이벤트
    tabItems.forEach(tab => {
        tab.addEventListener('click', function() {
            // 모든 탭에서 active 제거
            tabItems.forEach(t => t.classList.remove('active'));
            // 클릭된 탭에 active 추가
            this.classList.add('active');

            currentType = this.getAttribute('data-type');

            // 탭 변경 시 그룹 필터 초기화 (전체보기로)
            if (groupSelect) groupSelect.value = "0";

            updateFilterOptions(currentType);
            loadSearchResults(true);
        });
    });

    // 2. 필터 변경 이벤트 (정렬, 기간, 그룹선택)
    const filters = [sortSelect, periodSelect, groupSelect];
    filters.forEach(select => {
        if(select) {
            select.addEventListener('change', function() {
                loadSearchResults(true);
            });
        }
    });

    // 3. 무한 스크롤
    window.addEventListener('scroll', function() {
        if ((window.innerHeight + window.scrollY) >= document.documentElement.scrollHeight - 100) {
            if (!isLoading && !isLastPage) {
                loadSearchResults(false);
            }
        }
    });

    // [유틸] 검색어 하이라이트 함수
    function highlightKeyword(text, keyword) {
        if (!keyword || !text) return text;
        const escapedKeyword = keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        const regex = new RegExp(`(${escapedKeyword})`, 'gi');
        return text.replace(regex, '<span class="pineKeywordHighlight">$1</span>');
    }

    // [핵심] 필터 옵션 UI 제어 (탭별 노출 옵션 관리)
    function updateFilterOptions(type) {
        const toggleDisplay = (selector, show) => {
            document.querySelectorAll(selector).forEach(el => {
                if (show) el.classList.remove('pineHidden');
                else el.classList.add('pineHidden');
            });
        };

        // --- 1. 초기화 (기본 상태: 포스트 기준) ---
        // 기간/정렬 박스 보이기
        if(periodSelect) periodSelect.classList.remove('pineHidden');
        if(sortSelect) sortSelect.classList.remove('pineHidden');
        if(groupSelect) groupSelect.classList.add('pineHidden'); // 그룹 선택은 기본 숨김

        // 옵션 초기화: '댓글순' 보이기, '멤버수순' 숨기기
        toggleDisplay('.postOnlyOption', true);
        toggleDisplay('.groupOnlyOption', false);

        // --- 2. 탭별 예외 처리 ---
        if (type === 'GROUP') {
            // [그룹] 댓글순 숨김, 멤버수순 보이기
            toggleDisplay('.postOnlyOption', false);
            toggleDisplay('.groupOnlyOption', true);

            // 기간 필터 숨김
            if(periodSelect) periodSelect.classList.add('pineHidden');

            // 만약 현재 정렬이 '댓글순'이면 '관련성순'으로 변경
            if (sortSelect.value === 'replies') sortSelect.value = 'relevance';

        } else if (type === 'GROUP_POST') {
            // [그룹 포스트] 그룹 선택 필터 보이기
            if(groupSelect) groupSelect.classList.remove('pineHidden');

            // (중요) 멤버수순 숨김 유지, 댓글순 보임 유지 (초기화 단계에서 이미 적용됨)

            // 만약 현재 정렬이 '멤버수순'이면 '관련성순'으로 리셋
            if (sortSelect.value === 'members') sortSelect.value = 'relevance';

        } else if (type === 'MEMBER') {
            // [멤버] 정렬/기간 박스 모두 숨김
            if(periodSelect) periodSelect.classList.add('pineHidden');
            if(sortSelect) sortSelect.classList.add('pineHidden');

        } else {
            // [커뮤니티, 쇼츠] (초기화 단계 설정 그대로 사용)
            // 만약 현재 정렬이 '멤버수순'이면 '관련성순'으로 리셋
            if (sortSelect.value === 'members') sortSelect.value = 'relevance';
        }
    }

    // 데이터 로드 함수
    function loadSearchResults(isReset) {
        if (isReset) {
            currentPage = 0;
            isLastPage = false;
            resultWrapper.innerHTML = '';
            noResult.classList.add('pineHidden');
        }

        if (isLoading || isLastPage) return;

        isLoading = true;
        loader.classList.remove('pineHidden'); // 로더 보이기

        const sort = sortSelect ? sortSelect.value : 'relevance';
        const period = periodSelect ? periodSelect.value : 'all';
        const groupId = groupSelect ? groupSelect.value : '0';

        const params = new URLSearchParams({
            type: currentType,
            keyword: currentKeyword,
            page: currentPage,
            sort: sort,
            period: period,
            groupId: groupId
        });

        fetch(`/api/search?${params.toString()}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(response => {
                const list = response.list;
                isLastPage = response.isLast;

                if (isReset && list.length === 0) {
                    noResult.classList.remove('pineHidden');
                } else {
                    renderList(list, currentType);
                    currentPage++;
                }
            })
            .catch(err => console.error("Search failed", err))
            .finally(() => {
                isLoading = false;
                loader.classList.add('pineHidden');
            });
    }

    // HTML 렌더링
    function renderList(list, type) {
        let html = '';

        list.forEach(item => {
            const defaultImg = '/images/user.png';
            const onErrorStr = `this.onerror=null; this.src='${defaultImg}';`;

            // 텍스트 하이라이팅 적용
            const highlightedTitle = item.title ? highlightKeyword(item.title, currentKeyword) : '';
            const highlightedContent = item.content ? highlightKeyword(item.content, currentKeyword) : '';
            const highlightedGroupName = item.groupName ? highlightKeyword(item.groupName, currentKeyword) : '';
            const highlightedGroupDesc = item.groupDescription ? highlightKeyword(item.groupDescription, currentKeyword) : '';
            const highlightedNickname = highlightKeyword(item.writerNickname || item.nickname, currentKeyword);

            // URL 및 클릭 액션 설정
            let detailUrl = '#';
            let clickAction = '';
            let cardClass = 'pineResultCard';

            if (type === 'GROUP') {
                detailUrl = `/group/gdetail/${item.id}`;
                clickAction = `location.href='${detailUrl}'`;

            } else if (type === 'COMMUNITY') {
                detailUrl = `/community/cdetail/${item.id}`;
                clickAction = `location.href='${detailUrl}'`;

            } else if (type === 'SHORTS') {
                detailUrl = `/shorts/view/${item.id}`;
                clickAction = `location.href='${detailUrl}'`;

            } else if (type === 'GROUP_POST') {
                const gid = item.groupId || 0;
                detailUrl = `/group/${gid}/post/detail/${item.id}`;
                clickAction = `location.href='${detailUrl}'`;

            } else if (type === 'MEMBER') {
                clickAction = '';
                cardClass += ' pineNoClick';
            }

            // HTML 생성
            if (type === 'GROUP') {
                html += `
                    <div class="${cardClass}" onclick="${clickAction}">
                        <img src="${item.groupImage || defaultImg}" class="pineCardThumbnail" onerror="${onErrorStr}">
                        <div class="pineCardContent">
                            <h3 class="pineCardTitle">${highlightedGroupName}</h3>
                            <div class="pineCardBody">${highlightedGroupDesc}</div>
                            <div class="pineGroupInfo">
                                <span class="pineMetaItem">멤버 ${item.memberCount}</span>
                                <span class="pineMetaItem">좋아요 ${item.likeCount || 0}</span>
                            </div>
                        </div>
                    </div>
                `;
            } else if (type === 'MEMBER') {
                html += `
                    <div class="${cardClass} pineMemberCard">
                        <img src="${item.profileImg || defaultImg}" class="pineUserProfileImg" onerror="${onErrorStr}">
                        <div class="pineCardContent">
                            <h3 class="pineCardTitle pineMemberTitle">${highlightedNickname}</h3>
                            <div class="pineCardBody pineMemberDesc">${item.profileMsg || ''}</div>
                        </div>
                    </div>
                `;
            } else {
                // 포스트 (커뮤니티, 쇼츠, 그룹포스트)
                const thumbHtml = item.thumbnail
                    ? `<img src="${item.thumbnail}" class="pineCardThumbnail" onerror="${onErrorStr}">`
                    : '';

                const titleHtml = highlightedTitle ? `<h3 class="pineCardTitle">${highlightedTitle}</h3>` : '';
                const dateStr = new Date(item.writeDate).toLocaleDateString();

                html += `
                    <div class="${cardClass}" onclick="${clickAction}">
                        ${thumbHtml}
                        <div class="pineCardContent">
                            <div class="pineCardHeader">
                                <img src="${item.writerProfile || defaultImg}" class="pineUserProfileImg" onerror="${onErrorStr}">
                                <span>${item.writerNickname}</span>
                                <span>${dateStr}</span>
                            </div>
                            ${titleHtml}
                            <div class="pineCardBody">${highlightedContent}</div>
                            
                            <div class="pineMetaInfo">
                                <span class="pineMetaItem">${item.likeCount || 0} 좋아요</span>
                                <span class="pineMetaItem">${item.replyCount || 0} 댓글</span>
                            </div>
                        </div>
                    </div>
                `;
            }
        });

        resultWrapper.insertAdjacentHTML('beforeend', html);
    }
});