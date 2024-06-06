import * as React from 'react';

import { StyleSheet, View, Text, Pressable } from 'react-native';
import {
  multiply,
  ChatPushClient,
  getPlatform,
  getDeviceType,
  type PushType,
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
      pushType = 'apns';
    } else {
      pushType = (getDeviceType() ?? 'unknown') as PushType;
    }
    console.log('test:zuoyu:init:', pushType);
    ChatPushClient.getInstance()
      .init({
        platform: getPlatform(),
        pushType: pushType as any,
      })
      .catch((e) => {
        console.warn('test:zuoyu:init:error:', e);
      });
  }, []);

  const onStartRegistration = () => {};
  const onStopRegistration = () => {};

  React.useEffect(() => {
    init();
  }, [init]);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
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
