package com.mhrc.apprastreador;

import android.util.Log;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.util.List;

public class ObjectDetectorHelper {
	private static final String TAG = "ObjectDetectorHelper";
	
	private final ObjectDetector detector;
	private DetectionListener listener;

	public interface DetectionListener {
		void onDetectionSuccess(List<DetectedObject> detectedObjects);
		void onDetectionFailure(Exception e);
	}

	public ObjectDetectorHelper() {
		ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
				.setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
				.enableMultipleObjects()
				.enableClassification()
				.build();

		detector = ObjectDetection.getClient(options);
	}

	public void setListener(DetectionListener listener) {
		this.listener = listener;
	}

	public void processImageProxy(ImageProxy imageProxy) {
		InputImage image = InputImage.fromMediaImage(
				imageProxy.getImage(),
				imageProxy.getImageInfo().getRotationDegrees()
		);

		detector.process(image)
				.addOnSuccessListener(detectedObjects -> {
					if (listener != null) {
						listener.onDetectionSuccess(detectedObjects);
					}
					imageProxy.close();
				})
				.addOnFailureListener(e -> {
					Log.e(TAG, "Error en detección", e);
					if (listener != null) {
						listener.onDetectionFailure(e);
					}
					imageProxy.close();
				});
	}

	public void close() {
		detector.close();
	}
}

