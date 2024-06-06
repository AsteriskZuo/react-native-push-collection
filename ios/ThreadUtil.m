//
//  ThreadUtil.m
//  react-native-push-collection
//
//  Created by asterisk on 2024.06.05.
//

#import "ThreadUtil.h"

@implementation ThreadUtil

+ (void)asyncExecute:(void (^)(void))callback {
    static dispatch_queue_t asyncQueue;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
      asyncQueue = dispatch_queue_create("com.pushcollection.queue.serial", DISPATCH_QUEUE_SERIAL);
    });
    dispatch_async(asyncQueue, callback);
}

+ (void)mainThreadExecute:(void (^)(void))callback {
    if ([NSThread isMainThread]) {
        callback();
    } else {
        dispatch_async(dispatch_get_main_queue(), callback);
    }
}

@end
