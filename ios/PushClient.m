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
            return [[NSString alloc] initWithData:_apnsToken encoding:NSUTF8StringEncoding];
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
    [self requestNotificationPermission];
    [self setUserNotificationDelegate];

    if ([_pushType isEqual:PushTypeFCM]) {
        __weak typeof(self) weakSelf = self;

        [self setFirebaseOption:[FIROptions defaultOptions]];
        [self setFirebaseMessageDelegate];

        [[FIRMessaging messaging] tokenWithCompletion:^(NSString *token, NSError *error) {
          if (error != nil) {
              NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
              map[@"message"] = [error userInfo];
              [self sendEvent:onError withData:map];
          } else {
              [weakSelf setPushToken:token];
          }
        }];
    } else if ([_pushType isEqual:PushTypeAPNS]) {
    }

    [[UIApplication sharedApplication] registerForRemoteNotifications];
}
- (void)unregisterPush {
    if ([_pushType isEqual:PushTypeFCM]) {
        __weak typeof(self) weakSelf = self;
        [FIRMessaging messaging].delegate = nil;
        [[UIApplication sharedApplication] unregisterForRemoteNotifications];
        [[FIRMessaging messaging] deleteTokenWithCompletion:^(NSError *_Nullable error) {
          if (error != nil) {
              NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
              map[@"message"] = [error userInfo];
              [self sendEvent:onError withData:map];
          } else {
              [weakSelf setPushToken:nil];
          }
        }];
    } else if ([_pushType isEqual:PushTypeAPNS]) {
    }
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

- (void)setFirebaseOption:(FIROptions *)options {
    if (!options) {
        [NSException raise:@"com.firebase.core"
                    format:@"`FirebaseApp.configure()` could not find "
                           @"a valid GoogleService-Info.plist in your project. Please download one "
                           @"from %@.",
                           @"https://console.firebase.google.com/"];
    }
    [FIRApp configureWithOptions:options];
}
- (void)setFirebaseMessageDelegate {
    [FIRMessaging messaging].delegate = self;
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
                            map[@"message"] = [error userInfo];
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
    return true;
}

- (void)application:(UIApplication *)application
    didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [self setPushToken:deviceToken];
    if ([_pushType isEqual:PushTypeFCM]) {
        [FIRMessaging messaging].APNSToken = deviceToken;
    } else if ([_pushType isEqual:PushTypeAPNS]) {
        NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
        map[@"token"] = [[NSString alloc] initWithData:deviceToken encoding:NSUTF8StringEncoding];
        [self sendEvent:onReceivePushToken withData:map];
    }
}
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"didFailToRegisterForRemoteNotificationsWithError:%@", error);
}
- (void)application:(UIApplication *)application
    didReceiveRemoteNotification:(NSDictionary *)userInfo
          fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    map[@"message"] = userInfo;
    [self sendEvent:onReceivePushMessage withData:map];
}

#pragma mark - FIRMessagingDelegate

- (void)messaging:(FIRMessaging *)messaging didReceiveRegistrationToken:(nullable NSString *)fcmToken {
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
    NSLog(@"%@", userInfo);
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
    NSLog(@"%@", userInfo);
    NSMutableDictionary *map = [NSMutableDictionary dictionaryWithCapacity:2];
    map[@"message"] = userInfo;
    [self sendEvent:onNotificationClick withData:map];

    completionHandler();
}

@end
