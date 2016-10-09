package de.gnox.rovy.ocv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RovyOpenCVWrapper {

	static {
		System.loadLibrary("rovyocv");
	}

	public Collection<ArucoMarker> arucoDetectMarkers() {
		
		int detectedMarkers = nArucoDetectMarkers();
		
		if (detectedMarkers == 0)
			return Collections.emptyList();
		
		List<ArucoMarker> resultList = new ArrayList<>(detectedMarkers);
	
		for (int markerIdx = 0; markerIdx < detectedMarkers; markerIdx++) {
			ArucoMarker marker = new ArucoMarker(nArucoGetMarkerId(markerIdx));
			int[] markerCornersData = nArucoGetMarkerCorners(markerIdx);
			if (markerCornersData.length % 2 != 0)
				throw new RuntimeException("error in marker corners data");
			for (int i = 0; i < markerCornersData.length; i += 2)
				marker.getCorners().add(new Point2i(markerCornersData[i], markerCornersData[i + 1]));
			
			double[] rVec = nArucoGetMarkerRotationVector(markerIdx);
			double[] tVec = nArucoGetMarkerTranslationVector(markerIdx);
			if (rVec != null)
				marker.setRotationVector(new Vector3d(rVec));
			if (tVec != null)
				marker.setTranslationVector(new Vector3d(tVec));			
			
			resultList.add(marker);
		}

		
		
		return resultList;
	}
	
	public void arucoInit(ArucoDictionary dict) {
		nArucoInit(dict.getId());
	}
	
	public void arucoInitWithPoseEstimation(ArucoDictionary dict, float markerLength) {
		nArucoInitWithPoseEstimation(dict.getId(), markerLength);
	}
	
	private native int nArucoInit(int markerDict);
	
	private native int nArucoInitWithPoseEstimation(int markerDict, float markerLength);
	
	private native int nArucoDetectMarkers();

	private native int[] nArucoGetMarkerCorners(int markerIndex);
	
	private native double[] nArucoGetMarkerRotationVector(int markerIndex);
	
	private native double[] nArucoGetMarkerTranslationVector(int markerIndex);
	
	private native int nArucoGetMarkerId(int markerIndex);
 	
	public void arucoDrawDetectedMarkers() {
		nArucoDrawDetectedMarkers();
	}

	private native int nArucoDrawDetectedMarkers();

	public void openVideoCapture(int cam, boolean debug) {
		nOpenVideoCapture(cam, debug);
	}
	
	private native int nOpenVideoCapture(int cam, boolean debug);

	public void grab() {
		nGrab();
	}
	
	private native int nGrab();

	public void imshow() {
		nImshow();
	}

	private native int nImshow();

	public void releaseVideoCapture() {
		nReleaseVideoCapture();
	}
	
	private native int nReleaseVideoCapture();

	
}
