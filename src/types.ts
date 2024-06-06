export type PlatformType = 'ios' | 'android' | 'windows' | 'macos' | 'web';
export type PushType =
  | 'apns'
  | 'fcm'
  | 'honor'
  | 'huawei'
  | 'meizu'
  | 'oppo'
  | 'xiaomi'
  | 'vivo'
  | 'unknown';

export type InitOptions = {
  /**
   * If true, the push token will be sent to the server.
   *
   * See `getPlatform` in `src/__internal__/utils.ts` for more information.
   */
  platform: PlatformType;

  /**
   * The push type.
   *
   * See `getDeviceType` in `src/__internal__/utils.ts` for more information.
   *
   * @platform only android.
   */
  pushType?: PushType;
};
