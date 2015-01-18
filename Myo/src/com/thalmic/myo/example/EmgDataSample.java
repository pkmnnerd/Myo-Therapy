package com.thalmic.myo.example;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import com.thalmic.myo.enums.VibrationType;

import java.io.*;

public class EmgDataSample {
	public static void main(String[] args) throws Throwable{
		boolean first = false;
		int max = 0;
		int baseline = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader("baseline.txt"));
			baseline = Integer.parseInt(in.readLine());
		} catch (IOException e){
			first = true;
		}
		
		
		try {
			Hub hub = new Hub("com.example.emg-data-sample");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
			DeviceListener dataCollector = new EmgDataCollector();
			hub.addListener(dataCollector);
			
			max = 0;
			int value = 0;
			
			while (value < 500) {
				if (value > max){
					max = value;
				}
				hub.run(1000 / 20);
				//System.out.println(dataCollector);
				if (value > baseline)
					System.err.println(value);
				else
					System.out.println(value);
				value = ((EmgDataCollector)dataCollector).getData();
				
				
			}
			//System.out.println("Done.");
			
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		} finally{
			System.out.println("Done.");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("baseline.txt")));
			out.println(Math.max(baseline, max));
			out.close();
		}
	}
}