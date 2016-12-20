/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.examples.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientExampleApplication {

	public static void main(String[] args) {
		try {
			// configuration values
			String serverURI = "ssl://cloud.com2m.de:8883";
			String componentId = "7f555891-000f-47e3-9dea-7e5991996a0e";
			String username = "<exampleuser>";
			String password = "<examplepassword>";

			// init whirlpool
			final Whirlpool whirlpool = new Whirlpool(componentId);
			whirlpool.startTemperatureSimulation();

			// init MQTT client and start sending data
			final WhirlpoolMqttClient whirlpoolMqttClient = new WhirlpoolMqttClient(serverURI, componentId, whirlpool);
			whirlpoolMqttClient.connect(username, password);
			whirlpoolMqttClient.startSendingData();

			// shutdown hooks to close all threads properly
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					whirlpoolMqttClient.stopSendingData();
					whirlpoolMqttClient.disconnect();
					whirlpool.stopTemperatureSimulation();
				} catch (MqttException ex) {
					ex.printStackTrace();
				}
			}));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
