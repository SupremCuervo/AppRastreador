package com.mhrc.apprastreador;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class GraphicOverlay extends View {
	private final List<Graphic> graphics = new ArrayList<>();
	private float scaleFactor = 1.0f;
	private int imageWidth;
	private int imageHeight;
	private int imageOffsetX;
	private int imageOffsetY;

	public GraphicOverlay(Context context) {
		super(context);
		setupOverlay();
	}

	public GraphicOverlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupOverlay();
	}
	
	private void setupOverlay() {
		// Asegurar que el overlay sea completamente transparente
		setBackgroundColor(android.graphics.Color.TRANSPARENT);
		// Asegurar que el overlay no capture eventos táctiles (pasar eventos a la vista debajo)
		setClickable(false);
		setFocusable(false);
	}

	public void clear() {
		graphics.clear();
		postInvalidate();
	}

	public void add(Graphic graphic) {
		if (graphic != null) {
			graphics.add(graphic);
		}
	}
	
	/**
	 * Actualiza la vista después de agregar todos los gráficos
	 */
	public void update() {
		postInvalidate();
	}
	
	/**
	 * Obtiene el número de gráficos en el overlay
	 */
	public int getGraphicCount() {
		return graphics.size();
	}

	public void setImageSourceInfo(int imageWidth, int imageHeight, boolean isFlipped) {
		if (imageWidth > 0 && imageHeight > 0) {
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			updateTransformation();
		}
	}

	private void updateTransformation() {
		if (imageWidth <= 0 || imageHeight <= 0) {
			return;
		}
		
		float viewAspectRatio = (float) getWidth() / getHeight();
		float imageAspectRatio = (float) imageWidth / imageHeight;
		
		if (viewAspectRatio > imageAspectRatio) {
			// La vista es más ancha que la imagen
			scaleFactor = (float) getHeight() / imageHeight;
			imageOffsetX = (int) ((getWidth() - imageWidth * scaleFactor) / 2);
			imageOffsetY = 0;
		} else {
			// La vista es más alta que la imagen
			scaleFactor = (float) getWidth() / imageWidth;
			imageOffsetX = 0;
			imageOffsetY = (int) ((getHeight() - imageHeight * scaleFactor) / 2);
		}
		
		postInvalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		updateTransformation();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Validar que el canvas y las dimensiones sean válidas
		if (canvas == null) {
			android.util.Log.w("GraphicOverlay", "Canvas es null en onDraw");
			return;
		}
		
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		
		if (viewWidth <= 0 || viewHeight <= 0) {
			android.util.Log.w("GraphicOverlay", "Dimensiones de vista inválidas: " + viewWidth + "x" + viewHeight);
			return;
		}
		
		// Validar que tengamos dimensiones de imagen válidas
		if (imageWidth <= 0 || imageHeight <= 0) {
			android.util.Log.w("GraphicOverlay", "Dimensiones de imagen no configuradas: " + imageWidth + "x" + imageHeight);
			// No retornar, intentar dibujar de todas formas (puede que aún no se hayan configurado)
		}
		
		// Dibujar todos los gráficos
		int drawnCount = 0;
		for (Graphic graphic : graphics) {
			if (graphic != null) {
				try {
					graphic.draw(canvas);
					drawnCount++;
				} catch (Exception e) {
					android.util.Log.e("GraphicOverlay", "Error dibujando gráfico: " + e.getMessage(), e);
				}
			}
		}
		
		if (drawnCount > 0) {
			android.util.Log.i("GraphicOverlay", "✅ Dibujados " + drawnCount + " gráficos en canvas (" + viewWidth + "x" + viewHeight + 
				"), imagen: " + imageWidth + "x" + imageHeight);
		} else if (graphics.size() > 0) {
			android.util.Log.w("GraphicOverlay", "⚠️ Hay " + graphics.size() + " gráficos pero no se dibujó ninguno");
		}
	}

	public abstract static class Graphic {
		protected GraphicOverlay overlay;

		public Graphic(GraphicOverlay overlay) {
			this.overlay = overlay;
		}

		public abstract void draw(Canvas canvas);

		protected float translateX(float x) {
			return x * overlay.scaleFactor + overlay.imageOffsetX;
		}

		protected float translateY(float y) {
			return y * overlay.scaleFactor + overlay.imageOffsetY;
		}

		protected void scaleRect(android.graphics.Rect rect) {
			rect.left = (int) translateX(rect.left);
			rect.top = (int) translateY(rect.top);
			rect.right = (int) translateX(rect.right);
			rect.bottom = (int) translateY(rect.bottom);
		}
	}
}

