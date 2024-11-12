const carousel = document.querySelector('.carousel2');
const wrapper = document.querySelector('.carousel-wrapper2');

const prevBtn = document.querySelector('.prev-btn');
const nextBtn = document.querySelector('.next-btn');
let currentIndex = 0;

// Función para determinar el número de elementos a mover en función del tamaño de la pantalla
function getItemsToMove() {
    const screenWidth = window.innerWidth;
    if (screenWidth <= 480) {
        return 2; // En móviles movemos 1 producto a la vez
    } else if (screenWidth <= 768) {
        return 3; // En tabletas movemos 2 productos a la vez
    } else {
        return 5; // En pantallas grandes movemos 4 productos a la vez
    }
}

let itemsToMove = getItemsToMove();  // Número de productos a mover según el tamaño de pantalla
const partialVisibleWidth = 165; // Ancho del producto parcialmente visible
const initialMarginLeft = 165; // Margen inicial para alinearlo con el título

// Inicializamos el estado del botón de la izquierda como oculto
prevBtn.style.display = 'none';

// Esta función se ajusta a la cantidad de productos que se mueven dinámicamente
function moveCarousel(direction) {
    const itemWidth = 306; // Ancho de cada producto
    const itemsCount = document.querySelectorAll('.carousel-item').length;
    const visibleItems = Math.floor(wrapper.offsetWidth / itemWidth);
    const maxIndex = Math.floor((itemsCount - visibleItems) / itemsToMove) * itemsToMove;

    // Ajustamos el índice para que no pase el límite
    currentIndex += direction * itemsToMove;
    if (currentIndex < 0) currentIndex = 0;
    if (currentIndex > maxIndex) currentIndex = maxIndex;

    // Calcula el desplazamiento aplicando el margen parcial si no es la primera posición
    let offset = currentIndex * itemWidth - partialVisibleWidth;
    if (currentIndex === 0) {
        offset = 0; // Sin margen en el inicio
    }

    carousel.style.transform = `translateX(-${offset}px)`;

    // Ajusta el margen izquierdo del wrapper solo cuando estés en el inicio
    const screenWidth = window.innerWidth;

    // Verifica si la pantalla tiene un tamaño menor o igual a 768px
    if (screenWidth >= 768) {
        wrapper.style.marginLeft = currentIndex === 0 ? `${initialMarginLeft}px` : "0";
    } else {
        wrapper.style.marginLeft = "0"; // Asegura que no haya margen en pantallas grandes
    }

    // Oculta o muestra el botón de la izquierda según la posición actual
    prevBtn.style.display = currentIndex === 0 ? 'none' : 'block';
    nextBtn.style.display = currentIndex === maxIndex ? 'none' : 'block';
}


// Detectar cambios de tamaño de la ventana y ajustar el número de productos a mover
window.addEventListener('resize', () => {
    itemsToMove = getItemsToMove();  // Actualiza el número de productos a mover
    wrapper.style.marginLeft = 0;  // Aseguramos que el carrusel siempre se alinee a la izquierda
});
