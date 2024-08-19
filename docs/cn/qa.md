[返回父文档](./index.md)

- [介绍](#介绍)
  - [如果仅用其中一个厂商，是否也需要配置所有证书](#如果仅用其中一个厂商是否也需要配置所有证书)
  - [Direct local .aar file dependencies are not supported when building an AAR.](#direct-local-aar-file-dependencies-are-not-supported-when-building-an-aar)
  - [在华为手机中，无法使用 fcm 推送。](#在华为手机中无法使用-fcm-推送)

# 介绍

## 如果仅用其中一个厂商，是否也需要配置所有证书

答：目前是这样的。 如果觉得麻烦可以使用模板文件占位。

## Direct local .aar file dependencies are not supported when building an AAR.

答：https://github.com/facebook/react-native/issues/33062

## 在华为手机中，无法使用 fcm 推送。

答：如果华为手机没有安装谷歌套件，那么确实无法使用 fcm 推送接收离线消息。日志提示 `Google Play services missing or without correct permission.`

类似的，其它国产手机，如果没有安装谷歌套件，那么也无法获取 token。报错信息类似：`Topic sync or token retrieval failed on hard failure exceptions: java.util.concurrent.ExecutionException: java.io.IOException: SERVICE_NOT_AVAILABLE. Won't retry the operation.`
