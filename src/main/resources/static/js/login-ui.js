const inputs = document.querySelectorAll(".input-field");
const toggleBtn = document.querySelectorAll(".toggle");
const main = document.querySelector("main");
const bullets = document.querySelectorAll(".bullets span");
const images = document.querySelectorAll(".image");

inputs.forEach((inp) => {
    inp.addEventListener("focus", () => {
        inp.classList.add("active");
    });
    inp.addEventListener("blur", () => {
        if (inp.value != "") return;
        inp.classList.remove("active");
    });
});

toggleBtn.forEach((btn) => {
    btn.addEventListener("click", () => {
        main.classList.toggle("sign-up-mode");
    })
});

function moveSlider() {
    let index = this.dataset.value;

    let currentImage = document.querySelector(`.img-${index}`);
    images.forEach(img => img.classList.remove("show"));
    currentImage.classList.add("show");

    const textSlider = document.querySelector(".text-group");
    textSlider.style.transform = `translateY(${-(index - 1) * 2.2}rem)`;

    bullets.forEach((bull) => bull.classList.remove("active"));
    this.classList.add("active");
}

bullets.forEach((bullet) => {
    bullet.addEventListener("click", moveSlider);
});

// Carousel automático
let currentSlide = 1;
const totalSlides = 3;

function autoSlide() {
    currentSlide++;
    if (currentSlide > totalSlides) {
        currentSlide = 1;
    }

    // Simular click en el bullet correspondiente
    const currentBullet = document.querySelector(`.bullets span[data-value="${currentSlide}"]`);
    if (currentBullet) {
        moveSlider.call(currentBullet);
    }
}

// Iniciar el carousel automático cada 3 segundos
const carouselInterval = setInterval(autoSlide, 3000);

// Pausar el carousel automático cuando el usuario hace click manual
bullets.forEach((bullet) => {
    bullet.addEventListener("click", () => {
        clearInterval(carouselInterval);
        // Reiniciar el carousel automático después de 5 segundos de inactividad
        setTimeout(() => {
            currentSlide = parseInt(bullet.dataset.value);
            setInterval(autoSlide, 3000);
        }, 5000);
    });
});