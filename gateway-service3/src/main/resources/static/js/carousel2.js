const carousel = document.querySelector('.carousel2');
const wrapper = document.querySelector('.carousel-wrapper2');
let currentIndex = 0; // Índice del primer elemento visible
const initialMargin = 50; // Debe coincidir con el margen en CSS

function moveCarousel(direction) {
    const itemWidth = 220; // Ajusta según el ancho real de cada elemento
    const itemsCount = document.querySelectorAll('.carousel-item').length;
    const visibleItems = Math.floor(wrapper.offsetWidth / itemWidth);
    const maxIndex = itemsCount - visibleItems;

    currentIndex += direction;
    if (currentIndex < 0) currentIndex = 0;
    if (currentIndex > maxIndex) currentIndex = maxIndex;

    // Ajusta el desplazamiento para eliminar el margen inicial a medida que avanza
    const offset = initialMargin + currentIndex * itemWidth;
    carousel.style.transform = `translateX(-${offset}px)`;
}
