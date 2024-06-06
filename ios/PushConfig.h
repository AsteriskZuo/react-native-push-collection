//
//  PushConfig.h
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface PushConfig : NSObject

@property(nonatomic, strong) NSString *pushType;

- (instancetype)initWithType:(NSString *)type;
- (NSDictionary *)dictionaryRepresentation;

@end

@interface ApnsPushConfig : PushConfig

- (instancetype)init;
- (NSDictionary *)dictionaryRepresentation;

@end

@interface FcmPushConfig : PushConfig

@property(nonatomic, strong) NSString *senderId;

- (instancetype)initWithId:(NSString *)senderId;
- (NSDictionary *)dictionaryRepresentation;

@end

NS_ASSUME_NONNULL_END
