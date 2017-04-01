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
#日志跟踪tag
1. 自定义LogUtils
2. 网络"http"
3. 性能检测StrictMode
4. activity生命周期:  ===========>onActivityXXX() 如:===========>onActivityCreated:com.tcmopen.tcmmooc.activitys.SplashActivity
5. fragment生命周期:  ===========>fragmentOnXXX() 如:===========>fragmentOnResume:HomePageFragment{839ccb9 #0 id=0x7f0e0190 android:switcher:2131624336:0}
