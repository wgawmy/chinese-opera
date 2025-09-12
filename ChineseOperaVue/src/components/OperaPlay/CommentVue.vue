<template>
    <!-- 用户输入完之后，无法回复用户 -->
  <div class="comment-container">
    <div class="my-reply">
        <el-avatar class="header-img" :src="userStore.avatar"></el-avatar>
        <div class="reply-info">
            <div tabindex="0" contenteditable="true" id="replyInput" placeholder="输入评论..." class="reply-input" @focus="showReplyBtn" @input="onDivInput($event)"></div>
        </div>
        <div class="reply-btn-box" v-show="btnShow">
          <el-button @click="sendComment" type="primary">发布</el-button>
        </div>
    </div>
    <CommentList :comments="comments"></CommentList>
  </div>
</template>

<script>
import {getCommentsByOperaId,addComment} from '@/api/comment.js';
import CommentList from '@/components/OperaPlay/CommentList.vue';
import useUserStore from '@/stores/userStore';
export default {
    name: "Comment",
    props: {
        operaid: {
            type: Number,
            required: true
        }
    },
    computed:{
      userStore(){
        return useUserStore();
      }
    },
    data() {
        return {
            btnShow: false,
            replyComment: "",
            ReplyIndex: -1,
            replyname: '',
            comments: []
        };
    },
    methods: {
        getComments() {
            getCommentsByOperaId(this.operaid).then(res => {
                this.comments = res.data;
            });
        },
        showReplyBtn() {
            this.btnShow = true;
        },
        hideReplyBtn() {
            this.btnShow = false;
        },
        onDivInput(e) {
            this.replyComment = e.target.innerHTML;
        },
        sendComment() {
            if (!this.replyComment.trim()) {
                alert("评论不能为空");
                return;
            }
            const insertComment = {
                userId:this.userStore.userId,
                operaId:this.operaid,
                content:this.replyComment
            }
            addComment(insertComment).then(res=>{
              document.getElementById("replyInput").innerHTML = ""; //清空
              this.getComments();
            })
            
        },
       
        inputShow(index) {
            if (this.ReplyIndex === index) {
                return true;
            }
            return false;
        },
    },
    created() {
      this.getComments();
    },
    watch:{
      operaid(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.getComments(); // operaid 变化时重新获取评论
        }
      }
    },
    components: { CommentList }
}
</script>

<style lang="scss" scoped>
.comment-container{
  border: solid 1px rgba(217, 210, 210, 0.591);
  border-radius:$border_radius;
}
.my-reply {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  margin-top: 20px;
  .header-img {
    width: 30px; /* 头像宽度 */
    height: 30px; /* 头像高度 */
    border-radius: 50%;
    margin-right: 10px;
    object-fit: cover;
  }
  .reply-info {
    flex-grow: 1;
    .reply-input {
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 5px;
      min-height: 40px;
      width: 100%;
      &:empty:before {
        content: attr(placeholder);
        color: #ccc;
      }
    }
  }
  .reply-btn-box {
    margin: 5px 5px 0 5px;
  }
}

</style>


<!-- {
  name: "Lana Del Rey",
  headImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
  comment: "我发布一张新专辑Norman Fucking Rockwell,大家快来听啊",
  time: "2019年9月16日 18:43",
  reply: [
    {
      from: "Taylor Swift",
      fromHeadImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
      to: "Lana Del Rey",
      comment: "我很喜欢你的新专辑！！",
      time: "2019年9月16日 18:43",
    },
    {
      from: "Ariana Grande",
      fromHeadImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
      to: "Lana Del Rey",
      comment: "别忘记宣传我们的合作单曲啊",
      time: "2019年9月16日 18:43",
    },
  ],
},
{
  name: "Lana Del Rey",
  headImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
  comment: "这是我第二次发评论",
  time: "2019年9月16日 18:43",
  reply: [
    {
      from: "Taylor Swift",
      fromHeadImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
      to: "Lana Del Rey",
      comment: "我很喜欢你的新专辑！！",
      time: "2019年9月16日 18:43",
    },
    {
      from: "Ariana Grande",
      fromHeadImg: "https://ae01.alicdn.com/kf/Hf6c0b4a7428b4edf866a9fbab75568e6U.jpg",
      to: "Lana Del Rey",
      comment: "别忘记宣传我们的合作单曲啊",
      time: "2019年9月16日 18:43",
    },
  ],
}, -->