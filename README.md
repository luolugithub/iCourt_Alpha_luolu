#alpha android客户端  
#接入流程  
 1. 申请企业邮箱(eg. youxuan@icourt.cc),负责人(HR 韩时雨);用企业邮箱注册gitlab,并让管理员(吴佑炫)邀请  
 2. 下载最新的Android Studio,并安装git AS与git链接成功;  
 3. 将gitlab的代码导入AS(时间可能持续很长) https://code.alphalawyer.cn/users/sign_in  
 4. 下载zeplin客户端(UI设计图)并注册账号,让管理员(李方明)邀请加入Android项目;
 5. 下载享聊客户端活着手机客户端 找李珊珊邀请加入jira bug管理系统 (http://jira.alphalawyer.cn/secure/BrowseProjects.jspa#all)  
 6. 需求文档(wiki http://wiki.alphalawyer.cn) 找吕东东开一个账户;  
 7. app bug监控系统 bugtags,请自行注册,然后让吴佑炫邀请加入;  
 8. 查看app gradle环境与打包,安装好app,试用并测试和熟悉功能与业务;
 9. 接口文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#/ 
 
#merge requests  
   分支名称规则： br_dev_版本号(2.1.0)_模块名称(chat)_个人名字简写(xyw)  eg.  br_dev_2.1.0_chat_xyw   
   默认情况:都从dev分支开;不能独立操作test,master分支  
   插件:GitLab Projects
 4
#fir账号
 内部：wangchu@icourt.cc   q****  
 外部：104421133@qq.com    a*****  
 外部：it@icourt.cc        z****** （包内的更新token和id都引用的是1044账号的token和id）  
 test：394337795@qq.com    3*****  
 内部日更新：zhouyong@icourt.cc     I******* 


#上线打包日志  
 2017/7/22 v2.0.1 内部包 (fir账号：wangchu@icourt.cc)
 2017/7/26 v2.0.3 内部包 (fir账号：wangchu@icourt.cc)

 2017/7/25 v2.0.0 外部包 (fir账号：104421133@qq.com)
 2017/7/26 v2.0.1 外部包 (fir账号：104421133@qq.com) 
  
 alpha-BaiDu-release-v2.0.2_2017-08-08 20/25_v202_jiagu_sign.apk  
 alpha-BaiDu-release-v2.0.3_2017-09-05 23/50_v203_jiagu_sign.apk    
 2017/7/26 v2.0.1 外部包 (fir账号：104421133@qq.com)  
 2017/8/8  v2.0.2 alpha-BaiDu-release-v2.0.2_2017-08-08 20/25_v202_jiagu_sign.apk

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

 云信IM账号:  
 1. 测试:13466661048      密码: iC******  
 2. 正式:chenli@icourt.cc 密码: iC******  
   
 推送:  
 1. 华为: zhouyong@icourt.cc  Icourtadmin*****  
 2. 小米: 15666794049         Icourt******

 查看证书信息命令:  
     keytool -list -v -keystore ~/Desktop/agency.jks -alias 别名 -storepass 密码 -keypass 密码  
      
 神策账号:  
  地址: https://shenceadmin.alphalawyer.cn  
  测试: 用户名:admin 密码:ud***** 
  正式: 用户名:admin 密码:v*****  
  集成文档:https://github.com/sensorsdata/sa-sdk-android  
   
#日志跟踪tag
1. 自定义LogUtils
2. 网络"http"
3. 性能检测StrictMode
4. activity生命周期:  ===========>onActivityXXX() 如:===========>onActivityCreated:com.tcmopen.tcmmooc.activitys.SplashActivity
5. fragment生命周期:  ===========>fragmentOnXXX() 如:===========>fragmentOnResume:HomePageFragment{839ccb9 #0 id=0x7f0e0190 android:switcher:2131624336:0}  


#网易云通信状态码
  http://dev.netease.im/docs/product/%E9%80%9A%E7%94%A8/%E7%8A%B6%E6%80%81%E7%A0%81  