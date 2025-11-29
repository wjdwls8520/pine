<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"></jsp:include>

    <link rel="stylesheet" href="/css/group_common.css">


</head>
<body>
<jsp:include page="../include/header.jsp"></jsp:include>

<div class="wrap">
    <jsp:include page="../include/sideBar.jsp"></jsp:include>
    <article class="article workspace group">

        <%-- section page--%>
        <section class="section groupMain">
            <h2 class="bigTitle">그룹 만들기</h2>

            <div id="contentsWrap" class="contentsWrap groupCreateForm">
                <form id="groupCreateForm" method="post" action="/group/gcreate" enctype="multipart/form-data">

                    <div class="stepIndicator">
                        <div class="stepItem active" data-step="1">
                            <span class="stepNumber">1</span>
                            <span class="stepLabel">기본 정보</span>
                        </div>
                        <div class="stepItem" data-step="2">
                            <span class="stepNumber">2</span>
                            <span class="stepLabel">가입 설정</span>
                        </div>
                        <div class="stepItem" data-step="3">
                            <span class="stepNumber">3</span>
                            <span class="stepLabel">카테고리</span>
                        </div>
                    </div>

                    <div class="formStep active" data-step="1">
                        <%-- 그룹 이름 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">그룹명</h3>
                                <p class="formDescription">그룹 이름은 변경할 수 없으니 신중하게 선택하세요.</p>
                            </div>
                            <input type="text"
                                   id="groupName"
                                   name="groupName"
                                   class="formInput"
                                   placeholder="그룹 이름을 입력하세요"
                                   maxlength="50"
                                   required />
                            <div class="inputHelper">
                                <span class="charCount"><span id="nameCharCount">0</span>/50</span>
                            </div>
                        </div>

                        <%-- 그룹 소개 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">그룹 소개</h3>
                                <p class="formDescription">그룹에 대한 간단한 설명을 작성해주세요.</p>
                            </div>
                            <textarea id="groupDescription"
                                      name="groupDescription"
                                      class="formTextarea"
                                      placeholder="이 그룹은 무엇에 관한 것인가요?"
                                      maxlength="500"
                                      rows="4"
                                      required></textarea>
                            <div class="inputHelper">
                                <span class="charCount"><span id="descCharCount">0</span>/500</span>
                            </div>
                        </div>

                        <%-- 그룹 대표 이미지 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">그룹 대표 이미지</h3>
                                <p class="formDescription">그룹을 대표하는 이미지를 업로드하세요. (필수)</p>
                            </div>
                            <div class="imageUploadArea">
                                <div class="imagePreview" id="iconPreview">
                                    <img id="iconPreviewImg" src="" alt="이미지 미리보기" style="display: none;" />
                                    <div class="imagePlaceholder">
                                        <span>이미지 업로드</span>
                                    </div>
                                </div>
                                <input type="file"
                                       id="iconUpload"
                                       name="groupImg"
                                       accept="image/*"
                                       class="fileInput"
                                       required />
                                <label for="iconUpload" class="uploadButton">이미지 선택</label>
                            </div>
                        </div>
                    </div>

                    <div class="formStep" data-step="2">
                        <%-- 가입 승인 설정 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">가입 승인 설정</h3>
                                <p class="formDescription">그룹 가입 시 승인이 필요한지 설정하세요.</p>
                            </div>
                            <div class="radioGroup">
                                <label class="radioOption">
                                    <input type="radio" name="joinState" value="1" checked />
                                    <div class="radioContent">
                                        <div class="radioTitle">승인 필요</div>
                                        <div class="radioDescription">그룹 관리자가 가입 요청을 승인해야 합니다</div>
                                    </div>
                                </label>
                                <label class="radioOption">
                                    <input type="radio" name="joinState" value="0" />
                                    <div class="radioContent">
                                        <div class="radioTitle">승인 불필요</div>
                                        <div class="radioDescription">누구나 자유롭게 가입할 수 있습니다</div>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <%-- 자동 승인 설정 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">자동 승인 설정</h3>
                                <p class="formDescription">가입 요청 시 자동으로 승인할지 설정하세요.</p>
                            </div>
                            <div class="radioGroup">
                                <label class="radioOption">
                                    <input type="radio" name="autoJoin" value="1" checked />
                                    <div class="radioContent">
                                        <div class="radioTitle">자동 승인</div>
                                        <div class="radioDescription">가입 요청이 자동으로 승인됩니다</div>
                                    </div>
                                </label>
                                <label class="radioOption">
                                    <input type="radio" name="autoJoin" value="0" />
                                    <div class="radioContent">
                                        <div class="radioTitle">수동 승인</div>
                                        <div class="radioDescription">그룹 관리자가 직접 승인해야 합니다</div>
                                    </div>
                                </label>
                            </div>
                        </div>

                        <%-- 사용자 제한 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">그룹 인원 제한</h3>
                                <p class="formDescription">그룹에 가입할 수 있는 최대 인원을 설정하세요.</p>
                            </div>
                            <input type="number"
                                   id="userLimit"
                                   name="userLimit"
                                   class="formInput"
                                   placeholder="최대 인원 수"
                                   min="1"
                                   max="1000"
                                   value="10"
                                   required />
                            <div class="inputHelper">
                                <span class="inputHint">1명 이상 1000명 이하로 설정 가능합니다</span>
                            </div>
                        </div>

                        <%-- 18+ 콘텐츠 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">성인 콘텐츠</h3>
                            </div>
                            <label class="checkboxOption">
                                <input type="checkbox" name="nsfw" value="true" />
                                <div class="checkboxContent">
                                    <div class="checkboxTitle">18+ 성인 콘텐츠</div>
                                    <div class="checkboxDescription">이 그룹에는 성인 콘텐츠가 포함되어 있습니다</div>
                                </div>
                            </label>
                        </div>
                    </div>

                    <div class="formStep" data-step="3">
                        <%-- 카테고리 선택 섹션 --%>
                        <div class="formSection">
                            <div class="formHeader">
                                <h3 class="formTitle">카테고리</h3>
                                <p class="formDescription">그룹을 분류할 카테고리를 선택하세요. (선택사항, 최대 5개)</p>
                            </div>
                            <div class="categoryGrid">
                                <c:forEach var="item" items="${list}" varStatus="idx">
                                    <label class="categoryCheckbox">
                                        <input type="checkbox"
                                               id="chk_${item.id}"
                                               name="categoryIds"
                                               value="${item.id}"
                                               class="categoryInput" />
                                        <span class="categoryLabel">${item.nameKor}</span>
                                    </label>
                                </c:forEach>
                            </div>
                            <div class="inputHelper">
                                <span class="categoryCount"><span id="categoryCount">0</span>/5</span>
                                <span class="inputHint">카테고리를 선택하면 추천 노출에 도움이 됩니다</span>
                            </div>
                        </div>
                    </div>

                    <%-- 제출 버튼 / 단계 이동 (group_footer에서 렌더링) --%>
                    <!-- <div class="formActions" id="formActions"></div> -->
                </form>
            </div>
        </section>

    <script>
        window.onload = function() {
            document.getElementById('groupCreateForm').reset();
        };

        const groupNameInput = document.getElementById('groupName');
        const groupDescriptionInput = document.getElementById('groupDescription');
        const userLimitInput = document.getElementById('userLimit');
        const iconUploadInput = document.getElementById('iconUpload');
        const categoryInputs = document.querySelectorAll('.categoryInput');
        const categoryCountSpan = document.getElementById('categoryCount');
        const steps = document.querySelectorAll('.formStep');
        const stepItems = document.querySelectorAll('.stepIndicator .stepItem');
        let prevBtn;
        let nextBtn;
        let submitBtn;
        let currentStep = 1;
        const totalSteps = steps.length;
        let buttonsInitialized = false;

        // 그룹명 입력 문자 카운트
        groupNameInput.addEventListener('input', function(e) {
            document.getElementById('nameCharCount').textContent = e.target.value.length;
        });

        // 그룹 설명 입력 문자 카운트
        groupDescriptionInput.addEventListener('input', function(e) {
            document.getElementById('descCharCount').textContent = e.target.value.length;
        });

        // 사용자 제한 입력 검증
        userLimitInput.addEventListener('input', function(e) {
            let value = parseInt(e.target.value, 10);
            if (isNaN(value) || value < 1) {
                value = 1;
            } else if (value > 1000) {
                value = 1000;
            }
            e.target.value = value;
        });

        // 카테고리 선택 제한 (최대 5개)
        categoryInputs.forEach(input => {
            input.addEventListener('change', function() {
                const checked = document.querySelectorAll('.categoryInput:checked').length;
                categoryCountSpan.textContent = checked;

                if (checked >= 5) {
                    categoryInputs.forEach(cb => {
                        if (!cb.checked) {
                            cb.disabled = true;
                        }
                    });
                } else {
                    categoryInputs.forEach(cb => {
                        cb.disabled = false;
                    });
                }
            });
        });

        // 그룹 대표 이미지 미리보기
        iconUploadInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            const placeholder = document.querySelector('#iconPreview .imagePlaceholder');
            const img = document.getElementById('iconPreviewImg');

            if (file) {
                if (file.size > 5 * 1024 * 1024) {
                    alert('이미지 파일 크기는 5MB 이하여야 합니다.');
                    e.target.value = '';
                    img.style.display = 'none';
                    placeholder.style.display = 'flex';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function(evt) {
                    img.src = evt.target.result;
                    img.style.display = 'block';
                    placeholder.style.display = 'none';
                };
                reader.readAsDataURL(file);
            } else {
                img.style.display = 'none';
                placeholder.style.display = 'flex';
            }
        });

        // 스텝 제어
        function showStep(step) {
            steps.forEach(stepEl => {
                const stepNumber = parseInt(stepEl.dataset.step, 10);
                stepEl.classList.toggle('active', stepNumber === step);
            });

            stepItems.forEach(item => {
                const stepNumber = parseInt(item.dataset.step, 10);
                item.classList.toggle('active', stepNumber === step);
                item.classList.toggle('completed', stepNumber < step);
            });

            if (prevBtn) {
                prevBtn.disabled = step === 1;
            }
            if (nextBtn) {
                nextBtn.classList.toggle('hidden', step === totalSteps);
            }
            if (submitBtn) {
                submitBtn.classList.toggle('hidden', step !== totalSteps);
            }

            steps[step - 1].scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        function validateStep(step) {
            if (step === 1) {
                const name = groupNameInput.value.trim();
                const description = groupDescriptionInput.value.trim();
                const image = iconUploadInput.files[0];

                if (!name) {
                    alert('그룹명을 입력해주세요.');
                    groupNameInput.focus();
                    return false;
                }

                if (name.length < 2) {
                    alert('그룹명은 최소 2자 이상이어야 합니다.');
                    groupNameInput.focus();
                    return false;
                }

                if (!description) {
                    alert('그룹 소개를 입력해주세요.');
                    groupDescriptionInput.focus();
                    return false;
                }

                if (!image) {
                    alert('그룹 대표 이미지를 업로드해주세요.');
                    iconUploadInput.focus();
                    return false;
                }
            }

            if (step === 2) {
                const userLimit = parseInt(userLimitInput.value, 10);
                if (isNaN(userLimit) || userLimit < 1 || userLimit > 1000) {
                    alert('그룹 인원 제한은 1명 이상 1000명 이하여야 합니다.');
                    userLimitInput.focus();
                    return false;
                }
            }

            return true;
        }

        function bindButtonEvents() {
            if (buttonsInitialized) {
                return true;
            }

            prevBtn = document.getElementById('prevStepBtn');
            nextBtn = document.getElementById('nextStepBtn');
            submitBtn = document.getElementById('submitBtn');

            if (!prevBtn || !nextBtn || !submitBtn) {
                return false;
            }

            nextBtn.addEventListener('click', function() {
                if (!validateStep(currentStep)) {
                    return;
                }

                if (currentStep < totalSteps) {
                    currentStep += 1;
                    showStep(currentStep);
                }
            });

            prevBtn.addEventListener('click', function() {
                if (currentStep > 1) {
                    currentStep -= 1;
                    showStep(currentStep);
                }
            });

            document.getElementById('groupCreateForm').addEventListener('submit', function(e) {
                if (!validateStep(1) || !validateStep(2)) {
                    e.preventDefault();
                    return false;
                }
            });

            buttonsInitialized = true;
            showStep(currentStep);
            return true;
        }

        function waitForButtons() {
            if (!bindButtonEvents()) {
                setTimeout(waitForButtons, 100);
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            showStep(currentStep);
            waitForButtons();
        });

        window.handleGroupButtonsReady = function() {
            if (bindButtonEvents()) {
                showStep(currentStep);
            }
        };
    </script>



    </article>
</div>
<jsp:include page="../include/group_footer.jsp"></jsp:include>