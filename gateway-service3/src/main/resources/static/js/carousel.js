const carousel = document.querySelector('.carousel');
const items = document.querySelectorAll('.carousel .item');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');

const itemsToShow = 6; // Número de elementos visibles a la vez
const totalItems = items.length;
let currentIndex = 0; // Asegúrate de que el índice inicial sea 0

function updateButtons() {
  // Desactiva el botón "anterior" si estamos al inicio
  prevBtn.disabled = currentIndex === 0;
  // Desactiva el botón "siguiente" si estamos en el último grupo
  nextBtn.disabled = currentIndex >= totalItems - itemsToShow;
}

function scrollToItem(index) {
  const itemWidth = items[0].offsetWidth + parseInt(getComputedStyle(carousel).gap); // Ancho de cada producto + margen
  carousel.scrollTo({
    left: index * itemWidth,
    behavior: 'smooth'
  });
  currentIndex = index; // Actualiza el índice actual
  updateButtons();
}

prevBtn.addEventListener('click', () => {
  if (currentIndex > 0) {
    scrollToItem(Math.max(0, currentIndex - itemsToShow)); // Evita que el índice sea negativo
  }
});

nextBtn.addEventListener('click', () => {
  if (currentIndex < totalItems - itemsToShow) {
    scrollToItem(Math.min(totalItems - itemsToShow, currentIndex + itemsToShow)); // Evita que el índice supere el número total de elementos
  }
});

// Inicializa el estado de los botones al cargar la página
updateButtons();
