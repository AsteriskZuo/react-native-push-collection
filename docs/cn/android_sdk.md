# 关键点

## 技术点

- 支持哪些厂商，小厂商的支持
  - 红魔（Red Magic）: 努比亚（nubia）的游戏手机品牌，努比亚是中兴通讯的子公司
  - IQOO: VIVO 的子品牌
  - 一加（OnePlus）: OPPO 的子品牌
  - 真我（Realme）: OPPO 的子品牌
- 提供的功能： 主动获取 token、主动更新 token、被动接收 token、点击通知栏处理
- 和原生 android 一起使用，会不会有问题？厂商 sdk 会不会重复？
  - 原生没有集成 各个厂商的 sdk，所以，不会冲突。
- 点击通知栏的回调通知是否可以统一？
  - 点击通知栏默认打开应用，以为华为为例：可以通过 onCreate 接收 Intent消息。
- 高版本的android是否需要适配？
  - <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
- 华为不支持点击通知回调。
  - 它支持透传和通知栏，网易使用的是透传，咱们后台不支持（和泽源确认）
  - 可能需要使用常规通知栏处理，常规通知栏需要侵占用户的代码。
  - https://doc.yunxin.163.com/messaging2/references/android/doxygen/Latest/zh/classcom_1_1netease_1_1nimlib_1_1sdk_1_1mixpush_1_1_h_w_push_message_service.html#af9e8dfedc5083bbdc6ee299d2b6bafbf
- 创建android项目
  - 方法1：创建应用项目，添加lib模块，在lib模块中实现推送sdk
  - 方法2：创建应用项目，创建lib项目，通过本地引用的方式实现推送sdk
- android的sdk如何打包？
  - 

