package de.com2m.iot.examples.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

public class WhirlpoolMqttClient implements MqttCallback {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Whirlpool whirlpool;
	private MqttClient mqttClient;
	private DataSenderThread dataSenderThread;

	public WhirlpoolMqttClient(String serverURI, String mqttClientId, Whirlpool whirlpool) throws MqttException {
		this.whirlpool = whirlpool;
		this.mqttClient = new MqttClient(serverURI, mqttClientId);
	}

	public void connect(String username, String password) throws MqttException {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password.toCharArray());

		mqttClient.connect(options);

		// subscribe to action topic in order to receive actions for this component
		mqttClient.subscribe("components/" + whirlpool.getId() + "/action/#");
		mqttClient.setCallback(this);
	}

	public void disconnect() throws MqttException {
		mqttClient.disconnect();
	}

	/**
	 * Sends current whirlpool values to the server
	 */
	private void sendValues() throws JsonProcessingException, MqttException {
		HashMap<String, Object> values = new HashMap<>();
		values.put("temperature", whirlpool.getCurrentTemperature());
		values.put("on", whirlpool.isOn());

		System.out.println("Sending current whirlpool values to server: " + values);

		MqttMessage message = new MqttMessage();
		message.setPayload(objectMapper.writeValueAsString(values).getBytes());
		mqttClient.publish("components/" + whirlpool.getId() + "/values", message);
	}

	private void sendTemperatureDeviationMessage() throws JsonProcessingException, MqttException {
		HashMap<String, Object> temperatureDeviationMessage = new HashMap<>();
		temperatureDeviationMessage.put("severity", "WARNING");
		temperatureDeviationMessage.put("message", "Current temperature deviates significantly from target temperature");
		temperatureDeviationMessage.put("acknowledgeable", true);
		HashMap<Object, Object> properties = new HashMap<>();
		properties.put("deviation", whirlpool.getTemperatureDeviation());
		temperatureDeviationMessage.put("properties", properties);

		System.out.println("Sending temperature deviation message to server.");

		MqttMessage message = new MqttMessage();
		message.setPayload(objectMapper.writeValueAsString(temperatureDeviationMessage).getBytes());
		mqttClient.publish("components/" + whirlpool.getId() + "/message", message);
	}

	/**
	 * This method is called, when the MQTT client receives an MQTT message from the server
	 */
	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		if(topic.endsWith("read")) {
			System.out.println("Received read action. Sending current temperature to server.");
			sendValues();
		} else if(topic.endsWith("write")) {
			System.out.println("Received write action");
			HashMap<String, Object> values = objectMapper.readValue(mqttMessage.getPayload(), HashMap.class);
			if(values.containsKey("temperature")) {
				float temperature = Float.parseFloat(String.valueOf(values.get("temperature")));
				System.out.println("Setting temperature of whirlpool to " + temperature);
				whirlpool.setTargetTemperature(temperature);
			}
			if(values.containsKey("on")) {
				boolean on = (Boolean) values.get("on");
				System.out.println("Switching whirlpool " + on);
				whirlpool.setOn(on);
			}
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection lost: " + throwable.getMessage());
		throwable.printStackTrace();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		// nothing to do
	}

	public void startSendingData() {
		dataSenderThread = new DataSenderThread();
		dataSenderThread.start();
	}

	public void stopSendingData() {
		dataSenderThread.interrupt();
	}

	private class DataSenderThread extends Thread {

		public static final int DATA_SEND_INTERVAL_IN_MS = 5000;


		@Override
		public void run() {
			while(!isInterrupted()) {
				try {
					sendValues();
					if(whirlpool.isTemperatureDeviationSignificant()) {
						sendTemperatureDeviationMessage();
					}
				} catch (Exception ex) {
					System.err.println("Sending data failed: " + ex.getMessage());
					ex.printStackTrace();
				}
				try {
					Thread.sleep(DATA_SEND_INTERVAL_IN_MS);
				} catch (InterruptedException thrownWhenThreadIsInterrupted) {
					interrupt();
				}
			}
			System.out.println("Data sending thread stopped.");
		}

	}

}

