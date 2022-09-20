import { NativeModules, NativeEventEmitter } from "react-native";
import { LatLng } from "./types";
const { AMapSdk } = NativeModules;
const eventEmitter = new NativeEventEmitter(AMapSdk);

export function init(apiKey?: string) {
  AMapSdk.initSDK(apiKey);
}

export function getVersion(): Promise<string> {
  return AMapSdk.getVersion();
}
/**计算距离*/
export function calculateLineDistance(latLng1: LatLng, latLng2: LatLng): Promise<number> {
  return AMapSdk.calculateLineDistance(latLng1, latLng2);
}

/**坐标转换*/
export function coordinateConverter(
  latLng: LatLng,
  type: -1 | 0 | 1 | 2 | 3 | 4 | 5 | 6
): Promise<LatLng> {
  return AMapSdk.coordinateConverter(latLng, type);
}

/**
 * 开始定位
 */
export const start = () => {
  return AMapSdk.start();
};

/**
 * 停止更新位置信息
 */
export const stop = () => {
  return AMapSdk.stop();
};

/**
 * 连续定位监听事件
 * @param {Function} listener
 */
export const addLocationListener = (listener: Function) => {
  return eventEmitter.addListener("AMapGeolocation", (info) => {
    let errorInfo = undefined;
    if (info.errorCode || info.errorInfo) {
      errorInfo = {
        code: info.errorCode,
        message: info.errorInfo,
      };
    }
    listener && listener(info, errorInfo);
  });
};
