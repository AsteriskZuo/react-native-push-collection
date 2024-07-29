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
    case PushErrorCodeUnkown:
        ret = @"unkown error";
        break;

    default:
        ret = @"";
        break;
    }
    return ret;
}
+ (NSError *)createError:(PushErrorCode)code withDomain:(NSErrorDomain)domain {
    return [NSError errorWithDomain:domain code:code userInfo:nil];
}

@end
