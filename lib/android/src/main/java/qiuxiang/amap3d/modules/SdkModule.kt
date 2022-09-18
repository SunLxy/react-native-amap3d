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
import com.amap.api.location.DPoint;

@Suppress("unused")
class SdkModule(val context: ReactApplicationContext) : ReactContextBaseJavaModule() {
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
    }
  }

  @ReactMethod
  fun getVersion(promise: Promise) {
    promise.resolve(MapsInitializer.getVersion())
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
    );
    converter.coord(pointer)
    val destPoint = converter.convert()
    val map = Arguments.createMap().apply{
      putDouble("latitude", destPoint.getLatitude())
      putDouble("longitude", destPoint.getLongitude())
    };
    promise.resolve(map)
  }
}
