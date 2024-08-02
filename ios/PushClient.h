//
//  PushClient.h
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "PushConfig.h"
#import <FirebaseCore/FIROptions.h>
#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

@interface PushClient : NSObject
+ (instancetype)sharedInstance;
- (void)setEventEmitter:(RCTEventEmitter *)eventEmitter;
- (void)init:(NSString *)deviceType
    withResolver:(RCTPromiseResolveBlock)resolve
    withRejecter:(RCTPromiseRejectBlock)reject;
- (nullable NSString *)getDeviceToken;
- (nullable PushConfig *)getPushConfig;
- (void)prepare:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject;
- (void)registerPush:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject;
- (void)unregisterPush:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject;
- (void)getTokenFlow:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)application:(UIApplication *)application
    didReceiveRemoteNotification:(NSDictionary *)userInfo
          fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler;
@end

NS_ASSUME_NONNULL_END
