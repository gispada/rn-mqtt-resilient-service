# React Native MQTT Resilient Service

A PoC React Native application that is always connected to a **MQTT** broker (**Mosquitto**) via an **Android service**. The app can be killed or the device restarted, but the connection to the broker will be reestablished eventually.

Everytime a new event is published on the broker, the service wakes up to process it. A [**headless task**](https://reactnative.dev/docs/headless-js-android) is used to send the event to the JS side. Since it is a proof of concept, all this app does is logging the events, both on the native side and on the JS side.

> The app leverages Android services to keep the connection alive regardless of the app lifecycle. **This mechanism does not work on iOS.**

## Implementation

To keep the connection with the MQTT broker alive, even when the app is in the background or killed, an **Android foreground service is running** (more details [here](https://developer.android.com/guide/components/foreground-services)). This kind of service runs in the foreground, visible to the user via a notification, and it's independent from the rest of the app. When `START_STICKY` is returned from the `onStartCommand` method, the service will be restarted if somehow was stopped.

Additionally, a **broadcast receiver** hooked to the boot event has been created to relaunch the service when the device is restarted.

When an event is received, it is also sent to the JS side through a headless task service, that will trigger a function registered with `registerHeadlessTask`. A headless task runs even when the app has been killed.

## MQTT broker

The MQTT message broker used is **Eclipse Mosquitto**, running on a container. To start it, run `docker compose up -d`; the credentials to access it are `admin:admin`. A GUI client like [MQTT X](https://mqttx.app/) can be used to interact with the broker to send messages, for instance.

To change some of the connection parameters for the broker, edit `ClientApp/android/mqtt.properties`.

#### Note
When the container is running locally, it might not be exposed to external devices in the same host's network. To expose it, one solution is to use **nginx** as a reverse proxy for routing incoming traffic to Mosquitto.

Add these lines at the end of `nginx.conf` and start nginx. With the following config, the exposed port is 1884.
```nginx
stream {
  server {
    listen     1884;
    proxy_pass 127.0.0.1:1883;
  }
}
```
