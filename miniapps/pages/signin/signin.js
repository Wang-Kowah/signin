var app = getApp()
Page({
  data: {
    lat: 0,
    lng: 0
  },
  //调用扫码功能
  callQRcode: function() {
    wx.scanCode({
      success: (res) => {
        var data = res.result;
        if (data.indexOf('&') > -1) {
          var arr = data.split('&');
          var api = "https://szuai.club:8888/signin/stu";
          var stu_id, latitude, longitude;

          wx.getStorage({
            key: 'stu_id',
            success: function(res) {
              stu_id = res.data;
            },
          })
          // wx.getStorage({
          //   key: 'lat',
          //   success: function(res) {
          //     latitude = res.data;
          //   },
          // })
          // wx.getStorage({
          //   key: 'lng',
          //   success: function(res) {
          //     longitude = res.data;
          //   },
          // })
          latitude = this.data.lat;
          longitude = this.data.lng;
          wx.request({
            url: api + "/signin",
            method: "POST",
            data: {
              class_id: arr[0],
              valid: arr[1],
              lat: latitude,
              lng: longitude
            },
            header: {
              "Content-Type": "application/x-www-form-urlencoded"
            },
            success: function(res) {
              if (res.data.retcode == 0) {
                wx.showToast({
                  title: "签到成功",
                  icon: "none",
                  duration: 3000,
                  mask: true
                });
                wx.request({
                  url: api + "/updateStatus",
                  method: "POST",
                  data: {
                    stu_id: stu_id,
                    class_id: arr[0],
                  },
                  header: {
                    "Content-Type": "application/x-www-form-urlencoded"
                  },
                  success: function(res) {
                    if (res.data.retcode == 0) {
                      wx.navigateTo({
                        url: '../seccess/seccess'
                      })
                    } else {
                      wx.showToast({
                        title: res.data.msg,
                        icon: "none",
                        duration: 5000,
                        mask: true
                      });
                    }
                  }
                })
              } else {
                wx.showToast({
                  title: res.data.msg,
                  icon: "none",
                  duration: 5000,
                  mask: true
                });
              }
            }
          })
        }
      }
    });
  },
  onLoad: function(options) {
    console.log('Signin onLoad')
    this.setData({
      lat: options.lat,
      lng: options.lng
    })
  }
})