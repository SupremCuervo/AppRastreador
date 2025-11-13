package com.mhrc.apprastreador;

import android.util.Log;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import java.util.List;

public class FaceDetectionHelper {
	private static final String TAG = "FaceDetectionHelper";
	
	private final FaceDetector faceDetector;
	private FaceDetectionListener listener;

	public interface FaceDetectionListener {
		void onFaceDetected(List<Face> faces);
		void onFaceDetectionFailure(Exception e);
	}

	public FaceDetectionHelper() {
		FaceDetectorOptions options = new FaceDetectorOptions.Builder()
				.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
				.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
				.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
				.setMinFaceSize(0.1f)
				.enableTracking()
				.build();

		faceDetector = FaceDetection.getClient(options);
	}

	public void setListener(FaceDetectionListener listener) {
		this.listener = listener;
	}

	@ExperimentalGetImage
	public void processImageProxy(ImageProxy imageProxy) {
		android.media.Image image = imageProxy.getImage();
		if (image == null) {
			Log.w(TAG, "⚠️ imageProxy.getImage() es null, cerrando ImageProxy");
			imageProxy.close();
			return;
		}
		
		InputImage inputImage = InputImage.fromMediaImage(
				image,
				imageProxy.getImageInfo().getRotationDegrees()
		);

		faceDetector.process(inputImage)
				.addOnSuccessListener(faces -> {
					if (listener != null) {
						listener.onFaceDetected(faces);
					}
					imageProxy.close();
				})
				.addOnFailureListener(e -> {
					Log.e(TAG, "Error en detección facial", e);
					if (listener != null) {
						listener.onFaceDetectionFailure(e);
					}
					imageProxy.close();
				});
	}

	public void close() {
		faceDetector.close();
	}
}

