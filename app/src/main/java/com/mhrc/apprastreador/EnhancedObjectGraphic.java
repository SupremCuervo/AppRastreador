package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.mlkit.vision.objects.DetectedObject;

/**
 * Gráfico mejorado que muestra etiquetas y confianza mejoradas
 */
public class EnhancedObjectGraphic extends GraphicOverlay.Graphic {
	private static final int TEXT_COLOR = Color.WHITE;
	private static final float TEXT_SIZE = 54.0f;
	private static final float STROKE_WIDTH = 6.0f;
	private static final float TEXT_BACKGROUND_ALPHA = 0.8f;

	private final DetectedObject object;
	private final String enhancedLabel;
	private final float enhancedConfidence;
	private final Rect smoothedRect;
	private final Paint boxPaint;
	private final Paint textPaint;
	private final Paint textBackgroundPaint;
	private final int boxColor;

	public EnhancedObjectGraphic(GraphicOverlay overlay, DetectedObject object, 
			String enhancedLabel, float enhancedConfidence, Rect smoothedRect) {
		super(overlay);
		this.object = object;
		this.enhancedLabel = enhancedLabel;
		this.enhancedConfidence = enhancedConfidence;
		this.smoothedRect = smoothedRect;
		
		// Obtener color según el tipo de objeto
		this.boxColor = ObjectDetectionInfo.getColorForObject(object);

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

	@Override
	public void draw(Canvas canvas) {
		// Usar rectángulo suavizado si está disponible
		Rect rect = (smoothedRect != null) ? new Rect(smoothedRect) : new Rect(object.getBoundingBox());
		scaleRect(rect);
		
		// Dibujar bounding box
		canvas.drawRect(rect, boxPaint);
		
		// Construir texto con etiqueta mejorada
		String text = enhancedLabel;
		if (enhancedConfidence > 0) {
			text += " (" + String.format("%.0f%%", enhancedConfidence * 100) + ")";
		}
		
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
		
		// Dibujar ID de seguimiento si existe
		if (object.getTrackingId() != null) {
			String trackingId = "ID: " + object.getTrackingId();
			float trackingY = rect.bottom + TEXT_SIZE + 60;
			float trackingWidth = textPaint.measureText(trackingId);
			
			Rect idBackgroundRect = new Rect(
				(int)(textX - padding),
				(int)(textY + padding + 10),
				(int)(textX + trackingWidth + padding),
				(int)(trackingY + padding)
			);
			canvas.drawRect(idBackgroundRect, textBackgroundPaint);
			canvas.drawText(trackingId, textX, trackingY, textPaint);
		}
	}
}

