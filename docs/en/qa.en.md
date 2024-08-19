[Return to Parent Document](./index.en.md)

- [Introduction](#introduction)
  - [Do I need to configure all certificates if I only use one manufacturer?](#do-i-need-to-configure-all-certificates-if-i-only-use-one-manufacturer)
  - [Direct local .aar file dependencies are not supported when building an AAR.](#direct-local-aar-file-dependencies-are-not-supported-when-building-an-aar)
  - [Unable to use FCM push notifications on Huawei phones.](#unable-to-use-fcm-push-notifications-on-huawei-phones)

# Introduction

## Do I need to configure all certificates if I only use one manufacturer?

Answer: Yes, currently it is necessary. If you find it troublesome, you can use template files as placeholders.

## Direct local .aar file dependencies are not supported when building an AAR.

答：https://github.com/facebook/react-native/issues/33062

## Unable to use FCM push notifications on Huawei phones.

Answer: If the Huawei phone does not have Google services installed, it will indeed be unable to use FCM push notifications to receive offline messages. The log indicates `Google Play services missing or without correct permission.`
