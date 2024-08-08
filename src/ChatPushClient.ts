import type { EmitterSubscription } from 'react-native';
import {
  PushCollection,
  PushCollectionEventEmitter,
} from './__internal__/native';
import type { PushConfig } from './ChatPushConfig';
import type { ChatPushListener } from './ChatPushListener';
import {
  _onError,
  _onNativeNotification,
  _onReceivePushToken,
} from './__internal__/const';
import type { InitOptions } from './types';
import { createError, tryCatch } from './ChatPushError';

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
   *
   * The manufacturer configuration object and registration object will be generated. If the initialization fails, an exception will be thrown.
   *
   * **Notes** `init` must be called before calling other APIs.
   *
   * @throws {@link ChatPushError}
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
    return tryCatch(
      PushCollection.init({
        ...option,
        pushType: option.pushType ?? 'unknown',
      })
    );
  }

  protected _onNativeNotification(params: any): void {
    this._listeners.forEach((listener) => {
      const eventType = params.type;
      if (eventType === _onReceivePushToken) {
        listener.onReceivePushToken?.(params.token);
      } else if (eventType === _onError) {
        listener.onError?.(createError(params.error));
      }
    });
  }

  /**
   * Enable push and prepare to enable push. Different manufacturers require different settings.
   *
   * **Notes** The order in which `registerPush` and `prepare` are called does not matter.
   */
  public prepare(): Promise<void> {
    return tryCatch(PushCollection.prepare({}));
  }

  /**
   * Register push notification service.
   *
   * An initialization operation needs to be performed first.
   *
   * **Note** Returning the result does not mean that the token can be obtained normally. You need to use the listener `ChatPushListener.onReceivePushToken` to obtain the result. If the listener has received the result, you can obtain the token through `getToken`.
   *
   * @throws {@link ChatPushError}
   */
  public registerPush(): Promise<void> {
    return tryCatch(PushCollection.registerPush({}));
  }

  /**
   * Unregister push notification service.
   *
   * **Note** After unregisterPush, you will not be able to obtain a token using `getToken`.
   *
   * @throws {@link ChatPushError}
   */
  public unregisterPush(): Promise<void> {
    return tryCatch(PushCollection.unregisterPush());
  }

  /**
   * Get the configuration of the push notification service.
   *
   * **Notes** Configuration parameters are obtained through the native part and need to be initialized first.
   *
   * @returns The configuration of the push notification service.
   */
  public getPushConfig(): Promise<PushConfig | undefined> {
    return tryCatch(PushCollection.getPushConfig());
  }

  /**
   * Get the push token.
   *
   * **Notes** Only when the registration is successful or has been successfully registered can you obtain the `token` normally.
   *
   * @returns The push token.
   *
   * @throws {@link ChatPushError}
   */
  public getToken(): Promise<string | undefined> {
    return tryCatch(PushCollection.getToken());
  }

  /**
   * Get token asynchronously through {@link ChatPushListener.onReceivePushToken}.
   *
   * It is a collection of {@link prepare}, {@link registerPush} and {@link getToken} interfaces.
   *
   * @throws {@link ChatPushError}
   */
  public getTokenAsync(): Promise<void> {
    return tryCatch(PushCollection.getTokenFlow({}));
  }
}
