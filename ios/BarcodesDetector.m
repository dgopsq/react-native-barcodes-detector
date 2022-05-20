#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(BarcodesDetector, NSObject)

RCT_EXTERN_METHOD(scan:(NSString)imageUrl
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

@end
