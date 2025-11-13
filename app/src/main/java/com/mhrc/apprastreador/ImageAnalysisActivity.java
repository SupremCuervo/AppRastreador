package com.mhrc.apprastreador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import java.io.IOException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import android.graphics.BitmapFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.face.Face;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageAnalysisActivity extends AppCompatActivity {
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 1003;
	private static final int PICK_IMAGE_REQUEST = 1004;
	private static final int STORAGE_PERMISSION_REQUEST_CODE = 1005;

	private PreviewView previewView;
	private ImageView imageView;
	private GraphicOverlay graphicOverlay;
	private ImageButton btnBackToMenu;
	private Button btnSwitchCamera;
	private Button btnTakePhoto;
	private Button btnSelectGallery;
	private Button btnAnalyze;
	private Spinner spinnerMode;
	
	private ExecutorService cameraExecutor;
	private ObjectDetectorHelper objectDetectorHelper;
	private TextRecognitionHelper textRecognitionHelper;
	private FaceDetectionHelper faceDetectionHelper;
	private boolean isImageSourceInfoSet = false;
	
	private String currentMode = "OBJETO";
	private int cameraFacing = CameraSelector.LENS_FACING_BACK;
	private Camera camera;
	private ProcessCameraProvider cameraProvider;
	private ImageCapture imageCapture;
	private ImageProxy lastImageProxy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_analysis);

		previewView = findViewById(R.id.previewView);
		imageView = findViewById(R.id.imageView);
		graphicOverlay = findViewById(R.id.graphicOverlay);
		btnBackToMenu = findViewById(R.id.btnBackToMenu);
		btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
		btnTakePhoto = findViewById(R.id.btnTakePhoto);
		btnSelectGallery = findViewById(R.id.btnSelectGallery);
		btnAnalyze = findViewById(R.id.btnAnalyze);
		spinnerMode = findViewById(R.id.spinnerMode);
		
		// Configurar spinner de modos
		setupModeSpinner();
		
		// Configurar botones
		setupButtons();
		
		// Inicializar helpers
		initializeHelpers();

		if (checkCameraPermission()) {
			startCamera();
		} else {
			requestCameraPermission();
		}
	}

	private void setupModeSpinner() {
		String[] modes = {"OBJETO", "HUMANO", "TEXTO"};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
			android.R.layout.simple_spinner_item, modes) {
			@Override
			public View getView(int position, View convertView, android.view.ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setTextColor(ContextCompat.getColor(ImageAnalysisActivity.this, android.R.color.white));
				return view;
			}
			
			@Override
			public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setTextColor(ContextCompat.getColor(ImageAnalysisActivity.this, android.R.color.white));
				view.setBackgroundResource(R.drawable.spinner_dropdown_background);
				return view;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinnerMode.setAdapter(adapter);
		
		spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentMode = modes[position];
				updateMode();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void updateMode() {
		graphicOverlay.clear();
		isImageSourceInfoSet = false;
		// Actualizar helpers para el nuevo modo
		updateHelpers();
		// Reiniciar cámara con nuevo modo
		if (cameraProvider != null) {
			startCamera();
		}
	}

	private void setupButtons() {
		// Botón volver al menú
		btnBackToMenu.setOnClickListener(v -> {
			Intent intent = new Intent(ImageAnalysisActivity.this, MainMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		});
		
		// Botón cambiar cámara
		btnSwitchCamera.setOnClickListener(v -> switchCamera());
		
		// Botón tomar foto
		btnTakePhoto.setOnClickListener(v -> takePhoto());
		
		// Botón seleccionar de galería
		btnSelectGallery.setOnClickListener(v -> {
			if (checkStoragePermission()) {
				selectImageFromGallery();
			} else {
				requestStoragePermission();
			}
		});
		
		// Botón analizar
		btnAnalyze.setOnClickListener(v -> {
			if (lastImageProxy != null) {
				analyzeCurrentFrame();
			} else {
				Toast.makeText(this, "Esperando frame de la cámara...", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initializeHelpers() {
		cameraExecutor = Executors.newSingleThreadExecutor();
		
		// Inicializar helpers según el modo actual
		updateHelpers();
	}

	private void updateHelpers() {
		// Cerrar helpers anteriores
		if (objectDetectorHelper != null) {
			objectDetectorHelper.close();
			objectDetectorHelper = null;
		}
		if (textRecognitionHelper != null) {
			textRecognitionHelper.close();
			textRecognitionHelper = null;
		}
		if (faceDetectionHelper != null) {
			faceDetectionHelper.close();
			faceDetectionHelper = null;
		}
		
		// Inicializar helper según el modo
		if ("TEXTO".equals(currentMode)) {
			textRecognitionHelper = new TextRecognitionHelper();
			textRecognitionHelper.setListener(new TextRecognitionHelper.TextRecognitionListener() {
				@Override
				public void onTextRecognized(List<Text.TextBlock> textBlocks) {
					runOnUiThread(() -> {
						graphicOverlay.clear();
						for (Text.TextBlock textBlock : textBlocks) {
							TextGraphic graphic = new TextGraphic(graphicOverlay, textBlock);
							graphicOverlay.add(graphic);
						}
						graphicOverlay.update();
					});
				}

				@Override
				public void onTextRecognitionFailure(Exception e) {
					Log.e("ImageAnalysisActivity", "Error en reconocimiento de texto", e);
				}
			});
		} else if ("HUMANO".equals(currentMode)) {
			faceDetectionHelper = new FaceDetectionHelper();
			faceDetectionHelper.setListener(new FaceDetectionHelper.FaceDetectionListener() {
				@Override
				public void onFaceDetected(List<Face> faces) {
					runOnUiThread(() -> {
						graphicOverlay.clear();
						for (Face face : faces) {
							FaceGraphic graphic = new FaceGraphic(graphicOverlay, face);
							graphicOverlay.add(graphic);
						}
						graphicOverlay.update();
					});
				}

				@Override
				public void onFaceDetectionFailure(Exception e) {
					Log.e("ImageAnalysisActivity", "Error en detección de caras", e);
				}
			});
		} else {
			// Modo OBJETO
			objectDetectorHelper = new ObjectDetectorHelper();
			objectDetectorHelper.setListener(new ObjectDetectorHelper.DetectionListener() {
				@Override
				public void onDetectionSuccess(List<DetectedObject> detectedObjects) {
					runOnUiThread(() -> {
						graphicOverlay.clear();
						for (DetectedObject object : detectedObjects) {
							ObjectGraphic graphic = new ObjectGraphic(graphicOverlay, object);
							graphicOverlay.add(graphic);
						}
						graphicOverlay.update();
					});
				}

				@Override
				public void onDetectionFailure(Exception e) {
					Log.e("ImageAnalysisActivity", "Error en detección de objetos", e);
				}
			});
		}
	}
	
	private void switchCamera() {
		if (cameraProvider == null) return;
		
		// Cambiar entre cámara frontal y trasera
		cameraFacing = (cameraFacing == CameraSelector.LENS_FACING_BACK) 
			? CameraSelector.LENS_FACING_FRONT 
			: CameraSelector.LENS_FACING_BACK;
		
		// Reiniciar cámara con nueva orientación
		isImageSourceInfoSet = false;
		startCamera();
	}

	private boolean checkCameraPermission() {
		return ContextCompat.checkSelfPermission(
				this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestCameraPermission() {
		ActivityCompat.requestPermissions(
				this,
				new String[]{Manifest.permission.CAMERA},
				CAMERA_PERMISSION_REQUEST_CODE
		);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startCamera();
			} else {
				Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				selectImageFromGallery();
			} else {
				Toast.makeText(this, "Se necesita permiso para acceder a las imágenes", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean checkStoragePermission() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
			return ContextCompat.checkSelfPermission(
					this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
		} else {
			return ContextCompat.checkSelfPermission(
					this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		}
	}

	private void requestStoragePermission() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.READ_MEDIA_IMAGES},
					STORAGE_PERMISSION_REQUEST_CODE
			);
		} else {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					STORAGE_PERMISSION_REQUEST_CODE
			);
		}
	}

	private void selectImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
				Uri imageUri = data.getData();
				try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
				
				if (bitmap != null) {
					// Ocultar preview de cámara y mostrar imagen seleccionada
					previewView.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageBitmap(bitmap);
					
					// Configurar overlay con las dimensiones de la imagen
					graphicOverlay.setImageSourceInfo(
						bitmap.getWidth(),
						bitmap.getHeight(),
						false
					);
					
					// Analizar la imagen seleccionada
					analyzeBitmap(bitmap);
				} else {
					Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
				Log.e("ImageAnalysisActivity", "Error al cargar imagen de galería", e);
				Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void startCamera() {
		ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
				ProcessCameraProvider.getInstance(this);

		cameraProviderFuture.addListener(() -> {
			try {
				cameraProvider = cameraProviderFuture.get();

				Preview preview = new Preview.Builder().build();
				preview.setSurfaceProvider(previewView.getSurfaceProvider());

				CameraSelector cameraSelector = new CameraSelector.Builder()
						.requireLensFacing(cameraFacing)
						.build();

				// ImageCapture para tomar fotos
				imageCapture = new ImageCapture.Builder()
						.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
						.build();

				ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
						.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
						.setTargetResolution(new android.util.Size(480, 360))
						.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
						.build();

				imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
					// Guardar último frame para análisis
					// Cerrar el frame anterior si existe
					synchronized (this) {
						if (lastImageProxy != null) {
							lastImageProxy.close();
						}
						lastImageProxy = imageProxy;
					}
					
					// Configurar dimensiones de la imagen en el overlay
					if (!isImageSourceInfoSet) {
						ContextCompat.getMainExecutor(ImageAnalysisActivity.this).execute(() -> {
			graphicOverlay.setImageSourceInfo(
									imageProxy.getWidth(),
									imageProxy.getHeight(),
					false
			);
							graphicOverlay.post(() -> {
								int overlayWidth = graphicOverlay.getWidth();
								int overlayHeight = graphicOverlay.getHeight();
								if (overlayWidth > 0 && overlayHeight > 0) {
									isImageSourceInfoSet = true;
								}
							});
						});
					}
					
					// No procesar automáticamente, solo guardar el frame
					// El usuario presionará "Analizar" para procesar
					// No cerrar el ImageProxy aquí, se cerrará cuando se analice o se reemplace
				});

				// Desvincular cámaras anteriores
				cameraProvider.unbindAll();
				
				camera = cameraProvider.bindToLifecycle(
						this, cameraSelector, preview, imageAnalysis, imageCapture);

				} catch (Exception e) {
				Log.e("ImageAnalysisActivity", "Error iniciando cámara: " + e.getMessage(), e);
			}
		}, ContextCompat.getMainExecutor(this));
	}

	private void takePhoto() {
		if (imageCapture == null) {
			Toast.makeText(this, "Cámara no lista", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Crear archivo para la foto
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(new java.util.Date());
		File photoFile = new File(getExternalFilesDir(null), timeStamp + ".jpg");
		
		ImageCapture.OutputFileOptions outputOptions = 
			new ImageCapture.OutputFileOptions.Builder(photoFile).build();
		
		imageCapture.takePicture(
			outputOptions,
			ContextCompat.getMainExecutor(this),
			new ImageCapture.OnImageSavedCallback() {
				@Override
				public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
					// Cargar la foto y analizarla automáticamente
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
					
					if (bitmap != null) {
						// Ocultar preview de cámara y mostrar foto capturada
						previewView.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(bitmap);
						
						// Configurar overlay con las dimensiones de la foto
						graphicOverlay.setImageSourceInfo(
							bitmap.getWidth(),
							bitmap.getHeight(),
							false
						);
						
						// Analizar la imagen capturada
						analyzeBitmap(bitmap);
					} else {
						Toast.makeText(ImageAnalysisActivity.this, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
					}
				}
				
				@Override
				public void onError(@NonNull ImageCaptureException exception) {
					Log.e("ImageAnalysisActivity", "Error al capturar foto: " + exception.getMessage(), exception);
					Toast.makeText(ImageAnalysisActivity.this, "Error al capturar foto: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		);
	}

	private void analyzeBitmap(Bitmap bitmap) {
		graphicOverlay.clear();
		
		com.google.mlkit.vision.common.InputImage image = 
			com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, 0);
		
		if ("HUMANO".equals(currentMode)) {
		com.google.mlkit.vision.face.FaceDetectorOptions options = 
			new com.google.mlkit.vision.face.FaceDetectorOptions.Builder()
				.setPerformanceMode(com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
				.setLandmarkMode(com.google.mlkit.vision.face.FaceDetectorOptions.LANDMARK_MODE_ALL)
				.setClassificationMode(com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
				.setMinFaceSize(0.1f)
				.build();
		
		com.google.mlkit.vision.face.FaceDetector faceDetector = 
			com.google.mlkit.vision.face.FaceDetection.getClient(options);
		
		faceDetector.process(image)
			.addOnSuccessListener(faces -> {
				runOnUiThread(() -> {
					graphicOverlay.clear();
					for (Face face : faces) {
						FaceGraphic graphic = new FaceGraphic(graphicOverlay, face);
						graphicOverlay.add(graphic);
					}
						graphicOverlay.update();
				});
			})
			.addOnFailureListener(e -> {
				Log.e("ImageAnalysisActivity", "Error en detección de caras", e);
				});
		} else if ("TEXTO".equals(currentMode)) {
			com.google.mlkit.vision.text.latin.TextRecognizerOptions options = 
				new com.google.mlkit.vision.text.latin.TextRecognizerOptions.Builder().build();
			com.google.mlkit.vision.text.TextRecognizer textRecognizer = 
				com.google.mlkit.vision.text.TextRecognition.getClient(options);
		
		textRecognizer.process(image)
			.addOnSuccessListener(text -> {
				runOnUiThread(() -> {
					graphicOverlay.clear();
					for (Text.TextBlock textBlock : text.getTextBlocks()) {
						TextGraphic graphic = new TextGraphic(graphicOverlay, textBlock);
						graphicOverlay.add(graphic);
					}
						graphicOverlay.update();
				});
			})
			.addOnFailureListener(e -> {
				Log.e("ImageAnalysisActivity", "Error en reconocimiento de texto", e);
				});
		} else {
			// Modo OBJETO
			com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions options = 
				new com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions.Builder()
					.setDetectorMode(com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions.SINGLE_IMAGE_MODE)
				.enableMultipleObjects()
				.enableClassification()
				.build();
		
			com.google.mlkit.vision.objects.ObjectDetector detector = 
				com.google.mlkit.vision.objects.ObjectDetection.getClient(options);
		
				detector.process(image)
			.addOnSuccessListener(detectedObjects -> {
				runOnUiThread(() -> {
					graphicOverlay.clear();
					for (DetectedObject object : detectedObjects) {
							ObjectGraphic graphic = new ObjectGraphic(graphicOverlay, object);
							graphicOverlay.add(graphic);
						}
						graphicOverlay.update();
				});
			})
			.addOnFailureListener(e -> {
				Log.e("ImageAnalysisActivity", "Error en detección de objetos", e);
				});
		}
	}

	private void analyzeCurrentFrame() {
		ImageProxy imageProxyToProcess = null;
		
		// Obtener el frame de forma thread-safe
		synchronized (this) {
			if (lastImageProxy == null) {
				runOnUiThread(() -> {
					Toast.makeText(this, "No hay frame disponible", Toast.LENGTH_SHORT).show();
				});
				return;
			}
			imageProxyToProcess = lastImageProxy;
			lastImageProxy = null; // Limpiar para que el siguiente frame se guarde
		}
		
		graphicOverlay.clear();
		
		// Los helpers cerrarán el ImageProxy después de procesarlo
		if ("HUMANO".equals(currentMode) && faceDetectionHelper != null) {
			faceDetectionHelper.processImageProxy(imageProxyToProcess);
		} else if ("TEXTO".equals(currentMode) && textRecognitionHelper != null) {
			textRecognitionHelper.processImageProxy(imageProxyToProcess);
		} else if ("OBJETO".equals(currentMode) && objectDetectorHelper != null) {
			objectDetectorHelper.processImageProxy(imageProxyToProcess);
		} else {
			// Si no hay helper, cerrar el ImageProxy manualmente
			imageProxyToProcess.close();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (objectDetectorHelper != null) {
			objectDetectorHelper.close();
		}
		if (textRecognitionHelper != null) {
			textRecognitionHelper.close();
		}
		if (faceDetectionHelper != null) {
			faceDetectionHelper.close();
		}
		if (cameraExecutor != null) {
			cameraExecutor.shutdown();
		}
		if (lastImageProxy != null) {
			lastImageProxy.close();
		}
	}
}
