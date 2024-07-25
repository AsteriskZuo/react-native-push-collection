import * as React from 'react';

import { StyleSheet, View, Text, Pressable } from 'react-native';
import {
  multiply,
  ChatPushClient,
  getPlatform,
  getDeviceType,
  type PushType,
  type ChatPushListener,
} from 'react-native-push-collection';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  const init = React.useCallback(() => {
    const platform = getPlatform();
    let pushType: PushType;
    if (platform === 'ios') {
      pushType = 'fcm';
    } else {
      pushType = (getDeviceType() ?? 'unknown') as PushType;
    }
    console.log('test:zuoyu:init:', pushType);
    ChatPushClient.getInstance()
      .init({
        platform: getPlatform(),
        pushType: pushType as any,
      })
      .then(() => {
        console.log('test:zuoyu:init:addListener');
        ChatPushClient.getInstance().addListener({
          onAppBackground: (params) => {
            console.log('test:zuoyu:onAppBackground:', params);
          },
          onClickNotification: (message) => {
            console.log('test:zuoyu:onClickNotification:', message);
          },
          onError: (error) => {
            console.log('test:zuoyu:onError:', error);
          },
          onAppForeground: (params) => {
            console.log('test:zuoyu:onAppForeground:', params);
          },
          onReceivePushMessage: (message) => {
            console.log('test:zuoyu:onReceivePushMessage:', message);
          },
          onReceivePushToken: (token) => {
            console.log('test:zuoyu:onReceivePushToken:', token);
          },
        } as ChatPushListener);
      })
      .catch((e) => {
        console.warn('test:zuoyu:init:error:', e);
      });
  }, []);

  const uninit = React.useCallback(() => {
    ChatPushClient.getInstance().clearListener();
  }, []);

  const onPrepare = () => {
    console.log('test:zuoyu:click:onPrepare');
    ChatPushClient.getInstance()
      .prepare()
      .then(() => {
        console.log('test:zuoyu:prepare');
      })
      .catch((e) => {
        console.warn('test:zuoyu:prepare', e);
      });
  };

  const onStartRegistration = () => {
    console.log('test:zuoyu:click:onStartRegistration');
    ChatPushClient.getInstance()
      .registerPush()
      .then(() => {
        setTimeout(() => {
          ChatPushClient.getInstance()
            .getToken()
            .then((token) => {
              console.log('test:zuoyu:getToken:', token);
            })
            .catch((e) => {
              console.warn('test:zuoyu:getToken:error:', e);
            });
        }, 1000);
      })
      .catch((e) => {
        console.warn('test:zuoyu:registerPush:error:', e);
      });
  };
  const onStopRegistration = () => {
    console.log('test:zuoyu:click:onStopRegistration');
    ChatPushClient.getInstance()
      .unregisterPush()
      .then(() => {
        ChatPushClient.getInstance()
          .getToken()
          .then((token) => {
            console.log('test:zuoyu:getToken:', token);
          })
          .catch((e) => {
            console.warn('test:zuoyu:getToken:error:', e);
          });
      })
      .catch((e) => {
        console.warn('test:zuoyu:unregisterPush:error:', e);
      });
  };

  React.useEffect(() => {
    init();
    return () => {
      uninit();
    };
  }, [init, uninit]);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Pressable style={styles.button} onPress={onPrepare}>
        <Text>{'prepare operation'}</Text>
      </Pressable>
      <Pressable style={styles.button} onPress={onStartRegistration}>
        <Text>{'start Registration'}</Text>
      </Pressable>
      <Pressable style={styles.button} onPress={onStopRegistration}>
        <Text>{'stop Registration'}</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  button: {
    width: 150,
    height: 40,
    marginVertical: 20,
    backgroundColor: 'lightblue',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
