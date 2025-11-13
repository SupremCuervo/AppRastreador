package com.mhrc.apprastreador;

import android.util.Log;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.List;

public class TextRecognitionHelper {
	private static final String TAG = "TextRecognitionHelper";
	
	private final TextRecognizer textRecognizer;
	private TextRecognitionListener listener;

	public interface TextRecognitionListener {
		void onTextRecognized(List<Text.TextBlock> textBlocks);
		void onTextRecognitionFailure(Exception e);
	}

	public TextRecognitionHelper() {
		TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();
		textRecognizer = TextRecognition.getClient(options);
	}

	public void setListener(TextRecognitionListener listener) {
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

		textRecognizer.process(inputImage)
				.addOnSuccessListener(text -> {
					if (listener != null) {
						listener.onTextRecognized(text.getTextBlocks());
					}
					imageProxy.close();
				})
				.addOnFailureListener(e -> {
					Log.e(TAG, "Error en reconocimiento de texto", e);
					if (listener != null) {
						listener.onTextRecognitionFailure(e);
					}
					imageProxy.close();
				});
	}

	public void close() {
		textRecognizer.close();
	}
}

