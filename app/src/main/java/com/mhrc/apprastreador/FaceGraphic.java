package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.mlkit.vision.face.Face;

public class FaceGraphic extends GraphicOverlay.Graphic {
	private static final float TEXT_SIZE = 54.0f;
	private static final float STROKE_WIDTH = 6.0f;
	private static final int BOX_COLOR = Color.BLUE;
	
	private final Face face;
	private final Paint boxPaint;
	private final Paint textPaint;
	private final Paint textBackgroundPaint;

	public FaceGraphic(GraphicOverlay overlay, Face face) {
		super(overlay);
		this.face = face;

		boxPaint = new Paint();
		boxPaint.setColor(BOX_COLOR);
		boxPaint.setStyle(Paint.Style.STROKE);
		boxPaint.setStrokeWidth(STROKE_WIDTH);
		boxPaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(TEXT_SIZE);
		textPaint.setAntiAlias(true);
		textPaint.setFakeBoldText(true);
		
		textBackgroundPaint = new Paint();
		textBackgroundPaint.setColor(BOX_COLOR);
		textBackgroundPaint.setAlpha(200);
		textBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void draw(Canvas canvas) {
		Rect rect = face.getBoundingBox();
		Rect scaledRect = new Rect(rect);
		scaleRect(scaledRect);
		
		// Dibujar bounding box
		canvas.drawRect(scaledRect, boxPaint);
		
		// Construir información del rostro
		StringBuilder faceInfo = new StringBuilder();
		
		// Edad
		if (face.getTrackingId() != null) {
			faceInfo.append("ID: ").append(face.getTrackingId()).append("\n");
		}
		
		// Nota: ML Kit Face Detection no proporciona edad y género directamente
		// Se necesita un modelo adicional o API externa para esto
		// Por ahora mostraremos información disponible
		if (face.getSmilingProbability() != null) {
			float smilingProb = face.getSmilingProbability();
			faceInfo.append("Sonrisa: ").append(String.format("%.0f%%", smilingProb * 100));
		}
		
		if (face.getLeftEyeOpenProbability() != null && face.getRightEyeOpenProbability() != null) {
			float leftEye = face.getLeftEyeOpenProbability();
			float rightEye = face.getRightEyeOpenProbability();
			faceInfo.append("\nOjos abiertos: ").append(String.format("%.0f%%", ((leftEye + rightEye) / 2) * 100));
		}
		
		// Dibujar texto
		if (faceInfo.length() > 0) {
			String text = faceInfo.toString();
			float textX = scaledRect.left;
			float textY = scaledRect.top - 20;
			
			if (textY < TEXT_SIZE) {
				textY = scaledRect.bottom + TEXT_SIZE + 20;
			}
			
			float textWidth = textPaint.measureText(text.split("\n")[0]);
			float textHeight = textPaint.getTextSize();
			float padding = 20f;
			
			Rect textBackgroundRect = new Rect(
				(int)(textX - padding),
				(int)(textY - textHeight - padding),
				(int)(textX + textWidth + padding),
				(int)(textY + (text.split("\n").length * textHeight) + padding)
			);
			
			canvas.drawRect(textBackgroundRect, textBackgroundPaint);
			
			float yOffset = textY;
			for (String line : text.split("\n")) {
				canvas.drawText(line, textX, yOffset, textPaint);
				yOffset += textHeight + 10;
			}
		}
	}
}

