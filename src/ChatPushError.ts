export enum ChatPushErrorCode {
  /**
   * Parameter error.
   */
  Param = 1,
  /**
   * No support.
   */
  NoSupport = 2,
  /**
   * Json parse error.
   */
  JsonParse = 3,
  /**
   * Init error.
   */
  Init = 4,
  /**
   * Register error.
   */
  Register = 5,
  /**
   * Unregister error.
   */
  UnRegister = 6,
  /**
   * Prepare error.
   */
  Prepare = 7,
  /**
   * Others error.
   */
  Unknown = 1000,
}

export interface ChatPushError {
  /**
   * Error code.
   */
  code: ChatPushErrorCode | number;
  /**
   * Error description.
   */
  description: string;
}

export function createError(error: any): ChatPushError {
  try {
    console.log('dev:createError:raw_error:', JSON.stringify(error));
  } catch (e) {
    console.log('dev:createError:raw_error:2', error);
  }
  if (error.userInfo) {
    return {
      code: error.userInfo.code,
      description: error.userInfo.message,
    };
  } else {
    return {
      code: ChatPushErrorCode.Unknown,
      description: error.toString?.(),
    };
  }
}

export async function tryCatch<T>(promise: Promise<T>): Promise<T> {
  try {
    return await promise;
  } catch (error: any) {
    throw createError(error);
  }
}
