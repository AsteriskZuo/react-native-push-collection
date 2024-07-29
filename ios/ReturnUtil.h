//
//  ReturnUtil.h
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

@interface ReturnUtil : NSObject
+ (void)onEvent:(RCTEventEmitter *)eventEmitter withMethod:(NSString *)methodType withData:(nullable NSObject *)data;
+ (void)success:(RCTPromiseResolveBlock)resolve withData:(nullable NSObject *)data;
+ (void)fail:(RCTPromiseRejectBlock)reject withError:(NSError *)error;
+ (void)fail:(RCTPromiseRejectBlock)reject withCode:(NSInteger)code withMessage:(NSErrorDomain _Nonnull)domain;
@end

NS_ASSUME_NONNULL_END
