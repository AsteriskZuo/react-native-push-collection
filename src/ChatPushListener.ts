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
  /**
   * Callback for when a push notification is clicked.
   * @param message The message clicked.
   */
  onClickNotification?(message: any): void;
  /**
   * Callback for when the app enters the background.
   * @param params The params.
   */
  onAppBackground?(params: any): void;
  /**
   * Callback for when the app enters the foreground.
   * @param params The params.
   */
  onAppForeground?(params: any): void;
  /**
   * Callback for when an error occurs.
   * @param error The error that occurred.
   */
  onError?(error: any): void;
}
