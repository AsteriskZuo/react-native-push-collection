//
//  PushError.h
//  react-native-push-collection
//
//  Created by asterisk on 2024.07.29.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, PushErrorCode) {
    PushErrorCodeParam = 1,
    PushErrorCodeNoSupport = 2,
    PushErrorCodeJsonParse = 3,
    PushErrorCodeInit = 4,
    PushErrorCodeRegister = 5,
    PushErrorCodeUnRegister = 6,
    PushErrorCodePrepare = 7,
    PushErrorCodeUnkown = 1000
};

@interface PushErrorHelper : NSObject

+ (NSErrorDomain)getDomain:(PushErrorCode)code;
+ (NSError *)createError:(PushErrorCode)code withDomain:(NSErrorDomain)domain;

@end

NS_ASSUME_NONNULL_END
