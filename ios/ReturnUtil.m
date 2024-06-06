//
//  ReturnUtil.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "ReturnUtil.h"

@implementation ReturnUtil

+ (void)onEvent:(RCTEventEmitter *)eventEmitter withMethod:(NSString *)methodType withData:(nullable NSObject *)data {
    if ([data isKindOfClass:[NSDictionary class]]) {
        [eventEmitter sendEventWithName:methodType body:data];
    } else if ([data isKindOfClass:[NSMutableDictionary class]]) {
        [eventEmitter sendEventWithName:methodType body:data];
    } else if ([data isKindOfClass:[NSString class]]) {
        [eventEmitter sendEventWithName:methodType body:data];
    } else {
        [eventEmitter sendEventWithName:methodType body:nil];
    }
}
+ (void)success:(RCTPromiseResolveBlock)resolve withData:(nullable NSObject *)data {
    if ([data isKindOfClass:[NSDictionary class]]) {
        resolve(data);
    } else if ([data isKindOfClass:[NSMutableDictionary class]]) {
        resolve(data);
    } else if ([data isKindOfClass:[NSString class]]) {
        resolve(data);
    } else {
        resolve(nil);
    }
}
+ (void)fail:(RCTPromiseRejectBlock)reject withError:(NSError *)error {
    reject(nil, nil, error);
}
+ (void)fail:(RCTPromiseRejectBlock)reject withCode:(NSString *)code withMessage:(NSString *)message {
    reject(code, message, nil);
}

@end
