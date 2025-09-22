// Seleccionamos todos los carruseles
document.querySelectorAll('.carousel-container2').forEach((carouselContainer) => {
    const carousel = carouselContainer.querySelector('.carousel2');
    const wrapper = carouselContainer.querySelector('.carousel-wrapper2');
    const prevBtn = carouselContainer.querySelector('.prev-btn');
    const nextBtn = carouselContainer.querySelector('.next-btn');
    let currentIndex = 0;

    // Función para determinar el número de elementos a mover en función del tamaño de la pantalla
    function getItemsToMove() {
        const screenWidth = window.innerWidth;
        if (screenWidth <= 480) {
            return 2;
        } else if (screenWidth <= 768) {
            return 3;
        } else {
            return 5;
        }
    }

    let itemsToMove = getItemsToMove();
    const partialVisibleWidth = 165;
    const initialMarginLeft = 165;

    // Inicializamos el estado del botón de la izquierda como oculto
    prevBtn.style.display = 'none';

    // Función de movimiento del carrusel
    function moveCarousel(direction) {
        const itemWidth = 306;
        const itemsCount = carousel.querySelectorAll('.carousel-item').length;
        const visibleItems = Math.floor(wrapper.offsetWidth / itemWidth);
        const maxIndex = Math.floor((itemsCount - visibleItems) / itemsToMove) * itemsToMove;

        currentIndex += direction * itemsToMove;
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex > maxIndex) currentIndex = maxIndex;

        let offset = currentIndex * itemWidth - partialVisibleWidth;
        if (currentIndex === 0) offset = 0;

        carousel.style.transform = `translateX(-${offset}px)`;

        const screenWidth = window.innerWidth;
        if (screenWidth >= 768) {
            wrapper.style.marginLeft = currentIndex === 0 ? `${initialMarginLeft}px` : "0";
        } else {
            wrapper.style.marginLeft = "0";
        }

        prevBtn.style.display = currentIndex === 0 ? 'none' : 'block';
        nextBtn.style.display = currentIndex === maxIndex ? 'none' : 'block';
    }

    // Listeners para los botones
    prevBtn.addEventListener('click', () => moveCarousel(-1));
    nextBtn.addEventListener('click', () => moveCarousel(1));

    // Detectar cambios de tamaño de la ventana
    window.addEventListener('resize', () => {
        itemsToMove = getItemsToMove();
        wrapper.style.marginLeft = 0;
    });
});
