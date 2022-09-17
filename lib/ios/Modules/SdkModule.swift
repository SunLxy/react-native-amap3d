@objc(AMapSdk)
class AMapSdk: NSObject {
  @objc static func requiresMainQueueSetup() -> Bool {
    false
  }

  @objc func initSDK(_ apiKey: String) {
    AMapServices.shared().apiKey = apiKey
    MAMapView.updatePrivacyAgree(AMapPrivacyAgreeStatus.didAgree)
    MAMapView.updatePrivacyShow(AMapPrivacyShowStatus.didShow, privacyInfo: AMapPrivacyInfoStatus.didContain)
  }

  @objc func getVersion(_ resolve: RCTPromiseResolveBlock, reject _: RCTPromiseRejectBlock) {
    resolve("8.0.1")
  }
 // 计算距离
  @objc func calculateLineDistance(_ latLng1:NSDictionary,latLng2 _:NSDictionary, resolve _: RCTPromiseResolveBlock, reject _: RCTPromiseRejectBlock) {
    let point1 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:latLng1["latitude"], longitude: latLng1["longitude"]))
    let point2 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:latLng2["latitude"], longitude: latLng2["longitude"]))
    let distance = MAMetersBetweenMapPoints(point1,point2);
    resolve(distance)
  }
  // 坐标转换
  @objc func coordinateConverter(_ latLng:NSDictionary,type _:Int, resolve _: RCTPromiseResolveBlock, reject _: RCTPromiseRejectBlock) {
    let typeObj;
    switch (typeNum) {
        case -1:
            typeObj = AMapCoordinateTypeAMap;
            break;
        case 0:
            typeObj = AMapCoordinateTypeBaidu;
            break;
        case 1:
            typeObj = AMapCoordinateTypeMapBar;
            break;
        case 2:
            typeObj = AMapCoordinateTypeMapABC;
            break;
        case 3:
            typeObj = AMapCoordinateTypeSoSoMap;
            break;
        case 4:
            typeObj = AMapCoordinateTypeAliYun;
            break;
        case 5:
            typeObj = AMapCoordinateTypeGoogle;
            break;
        case 6:
            typeObj = AMapCoordinateTypeGPS;
            break;
        default:
            typeObj = AMapCoordinateTypeGPS;
            break;
    }
    let amapcoord = AMapCoordinateConvert(CLLocationCoordinate2D(latitude:latLng["latitude"], longitude: latLng["longitude"]), typeObj);
    resolve((amapcoord as? CLLocationCoordinate2D).json)
  }
}
