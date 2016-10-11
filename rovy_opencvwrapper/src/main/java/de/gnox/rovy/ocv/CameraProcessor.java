package de.gnox.rovy.ocv;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraProcessor {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private boolean debug;

	private int cam;

	AtomicBoolean stopCapturingNow = new AtomicBoolean(false);

	AtomicBoolean capturing = new AtomicBoolean(false);
	
	AtomicReference<Mat> currentFrame = new AtomicReference<>();

	private RovyOpenCVWrapper ocv;

	private CapturingRunnable capturingRunnable;
	
	private boolean arucoInitialized = false;
	
	public CameraProcessor(boolean debug, int cam) {
		ocv = new RovyOpenCVWrapper();
		this.cam = cam;
		this.debug = debug;
	}
	
	public RovyOpenCVWrapper getOpenCVWrapper() {
		return ocv;
	}

	public void startCapturing() {
		if (capturing.get())
			throw new IllegalStateException();
		capturingRunnable = new CapturingRunnable();
		capturing.set(true);
		stopCapturingNow.set(false);
		new Thread(capturingRunnable).start();
	}

	public void stopCapturing() {
		capturingRunnable = null;
		stopCapturingNow.set(true);
	}
	
	public <T> T processUpcommingFrame(Callable<T> callable) {
		if (capturingRunnable == null) 
			return null;
		
		FutureTask<T> futureTask = new FutureTask<>(callable);
		
		capturingRunnable.setNextFrameFutureTask(futureTask);
		
		try {
			return futureTask.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	public void initAruco(ArucoDictionary dict) {
		getOpenCVWrapper().arucoInit(dict);
		arucoInitialized = true;
	}
	
	public void initArucoWithPoseEstimation(ArucoDictionary dict, float markerLength) {
		getOpenCVWrapper().arucoInitWithPoseEstimation(dict, markerLength);
		arucoInitialized = true;
	}

	public Collection<ArucoMarker> detectArucoMarkers() {
		if (!arucoInitialized)
			throw new IllegalStateException("aruco not initialized");
		
		Collection<ArucoMarker> result = processUpcommingFrame(() -> {
			Collection<ArucoMarker> markers = getOpenCVWrapper().arucoDetectMarkers(currentFrame.get());
			if (debug) 
				getOpenCVWrapper().arucoDrawDetectedMarkers(currentFrame.get());
			return markers;
		});
		
		if (result == null)
			return Collections.emptyList();
		return result;
		
	}

	private class CapturingRunnable implements Runnable {

		private AtomicReference<FutureTask<?>> nextFrameFutureTask = new AtomicReference<>(null);
		
		public void setNextFrameFutureTask(FutureTask<?> nextFrameFutureTask) {
			this.nextFrameFutureTask.set( nextFrameFutureTask );
		}
		
		
		@Override
		public void run() {

			VideoCapture cap = new VideoCapture(cam);
			cap.open(cam);
		    if(!cap.isOpened())
		        throw new RuntimeException("camera errro");

		    Mat frame = new Mat();
		    
		    DebugPanel debugPanel = null;
		    JFrame debugFrame = null;
		    if (debug) {
		    	debugPanel = new DebugPanel(frame);
			    debugFrame = new JFrame();
			    debugFrame.setLayout(new BorderLayout());
			    debugFrame.add(debugPanel, BorderLayout.CENTER);
			    debugFrame.pack();
			    debugFrame.setSize(640, 480);
			    debugFrame.setVisible(true);
		    }
		    
			currentFrame.set(frame);
			
			while (!stopCapturingNow.get()) {
				
				long startTime = System.currentTimeMillis();

				cap.grab();
				
				FutureTask<?> task = nextFrameFutureTask.get();
				if (task != null) {
					cap.retrieve(frame);
					task.run();
				}	

				if (debug) 
					debugPanel.repaint();
				
				try {
					
					long duration = System.currentTimeMillis() - startTime;
					long sleepTime = 20 - duration;
					if (sleepTime < 1)
						sleepTime = 1;
					TimeUnit.MILLISECONDS.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			cap.release();

			capturing.set(false);
			
			if (debug) 
				debugFrame.dispose();
			
		}

	};

}
