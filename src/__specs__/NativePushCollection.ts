import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  // Keep: Required for RN built in Event Emitter Calls.
  addListener(eventName: string): void;

  // Keep: Required for RN built in Event Emitter Calls.
  removeListeners(count: number): void;

  // Keep: Required for RN built in Event Emitter Calls.
  removeAllListeners(): void;

  // for testing
  multiply(a: number, b: number): Promise<number>;

  init(option: Object): Promise<void>;

  prepare(option: Object): Promise<void>;

  registerPush(option: Object): Promise<void>;

  unregisterPush(): Promise<void>;

  getPushConfig(): Promise<Object | undefined>;

  getToken(): Promise<string | undefined>;

  getTokenFlow(option: Object): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PushCollection');
