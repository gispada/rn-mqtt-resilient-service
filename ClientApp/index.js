import { AppRegistry } from 'react-native'
import App from './App'
import { name as appName } from './app.json'
import MqttEventReceived from './MqttEventReceivedHeadless'

AppRegistry.registerHeadlessTask('MqttEventReceived', () => MqttEventReceived)
AppRegistry.registerComponent(appName, () => App)
