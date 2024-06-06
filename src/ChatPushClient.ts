import type { EmitterSubscription } from 'react-native';
import {
  PushCollection,
  PushCollectionEventEmitter,
} from './__internal__/native';
import type { PushConfig } from './ChatPushConfig';
import type { ChatPushListener } from './ChatPushListener';
import {
  _onAppBackground,
  _onAppForeground,
  _onError,
  _onNativeNotification,
  _onNotificationClick,
  _onReceivePushMessage,
  _onReceivePushToken,
} from './__internal__/const';
import type { InitOptions } from './types';

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
   * Initialize the ChatPushClient.
   */
  public init(option: InitOptions): Promise<void> {
    this._nativeSubs.forEach((sub) => sub.remove());
    this._nativeSubs.clear();
    this._nativeSubs.set(
      _onNativeNotification,
      PushCollectionEventEmitter.addListener(
        _onNativeNotification,
        (params: any) => this._onNativeNotification(params)
      )
    );
    return PushCollection.init({
      ...option,
      pushType: option.pushType ?? 'unknown',
    });
  }

  protected _onNativeNotification(params: any): void {
    this._listeners.forEach((listener) => {
      const eventType = params.type;
      if (eventType === _onReceivePushToken) {
        listener.onReceivePushToken?.(params.token);
      } else if (eventType === _onReceivePushMessage) {
        listener.onReceivePushMessage?.(params.message);
      } else if (eventType === _onNotificationClick) {
        listener.onClickNotification?.(params.message);
      } else if (eventType === _onAppBackground) {
        listener.onAppBackground?.(params);
      } else if (eventType === _onAppForeground) {
        listener.onAppForeground?.(params);
      } else if (eventType === _onError) {
        listener.onError?.(params.error);
      }
    });
  }

  /**
   * Register push notification service.
   *
   * An initialization operation needs to be performed first.
   *
   */
  public registerPush(): Promise<void> {
    return PushCollection.registerPush();
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
   * Get the push token.
   * @returns The push token.
   */
  public getToken(): Promise<string | undefined> {
    return PushCollection.getToken();
  }
}
