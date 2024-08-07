//
//  ReturnUtil.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "ReturnUtil.h"
#import "PushError.h"
#import "ThreadUtil.h"

@implementation ReturnUtil

+ (void)onEvent:(RCTEventEmitter *)eventEmitter withMethod:(NSString *)methodType withData:(nullable NSObject *)data {
    [ThreadUtil mainThreadExecute:^{
      if ([data isKindOfClass:[NSDictionary class]]) {
          [eventEmitter sendEventWithName:methodType body:data];
      } else if ([data isKindOfClass:[NSMutableDictionary class]]) {
          [eventEmitter sendEventWithName:methodType body:data];
      } else if ([data isKindOfClass:[NSString class]]) {
          [eventEmitter sendEventWithName:methodType body:data];
      } else if ([data isKindOfClass:[NSError class]]) {
          [eventEmitter sendEventWithName:methodType body:data];
      } else {
          [eventEmitter sendEventWithName:methodType body:nil];
      }
    }];
}
+ (void)success:(RCTPromiseResolveBlock)resolve withData:(nullable NSObject *)data {
    [ThreadUtil mainThreadExecute:^{
      if ([data isKindOfClass:[NSDictionary class]]) {
          resolve(data);
      } else if ([data isKindOfClass:[NSMutableDictionary class]]) {
          resolve(data);
      } else if ([data isKindOfClass:[NSString class]]) {
          resolve(data);
      } else {
          resolve(nil);
      }
    }];
}
+ (void)fail:(RCTPromiseRejectBlock)reject withError:(NSError *)error {
    [ThreadUtil mainThreadExecute:^{
      reject([NSString stringWithFormat:@"%ld", (long)error.code], error.domain, error);
    }];
}

@end
