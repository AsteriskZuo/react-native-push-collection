//
//  PushConfig.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "PushConfig.h"
#import "PushType.h"

@implementation PushConfig

- (instancetype)initWithType:(NSString *)type {
    self = [super init];
    if (self) {
        self.pushType = type;
    }
    return self;
}
- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary *ret = [NSMutableDictionary dictionaryWithCapacity:2];
    ret[@"type"] = self.pushType;
    return ret;
}

@end

@implementation ApnsPushConfig

- (instancetype)init {
    self = [super initWithType:PushTypeAPNS];
    return self;
}
- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary *ret = (NSMutableDictionary *)[super dictionaryRepresentation];
    return ret;
}

@end

@implementation FcmPushConfig

- (instancetype)initWithId:(NSString *)senderId {
    self = [super initWithType:PushTypeFCM];
    if (self) {
        self.senderId = senderId;
    }
    return self;
}
- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary *ret = (NSMutableDictionary *)[super dictionaryRepresentation];
    ret[@"senderId"] = self.senderId;
    return ret;
}

@end
