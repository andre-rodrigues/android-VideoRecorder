package com.example.cameratest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;

import com.example.cameratest.utils.VideoRecorder;

public class VideoActivity extends Activity {
	private VideoRecorder recorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		// Create our Preview view and set it as the content of our activity.
		recorder = new VideoRecorder(this,
				(SurfaceView) findViewById(R.id.camera_preview));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 0, 0, "StartRecording");
		menu.add(0, 1, 0, "StopRecording");
		// getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			try {
				recorder.startRecording();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			recorder.stopRecording();
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
