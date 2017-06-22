#alpha android客户端  
#上线打包日志  
 eg. 2017/1/28 v3.3.8  
#打包配置  
 已在gradle与Hconst中配置完成;  
 暂时不混淆代码,采用360加固服务;  
 http主要有四个环境  
 1. debug     开发版   https://dev.alphalawyer.cn/  
 2. innertest 内测版   https://test.alphalawyer.cn/  
 3. preview   预览版   https://pre.alphalawyer.cn／  
 4. release   正式版   https://alphalawyer.cn/  
 
 云信配置 com.netease.nim.appKey
 1. 云信开发版 b0888ddeed001121372f4c050e1de737
 2. 云信开内测版 4767e87cb6cb9c86946956cc3ecf605c
 3. 云信预览版 c8c7a60c918645e80229e500f4c0e58f
 4. 云信正式版 c8c7a60c918645e80229e500f4c0e58f
 
#日志跟踪tag
1. 自定义LogUtils
2. 网络"http"
3. 性能检测StrictMode
4. activity生命周期:  ===========>onActivityXXX() 如:===========>onActivityCreated:com.tcmopen.tcmmooc.activitys.SplashActivity
5. fragment生命周期:  ===========>fragmentOnXXX() 如:===========>fragmentOnResume:HomePageFragment{839ccb9 #0 id=0x7f0e0190 android:switcher:2131624336:0}  


#网易云通信状态码
  http://dev.netease.im/docs/product/%E9%80%9A%E7%94%A8/%E7%8A%B6%E6%80%81%E7%A0%81  