<template>
  <div 
    class="hero-banner" 
    :style="bannerStyle"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <div v-if="props.backgroundType === 'video' && props.backgroundUrl" class="hero-video">
      <video autoplay loop muted playsinline>
        <source :src="props.backgroundUrl" type="video/mp4">
      </video>
    </div>
    
    <div class="hero-overlay"></div>
    
    <div class="hero-content" :class="`content-${props.contentPosition}`">
      <h1 class="hero-title" :class="{ typing: props.enableTypingEffect }">
        {{ displayTitle }}
      </h1>
      <p class="hero-subtitle">{{ props.subtitle }}</p>
      <button v-if="props.ctaText" class="hero-cta" @click="handleCTA">
        {{ props.ctaText }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import type { HeroBannerProps } from '@/types/page-builder';

const props = withDefaults(defineProps<HeroBannerProps>(), {
  backgroundType: 'gradient',
  gradientColors: () => ['#667eea', '#764ba2'],
  title: '欢迎来到我的博客',
  subtitle: '分享技术，记录生活',
  height: '500px',
  contentPosition: 'center',
  enableParallax: false,
  enableTypingEffect: false,
});

const displayTitle = ref('');
const mouseY = ref(0);

const bannerStyle = computed(() => {
  const style: Record<string, string> = {
    height: props.height,
  };

  if (props.backgroundType === 'image' && props.backgroundUrl) {
    style.backgroundImage = `url(${props.backgroundUrl})`;
    if (props.enableParallax) {
      style.transform = `translateY(${mouseY.value * 0.1}px)`;
    }
  } else if (props.backgroundType === 'gradient') {
    style.background = `linear-gradient(135deg, ${props.gradientColors.join(', ')})`;
  }

  return style;
});

const handleMouseEnter = (e: MouseEvent) => {
  if (props.enableParallax) {
    mouseY.value = e.clientY;
  }
};

const handleMouseLeave = () => {
  mouseY.value = 0;
};

const handleCTA = () => {
  if (props.ctaLink) {
    window.location.href = props.ctaLink;
  }
};

// 打字机效果
const typeWriter = () => {
  if (!props.enableTypingEffect) {
    displayTitle.value = props.title;
    return;
  }

  let i = 0;
  displayTitle.value = '';
  const interval = setInterval(() => {
    if (i < props.title.length) {
      displayTitle.value += props.title.charAt(i);
      i++;
    } else {
      clearInterval(interval);
    }
  }, 100);
};

onMounted(() => {
  typeWriter();
});

watch(() => props.title, () => {
  typeWriter();
});
</script>

<style scoped>
.hero-banner {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  transition: transform 0.3s ease;
}

.hero-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.hero-video video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
}

.hero-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  padding: 2rem;
  max-width: 800px;
}

.content-left {
  align-self: flex-start;
  text-align: left;
  margin-left: 5%;
}

.content-right {
  align-self: flex-end;
  text-align: right;
  margin-right: 5%;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.8s ease;
}

.hero-title.typing::after {
  content: '|';
  animation: blink 1s infinite;
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  animation: fadeInUp 0.8s ease 0.2s both;
}

.hero-cta {
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid white;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.8s ease 0.4s both;
  backdrop-filter: blur(10px);
}

.hero-cta:hover {
  background: white;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  .hero-subtitle {
    font-size: 1rem;
  }
}
</style>

