package com.mhrc.apprastreador;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DetectionStats {
	private int totalFramesProcessed = 0;
	private int humanCount = 0;
	private int knownObjectCount = 0;
	private int unknownObjectCount = 0;
	private Map<String, Integer> objectTypeCount = new HashMap<>();
	private Set<Integer> trackedObjectIds = new HashSet<>();
	private Map<Integer, String> lastObjectType = new HashMap<>();
	
	/**
	 * Registra objetos detectados en un frame, evitando contar duplicados
	 */
	public void recordDetections(java.util.List<com.google.mlkit.vision.objects.DetectedObject> objects) {
		totalFramesProcessed++;
		Set<Integer> currentFrameIds = new HashSet<>();
		
		// Procesar objetos del frame actual
		for (com.google.mlkit.vision.objects.DetectedObject object : objects) {
			Integer trackingId = object.getTrackingId();
			
			if (trackingId != null) {
				currentFrameIds.add(trackingId);
				
				// Solo contar si es un objeto nuevo o cambió de tipo
				String currentType = getObjectTypeString(object);
				String lastType = lastObjectType.get(trackingId);
				
				if (!trackedObjectIds.contains(trackingId) || !currentType.equals(lastType)) {
					recordNewDetection(object, currentType);
					lastObjectType.put(trackingId, currentType);
				}
			} else {
				// Si no tiene ID de seguimiento, contar siempre (puede ser objeto nuevo)
				recordNewDetection(object, getObjectTypeString(object));
			}
		}
		
		// Limpiar objetos que ya no están en el frame
		trackedObjectIds.retainAll(currentFrameIds);
		lastObjectType.keySet().retainAll(currentFrameIds);
	}
	
	private void recordNewDetection(com.google.mlkit.vision.objects.DetectedObject object, String typeString) {
		ObjectDetectionInfo.ObjectType type = ObjectDetectionInfo.getObjectType(object);
		Integer trackingId = object.getTrackingId();
		
		if (trackingId != null) {
			trackedObjectIds.add(trackingId);
		}
		
		switch (type) {
			case HUMANO:
				humanCount++;
				incrementObjectType("Humano");
				break;
			case CONOCIDO:
				knownObjectCount++;
				if (!object.getLabels().isEmpty()) {
					String label = ObjectDetectionInfo.getTextForObject(object);
					String cleanLabel = label.split("\\(")[0].trim();
					incrementObjectType(cleanLabel);
				}
				break;
			case DESCONOCIDO:
				unknownObjectCount++;
				incrementObjectType("Desconocido");
				break;
		}
	}
	
	private String getObjectTypeString(com.google.mlkit.vision.objects.DetectedObject object) {
		ObjectDetectionInfo.ObjectType type = ObjectDetectionInfo.getObjectType(object);
		if (type == ObjectDetectionInfo.ObjectType.HUMANO) {
			return "HUMANO";
		} else if (type == ObjectDetectionInfo.ObjectType.CONOCIDO) {
			if (!object.getLabels().isEmpty()) {
				return ObjectDetectionInfo.getTextForObject(object).split("\\(")[0].trim();
			}
			return "CONOCIDO";
		} else {
			return "DESCONOCIDO";
		}
	}
	
	private void incrementObjectType(String type) {
		objectTypeCount.put(type, objectTypeCount.getOrDefault(type, 0) + 1);
	}
	
	public void reset() {
		totalFramesProcessed = 0;
		humanCount = 0;
		knownObjectCount = 0;
		unknownObjectCount = 0;
		objectTypeCount.clear();
		trackedObjectIds.clear();
		lastObjectType.clear();
	}
	
	// Métodos para incrementar contadores directamente (para TensorFlow Lite)
	public void incrementHumanCount() {
		humanCount++;
		incrementObjectType("Humano");
	}
	
	public void incrementKnownObjectCount() {
		knownObjectCount++;
	}
	
	public void incrementUnknownObjectCount() {
		unknownObjectCount++;
		incrementObjectType("Desconocido");
	}
	
	// Getters
	public int getTotalFramesProcessed() {
		return totalFramesProcessed;
	}
	
	public int getHumanCount() {
		return humanCount;
	}
	
	public int getKnownObjectCount() {
		return knownObjectCount;
	}
	
	public int getUnknownObjectCount() {
		return unknownObjectCount;
	}
	
	public Map<String, Integer> getObjectTypeCount() {
		return new HashMap<>(objectTypeCount);
	}
	
	public String getStatsSummary() {
		int totalObjects = humanCount + knownObjectCount + unknownObjectCount;
		return String.format(
			"Objetos: %d | Humanos: %d | Conocidos: %d | Desconocidos: %d",
			totalObjects, humanCount, knownObjectCount, unknownObjectCount
		);
	}
}

