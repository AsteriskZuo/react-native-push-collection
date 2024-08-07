//
//  PushClient.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "PushClient.h"
#import "Const.h"
#import "PushError.h"
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
    PushConfig *_config;
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
- (void)init:(NSString *)pushType
    withResolver:(RCTPromiseResolveBlock)resolve
    withRejecter:(RCTPromiseRejectBlock)reject {

    [ThreadUtil mainThreadExecute:^{
      [self requestNotificationPermission];
      [self setUserNotificationDelegate];
      [[UIApplication sharedApplication] registerForRemoteNotifications];
    }];

    if ([pushType isEqual:PushTypeFCM]) {
        NSString *senderId = [[[FIRApp defaultApp] options] GCMSenderID];
        _config = [[FcmPushConfig alloc] initWithId:senderId];
        [self initFireMessaging:self];
    } else if ([pushType isEqual:PushTypeAPNS]) {
        _config = [[ApnsPushConfig alloc] init];
    } else {
        [ReturnUtil fail:reject
               withError:[PushErrorHelper createError:PushErrorCodeParam
                                           withDomain:[PushErrorHelper getDomain:PushErrorCodeParam]
                                         withUserInfo:nil]];
        return;
    }
    [ReturnUtil success:resolve withData:nil];
}
- (nullable NSString *)getDeviceToken {
    NSString *_pushType = [[self getPushConfig] pushType];
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
    return self->_config;
}
- (void)prepare:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
    [ReturnUtil success:resolve withData:nil];
}
- (void)registerPush:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
    NSString *_pushType = [[self getPushConfig] pushType];
    if ([_pushType isEqual:PushTypeFCM]) {
        __weak typeof(self) weakSelf = self;
        [[FIRMessaging messaging] tokenWithCompletion:^(NSString *token, NSError *error) {
          if (error != nil) {
              [ReturnUtil fail:reject
                     withError:[PushErrorHelper createError:error.code
                                                 withDomain:error.domain
                                               withUserInfo:@{
                                                   @"code" : @(PushErrorCodeRegister),
                                                   @"message" : [PushErrorHelper getDomain:PushErrorCodeRegister]
                                               }]];
          } else {
              [weakSelf setPushToken:token];
              [ReturnUtil success:resolve withData:token];
          }
        }];
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        if (_apnsToken != nil) {
            [ReturnUtil success:resolve withData:[self stringWithDeviceToken:_apnsToken]];
        } else {
            [ReturnUtil success:resolve withData:nil];
        }
    }
}
- (void)unregisterPush:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
    NSString *_pushType = [[self getPushConfig] pushType];
    if ([_pushType isEqual:PushTypeFCM]) {
        __weak typeof(self) weakSelf = self;
        [[FIRMessaging messaging] deleteTokenWithCompletion:^(NSError *_Nullable error) {
          if (error != nil) {
              [ReturnUtil fail:reject
                     withError:[PushErrorHelper createError:error.code
                                                 withDomain:error.domain
                                               withUserInfo:@{
                                                   @"code" : @(PushErrorCodeUnRegister),
                                                   @"message" : [PushErrorHelper getDomain:PushErrorCodeUnRegister]
                                               }]];
          } else {
              [weakSelf setPushToken:nil];
              [ReturnUtil success:resolve withData:nil];
          }
        }];
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        [ReturnUtil success:resolve withData:nil];
    }
}

- (void)getTokenFlow:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
    NSString *token = [self getDeviceToken];
    if (token == nil) {
        [self
            registerPush:^(id result) {
              if ([result isKindOfClass:[NSString class]]) {
                  NSMutableDictionary *map = [NSMutableDictionary dictionary];
                  map[@"token"] = (NSString *)result;
                  [self sendEvent:onReceivePushToken withData:map];
              }
              [ReturnUtil success:resolve withData:nil];
            }
            withRejecter:reject];
    } else {
        NSMutableDictionary *map = [NSMutableDictionary dictionary];
        map[@"token"] = token;
        [self sendEvent:onReceivePushToken withData:map];
        [ReturnUtil success:resolve withData:nil];
    }
}

- (void)sendEvent:(NSString *)methodType withData:(NSMutableDictionary *)data {
    data[@"type"] = methodType;
    [ReturnUtil onEvent:_eventEmitter withMethod:onNativeNotification withData:data];
}

- (void)setPushToken:(id)token {
    NSString *_pushType = [[self getPushConfig] pushType];
    if ([_pushType isEqual:PushTypeFCM]) {
        _fcmToken = token;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        _apnsToken = token;
    }
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
- (void)initFireMessaging:(id<FIRMessagingDelegate>)delegate {
    [self setFirebaseOption:[FIROptions defaultOptions]];
    //    if ([[FIRMessaging messaging] isAutoInitEnabled] == NO) {
    //        [[FIRMessaging messaging] setAutoInitEnabled:YES];
    //    }
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
                            NSError *myError =
                                [PushErrorHelper createError:error.code
                                                  withDomain:error.domain
                                                withUserInfo:@{
                                                    @"code" : @(PushErrorCodePrepare),
                                                    @"message" : [PushErrorHelper getDomain:PushErrorCodePrepare]
                                                }];
                            map[@"error"] = [ToMapUtil toMap:myError];
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
    return [self _extractTokenFromRawData:deviceToken];
    //    return [deviceToken base64EncodedStringWithOptions:0];
    //    const char *data = [deviceToken bytes];
    //    NSMutableString *token = [NSMutableString string];
    //
    //    for (NSUInteger i = 0; i < [deviceToken length]; i++) {
    //        [token appendFormat:@"%02.2hhX", data[i]];
    //    }
    //
    //    return [token copy];
}

- (NSString *)_extractTokenFromRawData:(NSData *)deviceToken {
    NSString *token = @"";
    do {
        if (@available(iOS 13.0, *)) {
            if ([deviceToken isKindOfClass:[NSData class]]) {
                const unsigned *tokenBytes = (const unsigned *)[deviceToken bytes];
                token = [NSString stringWithFormat:@"%08x%08x%08x%08x%08x%08x%08x%08x", ntohl(tokenBytes[0]),
                                                   ntohl(tokenBytes[1]), ntohl(tokenBytes[2]), ntohl(tokenBytes[3]),
                                                   ntohl(tokenBytes[4]), ntohl(tokenBytes[5]), ntohl(tokenBytes[6]),
                                                   ntohl(tokenBytes[7])];
                break;
            } else if ([deviceToken isKindOfClass:[NSString class]]) {
                token = [NSString stringWithFormat:@"%@", deviceToken];
                token = [token stringByReplacingOccurrencesOfString:@"<" withString:@""];
                token = [token stringByReplacingOccurrencesOfString:@">" withString:@""];
                token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
                break;
            }
        } else {
            token = [NSString stringWithFormat:@"%@", deviceToken];
            token = [token stringByReplacingOccurrencesOfString:@"<" withString:@""];
            token = [token stringByReplacingOccurrencesOfString:@">" withString:@""];
            token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
            break;
        }
    } while (0);

    return token;
}

- (void)test1:(NSData *)deviceTokenData {

    // 假设你有一个 NSData 类型的 deviceToken
    // NSData *deviceTokenData = ...;

    // 转换为 NSString
    NSMutableString *deviceTokenString = [NSMutableString string];
    const char *bytes = deviceTokenData.bytes;
    NSInteger count = deviceTokenData.length;
    for (int i = 0; i < count; i++) {
        [deviceTokenString appendFormat:@"%02x", bytes[i] & 0x000000FF];
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
    // NSData *deviceTokenData = ...;

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
    NSString *_pushType = [[self getPushConfig] pushType];
    if ([_pushType isEqual:PushTypeFCM]) {
        [FIRMessaging messaging].APNSToken = deviceToken;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        NSString *token = [self stringWithDeviceToken:deviceToken];
        NSMutableDictionary *map = [NSMutableDictionary dictionary];
        map[@"token"] = token;
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
    NSString *_pushType = [[self getPushConfig] pushType];
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
