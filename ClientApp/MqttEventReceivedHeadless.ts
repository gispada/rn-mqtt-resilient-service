type MqttEvent = {
  id: number
  payload: string
}

export default async function MqttEventReceived(event: MqttEvent) {
  console.log('Event received by headless task', JSON.parse(event.payload))
}
