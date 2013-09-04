package com.example.cameratest.utils;

import android.os.FileObserver;
import android.util.Log;

public class FileReadyObserver extends FileObserver{
	private FileReadyListener listener;
	
	public FileReadyObserver(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	public FileReadyListener getMotionListener() {
		return listener;
	}
	
	public void setFileReadyListener(FileReadyListener listener) {
		this.listener = listener;
	}

	@Override
	public void onEvent(int event, String path) {
		// TODO Auto-generated method stub
		Log.d("Observer", Integer.toString(FileObserver.CLOSE_WRITE));
		Log.d("Observer", Integer.toString(event));
		if(FileObserver.CLOSE_WRITE == event) {
			listener.finishedSavingFile();
		}
	}
}
