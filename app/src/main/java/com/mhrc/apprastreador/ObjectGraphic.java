package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.mlkit.vision.objects.DetectedObject;

public class ObjectGraphic extends GraphicOverlay.Graphic {
	private static final int TEXT_COLOR = Color.WHITE;
	private static final int BOX_COLOR = Color.GREEN;
	private static final float TEXT_SIZE = 54.0f;
	private static final float STROKE_WIDTH = 4.0f;

	private final DetectedObject object;
	private final Paint boxPaint;
	private final Paint textPaint;

	public ObjectGraphic(GraphicOverlay overlay, DetectedObject object) {
		super(overlay);
		this.object = object;

		boxPaint = new Paint();
		boxPaint.setColor(BOX_COLOR);
		boxPaint.setStyle(Paint.Style.STROKE);
		boxPaint.setStrokeWidth(STROKE_WIDTH);

		textPaint = new Paint();
		textPaint.setColor(TEXT_COLOR);
		textPaint.setTextSize(TEXT_SIZE);
	}

	@Override
	public void draw(Canvas canvas) {
		Rect rect = new Rect(object.getBoundingBox());
		scaleRect(rect);
		canvas.drawRect(rect, boxPaint);

		// Dibujar etiquetas si existen
		if (!object.getLabels().isEmpty()) {
			String label = object.getLabels().get(0).getText();
			float confidence = object.getLabels().get(0).getConfidence();
			String text = label + " (" + String.format("%.0f%%", confidence * 100) + ")";
			
			float x = rect.left;
			float y = rect.top - 10;
			if (y < 0) {
				y = rect.bottom + TEXT_SIZE;
			}
			canvas.drawText(text, x, y, textPaint);
		} else {
			// Objeto detectado pero no clasificado
			String text = "Objeto detectado";
			float x = rect.left;
			float y = rect.top - 10;
			if (y < 0) {
				y = rect.bottom + TEXT_SIZE;
			}
			canvas.drawText(text, x, y, textPaint);
		}

		// Dibujar ID de seguimiento si existe
		if (object.getTrackingId() != null) {
			String trackingId = "ID: " + object.getTrackingId();
			canvas.drawText(trackingId, rect.left, rect.bottom + TEXT_SIZE, textPaint);
		}
	}
}

