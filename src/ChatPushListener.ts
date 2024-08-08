import type { ChatPushError } from './ChatPushError';

/**
 * Interface for listening to push events.
 */
export interface ChatPushListener {
  /**
   * Callback for when a push token is received.
   *
   * You will receive this notification when the token is updated.
   * You will receive this notification when {@link ChatPushClient.getTokenAsync} is called.
   * You may receive this notification when {@link ChatPushClient.registerPush} is called.
   *
   * @param token The push token.
   */
  onReceivePushToken?(token: string): void;
  /**
   * Callback for when an error occurs.
   * @param error The error that occurred.
   */
  onError?(error: ChatPushError): void;
}
