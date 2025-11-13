package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.mhrc.apprastreador.ObjectDetectionInfo;

/**
 * Gráfico para objetos detectados con TensorFlow Lite
 */
public class TFLiteObjectGraphic extends GraphicOverlay.Graphic {
	private static final int TEXT_COLOR = Color.WHITE;
	private static final float TEXT_SIZE = 54.0f;
	private static final float STROKE_WIDTH = 6.0f;
	private static final float TEXT_BACKGROUND_ALPHA = 0.8f;

	private final String label;
	private final float confidence;
	private final Rect boundingBox;
	private final Paint boxPaint;
	private final Paint textPaint;
	private final Paint textBackgroundPaint;
	private final int boxColor;

	public TFLiteObjectGraphic(GraphicOverlay overlay, String label, float confidence, Rect boundingBox) {
		super(overlay);
		this.label = label;
		this.confidence = confidence;
		this.boundingBox = boundingBox;
		
		// Determinar color según el tipo de objeto
		this.boxColor = getColorForLabel(label);

		// Paint para el bounding box
		boxPaint = new Paint();
		boxPaint.setColor(boxColor);
		boxPaint.setStyle(Paint.Style.STROKE);
		boxPaint.setStrokeWidth(STROKE_WIDTH);
		boxPaint.setAntiAlias(true);

		// Paint para el texto
		textPaint = new Paint();
		textPaint.setColor(TEXT_COLOR);
		textPaint.setTextSize(TEXT_SIZE);
		textPaint.setAntiAlias(true);
		textPaint.setFakeBoldText(true);
		
		// Paint para el fondo del texto
		textBackgroundPaint = new Paint();
		textBackgroundPaint.setColor(boxColor);
		textBackgroundPaint.setAlpha((int)(255 * TEXT_BACKGROUND_ALPHA));
		textBackgroundPaint.setStyle(Paint.Style.FILL);
	}
	
	/**
	 * Obtiene el color según la etiqueta del objeto
	 */
	private int getColorForLabel(String label) {
		// Usar ObjectDetectionInfo para determinar si es conocido
		String lowerLabel = label.toLowerCase().trim();
		
		// Humano
		if (lowerLabel.equals("person") || lowerLabel.contains("person")) {
			return ObjectDetectionInfo.COLOR_HUMANO;
		}
		
		// Verificar si es un objeto conocido usando ObjectDetectionInfo
		if (ObjectDetectionInfo.isKnownLabel(label)) {
			return ObjectDetectionInfo.COLOR_OBJETO_CONOCIDO;
		}
		
		// Objetos desconocidos
		return ObjectDetectionInfo.COLOR_OBJETO_DESCONOCIDO;
	}

	@Override
	public void draw(Canvas canvas) {
		Rect rect = new Rect(boundingBox);
		scaleRect(rect);
		
		// Dibujar bounding box
		canvas.drawRect(rect, boxPaint);
		
		// Construir texto con etiqueta traducida y confianza
		String translatedLabel = translateLabel(label);
		String text = translatedLabel + " (" + String.format("%.0f%%", confidence * 100) + ")";
		
		// Calcular posición del texto
		float textX = rect.left;
		float textY = rect.top - 20;
		
		if (textY < TEXT_SIZE) {
			textY = rect.bottom + TEXT_SIZE + 20;
		}
		
		// Medir ancho del texto
		float textWidth = textPaint.measureText(text);
		float textHeight = textPaint.getTextSize();
		float padding = 20f;
		
		// Dibujar fondo del texto
		Rect textBackgroundRect = new Rect(
			(int)(textX - padding),
			(int)(textY - textHeight - padding),
			(int)(textX + textWidth + padding),
			(int)(textY + padding)
		);
		canvas.drawRect(textBackgroundRect, textBackgroundPaint);
		
		// Dibujar texto
		canvas.drawText(text, textX, textY, textPaint);
	}
	
	/**
	 * Traduce etiquetas en inglés a español usando ObjectDetectionInfo
	 */
	private String translateLabel(String label) {
		// Usar ObjectDetectionInfo para obtener la traducción
		// Crear un objeto temporal para usar getTextForObject
		return ObjectDetectionInfo.getTextForLabel(label);
	}
}

