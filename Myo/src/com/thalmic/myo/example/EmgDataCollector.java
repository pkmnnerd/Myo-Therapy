package com.thalmic.myo.example;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.*;

import com.firebase.client.Firebase;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;

public class EmgDataCollector extends AbstractDeviceListener {
	private byte[] emgSamples;
	PrintWriter out;
	HashMap<Integer, ArrayList<Integer>> data = new HashMap<Integer, ArrayList<Integer>>();

	public EmgDataCollector() {
		for (int i = 0; i < 8; i++)
			data.put(i, new ArrayList<Integer>());
	}

	@Override
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		if (emgSamples != null) {
			for (int i = 0; i < emgSamples.length; i++) {
				emgSamples[i] = 0;
			}
		}
	}

	@Override
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
		this.emgSamples = emg;
		if (emgSamples != null) {
			for (int i = 0; i < emgSamples.length; i++) {
				data.get(i).add((int) emgSamples[i]);
			}
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(emgSamples);
	}

	public HashMap<Integer, ArrayList<Integer>> getMap() {
		HashMap<Integer, ArrayList<Integer>> data2 = new HashMap<Integer, ArrayList<Integer>>();
		for(int i = 0; i < 8; i++){
			ArrayList<Integer> a = data.get(i);
			ArrayList<Integer> a2 = new ArrayList<Integer>();
			
			for(int j = 6; j < a.size()-6; j+=3){
				int n = 0;
				for(int k = -5; k <= 5; k++){
					n+=a.get(j+k)*a.get(j+k);
				}
				a2.add((int)Math.sqrt(n/5));
			}
			data2.put(i, a2);
			data2.put(i+8, a);
		}
		
		return data2;
	}

	public int getData() {
		try {
			int x = 0;
			for (int i = 0; i < emgSamples.length; i++) {
				x += Math.abs(emgSamples[i]);
			}
			return x;
		} catch (Exception e) {
			return 0;
		}
	}
}
