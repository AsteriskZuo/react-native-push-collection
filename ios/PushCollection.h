
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNPushCollectionSpec.h"

@interface PushCollection : NSObject <NativePushCollectionSpec>
#else
#import <React/RCTBridgeModule.h>

@interface PushCollection : NSObject <RCTBridgeModule>
#endif

@end
