import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-push-collection' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const PushCollection = NativeModules.PushCollection
  ? NativeModules.PushCollection
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return PushCollection.multiply(a, b);
}
