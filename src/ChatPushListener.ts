/**
 * Interface for listening to push events.
 */
export interface ChatPushListener {
  /**
   * Callback for when a push token is received.
   * @param token The push token.
   */
  onReceivePushToken?(token: string): void;
  /**
   * Callback for when a push message is received.
   * @param message The message received.
   */
  onReceivePushMessage?(message: any): void;
}
