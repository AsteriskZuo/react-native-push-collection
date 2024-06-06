#import "PushCollection.h"
#import "Const.h"
#import "PushClient.h"
#import "PushType.h"
#import "ReturnUtil.h"
#import "ToMapUtil.h"

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
               withError:[NSError errorWithDomain:NSCocoaErrorDomain
                                             code:commonError
                                         userInfo:@{NSLocalizedDescriptionKey : @"Platform is required"}]];
    }
    if (![platform isEqual:@"ios"]) {
        [ReturnUtil fail:reject
               withError:[NSError errorWithDomain:NSCocoaErrorDomain
                                             code:commonError
                                         userInfo:@{NSLocalizedDescriptionKey : @"Platform is not supported"}]];
    }
    if (![pushType isEqual:PushTypeFCM] && ![pushType isEqual:PushTypeAPNS]) {
        [ReturnUtil fail:reject
               withError:[NSError errorWithDomain:NSCocoaErrorDomain
                                             code:commonError
                                         userInfo:@{NSLocalizedDescriptionKey : @"Device type is not supported"}]];
    }
    [[PushClient sharedInstance] setConfig:pushType];
}

RCT_REMAP_METHOD(registerPush, registerPush:withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [[PushClient sharedInstance] registerPush];
    [ReturnUtil success:resolve withData:nil];
}

RCT_REMAP_METHOD(unregisterPush, unregisterPush:withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    [[PushClient sharedInstance] unregisterPush];
    [ReturnUtil success:resolve withData:nil];
}

RCT_REMAP_METHOD(getPushConfig, getPushConfig:withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSDictionary *ret = [ToMapUtil toMap:[[PushClient sharedInstance] getPushConfig]];
    [ReturnUtil success:resolve withData:ret];
}

RCT_REMAP_METHOD(getToken, getToken:withResolver
                 : (RCTPromiseResolveBlock)resolve withRejecter
                 : (RCTPromiseRejectBlock)reject) {
    NSString *ret = [[PushClient sharedInstance] getDeviceToken];
    [ReturnUtil success:resolve withData:ret];
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
    //  WARN  Module PushCollection requires main queue setup since it overrides `init` but doesn't implement `requiresMainQueueSetup`. In a future release React Native will default to initializing all native modules on a background thread unless explicitly opted-out of.
    return YES;
}

@end
