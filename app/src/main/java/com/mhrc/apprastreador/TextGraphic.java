package com.mhrc.apprastreador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.mlkit.vision.text.Text;

public class TextGraphic extends GraphicOverlay.Graphic {
	private static final float TEXT_SIZE = 40.0f;
	private static final float STROKE_WIDTH = 4.0f;
	private static final int BOX_COLOR = Color.YELLOW;
	
	private final Text.TextBlock textBlock;
	private final Paint boxPaint;
	private final Paint textPaint;
	private final Paint textBackgroundPaint;

	public TextGraphic(GraphicOverlay overlay, Text.TextBlock textBlock) {
		super(overlay);
		this.textBlock = textBlock;

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
		textBackgroundPaint.setAlpha(180);
		textBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void draw(Canvas canvas) {
		Rect rect = textBlock.getBoundingBox();
		Rect scaledRect = new Rect(rect);
		scaleRect(scaledRect);
		
		// Dibujar bounding box
		canvas.drawRect(scaledRect, boxPaint);
		
		// Dibujar texto reconocido
		String text = textBlock.getText();
		if (text != null && !text.isEmpty()) {
			float textX = scaledRect.left;
			float textY = scaledRect.top - 10;
			
			if (textY < TEXT_SIZE) {
				textY = scaledRect.bottom + TEXT_SIZE + 10;
			}
			
			float textWidth = textPaint.measureText(text);
			float textHeight = textPaint.getTextSize();
			float padding = 15f;
			
			Rect textBackgroundRect = new Rect(
				(int)(textX - padding),
				(int)(textY - textHeight - padding),
				(int)(textX + textWidth + padding),
				(int)(textY + padding)
			);
			
			canvas.drawRect(textBackgroundRect, textBackgroundPaint);
			canvas.drawText(text, textX, textY, textPaint);
		}
	}
}

