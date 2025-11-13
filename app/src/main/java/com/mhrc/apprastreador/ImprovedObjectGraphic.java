package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.mlkit.vision.objects.DetectedObject;

/**
 * Versión mejorada de ObjectGraphic con soporte para rectángulos suavizados
 */
public class ImprovedObjectGraphic extends GraphicOverlay.Graphic {
	private static final int TEXT_COLOR = Color.WHITE;
	private static final float TEXT_SIZE = 54.0f;
	private static final float STROKE_WIDTH = 6.0f;
	private static final float TEXT_BACKGROUND_ALPHA = 0.8f;

	private final DetectedObject object;
	private final Rect smoothedRect;
	private final Paint boxPaint;
	private final Paint textPaint;
	private final Paint textBackgroundPaint;
	private final int boxColor;
	private final String objectText;

	public ImprovedObjectGraphic(GraphicOverlay overlay, DetectedObject object, Rect smoothedRect) {
		super(overlay);
		this.object = object;
		this.smoothedRect = smoothedRect;
		
		// Obtener color y texto según el tipo de objeto
		this.boxColor = ObjectDetectionInfo.getColorForObject(object);
		this.objectText = ObjectDetectionInfo.getTextForObject(object);

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
		if (canvas == null || object == null) {
			return;
		}
		
		try {
			// Usar rectángulo suavizado si está disponible, sino usar el original
			Rect originalBox = object.getBoundingBox();
			if (originalBox == null) {
				return;
			}
			
			Rect rect = (smoothedRect != null) ? new Rect(smoothedRect) : new Rect(originalBox);
			scaleRect(rect);
			
			// Validar que el rectángulo escalado esté dentro de los límites del canvas
			if (rect.left >= rect.right || rect.top >= rect.bottom) {
				android.util.Log.w("ImprovedObjectGraphic", "Rectángulo inválido después de escalar");
				return;
			}
			
			// Validar que el rectángulo esté dentro de los límites razonables del overlay
			int overlayWidth = overlay.getWidth();
			int overlayHeight = overlay.getHeight();
			if (overlayWidth > 0 && overlayHeight > 0) {
				// Permitir un margen pequeño fuera de los límites (puede pasar por el escalado)
				if (rect.right < -50 || rect.left > overlayWidth + 50 || 
					rect.bottom < -50 || rect.top > overlayHeight + 50) {
					android.util.Log.v("ImprovedObjectGraphic", "Rectángulo fuera de límites del overlay");
					return;
				}
			}
			
			// Dibujar bounding box con el color apropiado
			canvas.drawRect(rect, boxPaint);
			
			// Calcular posición del texto
			float textX = rect.left;
			float textY = rect.top - 20;
			
			// Si el texto está muy arriba, ponerlo abajo
			if (textY < TEXT_SIZE) {
				textY = rect.bottom + TEXT_SIZE + 20;
			}
			
			// Medir el ancho del texto para el fondo
			float textWidth = textPaint.measureText(objectText);
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
			
			// Dibujar el texto
			if (objectText != null && !objectText.isEmpty()) {
				canvas.drawText(objectText, textX, textY, textPaint);
			}
			
			// Dibujar ID de seguimiento si existe
			if (object.getTrackingId() != null) {
				String trackingId = "ID: " + object.getTrackingId();
				float trackingY = rect.bottom + TEXT_SIZE + 60;
				float trackingWidth = textPaint.measureText(trackingId);
				
				// Fondo para el ID
				Rect idBackgroundRect = new Rect(
					(int)(textX - padding),
					(int)(textY + padding + 10),
					(int)(textX + trackingWidth + padding),
					(int)(trackingY + padding)
				);
				canvas.drawRect(idBackgroundRect, textBackgroundPaint);
				canvas.drawText(trackingId, textX, trackingY, textPaint);
			}
		} catch (Exception e) {
			android.util.Log.e("ImprovedObjectGraphic", "Error dibujando objeto: " + objectText + ", error: " + e.getMessage(), e);
		}
	}
}

