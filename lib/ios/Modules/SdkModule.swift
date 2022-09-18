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
  @objc func calculateLineDistance(_ latLng1:NSDictionary,latLng2:NSDictionary, resolve : RCTPromiseResolveBlock, reject : RCTPromiseRejectBlock) {
    let point1 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:(latLng1["latitude"] as! Double), longitude: (latLng1["longitude"] as! Double)))
    let point2 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:(latLng2["latitude"] as! Double), longitude: (latLng2["longitude"] as! Double)))
    let distance = MAMetersBetweenMapPoints(point1,point2);
    resolve(distance)
  }

  // 坐标转换
  @objc func coordinateConverter(_ latLng:NSDictionary,type :Int, resolve _: RCTPromiseResolveBlock, reject _: RCTPromiseRejectBlock) {
    let typeObj=AMapCoordinateTypeGPS;
    switch type {
      case -1  :
          typeObj = AMapCoordinateTypeAMap;
      case 0  :
          typeObj = AMapCoordinateTypeBaidu;
      case 1  :
          typeObj = AMapCoordinateTypeMapBar;
      case 2  :
          typeObj = AMapCoordinateTypeMapABC;
      case 3  :
          typeObj = AMapCoordinateTypeSoSoMap;
      case 4  :
          typeObj = AMapCoordinateTypeAliYun;
      case 5  :
          typeObj = AMapCoordinateTypeGoogle;
      case 6  :
          typeObj = AMapCoordinateTypeGPS;
      default :
          typeObj = AMapCoordinateTypeGPS;
    }
    
    let amapcoord = AMapCoordinateConvert(CLLocationCoordinate2D(latitude:(latLng["latitude"] as! Double), longitude: (latLng["longitude"] as! Double)), typeObj);
    var json = {["latitude":amapcoord.latitude,"longitude": amapcoord.longitude]}
    resolve(json)
  }
}
