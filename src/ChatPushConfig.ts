import type { PushType } from './types';

export abstract class PushConfig {
  /**
   * The type of the push notification service.
   */
  type: PushType;
  constructor(params: { type: PushType }) {
    this.type = params.type;
  }
}

export class ApnsPushConfig extends PushConfig {
  constructor() {
    super({ type: 'apns' });
  }
}

export class FcmPushConfig extends PushConfig {
  senderId: string;
  constructor(params: { senderId: string }) {
    super({ type: 'fcm' });
    this.senderId = params.senderId;
  }
}

export class HuaweiPushConfig extends PushConfig {
  appId: string;
  constructor(params: { appId: string }) {
    super({ type: 'huawei' });
    this.appId = params.appId;
  }
}

export class HonorPushConfig extends PushConfig {
  appId: string;
  constructor(params: { appId: string }) {
    super({ type: 'honor' });
    this.appId = params.appId;
  }
}

export class XiaomiPushConfig extends PushConfig {
  appId: string;
  appKey: string;
  constructor(params: { appId: string; appKey: string }) {
    super({ type: 'xiaomi' });
    this.appId = params.appId;
    this.appKey = params.appKey;
  }
}

export class MeizuPushConfig extends PushConfig {
  appId: string;
  appKey: string;
  constructor(params: { appId: string; appKey: string }) {
    super({ type: 'meizu' });
    this.appId = params.appId;
    this.appKey = params.appKey;
  }
}

export class VivoPushConfig extends PushConfig {
  appId: string;
  appKey: string;
  constructor(params: { appId: string; appKey: string }) {
    super({ type: 'vivo' });
    this.appId = params.appId;
    this.appKey = params.appKey;
  }
}

export class OppoPushConfig extends PushConfig {
  appKey: string;
  secret: string;
  constructor(params: { appKey: string; secret: string }) {
    super({ type: 'oppo' });
    this.appKey = params.appKey;
    this.secret = params.secret;
  }
}
