import { NativeModules } from "react-native";
import { LatLng } from "./types";
const { AMapSdk } = NativeModules;

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
