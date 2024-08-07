//
//  PushError.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.07.29.
//

#import "PushError.h"

@implementation PushErrorHelper

+ (NSErrorDomain)getDomain:(PushErrorCode)code {
    NSErrorDomain ret;
    switch (code) {
    case PushErrorCodeParam:
        ret = @"params error";
        break;
    case PushErrorCodeNoSupport:
        ret = @"no support error";
        break;
    case PushErrorCodeJsonParse:
        ret = @"json parse error";
        break;
    case PushErrorCodeInit:
        ret = @"init error";
        break;
    case PushErrorCodeRegister:
        ret = @"register error";
        break;
    case PushErrorCodeUnRegister:
        ret = @"unregister error";
        break;
    case PushErrorCodePrepare:
        ret = @"prepare error";
        break;
    case PushErrorCodeUnknown:
        ret = @"unkown error";
        break;

    default:
        ret = @"";
        break;
    }
    return ret;
}
+ (NSError *)createError:(PushErrorCode)code
              withDomain:(NSErrorDomain)domain
            withUserInfo:(nullable NSDictionary<NSErrorUserInfoKey, id> *)userInfo {
    if (userInfo == nil) {
        return [NSError errorWithDomain:domain code:code userInfo:@{@"code" : @(code), @"message" : domain}];
    }
    return [NSError errorWithDomain:domain code:code userInfo:userInfo];
}

@end

@implementation NSError (map)

- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary* ret = [NSMutableDictionary dictionary];
    NSMutableDictionary* sub = [NSMutableDictionary dictionary];
    sub[@"code"] = @(self.code);
    sub[@"message"] = self.domain;
    ret[@"userInfo"] = sub;
    return ret;
}

@end
