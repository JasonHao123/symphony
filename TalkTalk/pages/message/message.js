var app = getApp()
var page = undefined;
Page({
  data:{
    text:"这是消息页面，研发中。。。",
    title:"标题",
    id:null,
    recording:false,
    loading:false,
    page: 0,
    userInfo: {},
    message:[],
    animation:{},
    animation_2:{},
    tap:"tapOff",
    doommData: []
  },
  playVoice: function(e) {
    console.log(e.currentTarget.dataset.voice);
    if(!this.data.recording) {
    const innerAudioContext = wx.createInnerAudioContext()
    innerAudioContext.autoplay = true
    innerAudioContext.src = e.currentTarget.dataset.voice
    innerAudioContext.onPlay(() => {
      console.log('开始播放')
    })
    innerAudioContext.onError((res) => {
      console.log(res.errMsg)
      console.log(res.errCode)
    })
    }
  },
  handleMessage: function(msg) {
    var that = this;
    console.log("handle message"+ JSON.stringify(msg));
    if (msg.messages && msg.messages.length > 0) {
      var items = that.data.message;
      for (let i = 0; i < msg.messages.length; i++) {
        items.push(msg.messages[i]);
      }
      that.setData({
        loading: false,
        page: that.data.page + 1,
        doommData: doommList,
        message: items
      })
    } else if(msg.message){
      doommList.push(new Doomm(msg.message.content, Math.ceil(Math.random() * 100), Math.ceil(Math.random() * 10), getRandomColor()));
      that.setData({
        loading: false,
        doommData: doommList
      })
    }else{
      that.setData({
        loading: false
      })
    }
  },
  init: function () {
    var that = this;
    if (app.globalData.socketOpen) {
      that.reload();
    } else {
      setTimeout(that.init, 500);
    }
  },
  reload: function () {
    this.setData({
      page: 0,
      message: []
    })
    this.loadMore();
  },
  loadMore: function () {
    var loadMoreMessage = {
      id: this.data.id,
      page: this.data.page
    };
    app.globalData.stompClient.send("/app/article", {}, JSON.stringify(loadMoreMessage));
    this.setData({
      loading: true
    })

    setTimeout(function () {
      wx.hideLoading()
    }, 5000)
  },
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数
    var _self = this
    page = this;
    _self.init()
    _self.setData({
      title:options.title,
      id:options.id
    })
    _self.setData({
        userInfo:app.globalData.userInfo
    })
  },
  onReady:function(){
    // 页面渲染完成
    var _self = this
    wx.setNavigationBarTitle({
      title: _self.data.title
    })
    this.animation = wx.createAnimation();
    this.animation_2 = wx.createAnimation()
  },
  onShow:function(){
    // 页面显示
    this.init();
  },
  onHide:function(){
    // 页面隐藏
    app.globalData.stompClient.send("/app/leaveChannel", {}, '');
  },
  onUnload:function(){
    // 页面关闭
  },
  startRecord: function (e) {
    var that = this;
    var parentId = null;
    if(e.currentTarget.dataset.parent) {
      parentId = e.currentTarget.dataset.parent;
    }
    console.log(that.data.userInfo);
    wx.getRecorderManager().start({ duration: 60000, sampleRate: 16000, numberOfChannels: 1 })
    that.setData({
      recording: true
    })
    wx.getRecorderManager().onStop(function (res) {
      console.log(res.tempFilePath);
      that.setData({
        recording: false
      })
      wx.getFileSystemManager().readFile({
        filePath: res.tempFilePath,
        encoding: 'base64',
        success: function (res) {
          // console.log(res.data);
          var chatMessage = {
            author: that.data.userInfo.nickName,
            avatar: that.data.userInfo.avatarUrl,
            articleId: that.data.id,
            parentId: parentId,
            content: res.data
          };
          app.globalData.stompClient.send("/app/createMessage", {}, JSON.stringify(chatMessage));
        }
      });
    });
  },
  endRecord: function () {
    wx.getRecorderManager().stop()
  },
  onPullDownRefresh: function () {
    console.log("pull down")

    this.reload();
    wx.stopPullDownRefresh()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.loadMore();
  },

  bindbt: function () {
    console.log("btn click");
    doommList.push(new Doomm("你是我的小苹果,小呀小苹果，怎么爱你都不嫌多", Math.ceil(Math.random() * 100), Math.ceil(Math.random() * 10), getRandomColor()));
    this.setData({
      doommData: doommList
    })
  },

})

var doommList = [];
var i = 0;
class Doomm {
  constructor(text, top, time, color) {
    this.text = text + i;
    this.top = top;
    this.time = time;
    this.color = color;
    this.display = true;
    let that = this;
    this.id = i++;
    setTimeout(function () {
      doommList.splice(doommList.indexOf(that), 1);
      page.setData({
        doommData: doommList
      })
    }, this.time * 1000)
  }
}
function getRandomColor() {
  let rgb = []
  for (let i = 0; i < 3; ++i) {
    let color = Math.floor(Math.random() * 256).toString(16)
    color = color.length == 1 ? '0' + color : color
    rgb.push(color)
  }
  return '#' + rgb.join('')
}