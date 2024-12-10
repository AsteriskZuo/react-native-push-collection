import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-push-collection' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
export const isTurboModuleEnabled = global.__turboModuleProxy != null;

const PushCollectionModule = isTurboModuleEnabled
  ? require('./NativePushCollection').default
  : NativeModules.PushCollection;

export const PushCollection = PushCollectionModule
  ? PushCollectionModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export const eventEmitter = new NativeEventEmitter(PushCollection);

export function multiply(a: number, b: number): Promise<number> {
  return PushCollection.multiply(a, b);
}
