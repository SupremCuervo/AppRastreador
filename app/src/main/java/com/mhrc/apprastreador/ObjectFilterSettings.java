package com.mhrc.apprastreador;

public class ObjectFilterSettings {
	private boolean showHumans = true;
	private boolean showKnownObjects = true;
	private boolean showUnknownObjects = true;
	private float minConfidence = 0.25f; // Umbral muy bajo para detectar más objetos en tiempo real (25%)
	private boolean enableTracking = true;
	
	public boolean isShowHumans() {
		return showHumans;
	}
	
	public void setShowHumans(boolean showHumans) {
		this.showHumans = showHumans;
	}
	
	public boolean isShowKnownObjects() {
		return showKnownObjects;
	}
	
	public void setShowKnownObjects(boolean showKnownObjects) {
		this.showKnownObjects = showKnownObjects;
	}
	
	public boolean isShowUnknownObjects() {
		return showUnknownObjects;
	}
	
	public void setShowUnknownObjects(boolean showUnknownObjects) {
		this.showUnknownObjects = showUnknownObjects;
	}
	
	public float getMinConfidence() {
		return minConfidence;
	}
	
	public void setMinConfidence(float minConfidence) {
		this.minConfidence = minConfidence;
	}
	
	public boolean isEnableTracking() {
		return enableTracking;
	}
	
	public void setEnableTracking(boolean enableTracking) {
		this.enableTracking = enableTracking;
	}
	
	/**
	 * Verifica si un objeto debe mostrarse según los filtros
	 * Este método aplica TODOS los filtros: confianza, tamaño, tipo de objeto
	 */
	public boolean shouldShowObject(com.google.mlkit.vision.objects.DetectedObject object) {
		return shouldShowObject(object, false);
	}
	
	/**
	 * Verifica si un objeto debe mostrarse según los filtros
	 * @param object Objeto a verificar
	 * @param ignoreTypeFilters Si es true, ignora los filtros de tipo (humanos, conocidos, desconocidos) y solo usa confianza
	 */
	public boolean shouldShowObject(com.google.mlkit.vision.objects.DetectedObject object, boolean ignoreTypeFilters) {
		if (object == null) {
			return false;
		}
		
		// Verificar si tiene etiquetas
		if (object.getLabels().isEmpty()) {
			// Objeto sin etiquetas: si se ignoran los filtros de tipo, mostrar si tiene confianza suficiente
			// En caso contrario, solo mostrar si showUnknownObjects está activado
			if (ignoreTypeFilters) {
				return true; // Mostrar objetos sin etiquetas si se ignoran filtros de tipo
			}
			return showUnknownObjects;
		}
		
		// Obtener confianza
		float confidence = object.getLabels().get(0).getConfidence();
		
		// Verificar confianza mínima (SIEMPRE aplicar este filtro)
		if (confidence < minConfidence) {
			return false;
		}
		
		// Si se ignoran los filtros de tipo, solo verificar confianza (ya verificada arriba)
		if (ignoreTypeFilters) {
			return true;
		}
		
		// Obtener tipo de objeto
		ObjectDetectionInfo.ObjectType type = ObjectDetectionInfo.getObjectType(object);
		
		// Verificar tipo de objeto según configuración (solo si no se ignoran los filtros de tipo)
		switch (type) {
			case HUMANO:
				return showHumans;
			case CONOCIDO:
				return showKnownObjects;
			case DESCONOCIDO:
				return showUnknownObjects;
			default:
				// Por defecto, mostrar si tiene confianza suficiente
				return true;
		}
	}
	
	/**
	 * Verifica si un objeto pasa el filtro de confianza (sin verificar tipo)
	 */
	public boolean passesConfidenceFilter(com.google.mlkit.vision.objects.DetectedObject object) {
		if (object == null || object.getLabels().isEmpty()) {
			return false;
		}
		float confidence = object.getLabels().get(0).getConfidence();
		return confidence >= minConfidence;
	}
}

