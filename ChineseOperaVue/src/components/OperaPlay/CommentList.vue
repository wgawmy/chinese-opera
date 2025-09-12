<template>
    <!-- 评论区 -->
    <div v-for="(comment, index) in localComments" :key="index" class="comment-block">
        <div class="author-gambits" :class="{ 'hasfather':father}">
            <div class="author-info">
                <el-avatar class="header-img" :src="comment.user.avatar"></el-avatar>
                <span class="author-name" >{{ comment.user.nickName }}</span>
                <span class="author-name" v-if="father"> 回复给：{{father.user.nickName}}</span>
                <span class="author-time">{{ comment.createdAt }}</span>
                <span class="reply-people" @click=getUserInfo(comment,index)>回复</span>
            </div>
            <div class="comment-content">{{ comment.content}}</div>
        </div>
        <div v-if="comment.children && comment.children.length" class="reply-list">
          <comment-list :comments="comment.children" :father="comment" :fatherIndex="index" @CommentFromSon="setCommentFromSon"/>
        </div>

        <!-- 回复输入框 -->
        <div class="reply-comment" v-if="!father&&inputIndex(index)">
            <el-avatar class="header-img" :src="userStore.avatar"></el-avatar>
            <span class="replying-to">回复:{{reply.replyname}}</span>
            <div class="reply-info">
                <div contenteditable="true" placeholder="输入回复..." :ref="'replyInput' + index" class="reply-input reply-comment-input" @input="onDivInput($event)"></div>
            </div>
            <div class="reply-btn-box">
                <el-button @click="sendCommentReply(index)" type="primary">发布</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import {addComment,getCommentsByOperaId} from '@/api/comment.js';

import useUserStore from '@/stores/userStore';
export default {
    emits: ['CommentFromSon'],
    props:{
        comments:{
            type:Array
        },
        father:{
            type:Object,
        },
        fatherIndex:{
            type:Number
        }
    },
    computed:{
      userStore(){
        return useUserStore();
      }
    },
    data(){
        return{
            isShowInput:false,  //用于判断是否展示输入框
            ischoicedcommentId:'', //用于判断是否连续两次点击相同的comment
            reply:{
                replyindex:-1,
                replyname:'',
                replyComment:''
            },
            localComments: [...this.comments]
        }
    },
    watch: {
        comments(newComments) {
            this.localComments = [...newComments]; // 当 props 的 comments 变化时更新 localComments
        },
    },
    methods:{
        
        inputIndex(index){
            return this.reply.replyindex == index&&this.isShowInput;
        },
        onDivInput(e) {
            this.reply.replyComment = e.target.innerHTML;
        },
        getUserInfo(comment,index) {
            if(this.father){
                this.$emit('CommentFromSon',comment,this.fatherIndex);
            }
            else{
                this.reply.replyname = comment.user.nickName;
                this.reply.replyindex = index;
                // 为了实现两次点击相同的commentid,评论输入框消失的。
                if(!this.ischoicedcommentId){
                    this.ischoicedcommentId=comment.id;
                    this.isShowInput = this.isShowInput==true ? false:true;
                }
                else if(this.ischoicedcommentId == comment.id){
                    this.isShowInput = this.isShowInput==true ? false:true;
                }
                else if(this.ischoicedcommentId&&this.ischoicedcommentId!=comment.id){
                    this.ischoicedcommentId=comment.id;
                }
                this.isShowInput = true; // 确保输入框显示

                // 聚焦到输入框
                this.$nextTick(() => {
                    const inputContent = this.$refs['replyInput' + index]?.[0];
                    if (inputContent) {
                        inputContent.focus();
                    }
                });
            }
        },
        setCommentFromSon(comment,fatherIndex){
            if(this.father){
                this.$emit('CommentFromSon',comment,this.fatherIndex);
            }
            else{
                this.reply.replyname = comment.user.nickName;
                this.reply.replyindex = fatherIndex;
                if(!this.ischoicedcommentId){
                    this.ischoicedcommentId=comment.id;
                    this.isShowInput = this.isShowInput==true ? false:true;
                }
                else if(this.ischoicedcommentId == comment.id){
                    this.isShowInput = this.isShowInput==true ? false:true;
                }
                else if(this.ischoicedcommentId&&this.ischoicedcommentId!=comment.id){
                    this.ischoicedcommentId=comment.id;
                }
                this.isShowInput = true; // 确保输入框显示

                // 聚焦到输入框
                this.$nextTick(() => {
                    const inputContent = this.$refs['replyInput' + fatherIndex]?.[0];
                    if (inputContent) {
                        inputContent.focus();
                    }
                });
               
            }
        },
        sendCommentReply(index) {
            if (!this.reply.replyComment.trim()) {
                alert("回复不能为空");
                return;
            }
            // 执行插入评论数据的操作
           
            const insertComment = {
                userId:this.userStore.userId,
                operaId:this.comments[0].operaId,
                parentId:this.ischoicedcommentId,
                content:this.reply.replyComment,
            }
            
            addComment(insertComment).then(res=>{
                getCommentsByOperaId(this.comments[0].operaId).then(response => {

                    this.localComments = response.data; 
                });
                // 清空输入框操作。
                const inputContent = this.$refs['replyInput' + index]?.[0]; // .[0] 取第一个匹配的元素
                if (inputContent) {
                    inputContent.innerHTML = "";
                }
                
            })
            
        },
    }
}
</script>

<style lang="scss" scoped>
.comment-block {
  .author-gambits{
    .author-info {
        display: flex;
        .header-img {
            border-radius: 50%;
            margin-right: 10px;
            width:20px;
            height:20px;
        }
        .author-name {
            font-weight: bold;
        }
        .author-time,.reply-people{
            color: #999;
            font-size: 12px;
            margin-left: 10px;
        }
        .reply-people:hover{
          color: #7FCBF8;
        }
       
    }
    .comment-content {
        margin-left: 30px;
        font-size: 14px;
        line-height: 1.6;
    }
  }
  .hasfather{
    margin-left: 50px;
  }
}
 .reply-block {
    margin-left: 50px;
    border-radius: 5px;
    background: #f9f9f9;
    .header-img {
      width: 30px; /* 头像宽度 */
      height: 30px; /* 头像高度 */
      border-radius: 50%;
      margin-right: 10px;
    }
    .author-info {
      display: flex;
      .author-name {
        font-weight: bold;
        margin-right: 10px;
      }
      .author-time {
        color: #999;
        font-size: 12px;
      }
    }
    .reply-content {
      font-size: 14px;
    }
  }
.reply-comment {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 30px;
    .header-img {
      width: 30px;
      height: 30px; 
      border-radius: 50%;
      margin-right: 10px;
    }
    .reply-info {
        margin-left: 20px;
        display: flex;
        flex-grow: 1;
        .reply-input {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            min-height: 40px;
            width: 90%;
            &:empty:before {
            content: attr(placeholder);
            color: #ccc;
            }
        }
    }
    .reply-btn-box {
      margin-top: 5px;
      margin-right: 100px;
    }
}
.comment-block {
  .author-gambits {
    font-size: 15px; // 原先评论的字体较大
    color: #333; // 设置字体颜色
    font-weight: bold; // 字体加粗

    &.hasfather {
      font-size: 13px; // 回复的字体较小
      font-weight: normal; // 正常字体粗细
    }
  }

  .comment-content {
    font-size: 12px; // 设置原评论内容的字体大小
    line-height: 1.5; // 行高调整
  }

  .reply-list .comment-content {
    font-size: 12px; // 设置回复内容的字体大小
  }

  .reply-input {
    font-size: 12px; // 回复输入框中的字体大小
    padding: 5px; // 内边距
    border-radius: 4px; // 圆角
  }

  .replying-to {
    // color: #888; // 设置“回复:”文字的颜色
    font-style: italic; // 斜体
  }
}

</style>

