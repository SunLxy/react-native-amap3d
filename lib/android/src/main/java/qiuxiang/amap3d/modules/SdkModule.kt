package qiuxiang.amap3d.modules

import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.MapsInitializer
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.location.CoordinateConverter
import com.amap.api.location.CoordinateConverter.CoordType
import qiuxiang.amap3d.toJson
import qiuxiang.amap3d.toLatLng
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.amap.api.location.DPoint

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.WritableMap



@Suppress("unused")
class SdkModule(val context: ReactApplicationContext) : ReactContextBaseJavaModule() {
  // 声明AMapLocationClient类对象
  private lateinit var client: AMapLocationClient
    // 初始化 AMapLocationClientOption 对象
  private var option:AMapLocationClientOption = AMapLocationClientOption()
  private lateinit var eventEmitter:DeviceEventManagerModule.RCTDeviceEventEmitter 

  override fun getName(): String {
    return "AMapSdk"
  }

  @ReactMethod
  fun initSDK(apiKey: String?) {
    apiKey?.let {
      MapsInitializer.setApiKey(it)
      MapsInitializer.updatePrivacyAgree(context, true)
      MapsInitializer.updatePrivacyShow(context, true, true)
      AMapLocationClient.updatePrivacyAgree(context, true)
      AMapLocationClient.updatePrivacyShow(context, true, true)
      // 通过SDK提供的 `setApiKey(String key)` 接口设置Key，注意Key设置要在SDK业务初始化之前。
      // 需要在初始化的额前面设置 key
      AMapLocationClient.setApiKey(it)
      // 初始化定位
      client = AMapLocationClient(context.getApplicationContext())
      option.setNeedAddress(true)
      //设置定位参数
      client.setLocationOption(option)
      eventEmitter = context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      // 设置定位监听
      client.setLocationListener(object : AMapLocationListener {
        override fun onLocationChanged(location: AMapLocation) {
          getDeviceEventEmitter().emit("AMapGeolocation", toJSON(location));
        }
      })
    }
  }

  private fun getDeviceEventEmitter():DeviceEventManagerModule.RCTDeviceEventEmitter {
    if (eventEmitter == null) {
        eventEmitter = context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java);
    }
    return eventEmitter;
  };

  @ReactMethod
  fun getVersion(promise: Promise) {
    promise.resolve(MapsInitializer.getVersion())
  }

  @ReactMethod
  fun start() {
    client?.startLocation()
  }

  @ReactMethod
  fun stop() {
    client?.stopLocation()
  }

  /**
   * 设置是否返回地址信息，默认返回地址信息
   * 默认值：true, 返回地址信息
   */
  @ReactMethod
  fun setNeedAddress(value:Boolean) {
    option.setNeedAddress(value)
    client?.setLocationOption(option)
  }

  // 计算距离
  @ReactMethod
  fun calculateLineDistance(latLng1:ReadableMap,latLng2:ReadableMap, promise: Promise) {
    promise.resolve(AMapUtils.calculateLineDistance(latLng1.toLatLng(),latLng2.toLatLng()))
  }

  // 坐标转换
  @ReactMethod
  fun coordinateConverter(latLng:ReadableMap,type:Int,promise: Promise) {
    val converter = CoordinateConverter(context.getApplicationContext())
    /**
    * 设置坐标来源,这里使用百度坐标作为示例
    * 可选的来源包括：
    * - CoordType.BAIDU: 百度坐标
    * - CoordType.MAPBAR: 图吧坐标
    * - CoordType.MAPABC: 图盟坐标
    * - CoordType.SOSOMAP: 搜搜坐标
    * - CoordType.ALIYUN: 阿里云坐标
    * - CoordType.GOOGLE: 谷歌坐标
    * - CoordType.GPS: GPS坐标
    */
    when (type) {
      0 -> converter.from(CoordType.BAIDU)
      1 -> converter.from(CoordType.MAPBAR)
      2 -> converter.from(CoordType.MAPABC)
      3 -> converter.from(CoordType.SOSOMAP)
      4 -> converter.from(CoordType.ALIYUN)
      5 -> converter.from(CoordType.GOOGLE)
      6 -> converter.from(CoordType.GPS)
      else -> { // 注意这个块
      }
    }
    var pointer =  DPoint(
        latLng.getDouble("latitude"),
        latLng.getDouble("longitude")
    )
    converter.coord(pointer)
    val destPoint = converter.convert()
    val map = Arguments.createMap().apply{
      putDouble("latitude", destPoint.getLatitude())
      putDouble("longitude", destPoint.getLongitude())
    }
    promise.resolve(map)
  }

  private fun toJSON(location:AMapLocation):WritableMap {
      return Arguments.createMap().apply{
        putInt("errorCode", location.getErrorCode())
        putString("errorInfo", location.getErrorInfo())
        if(location.getErrorCode() == AMapLocation.LOCATION_SUCCESS){
          putDouble("accuracy", location.getAccuracy().toDouble())
          putDouble("latitude", location.getLatitude().toDouble())
          putDouble("longitude", location.getLongitude().toDouble())
          putDouble("altitude", location.getAltitude().toDouble())
          putDouble("speed", location.getSpeed().toDouble())
          if(!location.getAddress().isEmpty()){
            putString("address", location.getAddress())
            putString("country", location.getCountry())
            putString("province", location.getProvince())
            putString("city", location.getCity())
            putString("district", location.getDistrict())
            putString("cityCode", location.getCityCode())
            putString("adCode", location.getAdCode())
            putString("street", location.getStreet())
            putString("streetNumber", location.getStreetNum())
            putString("poiName", location.getPoiName())
            putString("aoiName", location.getAoiName())
          }
          // --------------------
          // 以上与 iOS 相同字
          // 获取坐标系类型 高德定位sdk会返回两种坐标系：
          // 坐标系 AMapLocation.COORD_TYPE_GCJ02 -- GCJ02
          // 坐标系 AMapLocation.COORD_TYPE_WGS84 -- WGS84
          // 国外定位时返回的是WGS84坐标系
          putString("coordType", location.getCoordType())
          // 获取位置语义信息
          putString("description", location.getDescription())
          // 返回支持室内定位的建筑物ID信息
          putString("buildingId", location.getBuildingId())
          // 室内外置信度
          // 室内：且置信度取值在[1 ～ 100]，值越大在在室内的可能性越大
          // 室外：且置信度取值在[-100 ～ -1] ,值越小在在室内的可能性越大
          // 无法识别室内外：置信度返回值为 0
          putInt("conScenario", location.getConScenario())
          // 获取室内定位的楼层信息
          putString("floor", location.getFloor())
          // 获取卫星信号强度，仅在卫星定位时有效,值为：
          // #GPS_ACCURACY_BAD，#GPS_ACCURACY_GOOD，#GPS_ACCURACY_UNKNOWN
          putInt("gpsAccuracyStatus", location.getGpsAccuracyStatus())
          // 获取定位信息描述
          putString("locationDetail", location.getLocationDetail())
          putInt("locationType", location.getLocationType())
          putString("provider", location.getProvider())
          putInt("satellites", location.getSatellites())
          putInt("trustedLevel", location.getTrustedLevel())
          putMap("locationQualityReport", Arguments.createMap().apply{
            var aMapLocationQualityReport = location.getLocationQualityReport()
            var adviseMessage = aMapLocationQualityReport.getAdviseMessage()
            if (adviseMessage != null) {
                // adviseMessage = adviseMessage.replaceAll("[\\t\\n\\r/]", "")
            }
            putString("adviseMessage", adviseMessage)
            putInt("gpsSatellites", aMapLocationQualityReport.getGPSSatellites())
            putInt("gpsStatus", aMapLocationQualityReport.getGPSStatus())
            putDouble("netUseTime", aMapLocationQualityReport.getNetUseTime().toDouble())
            putString("networkType", aMapLocationQualityReport.getNetworkType())
            putBoolean("isInstalledHighDangerMockApp", aMapLocationQualityReport.isInstalledHighDangerMockApp())
            putBoolean("isWifiAble", aMapLocationQualityReport.isWifiAble())
          })
        }
      }
  }
}
