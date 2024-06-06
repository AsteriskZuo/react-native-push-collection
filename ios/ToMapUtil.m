//
//  ToMapUtil.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "ToMapUtil.h"

@implementation ToMapUtil
+ (id)toMap:(id)object {
    if ([object isKindOfClass:[NSMutableDictionary class]]) {
        return object;
    } else if ([object isKindOfClass:[NSDictionary class]]) {
        return [NSMutableDictionary dictionaryWithDictionary:object];
    } else if ([object respondsToSelector:@selector(dictionaryRepresentation)]) {
        return [NSMutableDictionary dictionaryWithDictionary:[object dictionaryRepresentation]];
    } else {
        return nil;
    }
}
@end
