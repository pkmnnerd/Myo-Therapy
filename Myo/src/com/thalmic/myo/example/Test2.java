package com.thalmic.myo.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import processing.core.*;

import com.firebase.client.Firebase;
import com.thalmic.myo.*;
import com.thalmic.myo.enums.*;

public class Test2 extends PApplet {

	int mode;
	DeviceListener dataCollector;
	Hub hub;
	Myo myo;
	Bird bird;
	ArrayList<Pipe> a;
	String timestamp;
	Firebase fb;
	Firebase child;
	int score;

	public void setup() {
		
		size(1200, 800);
		
		
		java.util.Date date= new java.util.Date();
		timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Timestamp(date.getTime()));

		fb = new Firebase("https://feslkjflkjfelkjlkjf.firebaseio.com/");
		//child = fb.child(timestamp);
		//fb.setValue("test");
		restart();
		try {
			hub = new Hub("com.example.emg-data-sample");

			println("Attempting to find a Myo...");
			myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
			dataCollector = new EmgDataCollector();
			hub.addListener(dataCollector);
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
	}

	int prevValue;
	int c = 0;

	public void draw() {
		if (mode == 1) {
			hub.run(1000 / 20);
			int value = ((EmgDataCollector) dataCollector).getData();
			//println(value);
			background(0, 225, 255);

			int y = value;
			if (value - prevValue > 4)
				y = prevValue + 4;
			if (prevValue - value > 4)
				y = prevValue - 4;

			bird.move(y);
			bird.draw();
			prevValue = y;

			c++;
			if (c > 30) {
				c = 0;
				a.add(new Pipe());
			}
			for (Pipe p : a) {
				p.draw();
			}
			if (a.get(0).getX() < 250 && a.get(0).getX() > 50) {
				if (bird.getY() < a.get(0).getY()
						|| bird.getY() > a.get(0).getY() + 150) {
					println("lose");
					myo.vibrate(VibrationType.VIBRATION_MEDIUM);
					mode = 0;
				}

			}
			if (a.get(0).getX() <= 50 && !a.get(0).hasPassed()){
				a.get(0).pass();
				score++;
			}
			if (a.get(0).getX() < -150){
				a.remove(0);
			}
			
			fill(255, 225, 200);
			rect(0, 750, 1200, 50);
			fill(0);
			text("Score: " + score, 900, 200);
			
			
			fill(66, 255, 35);
			//if (y > baseline)
				//fill(255, 255, 0);

			rect(0, 600 - y, 150, y);
			//stroke(0, 0, 0, 0);
			//fill(255, 0, 0);
			//rect(0, 600 - (int) (baseline * 0.7), 150, 10);
		} else
			restart();

	}

	public void restart() {
		mode = 0;
		background(0, 225, 255);
		fill(66, 255, 35);
		stroke(0, 25, 0);
		rect(300, 200, 600, 300);
		rect(300, 510, 600, 200);
		// textMode(CENTER);
		fill(0);
		textSize(50);
		text("Start", 500, 350);
		text("Exit", 500, 600);
		text("Score: " + score, 900, 200);
		fill(255, 225, 200);
		rect(0, 750, 1200, 50);
		bird = new Bird();
		a = new ArrayList<Pipe>();
		a.add(new Pipe());
		c = 0;
		prevValue = 100;
	}

	public void mouseClicked() {
		if (mode == 0) {
			if (mouseX < 900 && mouseX > 300 && mouseY > 200 && mouseY < 500){
				mode = 1;
				score = 0;
			}
			else if (mouseX < 900 && mouseX > 300 && mouseY > 510 && mouseY < 710){
				System.out.println("Done.");
				//Map<String, Map<String, ArrayList<Integer>>> map = new HashMap<String, Map<String, ArrayList<Integer>>>();
				//map.put(timestamp, ((EmgDataCollector) dataCollector).getMap());
				//System.out.println(map);
				fb.child(timestamp).setValue(((EmgDataCollector) dataCollector).getMap2(2));
				fb.child(timestamp).child("test").setValue(2);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}

	class Bird {
		int x;
		int y;
		int v;

		public Bird() {
			x = 200;
			y = 300;
		}

		void move(int value) {
			y = 800-value*4;
		}

		void draw() {
			fill(255, 0, 0);
			rect(x, y, 50, 50);
		}

		int getY() {
			return y;
		}

	}

	class Pipe {

		int x;
		int y;
		boolean passed;

		public Pipe() {
			x = 1200;
			y = (int) (Math.random() * 300) + 100;
			passed = false;
		}
		
		void pass(){
			passed = true;
		}
		
		boolean hasPassed(){
			return passed;
		}

		void draw() {
			x -= 20;
			fill(0, 170, 0);
			rect(x, y-30, 150, 30);
			rect(x+15, 0, 120, y-30);
			
			rect(x, y + 200, 150, 30);
			rect(x+15, y+230, 120, 600);

		}

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

	}

}
