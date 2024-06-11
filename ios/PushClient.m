//
//  PushClient.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "PushClient.h"
#import "Const.h"
#import "PushType.h"
#import "ReturnUtil.h"
#import "ThreadUtil.h"
#import "ToMapUtil.h"
#import <FirebaseCore/FIROptions.h>
#import <FirebaseCore/FirebaseCore.h>
#import <FirebaseMessaging/FIRMessaging.h>
#import <React/RCTEventEmitter.h>
#import <UIKit/UIKit.h>
#import <UserNotifications/UNNotification.h>
#import <UserNotifications/UNNotificationContent.h>
#import <UserNotifications/UNNotificationRequest.h>
#import <UserNotifications/UNUserNotificationCenter.h>

static NSString *const TAG = @"PushClient";

@interface PushClient () <UIApplicationDelegate, FIRMessagingDelegate, UNUserNotificationCenterDelegate>

@end

@interface PushClient () {
    __weak RCTEventEmitter *_eventEmitter;
    NSData *_Nullable _apnsToken;
    NSString *_Nullable _fcmToken;
    NSString *_pushType;
}

@end

@implementation PushClient
+ (instancetype)sharedInstance {
    static PushClient *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        [self registerSystemNotify];
    }
    return self;
}
- (void)setEventEmitter:(RCTEventEmitter *)eventEmitter {
    _eventEmitter = eventEmitter;
}
- (void)setConfig:(NSString *)pushType {
    _pushType = pushType;
}
- (nullable NSString *)getDeviceToken {
    if ([_pushType isEqual:PushTypeFCM]) {
        return _fcmToken;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        if (_apnsToken != nil) {
            return [self stringWithDeviceToken:_apnsToken];
        }
    }
    return nil;
}
- (nullable PushConfig *)getPushConfig {
    if ([_pushType isEqual:PushTypeFCM]) {
        NSString *senderId = [[[FIRApp defaultApp] options] GCMSenderID];
        return [[FcmPushConfig alloc] initWithId:senderId];
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        return [[ApnsPushConfig alloc] init];
    }
    return nil;
}
- (void)registerPush {
    __weak typeof(self) weakSelf = self;
    NSString *pushType = _pushType;
    [ThreadUtil mainThreadExecute:^{
        [weakSelf requestNotificationPermission];
        [weakSelf setUserNotificationDelegate];

        if ([pushType isEqual:PushTypeFCM]) {

            [weakSelf setFirebaseOption:[FIROptions defaultOptions]];
            [weakSelf setFirebaseMessageDelegate:self];
            [weakSelf getFcmToken];
//            [[FIRMessaging messaging] tokenWithCompletion:^(NSString *token, NSError *error) {
//              if (error != nil) {
//                  NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
//                  map[@"error"] = [error userInfo];
//                  [self sendEvent:onError withData:map];
//              } else {
//                  [weakSelf setPushToken:token];
//              }
//            }];
        } else if ([pushType isEqual:PushTypeAPNS]) {
        }
        
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }];
}
- (void)unregisterPush {
    __weak typeof(self) weakSelf = self;
    NSString *pushType = _pushType;
    
    [ThreadUtil mainThreadExecute:^{
        if ([pushType isEqual:PushTypeFCM]) {
            
            [weakSelf setFirebaseMessageDelegate:nil];
            [weakSelf removeFcmToken];
//            [[FIRMessaging messaging] deleteTokenWithCompletion:^(NSError *_Nullable error) {
//              if (error != nil) {
//                  NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
//                  map[@"error"] = [error userInfo];
//                  [weakSelf sendEvent:onError withData:map];
//              } else {
//                  [weakSelf setPushToken:nil];
//              }
//            }];
        } else if ([pushType isEqual:PushTypeAPNS]) {
        }
        
        [[UIApplication sharedApplication] unregisterForRemoteNotifications];
    }];
}

- (void)sendEvent:(NSString *)methodType withData:(NSMutableDictionary *)data {
    data[@"type"] = methodType;
    [ReturnUtil onEvent:_eventEmitter withMethod:onNativeNotification withData:data];
}

- (void)setPushToken:(id)token {
    if ([_pushType isEqual:PushTypeFCM]) {
        _fcmToken = token;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        _apnsToken = token;
    }
}

- (void)getFcmToken {
    __weak typeof(self) weakSelf = self;
    NSData * apnsToken = [FIRMessaging messaging].APNSToken;
    if (apnsToken != nil) {
        [[FIRMessaging messaging] tokenWithCompletion:^(NSString *token, NSError *error) {
          if (error != nil) {
              NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
              map[@"error"] = [error userInfo];
              [self sendEvent:onError withData:map];
          } else {
              [weakSelf setPushToken:token];
              NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
              map[onReceivePushToken] = token;
              [weakSelf sendEvent:onReceivePushToken withData:map];
          }
        }];
    }
}

- (void)removeFcmToken {
    __weak typeof(self) weakSelf = self;
    [[FIRMessaging messaging] deleteTokenWithCompletion:^(NSError *_Nullable error) {
      if (error != nil) {
          NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
          map[@"error"] = [error userInfo];
          [weakSelf sendEvent:onError withData:map];
      } else {
          [weakSelf setPushToken:nil];
          NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
          map[onReceivePushToken] = nil;
          [weakSelf sendEvent:onReceivePushToken withData:map];
      }
    }];
}

- (void)setFirebaseOption:(FIROptions *)options {
    if (!options) {
        [NSException raise:@"com.firebase.core"
                    format:@"`FirebaseApp.configure()` could not find "
                           @"a valid GoogleService-Info.plist in your project. Please download one "
                           @"from %@.",
                           @"https://console.firebase.google.com/"];
    }
    FIROptions *_options = [[FIRApp defaultApp] options];
    if (_options == nil) {
        [FIRApp configureWithOptions:options];
    }
}
- (void)setFirebaseMessageDelegate:(id<FIRMessagingDelegate>)delegate {
    if ([FIRMessaging messaging].delegate == nil) {
        [FIRMessaging messaging].autoInitEnabled = YES;
        [FIRMessaging messaging].delegate = delegate;
    } else {
        [FIRMessaging messaging].autoInitEnabled = NO;
        [FIRMessaging messaging].delegate = nil;
    }
}
- (void)setUserNotificationDelegate {
    [UNUserNotificationCenter currentNotificationCenter].delegate = self;
}
- (void)requestNotificationPermission {
    UNAuthorizationOptions authOptions =
        UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
    [[UNUserNotificationCenter currentNotificationCenter]
        requestAuthorizationWithOptions:authOptions
                      completionHandler:^(BOOL granted, NSError *_Nullable error) {
                        if (error != nil) {
                            NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
                            map[@"error"] = [error userInfo];
                            [self sendEvent:onError withData:map];
                        }
                      }];
}

- (void)registerSystemNotify {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationWillEnterForeground:)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationDidEnterBackground:)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
}

- (void)applicationWillEnterForeground:(NSNotification *)notification {
    NSLog(@"%@: applicationWillEnterForeground:", TAG);
    [self sendEvent:onAppForeground withData:[NSMutableDictionary dictionaryWithCapacity:1]];
}
- (void)applicationDidEnterBackground:(NSNotification *)notification {
    NSLog(@"%@: applicationDidEnterBackground:", TAG);
    [self sendEvent:onAppBackground withData:[NSMutableDictionary dictionaryWithCapacity:1]];
}

#pragma mark - UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSLog(@"%@: didFinishLaunchingWithOptions:", TAG);
    return true;
}

- (NSString *)stringWithDeviceToken:(NSData *)deviceToken {
    const char *data = [deviceToken bytes];
    NSMutableString *token = [NSMutableString string];

    for (NSUInteger i = 0; i < [deviceToken length]; i++) {
        [token appendFormat:@"%02.2hhX", data[i]];
    }

    return [token copy];
}

- (void)test1:(NSData *)deviceTokenData {

// 假设你有一个 NSData 类型的 deviceToken
//NSData *deviceTokenData = ...;

// 转换为 NSString
NSMutableString *deviceTokenString = [NSMutableString string];
const char *bytes = deviceTokenData.bytes;
NSInteger count = deviceTokenData.length;
for (int i = 0; i < count; i++) {
    [deviceTokenString appendFormat:@"%02x", bytes[i]&0x000000FF];
}

// 打印转换后的 NSString
NSLog(@"Device Token String: %@", deviceTokenString);

// 转换回 NSData
NSMutableData *deviceTokenDataAgain = [NSMutableData data];
for (int i = 0; i < deviceTokenString.length; i += 2) {
    unsigned int byte;
    [[NSScanner scannerWithString:[deviceTokenString substringWithRange:NSMakeRange(i, 2)]] scanHexInt:&byte];
    [deviceTokenDataAgain appendBytes:&byte length:1];
}

// 打印转换回的 NSData
NSLog(@"Device Token Data Again: %@", deviceTokenDataAgain);
}

- (void)test2:(NSData *)deviceTokenData {

// 假设你有一个 NSData 类型的 deviceToken
//NSData *deviceTokenData = ...;

// 转换为 NSString
    NSString *deviceTokenString = [deviceTokenData base64EncodedStringWithOptions:0];


// 打印转换后的 NSString
NSLog(@"Device Token String: %@", deviceTokenString);

// 转换回 NSData
    NSData *deviceTokenDataAgain = [[NSData alloc] initWithBase64EncodedString:deviceTokenString options:0];

// 打印转换回的 NSData
NSLog(@"Device Token Data Again: %@", deviceTokenDataAgain);
}

- (void)application:(UIApplication *)application
    didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSLog(@"%@: didRegisterForRemoteNotificationsWithDeviceToken:%@", TAG, deviceToken);
    [self setPushToken:deviceToken];
    [self test1:deviceToken];
    [self test2:deviceToken];
    if ([_pushType isEqual:PushTypeFCM]) {
        [FIRMessaging messaging].APNSToken = deviceToken;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        NSMutableDictionary *map = [NSMutableDictionary dictionary];
        map[@"token"] = [self stringWithDeviceToken:deviceToken];
        [self sendEvent:onReceivePushToken withData:map];
    }
}
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"%@: didFailToRegisterForRemoteNotificationsWithError:%@", TAG, error);
}
- (void)application:(UIApplication *)application
    didReceiveRemoteNotification:(NSDictionary *)userInfo
          fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    NSLog(@"%@: didReceiveRemoteNotification:%@", TAG, userInfo);
    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    map[@"message"] = userInfo;
    [self sendEvent:onReceivePushMessage withData:map];
}

#pragma mark - FIRMessagingDelegate

- (void)messaging:(FIRMessaging *)messaging didReceiveRegistrationToken:(nullable NSString *)fcmToken {
    NSLog(@"%@: didReceiveRegistrationToken:%@", TAG, fcmToken);
    [self setPushToken:fcmToken];
    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    map[@"token"] = fcmToken;
    if ([_pushType isEqual:PushTypeFCM]) {
        [self sendEvent:onReceivePushToken withData:map];
    }
}

#pragma mark - UNUserNotificationCenterDelegate

// Receive displayed notifications for iOS 10 devices.
// Handle incoming notification messages while app is in the foreground.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    NSDictionary *userInfo = notification.request.content.userInfo;

    // With swizzling disabled you must let Messaging know about the message, for Analytics
    // [[FIRMessaging messaging] appDidReceiveMessage:userInfo];

    // ...

    // Print full message.
    NSLog(@"%@: userNotificationCenter:%@", TAG, userInfo);
    //    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    //    map[@"message"] = userInfo;
    //    [self sendEvent:onReceivePushMessage withData:map];

    // Change this to your preferred presentation option
    completionHandler(UNNotificationPresentationOptionBadge | UNNotificationPresentationOptionAlert);
}

// Handle notification messages after display notification is tapped by the user.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
    didReceiveNotificationResponse:(UNNotificationResponse *)response
             withCompletionHandler:(void (^)(void))completionHandler {
    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // With swizzling disabled you must let Messaging know about the message, for Analytics
    // [[FIRMessaging messaging] appDidReceiveMessage:userInfo];

    // Print full message.
    NSLog(@"%@: didReceiveNotificationResponse:%@", TAG, userInfo);
    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    map[@"message"] = userInfo;
    [self sendEvent:onNotificationClick withData:map];

    completionHandler();
}

@end
