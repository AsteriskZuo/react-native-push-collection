
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNPushCollectionSpec.h"

@interface PushCollection : RCTEventEmitter <NativePushCollectionSpec>
#else
#import <React/RCTBridgeModule.h>

@interface PushCollection : RCTEventEmitter <RCTBridgeModule>
#endif

@end
