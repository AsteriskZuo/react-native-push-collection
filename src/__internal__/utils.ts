import { Platform, type PlatformAndroidStatic } from 'react-native';
import type { PlatformType } from '../types';

export function getDeviceType(): string | undefined {
  if (Platform.OS === 'android') {
    const p = Platform as PlatformAndroidStatic;
    return p.constants.Manufacturer;
  } else {
    return undefined;
  }
}

export function getPlatform(): PlatformType {
  return Platform.OS;
}
