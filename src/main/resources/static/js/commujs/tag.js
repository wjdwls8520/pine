const tagInput = document.getElementById('tagInput');
const tagList = document.getElementById('tagList');
const hiddenTags = document.getElementById('hiddenTags');

let tags = [];

tagInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        const value = tagInput.value.trim();
        if (value) {
            addTag(value);
            tagInput.value = '';
        }
    }
});

function addTag(name) {
    // 단순 추가 (중복체크는 생략)
    tags.push(name);

    const tagItem = document.createElement('div');
    tagItem.className = 'tagItem';
    tagItem.innerHTML = `${name} <button type="button" onclick="removeTag(this)">x</button>`;
    tagList.appendChild(tagItem);

    updateHiddenInput();
}

function removeTag(btn) {
    const tagItem = btn.parentElement;
    const index = Array.from(tagList.children).indexOf(tagItem);
    tags.splice(index, 1);
    tagItem.remove();
    updateHiddenInput();
}

function updateHiddenInput() {
    hiddenTags.value = tags.join(','); // 서버로 전송되는 값: "tag1,tag2,tag3"
}
