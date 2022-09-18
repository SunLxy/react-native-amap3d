#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(AMapSdk, NSObject)

RCT_EXTERN_METHOD(initSDK: (NSString)apiKey)
RCT_EXTERN_METHOD(getVersion: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)_)
RCT_EXTERN_METHOD(coordinateConverter:(NSDictionary)latLng type:(int)_ resolve:(RCTPromiseResolveBlock)_ reject: (RCTPromiseRejectBlock)_)
RCT_EXTERN_METHOD(calculateLineDistance: (NSDictionary)latLng1 latLng2:(NSDictionary)_ resolve:(RCTPromiseResolveBlock)_ reject: (RCTPromiseRejectBlock)_)

@end
