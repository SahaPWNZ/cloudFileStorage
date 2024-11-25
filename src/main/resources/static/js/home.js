document.querySelectorAll(".drop-zone__input").forEach((inputElement) => {
    const dropZoneElement = inputElement.closest(".drop-zone");
    const submitButton = dropZoneElement.closest('form').querySelector('.drop-zone-button'); // Получаем кнопку загрузки

    // Деактивируем кнопку по умолчанию
    submitButton.disabled = true;

    dropZoneElement.addEventListener("click", () => {
        inputElement.click();
    });

    inputElement.addEventListener("change", () => {
        if (inputElement.files.length) {
            updateThumbnail(dropZoneElement, inputElement.files[0]);
            submitButton.disabled = false; // Активируем кнопку при загрузке файла
        }
    });
});

function updateThumbnail(dropZoneElement, file) {
    let thumbnailElement = dropZoneElement.querySelector(".drop-zone__thumb");

    if (dropZoneElement.querySelector(".drop-zone__prompt")) {
        dropZoneElement.querySelector(".drop-zone__prompt").remove();
    }

    if (!thumbnailElement) {
        thumbnailElement = document.createElement("div");
        thumbnailElement.classList.add("drop-zone__thumb");
        dropZoneElement.appendChild(thumbnailElement);
    }

    thumbnailElement.dataset.label = file.name;
    if (file.type.startsWith("image/")) {
        const reader = new FileReader();

        reader.readAsDataURL(file);
        reader.onload = () => {
            thumbnailElement.style.backgroundImage = `url('${reader.result}')`;
        };
    } else {
        thumbnailElement.style.backgroundImage = null;
    }
}

function toggleEdit(sectionId) {
    console.log('Toggling section:', sectionId); // Для отладки
    const editSection = document.getElementById(sectionId);
    if (editSection) {
        editSection.classList.toggle('hidden');
    } else {
        console.error('Element not found:', sectionId); // Если элемент не найден
    }
}

function closeError() {
    document.querySelector('.error-overlay').style.display = 'none';
}

