var app = getApp()
Page({
  data: {
    motto: 'Hello World',
    userInfo: {}
  },
  //事件处理函数
  // bindViewTap: function() {
  //   wx.navigateTo({
  //   url: '../logs/logs'
  //   })
  // },
  //调用扫码功能
  callQRcode: function() {
    wx.scanCode({
      success: (res) => {
        wx.getLocation({
          type: 'wgs84',
          success(res) {
            const lat = res.latitude
            const lng = res.longitude
          }
        })
        var data = res.result;
        var arr = data.split('&');
        if (data.indexOf(str) > -1) {
          arr = data.split(str);
          var api = "https://szuai.club:8888/signin/stu";
          var stu_id;

          wx.getStorage({
            key: 'stu_id',
            success: function(res) {
              stu_id = res.data;
            },
          })
          wx.request({
            url: api + "/signin",
            method: "POST",
            data: {
              stu_id: stu_id,
              class_id: arr[0],
              valid: arr[1],
              lat: lat,
              lng: lng
            },
            success: function(res) {
              if (res.data.succ) {
                wx.showToast({
                  title: "签到成功",
                  image: "../images/user.png",
                  duration: 3000,
                  mask: true
                });
                wx.request({
                  url: api + "/updateStatus",
                  method: "POST",
                  data: {
                    stu_id: s,
                    class_id: arr[0],
                  },
                  success: function(res) {
                    if (res.data.succ) {
                      wx.navigateTo({
                        url: '../logs/logs'
                      })
                    } else {
                      wx.showToast({
                        title: res.data.msg,
                        image: "../images/password.png",
                        duration: 3000,
                        mask: true
                      });
                    }
                  }
                })
              } else {
                wx.showToast({
                  title: res.data.msg,
                  image: "../images/password.png",
                  duration: 3000,
                  mask: true
                });
              }
            }
          })
        }

      }
    });
  },
  onLoad: function() {
    console.log('onLoad')
  }
})