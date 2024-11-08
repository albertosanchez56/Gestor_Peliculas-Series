const carousel = document.querySelector('.carousel');
const items = document.querySelectorAll('.carousel .item');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');

const itemsToShow = 6; // Número de elementos que se muestran a la vez
const totalItems = items.length;
let currentIndex = 0;

function updateButtons() {
  // Desactiva el botón "anterior" si estamos al principio
  prevBtn.disabled = currentIndex === 0;
  // Desactiva el botón "siguiente" si estamos al final
  nextBtn.disabled = currentIndex >= totalItems - itemsToShow;
}

function scrollToItem(index) {
  // Calcula el ancho de un elemento
  const itemWidth = items[0].offsetWidth + parseInt(getComputedStyle(carousel).gap);
  // Desplaza el carrusel al nuevo índice
  carousel.scrollTo({
    left: index * itemWidth,
    behavior: 'smooth'
  });
  currentIndex = index;
  updateButtons();
}

prevBtn.addEventListener('click', () => {
  if (currentIndex > 0) {
    scrollToItem(currentIndex - itemsToShow);
  }
});

nextBtn.addEventListener('click', () => {
  if (currentIndex < totalItems - itemsToShow) {
    scrollToItem(currentIndex + itemsToShow);
  }
});

// Inicializa el estado de los botones
updateButtons();
