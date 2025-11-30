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