//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    loading:false,
    recording: false,
    page: 0,
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    items: [],
    tags: []
  },
  goPage: function (e) {
    console.log(e)
    var _self = this;

    wx.navigateTo({
      url: '../message/message?title=' + e.currentTarget.dataset.title + "&id=" + e.currentTarget.dataset.id
    })
    // console.log(test);
  },
  /**
 * 页面相关事件处理函数--监听用户下拉动作
 */
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
  onChange(event) {
    const detail = event.detail;
    this.setData({
      ['tags[' + event.detail.name + '].checked']: detail.checked
    })

  },
  reload: function() {
    this.setData({
      page:0,
      items:[]
    })
    this.loadMore();
  },
  loadMore : function() {
    var loadMoreMessage = {
      tags: [],
      page: this.data.page
    };
    app.globalData.stompClient.send("/app/articles", {}, JSON.stringify(loadMoreMessage));
    this.setData({
      loading:true
    })

    setTimeout(function () {
      wx.hideLoading()
    }, 5000)
  },
  startRecord: function () {
    var that = this;
    console.log(that.data.userInfo);
    wx.getRecorderManager().start({ duration: 60000,sampleRate: 16000, numberOfChannels: 1 })
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
            content: res.data
          };
          app.globalData.stompClient.send("/app/create", {}, JSON.stringify(chatMessage));
        }
      });
    });
  },
  endRecord: function () {
    wx.getRecorderManager().stop()
  },
  handleMessage: function(msg) {
    console.log("handle message"+JSON.stringify(msg));
    var that = this;
      if (msg.tags) {
        that.setData({
          tags: msg.tags
        })
      } else {
        if (msg.articles && msg.articles.length > 0) {
          var items = that.data.items;
          for (let i = 0; i < msg.articles.length; i++) {
            items.push(msg.articles[i]);
          }
          that.setData({
            loading: false,
            page: that.data.page + 1,
            items: items
          })
        } else {
          that.setData({
            loading: false
          })
        }
      }
  },
  init:function() {
    var that = this;
    if (app.globalData.socketOpen) {

    // app.globalData.stompClient.subscribe('/user/queue/home', function (body, headers) {
    //   console.log('From MQ:', body.body);
    //   var msg = JSON.parse(body.body);
    //   if (msg.tags) {
    //     that.setData({
    //       tags: msg.tags
    //     })
    //   } else {
    //     if (msg.articles && msg.articles.length > 0) {
    //       var items = that.data.items;
    //       for (let i = 0; i < msg.articles.length; i++) {
    //         items.push(msg.articles[i]);
    //       }
    //       that.setData({
    //         loading: false,
    //         page: that.data.page + 1,
    //         items: items
    //       })
    //     } else {
    //       that.setData({
    //         loading: false
    //       })
    //     }
    //   }
    // });


    app.globalData.stompClient.send("/app/tags", {}, '');
    that.reload();
    }else {
      setTimeout(that.init,500);
    }
  },
  onLoad: function () {
    this.init();
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse){
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  getUserInfo: function(e) {
    console.log(e)
    if(e.detail.userInfo) {
      app.globalData.userInfo = e.detail.userInfo
      this.setData({
        userInfo: e.detail.userInfo,
        hasUserInfo: true
      })
    }
  }
})
