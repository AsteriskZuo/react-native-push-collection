
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNPushCollectionSpec.h"

@interface PushCollection : NSObject <NativePushCollectionSpec>
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface PushCollection : RCTEventEmitter <RCTBridgeModule>
#endif

@end
