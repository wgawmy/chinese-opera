<template>
  <div class="audio-player">
    <div>
      <el-image
        class="slow-rotate"
        :class="{ picactive: track.isPlaying }"
        style="width: 400px; height: 400px"
        :src="url"
        fit="cover"
      >
        <template #error>
          <div class="image-slot">
            <el-icon><Picture /></el-icon>
            <span>加载失败</span>
          </div>
        </template>
      </el-image>
    </div>
    <!-- <h2 v-if="currentTrack">{{ currentTrack.name }}</h2> -->

    <!-- <p v-if="currentTrack">{{ currentTrack.tag }}</p> -->
    <div class="controls" v-if="currentTrack">
      <el-icon style="margin-right: 50px" :size="30" @click="prevTrack"><CaretLeft /></el-icon>
      <el-icon :size="30" @click="togglePlay">
        <VideoPlay v-if="!track.isPlaying" />
        <VideoPause v-if="track.isPlaying" />
      </el-icon>
      <el-icon style="margin-left: 50px" :size="30" @click="nextTrack"><CaretRight /></el-icon>
    </div>
    <span>{{ formatTime(seek) }} / {{ formatTime(duration) }}</span>
    <div class="progress" v-if="currentTrack">
      <input type="range" min="0" :max="duration" v-model="seek" @input="seekTrack" step="0.01" />
    </div>
  </div>
</template>

<script>
import { Howl } from 'howler'
import { useTrackStore } from '@/stores/trackStore'
import { getAudioPictureUrl } from '@/api/audio.js'
import { Picture } from '@element-plus/icons-vue'

export default {
  components: {
    Picture
  },
  data() {
    return {
      currentTrack: null,
      sound: null,
      seek: 0,
      duration: 0,
      url: '',
      track: useTrackStore()
    }
  },
  watch: {
    'track.currentTrackId': function (newTrackId) {
      if (this.track.isfinishGetData) {
        const currentIndex = this.track.audioList.findIndex((track) => track.id === newTrackId)
        this.currentTrack = this.track.audioList[currentIndex]
        if (this.currentTrack) {
          this.loadTrack(this.currentTrack)
          this.playTrack()
        }
      }
    },

    // 监听 isPlaying 变化
    'track.isPlaying': function (isPlaying) {
      isPlaying ? this.playTrack() : this.pauseTrack()
    },

    'track.currentTag': function (newTag) {
      if (this.sound) {
        this.track.isPlaying = false
        this.track.isfinishGetData = false
        this.sound.unload()
      }

      this.url = getAudioPictureUrl(newTag)
      if (this.track.audioList.length > 0) {
        this.track.currentTrackId = this.track.audioList[0].id
      }
    },

    // 监听 isfinishGetData，加载完成时进行初始化
    'track.isfinishGetData': {
      handler: function (newVal) {
        if (this.track.audioList.length > 0) {
          this.track.currentTrackId = this.track.audioList[0].id
        }

        if (newVal) {
          const currentIndex = this.track.audioList.findIndex(
            (track) => track.id === this.track.currentTrackId
          )
          if (currentIndex !== -1) {
            this.currentTrack = this.track.audioList[currentIndex]
            this.loadTrack(this.currentTrack)
          }
        }
      },
      immediate: true // 确保组件初始化时立即执行一次
    }
  },
  mounted() {
    // 组件挂载时初始化
    if (this.track.audioList.length > 0 && !this.currentTrack) {
      this.track.currentTrackId = this.track.audioList[0].id
    }
  },

  beforeUnmount() {
    // 组件销毁前清理资源
    if (this.sound) {
      this.sound.unload()
      this.sound = null
    }
  },

  deactivated() {
    // 组件被切换到其他页面时暂停播放
    if (this.sound) {
      this.track.isPlaying = false
      this.pauseTrack()
    }
  },
  methods: {
    loadTrack(track) {
      if (this.sound) {
        this.sound.unload()
      }

      this.url = getAudioPictureUrl(track.tag)
      this.sound = new Howl({
        src: [track.audioUrl],
        html5: true,
        onload: () => {
          this.duration = this.sound.duration()
        },
        onend: () => {
          this.track.isPlaying = false
          // 自动播放下一首
          this.nextTrack()
        },
        onerror: (error) => {
          console.error('音频加载失败:', error)
          this.track.isPlaying = false
        }
      })
    },
    playTrack() {
      if (this.sound) {
        this.sound.play()
        this.track.isPlaying = true
        this.updateSeek()
      }
    },

    pauseTrack() {
      if (this.sound) {
        this.sound.pause()
        this.track.isPlaying = false
      }
    },

    togglePlay() {
      if (!this.sound) return
      this.track.isPlaying ? this.pauseTrack() : this.playTrack()
    },
    prevTrack() {
      const currentIndex = this.track.audioList.findIndex(
        (track) => track.id === this.track.currentTrackId
      )
      if (currentIndex > 0) {
        this.track.currentTrackId = this.track.audioList[currentIndex - 1].id
      } else {
        // 如果是第一首，可以跳到最后一首或保持不变
        // this.track.currentTrackId = this.track.audioList[this.track.audioList.length - 1].id;
      }
    },
    nextTrack() {
      const currentIndex = this.track.audioList.findIndex(
        (track) => track.id === this.track.currentTrackId
      )
      if (currentIndex < this.track.audioList.length - 1) {
        this.track.currentTrackId = this.track.audioList[currentIndex + 1].id
      } else {
        // 如果是最后一首，停止播放或循环到第一首
        this.track.isPlaying = false
        this.pauseTrack()
      }
    },
    updateSeek() {
      if (this.sound && this.track.isPlaying) {
        this.seek = this.sound.seek()
        requestAnimationFrame(this.updateSeek)
      }
    },
    seekTrack(event) {
      if (this.sound && this.sound.playing()) {
        this.sound.seek(parseFloat(event.target.value))
      }
    },
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60) || 0
      const secs = Math.floor(seconds - minutes * 60) || 0
      return `${minutes}:${secs < 10 ? '0' : ''}${secs}`
    }
  }
}
</script>

<style scoped lang="scss">
.slow-rotate {
  border-radius: 50%;
  transform-origin: center;
  animation: rotate 9s linear infinite;
  animation-play-state: paused;
}
// .slow-rotate:hover{
//   animation-play-state: paused;
// }
.picactive {
  animation-play-state: running;
}

.audio-player {
  width: 70vw;
  height: 70vh;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
h2,
p {
  @include no-wrap;
}
h2 {
  margin-bottom: 10px;
}

.controls {
  display: flex;
  justify-content: center;
  margin: 10px;
}

.progress {
  margin-top: 15px;
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
}

.progress input[type='range'] {
  width: 80%;
  max-width: 500px;
  height: 6px;
  border-radius: 3px;
  background: #ddd;
  outline: none;
  transition: background 0.3s;

  &::-webkit-slider-thumb {
    appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #409eff;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: #66b1ff;
    }
  }

  &::-moz-range-thumb {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #409eff;
    cursor: pointer;
    border: none;
  }
}

.image-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;

  .el-icon {
    font-size: 30px;
    margin-bottom: 8px;
  }
}
</style>
