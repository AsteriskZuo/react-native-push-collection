import type { EmitterSubscription } from 'react-native';
import { PushCollection } from './__internal__/native';
import type { PushConfig } from './ChatPushConfig';
import type { ChatPushListener } from './ChatPushListener';
import {
  _onNativeNotification,
  _onReceivePushMessage,
  _onReceivePushToken,
} from './__internal__/const';
import { NativeAppEventEmitter } from 'react-native';

export function multiply(a: number, b: number): Promise<number> {
  return PushCollection.multiply(a, b);
}

export class ChatPushClient {
  private static instance: ChatPushClient;
  private _listeners: Set<ChatPushListener>;
  private _nativeSubs: Map<string, EmitterSubscription>;

  private constructor() {
    // private constructor to prevent direct object creation
    this._listeners = new Set();
    this._nativeSubs = new Map();
  }

  /**
   * Singleton instance of ChatPushClient
   * @returns The singleton instance of ChatPushClient
   */
  public static getInstance(): ChatPushClient {
    if (!ChatPushClient.instance) {
      ChatPushClient.instance = new ChatPushClient();
    }
    return ChatPushClient.instance;
  }

  /**
   * Initialize the ChatPushClient.
   */
  public init(): Promise<void> {
    this._nativeSubs.forEach((sub) => sub.remove());
    this._nativeSubs.clear();
    this._nativeSubs.set(
      _onNativeNotification,
      NativeAppEventEmitter.addListener(_onNativeNotification, (params: any) =>
        this._onNativeNotification(params)
      )
    );
    return PushCollection.init();
  }

  protected _onNativeNotification(params: any): void {
    this._listeners.forEach((listener) => {
      const eventType = params.type;
      if (eventType === _onReceivePushToken) {
        listener.onReceivePushToken?.(params.token);
      } else if (eventType === _onReceivePushMessage) {
        listener.onReceivePushMessage?.(params.message);
      }
    });
  }

  /**
   * Register push notification service.
   *
   * An initialization operation needs to be performed first.
   *
   * @param config The configuration of the push notification service.
   */
  public registerPush(config: PushConfig): Promise<void> {
    return PushCollection.registerPush(config);
  }

  /**
   * Unregister push notification service.
   */
  public unregisterPush(): Promise<void> {
    return PushCollection.unregisterPush();
  }

  /**
   * Get the configuration of the push notification service.
   * @returns The configuration of the push notification service.
   */
  public getPushConfig(): Promise<PushConfig | undefined> {
    return PushCollection.getPushConfig();
  }

  /**
   * Add a listener to the ChatPushClient.
   *
   * You need to add a listener first.
   *
   * @param listener The listener to be added.
   */
  public addListener(listener: ChatPushListener): void {
    this._listeners.add(listener);
  }
  /**
   * Remove a listener from the ChatPushClient.
   * @param listener The listener to be removed.
   */
  public removeListener(listener: ChatPushListener): void {
    this._listeners.delete(listener);
  }
  /**
   * Clear all listeners from the ChatPushClient.
   */
  public clearListener(): void {
    this._listeners.clear();
  }

  /**
   * Get the push token.
   * @returns The push token.
   */
  public getToken(): Promise<string | undefined> {
    return PushCollection.getToken();
  }
}
