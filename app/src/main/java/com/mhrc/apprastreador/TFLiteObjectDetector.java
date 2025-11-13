package com.mhrc.apprastreador;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Detector de objetos usando TensorFlow Lite con modelo COCO SSD MobileNet
 * Modelo más simple, ligero y rápido para móviles
 */
public class TFLiteObjectDetector {
	private static final String TAG = "TFLiteObjectDetector";
	private static final float CONFIDENCE_THRESHOLD = 0.3f; // Umbral de confianza bajo para detectar más objetos en tiempo real
	private static final float NMS_THRESHOLD = 0.5f; // Umbral para Non-Maximum Suppression (aumentado para permitir más objetos)
	private static final int MIN_BOX_SIZE = 20; // Tamaño mínimo de bounding box en píxeles
	private static final float MIN_AREA_RATIO = 0.001f; // Área mínima relativa (0.1% de la imagen)
	
	// Umbrales moderados para objetos comúnmente mal detectados (más bajos para tiempo real)
	private static final java.util.Map<String, Float> SPECIFIC_THRESHOLDS = new java.util.HashMap<String, Float>() {{
		put("parking meter", 0.55f); // Reducido de 0.7f para detectar más
		put("fire hydrant", 0.5f);
		put("stop sign", 0.5f);
		put("traffic light", 0.45f);
		put("bench", 0.4f);
		put("umbrella", 0.45f);
		put("clock", 0.5f);
	}};
	
	private Interpreter tflite;
	private int inputImageWidth;
	private int inputImageHeight;
	private ImageProcessor imageProcessor;
	private List<String> labels;
	private Context context;
	private boolean modelLoaded = false;
	
	public interface DetectionResult {
		String getLabel();
		float getConfidence();
		Rect getBoundingBox();
	}
	
	public TFLiteObjectDetector(Context context) {
		this.context = context;
		// Intentar cargar modelo COCO SSD MobileNet
		loadModel();
	}
	
	/**
	 * Carga el modelo TensorFlow Lite COCO SSD MobileNet
	 */
	private void loadModel() {
		// Intentar cargar SSD MobileNet (modelo recomendado)
		if (loadModel("ssd_mobilenet.tflite", "coco_labels.txt")) {
			Log.d(TAG, "✅ Modelo COCO SSD MobileNet cargado exitosamente");
			return;
		}
		
		// Intentar nombres alternativos
		if (loadModel("detect.tflite", "labelmap.txt")) {
			Log.d(TAG, "✅ Modelo cargado exitosamente (detect.tflite)");
			return;
		}
		
		Log.w(TAG, "⚠️ No se encontró modelo COCO SSD MobileNet. Verifica que ssd_mobilenet.tflite y coco_labels.txt estén en app/src/main/assets/");
		modelLoaded = false;
	}
	
	/**
	 * Carga un modelo específico
	 */
	private boolean loadModel(String modelPath, String labelsPath) {
		try {
			Log.d(TAG, "Intentando cargar modelo: " + modelPath + " con etiquetas: " + labelsPath);
			
			// Cargar modelo
			MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, modelPath);
			if (modelBuffer == null) {
				Log.d(TAG, "No se encontró modelo: " + modelPath);
				return false;
			}
			Log.d(TAG, "Buffer del modelo cargado, tamaño: " + modelBuffer.capacity() + " bytes");
			
			// Crear intérprete
			Interpreter.Options options = new Interpreter.Options();
			options.setNumThreads(4);
			try {
				options.setUseNNAPI(true);
			} catch (Exception e) {
				Log.w(TAG, "NNAPI no disponible: " + e.getMessage());
			}
			try {
				options.setUseXNNPACK(true);
			} catch (Exception e) {
				Log.w(TAG, "XNNPACK no disponible: " + e.getMessage());
			}
			tflite = new Interpreter(modelBuffer, options);
			Log.d(TAG, "Intérprete creado exitosamente");
			
			// Obtener información del tensor de entrada
			int[] inputShape = tflite.getInputTensor(0).shape();
			org.tensorflow.lite.DataType inputDataType = tflite.getInputTensor(0).dataType();
			Log.d(TAG, "Forma de entrada: " + java.util.Arrays.toString(inputShape));
			Log.d(TAG, "Tipo de datos de entrada: " + inputDataType);
			
			if (inputShape.length >= 3) {
				inputImageHeight = inputShape[1];
				inputImageWidth = inputShape[2];
			} else {
				Log.e(TAG, "Forma de entrada inesperada: " + java.util.Arrays.toString(inputShape));
				return false;
			}
			
			// Configurar procesador de imágenes
			imageProcessor = new ImageProcessor.Builder()
					.add(new ResizeOp(inputImageHeight, inputImageWidth, ResizeOp.ResizeMethod.BILINEAR))
					.build();
			
			// Obtener información de los tensores de salida
			int outputTensorCount = tflite.getOutputTensorCount();
			Log.d(TAG, "Número de tensores de salida: " + outputTensorCount);
			for (int i = 0; i < outputTensorCount; i++) {
				int[] outputShape = tflite.getOutputTensor(i).shape();
				org.tensorflow.lite.DataType outputDataType = tflite.getOutputTensor(i).dataType();
				Log.d(TAG, "Salida " + i + ": forma=" + java.util.Arrays.toString(outputShape) + 
					", tipo=" + outputDataType);
			}
			
			// Cargar etiquetas
			try {
				labels = FileUtil.loadLabels(context, labelsPath);
				Log.d(TAG, "Etiquetas cargadas: " + labels.size() + " etiquetas");
				if (labels.size() > 0) {
					Log.d(TAG, "Primeras 5 etiquetas: " + 
						String.join(", ", labels.subList(0, Math.min(5, labels.size()))));
				}
			} catch (Exception e) {
				Log.w(TAG, "No se encontraron etiquetas en: " + labelsPath + ", intentando continuar sin etiquetas");
				labels = new ArrayList<>();
			}
			
			modelLoaded = true;
			
			Log.d(TAG, "✅ Modelo cargado exitosamente: " + modelPath);
			Log.d(TAG, "Dimensiones de entrada: " + inputImageWidth + "x" + inputImageHeight);
			Log.d(TAG, "Número de etiquetas: " + labels.size());
			
			return true;
			
		} catch (IOException e) {
			Log.d(TAG, "No se encontró modelo: " + modelPath + " - " + e.getMessage());
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Error al cargar modelo: " + modelPath, e);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Detecta objetos en una imagen usando COCO SSD MobileNet
	 */
	public List<DetectionResult> detect(Bitmap bitmap) {
		if (!modelLoaded || tflite == null) {
			Log.w(TAG, "Modelo no cargado o intérprete nulo");
			return new ArrayList<>();
		}
		
		try {
			// Procesar imagen
			TensorImage tensorImage = new TensorImage(org.tensorflow.lite.DataType.UINT8);
			tensorImage.load(bitmap);
			tensorImage = imageProcessor.process(tensorImage);
			
			Log.d(TAG, "Imagen procesada: " + bitmap.getWidth() + "x" + bitmap.getHeight() + 
					" -> " + inputImageWidth + "x" + inputImageHeight);
			
			// Procesar con SSD MobileNet
			return processSSDOutput(tensorImage, bitmap.getWidth(), bitmap.getHeight());
			
		} catch (Exception e) {
			Log.e(TAG, "Error en detección", e);
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	/**
	 * Procesa salida de modelo SSD MobileNet
	 * Formato SSD: locations, classes, scores (o detection_boxes, detection_classes, detection_scores)
	 */
	private List<DetectionResult> processSSDOutput(TensorImage tensorImage, int imageWidth, int imageHeight) {
		List<DetectionResult> results = new ArrayList<>();
		
		try {
			Log.d(TAG, "=== INICIO PROCESAMIENTO SSD ===");
			
			int numOutputs = tflite.getOutputTensorCount();
			Log.d(TAG, "Número de salidas del modelo: " + numOutputs);
			
			if (numOutputs < 3) {
				Log.e(TAG, "El modelo SSD debe tener al menos 3 salidas");
				return results;
			}
			
			// Crear buffers para las salidas
			TensorBuffer locationsBuffer = TensorBuffer.createFixedSize(
					tflite.getOutputTensor(0).shape(),
					tflite.getOutputTensor(0).dataType()
			);
			
			TensorBuffer classesBuffer = TensorBuffer.createFixedSize(
					tflite.getOutputTensor(1).shape(),
					tflite.getOutputTensor(1).dataType()
			);
			
			TensorBuffer scoresBuffer = TensorBuffer.createFixedSize(
					tflite.getOutputTensor(2).shape(),
					tflite.getOutputTensor(2).dataType()
			);
			
			// Ejecutar inferencia
			long startTime = System.currentTimeMillis();
			try {
				// Firma correcta: runForMultipleInputsOutputs(Object[] inputs, Map<Integer, Object> outputs)
				Object[] inputArray = new Object[1];
				inputArray[0] = tensorImage.getBuffer();
				
				java.util.Map<Integer, Object> outputsMap = new java.util.HashMap<>();
				outputsMap.put(0, locationsBuffer.getBuffer());
				outputsMap.put(1, classesBuffer.getBuffer());
				outputsMap.put(2, scoresBuffer.getBuffer());
				
				tflite.runForMultipleInputsOutputs(inputArray, outputsMap);
				
				long inferenceTime = System.currentTimeMillis() - startTime;
				Log.d(TAG, "✅ Inferencia ejecutada en " + inferenceTime + "ms");
			} catch (Exception e) {
				Log.e(TAG, "❌ Error ejecutando inferencia: " + e.getMessage(), e);
				e.printStackTrace();
				return results;
			}
			
			// Obtener información de las formas de los tensores
			int[] locationsShape = tflite.getOutputTensor(0).shape();
			int[] scoresShape = tflite.getOutputTensor(2).shape();
			int[] classesShape = tflite.getOutputTensor(1).shape();
			
			Log.d(TAG, "Formas de salida - Locations: " + java.util.Arrays.toString(locationsShape) + 
				", Classes: " + java.util.Arrays.toString(classesShape) + 
				", Scores: " + java.util.Arrays.toString(scoresShape));
			
			// Obtener arrays de datos
			float[] locations = locationsBuffer.getFloatArray();
			float[] classes = classesBuffer.getFloatArray();
			float[] scores = scoresBuffer.getFloatArray();
			
			Log.d(TAG, "Arrays obtenidos - Locations: " + locations.length + 
				", Classes: " + classes.length + 
				", Scores: " + scores.length);
			
			// Determinar el número de detecciones basado en la forma del tensor
			// SSD MobileNet puede tener formato [1, num_detections, 4] o [num_detections, 4]
			int numDetections;
			if (scoresShape.length == 2 && scoresShape[0] == 1) {
				// Formato [1, num_detections]
				numDetections = scoresShape[1];
			} else if (scoresShape.length == 1) {
				// Formato [num_detections]
				numDetections = scoresShape[0];
			} else {
				// Formato inesperado, usar longitud del array
				numDetections = scores.length;
				Log.w(TAG, "Formato de scores inesperado, usando longitud del array: " + numDetections);
			}
			
			// Limitar número de detecciones (aumentado para detectar más objetos)
			numDetections = Math.min(numDetections, 200); // Aumentado de 100 a 200
			numDetections = Math.min(numDetections, scores.length);
			
			Log.d(TAG, "Número de detecciones a procesar: " + numDetections);
			
			List<DetectionResult> rawDetections = new ArrayList<>();
			
			// Determinar el índice base para locations
			// Si locations tiene forma [1, num_detections, 4], necesitamos saltar el primer elemento
			int locationsOffset = 0;
			if (locationsShape.length == 3 && locationsShape[0] == 1) {
				locationsOffset = 0; // El array ya está aplanado
			}
			
			for (int i = 0; i < numDetections; i++) {
				// Obtener score
				int scoreIndex = i;
				if (scoresShape.length == 2 && scoresShape[0] == 1) {
					scoreIndex = i; // Ya está indexado correctamente en el array aplanado
				}
				
				if (scoreIndex >= scores.length) {
					break;
				}
				
				float score = scores[scoreIndex];
				
				// Validar score básico - más estricto
				if (score <= 0.01f || score > 1.0f || Float.isNaN(score) || Float.isInfinite(score)) {
					continue;
				}
				
				// Obtener classId
				int classIndex = i;
				if (classesShape.length == 2 && classesShape[0] == 1) {
					classIndex = i;
				}
				
				if (classIndex >= classes.length) {
					break;
				}
				
				int classId = (int) classes[classIndex];
				
				// Validar classId
				if (classId < 0) {
					continue;
				}
				
				// Ajustar classId (algunos modelos usan 1-indexed, otros 0-indexed)
				// COCO usa 1-indexed (clase 0 es background)
				if (classId == 0) {
					continue; // Ignorar background
				}
				
				if (classId >= labels.size()) {
					classId = classId - 1; // Ajustar a 0-indexed
				}
				
				if (classId < 0 || classId >= labels.size()) {
					Log.d(TAG, "ClassId fuera de rango: " + classId + " (tamaño labels: " + labels.size() + ")");
					continue;
				}
				
				String label = labels.get(classId);
				if (label == null || label.trim().isEmpty() || label.equals("???")) {
					Log.d(TAG, "Etiqueta inválida o vacía en índice: " + classId);
					continue;
				}
				
				// Aplicar umbral específico si existe
				String labelLower = label.toLowerCase().trim();
				float threshold = CONFIDENCE_THRESHOLD;
				if (SPECIFIC_THRESHOLDS.containsKey(labelLower)) {
					threshold = SPECIFIC_THRESHOLDS.get(labelLower);
					Log.d(TAG, "Aplicando umbral específico para " + label + ": " + threshold);
				}
				
				// Verificar umbral de confianza (más estricto)
				if (score < threshold) {
					Log.v(TAG, "Score bajo para " + label + ": " + String.format("%.2f", score) + " < " + threshold);
					continue;
				}
				
				// Extraer bounding box
				// Formato SSD: [ymin, xmin, ymax, xmax] normalizado (0-1)
				// Si locations tiene forma [1, num_detections, 4], el array está aplanado como [num_detections*4]
				int boxIndex;
				if (locationsShape.length == 3 && locationsShape[0] == 1) {
					// Formato [1, num_detections, 4] - aplanado
					boxIndex = i * 4;
				} else if (locationsShape.length == 2) {
					// Formato [num_detections, 4] - aplanado
					boxIndex = i * 4;
				} else {
					// Asumir formato aplanado
					boxIndex = i * 4;
				}
				
				if (boxIndex + 3 >= locations.length) {
					Log.w(TAG, "Índice de bounding box fuera de rango: " + boxIndex + " (tamaño locations: " + locations.length + ")");
					continue;
				}
				
				// Extraer coordenadas del bounding box
				// Formato SSD: [ymin, xmin, ymax, xmax] normalizado (0-1)
				float ymin_raw = locations[boxIndex];
				float xmin_raw = locations[boxIndex + 1];
				float ymax_raw = locations[boxIndex + 2];
				float xmax_raw = locations[boxIndex + 3];
				
				// Validar que las coordenadas estén en rango válido
				if (ymin_raw < 0 || xmin_raw < 0 || ymax_raw < 0 || xmax_raw < 0 ||
					ymin_raw > 1 || xmin_raw > 1 || ymax_raw > 1 || xmax_raw > 1) {
					Log.v(TAG, "Coordenadas normalizadas fuera de rango para " + label);
					continue;
				}
				
				// Validar orden de coordenadas
				if (ymin_raw >= ymax_raw || xmin_raw >= xmax_raw) {
					Log.v(TAG, "Bounding box inválido (coordenadas invertidas) para " + label);
					continue;
				}
				
				// Convertir a píxeles
				float ymin = Math.max(0.0f, Math.min(1.0f, ymin_raw)) * imageHeight;
				float xmin = Math.max(0.0f, Math.min(1.0f, xmin_raw)) * imageWidth;
				float ymax = Math.max(0.0f, Math.min(1.0f, ymax_raw)) * imageHeight;
				float xmax = Math.max(0.0f, Math.min(1.0f, xmax_raw)) * imageWidth;
				
				// Validar bounding box en píxeles
				int pixelXmin = Math.max(0, (int) xmin);
				int pixelYmin = Math.max(0, (int) ymin);
				int pixelXmax = Math.min(imageWidth, (int) xmax);
				int pixelYmax = Math.min(imageHeight, (int) ymax);
				
				int boxWidth = pixelXmax - pixelXmin;
				int boxHeight = pixelYmax - pixelYmin;
				
				// Validaciones estrictas de tamaño
				if (boxWidth <= 0 || boxHeight <= 0) {
					Log.v(TAG, "Bounding box con tamaño cero para " + label);
					continue;
				}
				
				if (boxWidth < MIN_BOX_SIZE || boxHeight < MIN_BOX_SIZE) {
					Log.v(TAG, "Bounding box muy pequeño para " + label + ": " + boxWidth + "x" + boxHeight);
					continue;
				}
				
				// Validar tamaño máximo (no puede cubrir toda la imagen)
				float boxArea = boxWidth * boxHeight;
				float totalImageArea = imageWidth * imageHeight;
				float areaRatio = boxArea / totalImageArea;
				
				if (areaRatio < MIN_AREA_RATIO) {
					Log.v(TAG, "Área muy pequeña para " + label + ": " + String.format("%.4f", areaRatio));
					continue;
				}
				
				if (areaRatio > 0.90f) {
					Log.v(TAG, "Bounding box cubre casi toda la imagen (falso positivo probable) para " + label + ": " + String.format("%.2f", areaRatio * 100) + "%");
					continue;
				}
				
				// Validar aspecto ratio razonable
				float aspectRatio = (float) boxWidth / boxHeight;
				if (aspectRatio > 8.0f || aspectRatio < 0.125f) {
					Log.v(TAG, "Aspect ratio extremo para " + label + ": " + String.format("%.2f", aspectRatio));
					continue;
				}
				
				// Validación adicional: el bounding box debe estar dentro de los límites de la imagen
				if (pixelXmin >= imageWidth || pixelYmin >= imageHeight || 
					pixelXmax <= 0 || pixelYmax <= 0) {
					Log.v(TAG, "Bounding box fuera de los límites para " + label);
					continue;
				}
				
				Rect boundingBox = new Rect(pixelXmin, pixelYmin, pixelXmax, pixelYmax);
				
				final String finalLabel = label;
				final float finalScore = score;
				final Rect finalBox = boundingBox;
				
				DetectionResult result = new DetectionResult() {
					@Override
					public String getLabel() {
						return finalLabel;
					}
					
					@Override
					public float getConfidence() {
						return finalScore;
					}
					
					@Override
					public Rect getBoundingBox() {
						return finalBox;
					}
				};
				
				rawDetections.add(result);
				Log.d(TAG, "✅ Detección cruda: " + label + " (" + String.format("%.1f", score * 100) + "%) - Tamaño: " + boxWidth + "x" + boxHeight);
			}
			
			// Aplicar Non-Maximum Suppression (NMS) para eliminar duplicados
			results = applyNMS(rawDetections);
			
			Log.d(TAG, "=== FIN PROCESAMIENTO SSD ===");
			Log.d(TAG, "Total detecciones válidas: " + results.size());
			
		} catch (Exception e) {
			Log.e(TAG, "❌ Error procesando salida SSD", e);
			e.printStackTrace();
		}
		
		// Ordenar por confianza (ya aplicado en applyNMS)
		// Limitar a las 50 mejores detecciones (aumentado para detectar más objetos simultáneamente)
		if (results.size() > 50) {
			results = results.subList(0, 50);
		}
		
		Log.d(TAG, "Total detecciones finales después de NMS: " + results.size());
		return results;
	}
	
	/**
	 * Aplica Non-Maximum Suppression (NMS) para eliminar detecciones duplicadas
	 */
	private List<DetectionResult> applyNMS(List<DetectionResult> detections) {
		if (detections.isEmpty()) {
			return detections;
		}
		
		// Ordenar por confianza descendente
		Collections.sort(detections, (a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));
		
		List<DetectionResult> filtered = new ArrayList<>();
		boolean[] suppressed = new boolean[detections.size()];
		
		for (int i = 0; i < detections.size(); i++) {
			if (suppressed[i]) {
				continue;
			}
			
			DetectionResult current = detections.get(i);
			filtered.add(current);
			
			// Suprimir detecciones que se superponen significativamente con la actual
			Rect currentBox = current.getBoundingBox();
			float currentArea = currentBox.width() * currentBox.height();
			
			for (int j = i + 1; j < detections.size(); j++) {
				if (suppressed[j]) {
					continue;
				}
				
				DetectionResult other = detections.get(j);
				Rect otherBox = other.getBoundingBox();
				
				// Calcular intersección
				int intersectLeft = Math.max(currentBox.left, otherBox.left);
				int intersectTop = Math.max(currentBox.top, otherBox.top);
				int intersectRight = Math.min(currentBox.right, otherBox.right);
				int intersectBottom = Math.min(currentBox.bottom, otherBox.bottom);
				
				if (intersectRight > intersectLeft && intersectBottom > intersectTop) {
					float intersectArea = (intersectRight - intersectLeft) * (intersectBottom - intersectTop);
					float otherArea = otherBox.width() * otherBox.height();
					
					// Calcular IoU (Intersection over Union)
					float unionArea = currentArea + otherArea - intersectArea;
					float iou = intersectArea / unionArea;
					
					// Si la superposición es alta, suprimir la detección con menor confianza
					if (iou > NMS_THRESHOLD) {
						suppressed[j] = true;
						Log.d(TAG, "Suprimiendo detección duplicada: " + other.getLabel() + " (IoU: " + String.format("%.2f", iou) + ")");
					}
				}
			}
		}
		
		return filtered;
	}
	
	public void close() {
		if (tflite != null) {
			tflite.close();
			tflite = null;
		}
		modelLoaded = false;
	}
	
	public boolean isModelLoaded() {
		return modelLoaded;
	}
}
