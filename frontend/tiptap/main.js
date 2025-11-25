import { Editor } from '@tiptap/core';
import StarterKit from '@tiptap/starter-kit';
import Placeholder from '@tiptap/extension-placeholder';
import CharacterCount from '@tiptap/extension-character-count';

const CHAR_LIMIT = 10000;
const MAX_MEDIA_ITEMS = 10;

const bindToolbarActions = (editor) => {
  document.querySelectorAll('[data-editor-action]').forEach((button) => {
    button.addEventListener('click', () => {
      const action = button.getAttribute('data-editor-action');
      const level = Number(button.getAttribute('data-heading-level'));

      editor.chain().focus();

      switch (action) {
        case 'bold':
          editor.chain().toggleBold().run();
          break;
        case 'italic':
          editor.chain().toggleItalic().run();
          break;
        case 'strike':
          editor.chain().toggleStrike().run();
          break;
        case 'bullet-list':
          editor.chain().toggleBulletList().run();
          break;
        case 'ordered-list':
          editor.chain().toggleOrderedList().run();
          break;
        case 'blockquote':
          editor.chain().toggleBlockquote().run();
          break;
        case 'code-block':
          editor.chain().toggleCodeBlock().run();
          break;
        case 'heading':
          editor.chain().toggleHeading({ level }).run();
          break;
        case 'undo':
          editor.chain().undo().run();
          break;
        case 'redo':
          editor.chain().redo().run();
          break;
        default:
          break;
      }
    });
  });
};

const updateOutputs = (editor) => {
  const htmlInput = document.getElementById('tiptapContentInput');
  const jsonInput = document.getElementById('tiptapJsonInput');
  const countLabel = document.getElementById('tiptapCharCount');

  if (htmlInput) {
    htmlInput.value = editor.getHTML();
  }

  if (jsonInput) {
    jsonInput.value = JSON.stringify(editor.getJSON());
  }

  if (countLabel) {
    countLabel.textContent = `${editor.storage.characterCount.characters()} / ${CHAR_LIMIT}`;
  }
};

const formatBytes = (bytes) => {
  if (!bytes) return '0 B';
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  const size = bytes / 1024 ** i;
  return `${size.toFixed(1)} ${sizes[i]}`;
};

const bindMediaManager = () => {
  const dropzone = document.getElementById('mediaDropzone');
  const uploadInput = document.getElementById('mediaUploadInput');
  const previewList = document.getElementById('mediaPreviewList');
  const hiddenInput = document.getElementById('mediaJsonInput');

  if (!dropzone || !uploadInput || !previewList || !hiddenInput) return;

  let mediaItems = [];

  const syncHiddenInput = () => {
    hiddenInput.value = JSON.stringify(
      mediaItems.map(({ name, size, type, tempUrl }) => ({
        name,
        size,
        type,
        url: tempUrl,
      })),
    );
  };

  const renderList = () => {
    previewList.innerHTML = '';

    if (!mediaItems.length) {
      const empty = document.createElement('li');
      empty.className = 'mediaEmpty';
      empty.textContent = '첨부된 파일이 없습니다.';
      previewList.appendChild(empty);
      return;
    }

    mediaItems.forEach((item, index) => {
      const li = document.createElement('li');

      const info = document.createElement('div');
      info.className = 'mediaInfo';

      const thumb = document.createElement('div');
      thumb.className = 'mediaThumb';
      thumb.textContent = item.type.startsWith('video') ? '🎬' : '🖼';

      const meta = document.createElement('div');
      meta.className = 'mediaMeta';

      const name = document.createElement('span');
      name.className = 'mediaName';
      name.textContent = item.name;

      const size = document.createElement('span');
      size.className = 'mediaSize';
      size.textContent = `${formatBytes(item.size)} · ${item.type}`;

      meta.appendChild(name);
      meta.appendChild(size);
      info.appendChild(thumb);
      info.appendChild(meta);

      const removeButton = document.createElement('button');
      removeButton.type = 'button';
      removeButton.textContent = '삭제';
      removeButton.addEventListener('click', () => {
        mediaItems.splice(index, 1);
        renderList();
        syncHiddenInput();
      });

      li.appendChild(info);
      li.appendChild(removeButton);
      previewList.appendChild(li);
    });
  };

  const handleFiles = (fileList) => {
    const files = Array.from(fileList);
    const availableSlots = MAX_MEDIA_ITEMS - mediaItems.length;
    const nextFiles = files.slice(0, availableSlots);

    nextFiles.forEach((file) => {
      mediaItems.push({
        name: file.name,
        size: file.size,
        type: file.type,
        tempUrl: URL.createObjectURL(file),
      });
    });

    renderList();
    syncHiddenInput();
  };

  dropzone.addEventListener('dragover', (event) => {
    event.preventDefault();
    dropzone.classList.add('isDragOver');
  });

  dropzone.addEventListener('dragleave', () => {
    dropzone.classList.remove('isDragOver');
  });

  dropzone.addEventListener('drop', (event) => {
    event.preventDefault();
    dropzone.classList.remove('isDragOver');
    if (event.dataTransfer?.files) {
      handleFiles(event.dataTransfer.files);
    }
  });

  dropzone.addEventListener('click', () => uploadInput.click());
  uploadInput.addEventListener('change', (event) => {
    handleFiles(event.target.files || []);
    uploadInput.value = '';
  });
};

const bootstrapEditor = () => {
  const editorElement = document.getElementById('tiptapEditor');

  if (!editorElement) {
    return;
  }

  const editor = new Editor({
    element: editorElement,
    extensions: [
      StarterKit.configure({
        heading: {
          levels: [1, 2, 3],
        },
      }),
      Placeholder.configure({
        placeholder: 'Tiptap 에디터에서 Threads/Reddit 감성으로 글을 시작해보세요...',
      }),
      CharacterCount.configure({
        limit: CHAR_LIMIT,
      }),
    ],
    editorProps: {
      attributes: {
        class: 'tiptapProse',
      },
    },
    autofocus: true,
    onCreate({ editor }) {
      updateOutputs(editor);
    },
    onUpdate({ editor }) {
      updateOutputs(editor);
    },
  });

  bindToolbarActions(editor);
  bindMediaManager();

  window.tiptapEditor = editor;
};

document.addEventListener('DOMContentLoaded', bootstrapEditor);

