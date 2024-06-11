import { Platform, type PlatformAndroidStatic } from 'react-native';
import type { PlatformType, PushType } from '../types';

export function getDeviceType(): string | undefined {
  if (Platform.OS === 'android') {
    const p = Platform as PlatformAndroidStatic;
    return convertToPushType(p.constants.Manufacturer);
  } else {
    return undefined;
  }
}

export function getPlatform(): PlatformType {
  return Platform.OS;
}

function convertToPushType(manufacturer?: string): PushType {
  let ret: PushType = 'unknown';
  if (manufacturer) {
    switch (manufacturer) {
      case 'Google':
        ret = 'fcm';
        break;
      case 'Xiaomi':
        ret = 'xiaomi';
        break;
      case 'OPPO':
        ret = 'oppo';
        break;
      case 'vivo':
        ret = 'vivo';
        break;
      case 'Huawei':
        ret = 'huawei';
        break;
      case 'Honor':
        ret = 'honor';
        break;
      case 'Sony':
      case 'Samsung':
      case 'Sony':
      case 'Motorola':
      case 'LG':
      case 'OnePlus':
        ret = 'unknown';
        break;

      default:
        ret = 'unknown';
        break;
    }
  }

  return ret;
}
