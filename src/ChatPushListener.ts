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
   * Callback for when a push message is received.
   *
   * oppo: The notification will not be called.
   *
   * @param message The message received.
   */
  onReceivePushMessage?(message: any): void;
  /**
   * Callback for when a push notification is clicked.
   *
   * fcm: The notification will not be called.
   * huawei: The notification will not be called.
   * honor: The notification will not be called.
   * oppo: The notification will not be called.
   *
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
