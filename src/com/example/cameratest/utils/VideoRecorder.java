package com.example.cameratest.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorder extends SurfaceView implements SurfaceHolder.Callback, FileReadyListener {
	private Camera camera = null;
	private SurfaceHolder mHolder;
	private MediaRecorder mMediaRecorder;

	private FileReadyObserver fileObserver;
	public static Boolean canPlay = false;

	private static File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),	"CameraTest");
	public static final String outputFile = mediaStorageDir.getPath() + File.separator + "VID_cabecao.mp4";

	public VideoRecorder(Context context, SurfaceView preview) {
		super(context);

		// Get front camera
		try {
			this.camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		} catch (Exception e) {
			Log.e("VideoPreview", "Cannot open front camera");
			e.printStackTrace();
		}

		// Setup preview holder
		mHolder = preview.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Create and prepare media recorder
		prepareVideoRecorder();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			Log.d("VideoPreview",
					"Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.stopPreview();
		}
	}

	private boolean prepareVideoRecorder() {
		mMediaRecorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		camera.unlock();
		Log.i("Camera", camera.toString());
		Log.i("Camera", mMediaRecorder.toString());
		mMediaRecorder.setCamera(camera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		setupMediaFile();
		mMediaRecorder.setOutputFile(outputFile);

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

		mMediaRecorder.setVideoSize(640, 480); // Its is not on android docs but
												// it needs to be done. (640x480
												// = VGA resolution)

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d("VideoPreview", "IllegalStateException preparing MediaRecorder: "	+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d("VideoPreview", "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void setupMediaFile() {
		// Create Folder
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
			Log.e("VideoPreview", "failed to create directory");
		}

		// Remove old media file
		File file = new File(outputFile);
		if (file.exists()) {
			Log.d("VideoPreview", "removing old media file");
			file.delete();
		}
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			camera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}

	public void startRecording() {
		fileObserver = new FileReadyObserver(outputFile);
		fileObserver.setFileReadyListener(this);
		fileObserver.startWatching();

		// initialize video camera
		if (prepareVideoRecorder()) {
			// Camera is available and unlocked, MediaRecorder is prepared,
			// now you can start recording
			mMediaRecorder.start();
		} else {
			// prepare didn't work, release the camera
			releaseMediaRecorder();
			// inform user
		}
	}

	public void stopRecording() {
		// stop recording and release camera
		mMediaRecorder.stop(); // stop the recording
		// releaseMediaRecorder(); // release the MediaRecorder object
		camera.lock(); // take camera access back from MediaRecorder
		releaseCamera(); // release camera
	}

	@Override
	public void finishedSavingFile() {
		// TODO Auto-generated method stub
		canPlay = true;
		Log.i("VideoRecorder", "Finished Saving from video recorder");
		fileObserver.stopWatching();
	}
}
