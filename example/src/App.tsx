import * as React from 'react';

import { StyleSheet, View, Text, Pressable, ToastAndroid } from 'react-native';
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
          onError: (error) => {
            console.warn('test:zuoyu:onError:', error);
            ToastAndroid.show(
              'onError' + JSON.stringify(error),
              ToastAndroid.SHORT
            );
          },
          onReceivePushToken: (token) => {
            console.log('test:zuoyu:onReceivePushToken:', token);
            ToastAndroid.show('onReceivePushToken' + token, ToastAndroid.SHORT);
          },
        } as ChatPushListener);
      })
      .catch((e) => {
        console.warn('test:zuoyu:init:error:', e);
        ToastAndroid.show(
          'init error:' + JSON.stringify(e),
          ToastAndroid.SHORT
        );
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
        ToastAndroid.show('prepare success', ToastAndroid.SHORT);
      })
      .catch((e) => {
        console.warn('test:zuoyu:prepare', e);
        ToastAndroid.show(
          'prepare error:' + JSON.stringify(e),
          ToastAndroid.SHORT
        );
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
              ToastAndroid.show('getToken:' + token, ToastAndroid.SHORT);
            })
            .catch((e) => {
              console.warn('test:zuoyu:getToken:error:', e);
              ToastAndroid.show(
                'getToken error:' + JSON.stringify(e),
                ToastAndroid.SHORT
              );
            });
        }, 1000);
      })
      .catch((e) => {
        console.warn('test:zuoyu:registerPush:error:', e);
        ToastAndroid.show(
          'registerPush error:' + JSON.stringify(e),
          ToastAndroid.SHORT
        );
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
            ToastAndroid.show('getToken:' + token, ToastAndroid.SHORT);
          })
          .catch((e) => {
            console.warn('test:zuoyu:getToken:error:', e);
            ToastAndroid.show(
              'getToken error:' + JSON.stringify(e),
              ToastAndroid.SHORT
            );
          });
      })
      .catch((e) => {
        console.warn('test:zuoyu:unregisterPush:error:', e);
        ToastAndroid.show(
          'unregisterPush error:' + JSON.stringify(e),
          ToastAndroid.SHORT
        );
      });
  };

  const onGetTokenAsync = () => {
    console.log('test:zuoyu:click:onGetTokenAsync');
    ChatPushClient.getInstance()
      .getTokenAsync()
      .then(() => {
        console.log('test:zuoyu:click:onGetTokenAsync:success');
        ToastAndroid.show('get token success', ToastAndroid.SHORT);
      })
      .catch((e) => {
        console.warn('test:zuoyu:click:onGetTokenAsync:error:', e);
        ToastAndroid.show(
          'get token error:' + JSON.stringify(e),
          ToastAndroid.SHORT
        );
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
      <Pressable style={styles.button} onPress={onGetTokenAsync}>
        <Text>{'get token async'}</Text>
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
