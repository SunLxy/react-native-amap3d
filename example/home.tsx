import { useNavigation, useTheme } from "@react-navigation/native";
import * as React from "react";
import {
  Constructor,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableNativeFeedback,
  TouchableOpacity,
  View,
} from "react-native";
import { AMapSdk } from "react-native-amap3d";
import screens from "./screens";
import { NavigationProps, ScreenName } from "./types";

let Touchable: Constructor<any> = TouchableOpacity;
if (Platform.OS === "android") {
  Touchable = TouchableNativeFeedback;
}

export default () => {
  const navigation = useNavigation<NavigationProps>();
  React.useEffect(() => {
    AMapSdk.init(
      Platform.select({
        android: "c52c7169e6df23490e3114330098aaac",
        ios: "186d3464209b74effa4d8391f441f14d",
      })
    );
    AMapSdk.getVersion().then((version) => {
      navigation.setOptions({ headerRight: () => <Text>v{version}</Text> });
    });
    const latlag = {
      latitude: 1.200475906371626,
      longitude: 12.26297997946493
    }
    const latlag2 = {
      latitude: 1.200475906371626,
      longitude: 12.26217997946493
    }
    AMapSdk.calculateLineDistance(latlag, latlag2).then((result) => {
      console.log("calculateLineDistance--->", result)
    });
    AMapSdk.coordinateConverter(latlag, 6).then((result) => {
      console.log("coordinateConverter--->", result)
    });
    AMapSdk.start()
    AMapSdk.addLocationListener((result: any) => {
      console.log("rest", result)
    })
  }, []);
  return (
    <ScrollView>
      {Object.keys(screens).map((i) => (
        <Item key={i} name={i as keyof typeof screens} />
      ))}
    </ScrollView>
  );
};

function Item({ name }: { name: ScreenName }) {
  const { colors } = useTheme();
  const navigation = useNavigation<NavigationProps>();
  return (
    <Touchable onPress={() => navigation.push(name)}>
      <View style={style.item}>
        <Text style={[style.itemText, { color: colors.text }]}>{name}</Text>
      </View>
    </Touchable>
  );
}

const style = StyleSheet.create({
  item: { padding: 16 },
  itemText: { fontSize: 16 },
});
