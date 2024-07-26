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
  console.log('test:zuoyu:convertToPushType:', manufacturer);
  if (manufacturer) {
    switch (manufacturer.toLowerCase()) {
      case 'google':
        ret = 'fcm';
        break;
      case 'xiaomi':
        ret = 'xiaomi';
        break;
      case 'oppo':
        ret = 'oppo';
        break;
      case 'vivo':
        ret = 'vivo';
        break;
      case 'huawei':
        ret = 'huawei';
        break;
      case 'honor':
        ret = 'honor';
        break;
      case 'meizu':
        ret = 'meizu';
        break;
      case 'sony':
      case 'samsung':
      case 'motorola':
      case 'lg':
      case 'realme':
      case 'oneplus':
        ret = 'unknown';
        break;

      default:
        ret = 'unknown';
        break;
    }
  }

  return ret;
}
