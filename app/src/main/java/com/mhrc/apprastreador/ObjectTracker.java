package com.mhrc.apprastreador;

import android.graphics.Rect;
import com.google.mlkit.vision.objects.DetectedObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTracker {
	private static final float SMOOTHING_FACTOR = 0.7f; // Factor de suavizado (0-1)
	private static final int MAX_HISTORY = 5; // Máximo de posiciones históricas
	
	private Map<Integer, TrackingData> trackedObjects = new HashMap<>();
	
	private static class TrackingData {
		Rect smoothedRect;
		float smoothedConfidence;
		int frameCount;
		Rect[] positionHistory;
		int historyIndex;
		
		TrackingData(Rect initialRect, float initialConfidence) {
			this.smoothedRect = new Rect(initialRect);
			this.smoothedConfidence = initialConfidence;
			this.frameCount = 1;
			this.positionHistory = new Rect[MAX_HISTORY];
			this.positionHistory[0] = new Rect(initialRect);
			this.historyIndex = 1;
		}
	}
	
	/**
	 * Actualiza el seguimiento de objetos y aplica suavizado
	 */
	public Map<Integer, Rect> updateTracking(List<DetectedObject> detectedObjects) {
		Map<Integer, Rect> smoothedRects = new HashMap<>();
		Map<Integer, Boolean> currentFrameObjects = new HashMap<>();
		
		// Procesar objetos detectados en el frame actual
		for (DetectedObject object : detectedObjects) {
			Integer trackingId = object.getTrackingId();
			Rect currentRect = object.getBoundingBox();
			
			if (trackingId != null) {
				currentFrameObjects.put(trackingId, true);
				
				// Obtener confianza
				float confidence = 0.5f;
				if (!object.getLabels().isEmpty()) {
					confidence = object.getLabels().get(0).getConfidence();
				}
				
				if (trackedObjects.containsKey(trackingId)) {
					// Objeto ya rastreado - aplicar suavizado
					TrackingData data = trackedObjects.get(trackingId);
					data.frameCount++;
					
					// Suavizado exponencial del bounding box
					Rect smoothed = smoothRect(data.smoothedRect, currentRect, SMOOTHING_FACTOR);
					data.smoothedRect = smoothed;
					
					// Suavizado de confianza
					data.smoothedConfidence = data.smoothedConfidence * SMOOTHING_FACTOR + 
											  confidence * (1 - SMOOTHING_FACTOR);
					
					// Guardar en historial
					data.positionHistory[data.historyIndex % MAX_HISTORY] = new Rect(currentRect);
					data.historyIndex++;
					
					smoothedRects.put(trackingId, smoothed);
				} else {
					// Nuevo objeto - inicializar seguimiento
					TrackingData newData = new TrackingData(currentRect, confidence);
					trackedObjects.put(trackingId, newData);
					smoothedRects.put(trackingId, new Rect(currentRect));
				}
			}
		}
		
		// Eliminar objetos que ya no están en el frame
		trackedObjects.entrySet().removeIf(entry -> !currentFrameObjects.containsKey(entry.getKey()));
		
		return smoothedRects;
	}
	
	/**
	 * Suaviza un rectángulo usando interpolación exponencial
	 */
	private Rect smoothRect(Rect oldRect, Rect newRect, float factor) {
		int left = (int)(oldRect.left * factor + newRect.left * (1 - factor));
		int top = (int)(oldRect.top * factor + newRect.top * (1 - factor));
		int right = (int)(oldRect.right * factor + newRect.right * (1 - factor));
		int bottom = (int)(oldRect.bottom * factor + newRect.bottom * (1 - factor));
		
		return new Rect(left, top, right, bottom);
	}
	
	/**
	 * Predice la siguiente posición basada en el historial
	 */
	public Rect predictNextPosition(Integer trackingId) {
		TrackingData data = trackedObjects.get(trackingId);
		if (data == null || data.frameCount < 2) {
			return null;
		}
		
		// Calcular velocidad promedio
		if (data.historyIndex >= 2) {
			Rect current = data.positionHistory[(data.historyIndex - 1) % MAX_HISTORY];
			Rect previous = data.positionHistory[(data.historyIndex - 2) % MAX_HISTORY];
			
			int dx = current.left - previous.left;
			int dy = current.top - previous.top;
			int dw = (current.right - current.left) - (previous.right - previous.left);
			int dh = (current.bottom - current.top) - (previous.bottom - previous.top);
			
			// Predecir siguiente posición
			int predictedLeft = current.left + dx;
			int predictedTop = current.top + dy;
			int predictedRight = current.right + dw;
			int predictedBottom = current.bottom + dh;
			
			return new Rect(predictedLeft, predictedTop, predictedRight, predictedBottom);
		}
		
		return null;
	}
	
	/**
	 * Obtiene la velocidad de un objeto
	 */
	public float getObjectSpeed(Integer trackingId) {
		TrackingData data = trackedObjects.get(trackingId);
		if (data == null || data.frameCount < 2) {
			return 0f;
		}
		
		if (data.historyIndex >= 2) {
			Rect current = data.positionHistory[(data.historyIndex - 1) % MAX_HISTORY];
			Rect previous = data.positionHistory[(data.historyIndex - 2) % MAX_HISTORY];
			
			int dx = current.left - previous.left;
			int dy = current.top - previous.top;
			
			return (float) Math.sqrt(dx * dx + dy * dy);
		}
		
		return 0f;
	}
	
	public void clear() {
		trackedObjects.clear();
	}
}

