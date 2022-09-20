@objc(AMapSdk)
class AMapSdk: NSObject {
  @objc static func requiresMainQueueSetup() -> Bool {
    false
  }
  var locationManager:AMapLocationManager?

  @objc func supportedEvents() -> [String]?{
    return ["AMapGeolocation"]
  }


  @objc func initSDK(_ apiKey: String) {
    AMapServices.shared().apiKey = apiKey
    MAMapView.updatePrivacyAgree(AMapPrivacyAgreeStatus.didAgree)
    MAMapView.updatePrivacyShow(AMapPrivacyShowStatus.didShow, privacyInfo: AMapPrivacyInfoStatus.didContain)

    locationManager = AMapLocationManager()
    locationManager.delegate = self
    locationManager.locatingWithReGeocode = true
  }

  @objc func start() {
    locationManager.startUpdatingLocation()
  }

  @objc func stop() {
    locationManager.stopUpdatingLocation()
  }

  @objc func getVersion(_ resolve: RCTPromiseResolveBlock, reject _: RCTPromiseRejectBlock) {
    resolve("8.0.1")
  }
 // 计算距离
  @objc func calculateLineDistance(_ latLng1:NSDictionary,latLng2:NSDictionary, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    let point1 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:(latLng1["latitude"] as! Double), longitude: (latLng1["longitude"] as! Double)))
    let point2 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude:(latLng2["latitude"] as! Double), longitude: (latLng2["longitude"] as! Double)))
    let distance = MAMetersBetweenMapPoints(point1,point2);
    resolve(distance)
  }

  // 坐标转换
  @objc func coordinateConverter(_ latLng:NSDictionary,type :Int, resolve : RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
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

  @objc func amapLocationManager(_ manager: AMapLocationManager!, doRequireLocationAuth locationManager: CLLocationManager!) {
    locationManager.requestAlwaysAuthorization()
  }

  @objc func amapLocationManager(_ manager: AMapLocationManager!, didUpdate location: CLLocation!) {
    self.sendEvent(withName: "AMapGeolocation", body: reGeocode(location))
  }

  @objc func amapLocationManager(_ manager: AMapLocationManager!, didFailWithError error: NSError!) {
    self.sendEvent(withName: "AMapGeolocation", body: {["errorCode":error.code,"errorInfo": error.localizedDescription]})
  }

  func reGeocode(_ location: CLLocation!, reGeocode: AMapLocationReGeocode!) {
    flag = AMapLocationDataAvailableForCoordinate(location.coordinate);
    if(reGeocode){
      return {[
        "accuracy" : location.horizontalAccuracy,
        "latitude" : location.coordinate.latitude,
        "longitude" : location.coordinate.longitude,
        "altitude" : location.altitude,
        "speed" : location.speed,
        "heading" : location.course,
        "timestamp" : location.timestamp.timeIntervalSince1970 * 1000,
        "isAvailableCoordinate": flag,

        "address" : reGeocode.formattedAddress ? reGeocode.formattedAddress : "",
        "country" : reGeocode.country ? reGeocode.country :"",
        "province" : reGeocode.province ? reGeocode.province : "",
        "city" : reGeocode.city ? reGeocode.city : "",
        "district" : reGeocode.district ? reGeocode.district : "",
        "cityCode" : reGeocode.citycode ? reGeocode.citycode : "",
        "adCode" : reGeocode.adcode ? reGeocode.adcode : "",
        "street" : reGeocode.street ? reGeocode.street : "",
        "streetNumber" : reGeocode.number ? reGeocode.number : "",
        "poiName" : reGeocode.POIName ? reGeocode.POIName : "",
        "aoiName" : reGeocode.AOIName ? reGeocode.AOIName : "",
      ]}
    }else{
      return {[
        "accuracy": location.horizontalAccuracy,
        "latitude": location.coordinate.latitude,
        "longitude": location.coordinate.longitude,
        "isAvailableCoordinate": flag,
        "altitude": location.altitude,
        "speed": location.speed,
        "heading": location.course,
        "timestamp": location.timestamp.timeIntervalSince1970 * 1000,
      ]}
    }
  }
}
