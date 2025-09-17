<template>
  <div class="card border-0 position-absolute top-50 start-50 translate-middle">
    <div class="loader">
      <p>Loading</p>
      <div class="words">
        <span class="word">topics</span>
        <span class="word">exercises</span>
        <span class="word">materials</span>
        <span class="word">statistics</span>
        <span class="word">infos</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  --bg-color: transparent;
  background-color: var(--bg-color);
  padding: 1rem 2rem;
  border-radius: 1.25rem;
}
.loader {
  color: rgb(124, 124, 124);
  font-family: 'Poppins', sans-serif;
  font-weight: 500;
  font-size: 25px;
  box-sizing: content-box;
  height: 40px;
  padding: 10px 10px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  border-radius: 8px;
}
.words {
  overflow: hidden;
  position: relative;
}
.words::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(
    var(--bg-color) 10%,
    transparent 30%,
    transparent 70%,
    var(--bg-color) 90%
  );
  z-index: 20;
}
.word {
  display: block;
  height: 100%;
  padding-left: 6px;
  color: #956afa;
  animation: spin 5s infinite;
}
@keyframes spin {
  0% {
    transform: translateY(0%) scale(1);
  }
  16% {
    transform: translateY(0%) scale(1);
  }
  20% {
    transform: translateY(-100%) scale(1);
  }
  36% {
    transform: translateY(-100%) scale(1);
  }
  40% {
    transform: translateY(-200%) scale(1);
  }
  56% {
    transform: translateY(-200%) scale(1);
  }
  60% {
    transform: translateY(-300%) scale(1);
  }
  76% {
    transform: translateY(-300%) scale(1);
  }
  80% {
    transform: translateY(-400%) scale(1);
  }
  92% {
    transform: translateY(-400%) scale(1);
  }
  95% {
    transform: translateY(20%) scale(0.8) rotate(-10deg);
  }
  97% {
    transform: translateY(-10%) scale(1.25) rotate(8deg);
  }
  99% {
    transform: translateY(5%) scale(0.9) rotate(-3deg);
  }
  100% {
    transform: translateY(0%) scale(1) rotate(0deg);
  }
}
</style>
