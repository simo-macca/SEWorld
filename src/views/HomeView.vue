<template>
  <div class="login-page">
    <svg
      width="100%"
      height="100%"
      id="svg"
      viewBox="0 0 1440 590"
      xmlns="http://www.w3.org/2000/svg"
      class="transition duration-300 ease-in-out delay-150"
    >
      <defs>
        <linearGradient id="gradient" x1="0%" y1="50%" x2="100%" y2="50%">
          <stop offset="5%" stop-color="#dd3675"></stop>
          <stop offset="95%" stop-color="#8ED1FC"></stop>
        </linearGradient>
      </defs>
      <path
        d="M 0,600 L 0,150 C 67.12918660287082,170.8803827751196 134.25837320574163,191.76076555023923 236,215 C 337.74162679425837,238.23923444976077 474.0956937799042,263.8373205741627 593,276 C 711.9043062200958,288.1626794258373 813.3588516746412,286.88995215311 912,312 C 1010.6411483253588,337.11004784689 1106.468899521531,388.60287081339715 1194,416 C 1281.531100478469,443.39712918660285 1360.7655502392345,446.6985645933014 1440,450 L 1440,600 L 0,600 Z"
        stroke="none"
        stroke-width="0"
        fill="url(#gradient)"
        fill-opacity="0.53"
        class="transition-all duration-300 ease-in-out delay-150 path-0"
      ></path>
      <defs>
        <linearGradient id="gradient" x1="0%" y1="50%" x2="100%" y2="50%">
          <stop offset="5%" stop-color="#F78DA7"></stop>
          <stop offset="95%" stop-color="#8ED1FC"></stop>
        </linearGradient>
      </defs>
      <path
        d="M 0,600 L 0,350 C 84.47846889952152,389.3492822966507 168.95693779904303,428.69856459330146 280,424 C 391.04306220095697,419.30143540669854 528.6507177033493,370.555023923445 613,393 C 697.3492822966507,415.444976076555 728.4401913875599,509.08133971291875 824,565 C 919.5598086124401,620.9186602870813 1079.5885167464114,639.1196172248804 1193,647 C 1306.4114832535886,654.8803827751196 1373.2057416267944,652.4401913875598 1440,650 L 1440,600 L 0,600 Z"
        stroke="none"
        stroke-width="0"
        fill="url(#gradient)"
        fill-opacity="1"
        class="transition-all duration-300 ease-in-out delay-150 path-1"
      ></path>
    </svg>

    <div class="content">
      <h1 class="wave-text">
        <span>W</span><span>e</span><span>l</span><span>c</span><span>material</span><span>m</span
        ><span>e</span>
        <span>&nbsp;</span>
        <span>t</span><span>material</span>
        <span>&nbsp;</span>
        <span class="highlight">S</span><span class="highlight">E</span><span>W</span
        ><span>material</span><span>r</span><span>l</span><span>d</span>
      </h1>
      <p class="subtext">
        Log in to dive into the learning experience of
        <span class="highlight">Software Engineering</span>
      </p>
      <BButton variant="outline-primary" class="login-btn" @click="redirectToLogin">
        Log I
      </BButton>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { BButton } from 'bootstrap-vue-next'
const loginUrl = ref('http://localhost:8080/oauth2/authorization/switch-edu-id')

// Redirect function
const redirectToLogin = () => {
  window.location.href = loginUrl.value
}

export default {
  name: 'LogIn',
  components: { BButton },
  methods: {
    redirectToLogin,
  },
}
</script>

<style scoped>
.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #0a192f;
  color: white;
  text-align: center;
  overflow: hidden;
}

svg {
  position: absolute;
  left: 0;
  bottom: -200px;
  width: 100%;
  height: 1000px;
  z-index: 0;
  animation: waveAnimation 3.5s ease-in-out infinite;
}

/* Content */
.content {
  position: relative;
  z-index: 1;
  width: 100%;
}

/* Wave-styled Heading */
.wave-text {
  font-size: 6rem;
  font-weight: bold;
  margin-bottom: 10px;
}

@keyframes waveAnimation {
  0% {
    transform: translateY(0px);
    opacity: 1;
  }
  50% {
    transform: translateY(-10px);
    opacity: 0.8;
  }
  100% {
    transform: translateY(0px);
    opacity: 1;
  }
}

.wave-text span {
  display: inline-block;
  animation: waveAnimation 1.5s ease-in-out infinite;
}

/* Apply delay to each letter */
.wave-text span:nth-child(1) {
  animation-delay: 0s;
}
.wave-text span:nth-child(2) {
  animation-delay: 0.1s;
}
.wave-text span:nth-child(3) {
  animation-delay: 0.2s;
}
.wave-text span:nth-child(4) {
  animation-delay: 0.3s;
}
.wave-text span:nth-child(5) {
  animation-delay: 0.4s;
}
.wave-text span:nth-child(6) {
  animation-delay: 0.5s;
}
.wave-text span:nth-child(7) {
  animation-delay: 0.6s;
}
.wave-text span:nth-child(8) {
  animation-delay: 0.7s;
}
.wave-text span:nth-child(9) {
  animation-delay: 0.8s;
}
.wave-text span:nth-child(10) {
  animation-delay: 0.9s;
}
.wave-text span:nth-child(11) {
  animation-delay: 1s;
}
.wave-text span:nth-child(12) {
  animation-delay: 1.1s;
}
.wave-text span:nth-child(13) {
  animation-delay: 1.2s;
}
.wave-text span:nth-child(14) {
  animation-delay: 1.3s;
}
.wave-text span:nth-child(15) {
  animation-delay: 1.4s;
}
.wave-text span:nth-child(16) {
  animation-delay: 1.5s;
}
.wave-text span:nth-child(17) {
  animation-delay: 1.6s;
}
.wave-text span:nth-child(18) {
  animation-delay: 1.7s;
}

.highlight {
  background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
  font-weight: bold;
}

/* Subtext */
.subtext {
  font-size: 2rem;
  opacity: 0.8;
  margin-bottom: 30px;
}

/* Login Button */
.login-btn {
  font-size: 3rem;
  padding: 10px 30px;
  border: none;
  background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
  color: white;
  transition: 0.3s ease-in-out;
}

.login-btn:hover {
  background: linear-gradient(to right, #b44593, #dd3675, #d8363a, #ee7724);
}

.login-btn:active {
  background-color: #12876f;
  transform: scale(0.95);
}
</style>
