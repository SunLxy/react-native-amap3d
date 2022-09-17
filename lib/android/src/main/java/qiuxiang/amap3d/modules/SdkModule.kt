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
  fun calculateLineDistance(latLng1:LatLng,latLng2:LatLng, promise: Promise) {
    promise.resolve(AMapUtils.calculateLineDistance(latLng1.toLatLng(),latLng2.toLatLng()))
  }

  // 坐标转换
  @ReactMethod
  fun coordinateConverter(latLng:LatLng,type:CoordType,promise: Promise) {
    converter = new CoordinateConverter()
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
    switch (type) {
        case 0:
            converter.from(CoordType.BAIDU);
            break;
        case 1:
            converter.from(CoordType.MAPBAR);
            break;
        case 2:
            converter.from(CoordType.MAPABC);
            break;
        case 3:
            converter.from(CoordType.SOSOMAP);
            break;
        case 4:
            converter.from(CoordType.ALIYUN);
            break;
        case 5:
            converter.from(CoordType.GOOGLE);
            break;
        case 6:
            converter.from(CoordType.GPS);
            break;
            default: break;
    }
    converter.coord(latLng)
    desLatLng = converter.convert()
    promise.resolve(desLatLng.toJson())
  }
}
