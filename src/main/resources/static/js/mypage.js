document.addEventListener('DOMContentLoaded', function() {
    // 상태 변수
    let currentTab = 'posts';
    let currentSubTab = 'all';
    let currentPage = 0;
    let totalPages = 0;
    let isLoading = false;

    // DOM 요소
    const tabItems = document.querySelectorAll('.mpTabBtn');
    const subTabItems = document.querySelectorAll('.mpSubBtn');
    const contentArea = document.getElementById('mpContentArea');
    const loader = document.getElementById('mpLoader');
    const emptyState = document.getElementById('mpEmptyState');
    const pagination = document.getElementById('mpPagination');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const pageInfo = document.getElementById('pageInfo');
    const postSubTabs = document.getElementById('postSubTabs');

    // 초기 실행
    init();

    function init() {
        if (tabItems.length > 0) {
            activateTab(tabItems[0]);
        }
        loadContent(true);
    }

    // 메인 탭 클릭 이벤트
    tabItems.forEach(tab => {
        tab.addEventListener('click', function() {
            if (isLoading) return;
            activateTab(this);
            loadContent(true);
        });
    });

    function activateTab(targetTab) {
        tabItems.forEach(t => t.classList.remove('active'));
        targetTab.classList.add('active');

        currentTab = targetTab.getAttribute('data-tab');
        currentPage = 0;
        currentSubTab = 'all';

        // 서브 탭 제어
        if (currentTab === 'posts') {
            postSubTabs.classList.remove('hidden');
            subTabItems.forEach(st => st.classList.remove('active'));
            subTabItems[0].classList.add('active');
        } else {
            postSubTabs.classList.add('hidden');
        }
    }

    // 서브 탭 클릭 이벤트
    subTabItems.forEach(subTab => {
        subTab.addEventListener('click', function() {
            if (isLoading || currentTab !== 'posts') return;

            subTabItems.forEach(st => st.classList.remove('active'));
            this.classList.add('active');

            currentSubTab = this.getAttribute('data-subtab');
            currentPage = 0;
            loadContent(true);
        });
    });

    // 페이지네이션
    prevBtn.addEventListener('click', () => { if (!isLoading && currentPage > 0) { currentPage--; loadContent(true); }});
    nextBtn.addEventListener('click', () => { if (!isLoading && currentPage < totalPages - 1) { currentPage++; loadContent(true); }});

    // 콘텐츠 로드 함수
    function loadContent(reset) {
        if (isLoading) return;
        isLoading = true;

        if (reset) contentArea.innerHTML = '';

        // [수정] 탭에 따라 CSS 클래스(그리드 vs 리스트) 결정
        contentArea.className = '';

        // 1. 쇼츠 탭이거나 2. 내 그룹 탭일 때만 그리드 적용
        // (전체 탭에서의 쇼츠는 리스트로 보여야 하므로 currentSubTab === 'SHORTS' 조건 필수)
        if ((currentTab === 'posts' && currentSubTab === 'SHORTS') || currentTab === 'groups') {
            contentArea.classList.add('mpGrid');
        } else {
            contentArea.classList.add('mpList');
        }

        loader.classList.remove('hidden');
        emptyState.classList.add('hidden');
        pagination.classList.add('hidden');

        let url = '';
        switch (currentTab) {
            case 'posts': url = `/mypage/api/posts?page=${currentPage}&size=10`; break;
            case 'comments': url = `/mypage/api/comments?page=${currentPage}&size=10`; break;
            case 'likes': url = `/mypage/api/likes?page=${currentPage}&size=10`; break;
            case 'groups': url = `/mypage/api/groups?page=${currentPage}&size=10`; break;
        }

        fetch(url)
            .then(res => {
                if (!res.ok) throw new Error('Network error');
                return res.json();
            })
            .then(data => {
                totalPages = data.totalPages || 0;

                if (currentTab === 'groups') {
                    renderGroups(data.content);
                } else {
                    renderPosts(data.content);
                }

                if (!data.content || data.content.length === 0) {
                    emptyState.classList.remove('hidden');
                } else if (totalPages > 1) {
                    pagination.classList.remove('hidden');
                    pageInfo.textContent = `${currentPage + 1} / ${totalPages}`;
                    prevBtn.disabled = currentPage === 0;
                    nextBtn.disabled = !data.hasNext;
                }
            })
            .catch(err => {
                console.error(err);
                emptyState.textContent = "오류가 발생했습니다.";
                emptyState.classList.remove('hidden');
            })
            .finally(() => {
                isLoading = false;
                loader.classList.add('hidden');
            });
    }

    // [핵심] 게시글 렌더링
    function renderPosts(posts) {
        if (!posts) return;

        let displayData = posts;
        if (currentTab === 'posts' && currentSubTab !== 'all') {
            displayData = posts.filter(p => p.postType === currentSubTab);
        }

        if (displayData.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }

        const html = displayData.map(post => {
            // 쇼츠인지 여부
            const isShorts = (post.postType === 'SHORTS');
            // 쇼츠 탭("전용 탭")에 있는지 여부
            const isShortsTab = (currentSubTab === 'SHORTS');

            const targetId = post.postId || post.id;
            // 그룹 포스트일 경우 groupId가 필요할 수 있음 (DTO에 groupId가 있다면 post.groupId 사용)
            const groupId = post.groupId || 0;

            // ============================================
            // CASE 1: 쇼츠 탭 (그리드 + 메타 정보 표시) [수정]
            // ============================================
            if (isShorts && isShortsTab) {
                let imgTag = post.thumbnailImage
                    ? `<img src="${escapeHtml(post.thumbnailImage)}" alt="shorts">`
                    : `<div style="width:100%;height:100%;background:#000;"></div>`;

                let displayText = post.title || post.content || 'No Content';

                return `
                    <div class="mpGridCard" onclick="goToPost('${post.postType}', ${targetId}, ${groupId})">
                        <div class="mpGridThumb shortsRatio">${imgTag}</div>
                        <div class="mpGridInfo">
                            <div class="mpGridTitle">${escapeHtml(displayText)}</div>
                            <div class="mpGridMeta">
                                좋아요 ${post.likeCount} · 댓글 ${post.replyCount} · ${formatDate(post.writeDate)}
                            </div>
                        </div>
                    </div>
                `;
            }

                // ============================================
                // CASE 2: 전체 탭 or 다른 탭 (리스트 스타일) [수정]
                // - 쇼츠도 여기서 리스트 형태로 렌더링됨
            // ============================================
            else {
                let thumbTag = '';
                if (post.thumbnailImage) {
                    thumbTag = `<div class="mpListThumb"><img src="${escapeHtml(post.thumbnailImage)}" alt="thumb"></div>`;
                } else {
                    thumbTag = `<div class="mpListThumb"><div class="mpListThumbPlaceholder">${isShorts ? 'SHORTS' : 'TEXT'}</div></div>`;
                }

                // 텍스트 정제
                let rawText = post.title ? post.title : post.content;
                if (!rawText) rawText = "내용 없음";
                let cleanText = rawText.replace(/<[^>]*>?/gm, '');

                let badge = `<span class="mpSmallBadge">${getPostTypeLabel(post.postType)}</span>`;

                return `
                    <div class="mpListCard" onclick="goToPost('${post.postType}', ${targetId}, ${groupId})">
                        ${thumbTag}
                        <div class="mpListContent">
                            <div class="mpContentText">
                                ${badge} ${escapeHtml(cleanText)}
                            </div>
                            <div class="mpMetaInfo">
                                좋아요 ${post.likeCount} · 댓글 ${post.replyCount} · ${formatDate(post.writeDate)}
                            </div>
                        </div>
                    </div>
                `;
            }
        }).join('');

        contentArea.innerHTML = html;
    }

    // [핵심] 그룹 렌더링 (그리드 스타일로 변경) [수정]
    function renderGroups(groups) {
        if (!groups || groups.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }

        const html = groups.map(group => {
            // 그룹 이미지는 1:1 비율이 예쁨
            let imgTag = group.groupImage
                ? `<img src="${escapeHtml(group.groupImage)}" alt="group">`
                : `<div style="width:100%;height:100%;background:linear-gradient(135deg, #dbeafe 0%, #1e40af 100%); display:flex; align-items:center; justify-content:center; color:#fff; font-weight:bold;">GROUP</div>`;

            let roleBadge = group.myRole === 'MASTER'
                ? `<span style="color:#fa233b; font-weight:bold; font-size:12px;">Master</span>`
                : `<span style="color:#86868b; font-size:12px;">Member</span>`;

            return `
                <div class="mpGridCard" onclick="goToGroup(${group.groupId})">
                    <div class="mpGridThumb">${imgTag}</div>
                    <div class="mpGridInfo">
                        <div class="mpGridTitle">${escapeHtml(group.groupName)}</div>
                        <div class="mpGridMeta">
                            ${roleBadge} · 멤버 ${group.totalMemberCount}명
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        contentArea.innerHTML = html;
    }

    // 유틸리티
    function getPostTypeLabel(type) {
        const map = { 'COMMUNITY': '커뮤니티', 'SHORTS': '쇼츠', 'GROUP': '그룹' };
        return map[type] || '게시글';
    }

    function formatDate(dateStr) {
        if(!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text.replace(/[&<>"']/g, function(m) {
            return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m];
        });
    }

    // 이동 함수 (groupId 추가됨)
    window.goToPost = function(type, postId, groupId) {
        if(type === 'SHORTS') location.href = `/shorts/view/${postId}`;
        else if(type === 'GROUP') {
            if(groupId) location.href = `/group/${groupId}/post/detail/${postId}`;
        }
        else location.href = `/community/cdetail/${postId}`;
    };

    window.goToGroup = function(groupId) {
        window.location.href = `/group/gdetail/${groupId}`;
    };
});