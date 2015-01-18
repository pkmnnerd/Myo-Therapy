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
	HashMap<String, ArrayList<Integer>> data = new HashMap<String, ArrayList<Integer>>();

	public EmgDataCollector() {
		for (int i = 0; i < 8; i++)
			data.put(""+ i, new ArrayList<Integer>());
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
				data.get(""+ i).add((int) emgSamples[i]);
			}
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(emgSamples);
	}

	public HashMap<String, ArrayList<Integer>> getMap() {
		HashMap<String, ArrayList<Integer>> data2 = new HashMap<String, ArrayList<Integer>>();
		for(int i = 0; i < 8; i++){
			ArrayList<Integer> a = data.get(""+i);
			ArrayList<Integer> a2 = new ArrayList<Integer>();
			
			for(int j = 10; j < a.size()-10; j+=5){
				int n = 0;
				for(int k = -9; k <= 9; k++){
					n+=a.get(j+k)*a.get(j+k);
				}
				a2.add((int)Math.sqrt(n/5));
			}
			data2.put("Sensor " + i, a2);
			data2.put("Raw Sensor " + i, a);
		}
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		int min = 600;
		int max = 0;
		for(int j = 0; j < data2.get("Sensor 0").size(); j++){
			int x = 0;
			for(int i = 0; i < 8; i++){
				x+= data2.get("Sensor "+ i).get(j);
			}
			x = x/8;
			a.add(x);
			if (x < min)
				min = x;
			if (x > max)
				max = x;
			
		}
		
		int cutoff = (max*2 + min)/3;
		ArrayList<Integer> maxes = new ArrayList<Integer>();
		int i = 0;
		while(i < a.size()){
			int max2 = 0;
			while(a.get(i) > cutoff && i < a.size()){
				if (a.get(i) > max2)
					max2 = a.get(i);
				
				i++;
				
			}
			if (max2 > 0)
				maxes.add(max2);
			i++;
		}
		
		data2.put("Maxes", maxes);
		
		data2.put("Averaged Data", a);
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
