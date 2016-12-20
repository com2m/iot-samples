/*
 * Copyright (c) 2016 com2m GmbH.
 * All rights reserved.
 */

package de.com2m.iot.examples.mqtt;

import java.util.Random;

public class Whirlpool {

	private final String id;

	private boolean on;

	private float currentTemperature;

	private float targetTemperature;

	private TemperatureSimulatorThread simulatorThread;

	public Whirlpool(String id) {
		this.id = id;
		this.targetTemperature = 30; // degrees celsius
		this.on = true;
	}

	public String getId() {
		return id;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public float getCurrentTemperature() {
		return currentTemperature;
	}

	public float getTargetTemperature() {
		return targetTemperature;
	}

	public void setTargetTemperature(float targetTemperature) {
		this.targetTemperature = targetTemperature;
	}


	public boolean isTemperatureDeviationSignificant() {
		return getTemperatureDeviation() >= 1.5;
	}

	public float getTemperatureDeviation() {
		return Math.abs(this.currentTemperature - targetTemperature);
	}

	public void startTemperatureSimulation() {
		simulatorThread = new TemperatureSimulatorThread();
		simulatorThread.start();
	}

	public void stopTemperatureSimulation() {
		simulatorThread.interrupt();
	}

	private class TemperatureSimulatorThread extends Thread {

		public static final int UPDATE_INTERVAL_IN_MS = 500;
		private Random random = new Random();

		@Override
		public void run() {
			while (!isInterrupted()) {
				// generate a random float value, which is near (+/- 2) to the target temperature
				float min = Whirlpool.this.targetTemperature - 2;
				float max = Whirlpool.this.targetTemperature + 2;
				Whirlpool.this.currentTemperature = random.nextFloat() * (max - min) + min;
				try {
					Thread.sleep(UPDATE_INTERVAL_IN_MS);
				} catch (InterruptedException thrownWhenThreadIsInterrupted) {
					interrupt();
				}
			}
			System.out.println("Whirlpool temperature simulation thread stopped.");
		}

	}
}
