//app.js
App({
  
  onShow :function() {
    this.login()
  },
  

  openSocket:function() {
    var that = this
    console.log("open socket")
    var sendSocketMessage = function(msg) {
      console.log('send msg:')
      console.log(msg);
      if (that.globalData.socketOpen) {
        that.globalData.socket.send({
          data: msg
        })
      } else {
        that.globalData.socketMsgQueue.push(msg)
        that.openSocket();
      }
    };
    var closeSocket = closeSocket = function() {
      console.log("close socket");
      that.globalData.socketOpen = false;
    };
    this.globalData.ws = {
      send: sendSocketMessage,
      close: closeSocket
    }
    that.globalData.socket = wx.connectSocket({
      url: 'ws://192.168.31.248:8080/gs-guide-websocket',
      header: {
        'Cookie': wx.getStorageSync("sessionCookie")
      }

    })
    that.globalData.socket.onOpen(function (res) {
      console.log("socket open")
      that.globalData.socketOpen = true
      that.globalData.ws.onopen()
    })

    that.globalData.socket.onClose(function (res) {
      console.log('WebSocket 已关闭！')
    })

    that.globalData.socket.onMessage(function (res) {
      console.log("socket message")
      that.globalData.ws.onmessage(res)
    })

    var Stomp = require('utils/stomp.js').Stomp;
    Stomp.setInterval = function (interval, f) {
      return setInterval(f, interval);
    };
    Stomp.clearInterval = function (id) {
      return clearInterval(id);
    };
    that.globalData.stompClient = Stomp.over(that.globalData.ws);

    that.globalData.stompClient.connect({}, function (sessionId) {

      that.globalData.stompClient.subscribe('/user/queue/home', function (body, headers) {
        console.log('From MQ:', body.body);
        var msg = JSON.parse(body.body);
        var pages = getCurrentPages()    //获取加载的页面

        var currentPage = pages[pages.length - 1]    //获取当前页面的对象
        currentPage.handleMessage(msg);
      });


      var chatMessage = {
        sender: "wechat",
        type: 'JOIN'
      };
      that.globalData.stompClient.send("/app/chat.addUser", {}, JSON.stringify(chatMessage));
      // for (let i = 0; i < that.globalData.socketMsgQueue.length; i++) {
      //   that.globalData.stompClient.send("/app/chat.addUser", {}, that.globalData.socketMsgQueue[i]);
      // }
      that.globalData.socketMsgQueue = [];

      
    })
  },
  onHide:function() {
    console.log("App hide");
    wx.request({
      url: 'http://192.168.31.248:8080/logout', //仅为示例，并非真实的接口地址
      method: "GET",
      success: function (res) {
        console.log(res)
       
      }
    })
    if (this.globalData.stompClient!=null) {
      this.globalData.stompClient.disconnect();
    }
    this.globalData.socketOpen = false;
    this.globalData.stompClient = null;
    this.globalData.socket.close({
    })
  },
  login: function() {
    var that = this;
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        if (res.code) {
          console.log(res.code);
          wx.request({
            url: 'http://192.168.31.248:8080/security/wxlogin', //仅为示例，并非真实的接口地址
            method: "POST",
            data: {
              authCode: res.code
            },
            header: {
              'content-type': 'application/json' // 默认值
            },
            success: function (res) {
              console.log(res)
              var sessionCookie = res.header["set-cookie"] == null ? res.header["Set-Cookie"] : res.header["set-cookie"];
              wx.setStorageSync("sessionCookie", sessionCookie)
              console.log("app show" + that.globalData.socketOpen);
              if (!that.globalData.socketOpen) {
                that.openSocket();
              }
            }
          })
        }
      }
    })
  },
  onLaunch: function () {
    // 登录

    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  globalData: {
    socket:null,
    userInfo: null,
    stompClient:null,
    socketOpen:false,
    socketMsgQueue:[],
    ws:null
  }
})