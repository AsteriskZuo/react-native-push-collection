//
//  ThreadUtil.h
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ThreadUtil : NSObject
+ (void)asyncExecute:(void (^)(void))callback;
+ (void)mainThreadExecute:(void (^)(void))callback;
@end

NS_ASSUME_NONNULL_END
