#import "PushCollection.h"
#import "Const.h"
#import "PushClient.h"
#import "PushError.h"
#import "PushType.h"
#import "ReturnUtil.h"
#import "ThreadUtil.h"
#import "ToMapUtil.h"

#import "RCTAppDelegate.h"

static NSString *const TAG = @"PushCollection";

@implementation PushCollection
RCT_EXPORT_MODULE()

- (instancetype)init {
    self = [super init];
    if (self) {
        [[PushClient sharedInstance] setEventEmitter:self];
    }
    return self;
}

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_REMAP_METHOD(multiply, multiplyWithA
                 : (double)a withB
                 : (double)b withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSNumber *result = @(a * b);

    resolve(result);
}

RCT_REMAP_METHOD(init, init
                 : (NSDictionary *)params withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSString *platform = params[@"platform"];
    NSString *pushType = params[@"pushType"];

    if (platform == nil) {
        [ReturnUtil fail:reject
               withError:[PushErrorHelper createError:PushErrorCodeParam
                                           withDomain:[PushErrorHelper getDomain:PushErrorCodeParam]
                                         withUserInfo:nil]];
    }
    if (![platform isEqual:@"ios"]) {
        [ReturnUtil fail:reject
               withError:[PushErrorHelper createError:PushErrorCodeNoSupport
                                           withDomain:[PushErrorHelper getDomain:PushErrorCodeNoSupport]
                                         withUserInfo:nil]];
    }
    if (![pushType isEqual:PushTypeFCM] && ![pushType isEqual:PushTypeAPNS]) {
        [ReturnUtil fail:reject
               withError:[PushErrorHelper createError:PushErrorCodeNoSupport
                                           withDomain:[PushErrorHelper getDomain:PushErrorCodeNoSupport]
                                         withUserInfo:nil]];
    }
    [ThreadUtil asyncExecute:^{
      [[PushClient sharedInstance] init:pushType withResolver:resolve withRejecter:reject];
    }];
}

RCT_REMAP_METHOD(prepare, prepare
                 : (NSDictionary *)params withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [ThreadUtil asyncExecute:^{
      [[PushClient sharedInstance] prepare:resolve withRejecter:reject];
    }];
}

RCT_REMAP_METHOD(registerPush, registerPush
                 : (NSDictionary *)params withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [ThreadUtil asyncExecute:^{
      [[PushClient sharedInstance] registerPush:resolve withRejecter:reject];
    }];
}

RCT_REMAP_METHOD(unregisterPush, unregisterPushWithResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [ThreadUtil asyncExecute:^{
      [[PushClient sharedInstance] unregisterPush:resolve withRejecter:reject];
    }];
}

RCT_REMAP_METHOD(getPushConfig, getPushConfigWithResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSDictionary *ret = [ToMapUtil toMap:[[PushClient sharedInstance] getPushConfig]];
    [ReturnUtil success:resolve withData:ret];
}

RCT_REMAP_METHOD(getToken, getTokenWithResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSString *ret = [[PushClient sharedInstance] getDeviceToken];
    [ReturnUtil success:resolve withData:ret];
}

RCT_REMAP_METHOD(getTokenFlow, getTokenFlow
                 : (NSDictionary *)params withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [ThreadUtil asyncExecute:^{
      [[PushClient sharedInstance] getTokenFlow:resolve withRejecter:reject];
    }];
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativePushCollectionSpecJSI>(params);
}
#endif

- (void)addListener:(NSString *)eventName {
    [super addListener:eventName];
}
- (void)removeListeners:(double)count {
    [super removeListeners:count];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[ onNativeNotification ];
}

+ (BOOL)requiresMainQueueSetup {
    //  WARN  Module PushCollection requires main queue setup since it overrides `init` but doesn't implement
    //  `requiresMainQueueSetup`. In a future release React Native will default to initializing all native modules on a
    //  background thread unless explicitly opted-out of.
    return YES;
}

@end

@implementation RCTAppDelegate (Push)

// !!! Do not implement it, otherwise the program will fail.
//- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
//    return [PushClient.sharedInstance application:application didFinishLaunchingWithOptions:launchOptions];
//}
- (void)application:(UIApplication *)application
    didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [PushClient.sharedInstance application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [PushClient.sharedInstance application:application didFailToRegisterForRemoteNotificationsWithError:error];
}
- (void)application:(UIApplication *)application
    didReceiveRemoteNotification:(NSDictionary *)userInfo
          fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    [PushClient.sharedInstance application:application
              didReceiveRemoteNotification:userInfo
                    fetchCompletionHandler:completionHandler];
}

@end
