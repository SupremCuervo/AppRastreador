package com.mhrc.apprastreador;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detector mejorado que combina ML Kit con reconocimiento mejorado de objetos
 * Utiliza técnicas de post-procesamiento para mejorar la detección de objetos específicos
 */
public class EnhancedObjectDetector {
	private static final String TAG = "EnhancedObjectDetector";
	
	private ObjectDetector mlKitDetector;
	private Map<String, Float> objectConfidenceBoost; // Boost de confianza para objetos conocidos
	private DetectionListener listener;
	
	public interface DetectionListener {
		void onDetectionSuccess(List<EnhancedDetection> detections);
		void onDetectionFailure(Exception e);
	}
	
	public static class EnhancedDetection {
		private DetectedObject originalObject;
		private String enhancedLabel;
		private float enhancedConfidence;
		private Rect boundingBox;
		
		public EnhancedDetection(DetectedObject originalObject, String enhancedLabel, float enhancedConfidence) {
			this.originalObject = originalObject;
			this.enhancedLabel = enhancedLabel;
			this.enhancedConfidence = enhancedConfidence;
			this.boundingBox = originalObject.getBoundingBox();
		}
		
		public DetectedObject getOriginalObject() {
			return originalObject;
		}
		
		public String getEnhancedLabel() {
			return enhancedLabel;
		}
		
		public float getEnhancedConfidence() {
			return enhancedConfidence;
		}
		
		public Rect getBoundingBox() {
			return boundingBox;
		}
		
		public Integer getTrackingId() {
			return originalObject.getTrackingId();
		}
	}
	
	public EnhancedObjectDetector() {
		// Configurar detector ML Kit con mejores opciones
		ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
				.setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
				.enableMultipleObjects()
				.enableClassification()
				.build();
		
		mlKitDetector = ObjectDetection.getClient(options);
		
		// Inicializar boost de confianza para objetos conocidos
		initializeConfidenceBoost();
	}
	
	private void initializeConfidenceBoost() {
		objectConfidenceBoost = new HashMap<>();
		// Aumentar confianza para objetos que queremos detectar mejor
		objectConfidenceBoost.put("PERSON", 1.1f); // 10% más de confianza
		objectConfidenceBoost.put("FURNITURE", 1.15f);
		objectConfidenceBoost.put("ELECTRONICS", 1.2f);
		objectConfidenceBoost.put("FOOD", 1.1f);
	}
	
	public void setListener(DetectionListener listener) {
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
		
		mlKitDetector.process(inputImage)
				.addOnSuccessListener(detectedObjects -> {
					List<EnhancedDetection> enhancedDetections = enhanceDetections(detectedObjects);
					if (listener != null) {
						listener.onDetectionSuccess(enhancedDetections);
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
	
	/**
	 * Mejora las detecciones aplicando técnicas de post-procesamiento
	 */
	private List<EnhancedDetection> enhanceDetections(List<DetectedObject> detectedObjects) {
		List<EnhancedDetection> enhanced = new ArrayList<>();
		
		for (DetectedObject object : detectedObjects) {
			// Obtener información del objeto
			String label = "";
			float confidence = 0f;
			
			if (!object.getLabels().isEmpty()) {
				label = object.getLabels().get(0).getText();
				confidence = object.getLabels().get(0).getConfidence();
			}
			
			// Filtrar objetos con confianza muy baja
			if (confidence < 0.4f) {
				Log.d(TAG, "Filtrando objeto con confianza baja: " + label + " (" + String.format("%.0f%%", confidence * 100) + ")");
				continue;
			}
			
			// Validar bounding box
			Rect box = object.getBoundingBox();
			int boxWidth = box.width();
			int boxHeight = box.height();
			
			// Filtrar bounding boxes inválidos o muy pequeños
			if (boxWidth < 20 || boxHeight < 20) {
				Log.d(TAG, "Filtrando objeto con bounding box muy pequeño: " + label + " (" + boxWidth + "x" + boxHeight + ")");
				continue;
			}
			
			// Filtrar objetos con formas extremas
			float aspectRatio = (float) boxWidth / boxHeight;
			if (aspectRatio > 10.0f || aspectRatio < 0.1f) {
				Log.d(TAG, "Filtrando objeto con forma extrema: " + label + " (aspect ratio: " + String.format("%.2f", aspectRatio) + ")");
				continue;
			}
			
			// Aplicar mejoras
			String enhancedLabel = enhanceLabel(label, object);
			float enhancedConfidence = enhanceConfidence(label, confidence, object);
			
			// Solo agregar si la confianza mejorada es razonable
			if (enhancedConfidence >= 0.4f) {
				EnhancedDetection detection = new EnhancedDetection(object, enhancedLabel, enhancedConfidence);
				enhanced.add(detection);
				Log.d(TAG, "Objeto mejorado agregado: " + enhancedLabel + " (" + String.format("%.0f%%", enhancedConfidence * 100) + ")");
			} else {
				Log.d(TAG, "Filtrando objeto después de mejorar confianza: " + label + " -> " + enhancedLabel);
			}
		}
		
		Log.d(TAG, "Total objetos mejorados: " + enhanced.size() + " de " + detectedObjects.size());
		return enhanced;
	}
	
	/**
	 * Mejora la etiqueta del objeto usando heurísticas
	 */
	private String enhanceLabel(String originalLabel, DetectedObject object) {
		// Buscar en objetos específicos conocidos
		String specificObject = ObjectDetectionInfo.getTextForObject(object);
		
		// Si encontramos un objeto específico, usarlo
		if (!specificObject.contains("Objeto") && !specificObject.contains("Desconocido")) {
			return specificObject.split("\\(")[0].trim();
		}
		
		// Aplicar heurísticas basadas en el tamaño y posición
		return applyHeuristics(originalLabel, object);
	}
	
	/**
	 * Aplica heurísticas para mejorar la identificación
	 */
	private String applyHeuristics(String label, DetectedObject object) {
		Rect box = object.getBoundingBox();
		float area = box.width() * box.height();
		float aspectRatio = (float) box.width() / box.height();
		
		// Heurísticas para objetos específicos
		if ("ELECTRONICS".equals(label)) {
			// Si es pequeño y rectangular, podría ser un mouse
			if (area < 0.1f && aspectRatio > 1.2f && aspectRatio < 2.0f) {
				return "Mouse (probable)";
			}
			// Si es grande y rectangular, podría ser una computadora
			if (area > 0.3f && aspectRatio > 1.3f) {
				return "Computadora (probable)";
			}
		}
		
		if ("FURNITURE".equals(label)) {
			// Si es grande y horizontal, podría ser una cama
			if (area > 0.4f && aspectRatio > 1.5f) {
				return "Cama (probable)";
			}
			// Si es mediano y tiene forma de silla
			if (area > 0.15f && area < 0.3f && aspectRatio > 0.8f && aspectRatio < 1.2f) {
				return "Silla (probable)";
			}
		}
		
		return label;
	}
	
	/**
	 * Mejora la confianza basada en características del objeto
	 */
	private float enhanceConfidence(String label, float originalConfidence, DetectedObject object) {
		float confidence = originalConfidence;
		
		// Aplicar boost si el objeto está en nuestra lista de boost
		if (objectConfidenceBoost.containsKey(label)) {
			confidence *= objectConfidenceBoost.get(label);
		}
		
		// Aumentar confianza si el objeto tiene buen tamaño
		Rect box = object.getBoundingBox();
		float area = (box.width() * box.height()) / (1920f * 1080f); // Asumiendo resolución típica
		
		if (area > 0.1f && area < 0.8f) {
			// Tamaño óptimo para detección
			confidence *= 1.1f;
		}
		
		// Limitar confianza a 1.0
		return Math.min(confidence, 1.0f);
	}
	
	public void close() {
		if (mlKitDetector != null) {
			mlKitDetector.close();
		}
	}
}

