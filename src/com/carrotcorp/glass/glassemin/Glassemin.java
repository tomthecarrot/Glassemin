// See README and LICENSE files for more information.

package com.carrotcorp.glass.glassemin;

import com.carrotcorp.glass.glassemin.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class Glassemin extends Activity implements SensorEventListener {
	SensorManager sensorManager;
	Thread thread;
	AudioTrack audioTrack;
	boolean isRunning = true;
	float lightValue = 0;
	WakeLock wl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Create the view:
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glassemin);
		
		// Prevent the screen from going to sleep:
		PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "com.carrotcorp.glass.glassemin");
		wl.acquire();
		
		// Create and start audio:
		createAudio();
		
		// Register the light sensor listener:
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	private void createAudio() {
		// Start a new Thread to create audio:
		thread = new Thread() {
			public void run() {
				int sr = 44100;
				int buffsize = AudioTrack.getMinBufferSize(sr, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
				audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sr, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, buffsize, AudioTrack.MODE_STREAM);
				
				short samples[] = new short[buffsize];
				int amp = 10000;
				double twopi = 8.*Math.atan(1.);
				double ph = 0.0;
				
				audioTrack.play();
				
				while (isRunning) {
					double fr = 200 + 5*lightValue;
					
					for (int i=0; i < buffsize; i++) {
						samples[i] = (short) (amp*Math.sin(ph));
						ph += twopi*fr/sr;
					}
					audioTrack.write(samples, 0, buffsize);
				}
			}
		};
		thread.start();
	}
	
	public void onPause() {
		super.onPause();
		
		// If the app is exited or interrupted:
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		
		// Stop the wake lock, only if it has already been acquired:
		if (wl != null) {
			if (wl.isHeld()) {
				wl.release();
			}
		}
		
		// Stop the AudioTrack:
		isRunning = false;
		
		audioTrack.stop();
		audioTrack.release();
		
		thread.interrupt();
		thread = null;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Get the distance value and log it:
		float distance = event.values[0];
		Log.i("glassemin", "distance=="+distance);
		
		// Round distance to nearest 100 for the lightValue:
		lightValue = distance - (distance % 10);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// unused
	}

}
