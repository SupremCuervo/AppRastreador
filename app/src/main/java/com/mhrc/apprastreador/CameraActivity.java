package com.mhrc.apprastreador;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import android.util.Log;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.face.Face;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

	private PreviewView previewView;
	private GraphicOverlay graphicOverlay;
	private ImageButton btnBackToMenu;
	private android.widget.Button btnHelp;
	private ImageButton btnFilters;
	private ImageButton btnSwitchCamera;
	
	private ExecutorService cameraExecutor;
	private ObjectDetectorHelper objectDetectorHelper;
	private TextRecognitionHelper textRecognitionHelper;
	private FaceDetectionHelper faceDetectionHelper;
	private boolean isImageSourceInfoSet = false;
	
	private String currentMode = "OBJETO";
	private int cameraFacing = CameraSelector.LENS_FACING_BACK;
	private Camera camera;
	private ProcessCameraProvider cameraProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			currentMode = extras.getString("MODE", "OBJETO");
		}

		previewView = findViewById(R.id.previewView);
		graphicOverlay = findViewById(R.id.graphicOverlay);
		btnBackToMenu = findViewById(R.id.btnBackToMenu);
		btnHelp = findViewById(R.id.btnHelp);
		btnFilters = findViewById(R.id.btnFilters);
		btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
		
		initializeHelpers();
		setupButtons();

		if (checkCameraPermission()) {
			startCamera();
		} else {
			requestCameraPermission();
		}
	}

	private void initializeHelpers() {
		cameraExecutor = Executors.newSingleThreadExecutor();
		
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
					});
				}

				@Override
				public void onTextRecognitionFailure(Exception e) {
					// Manejar error
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
					});
				}

				@Override
				public void onFaceDetectionFailure(Exception e) {
					// Manejar error
				}
			});
		} else {
			// Modo OBJETO - Usar lógica simple de Appconescaneo
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
					// Manejar error
				}
			});
		}
	}

	private void setupButtons() {
		// Botón volver al menú
		btnBackToMenu.setOnClickListener(v -> {
			Intent intent = new Intent(CameraActivity.this, MainMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		});
		
		btnHelp.setOnClickListener(v -> showHelpDialog());
		
		// Botón de filtros oculto en modo OBJETO (lógica simple)
		if (btnFilters != null) {
			btnFilters.setVisibility(View.GONE);
		}
		
		// Botón para cambiar cámara solo en modos HUMANO y OBJETO
		if ("HUMANO".equals(currentMode) || "OBJETO".equals(currentMode)) {
			if (btnSwitchCamera != null) {
				btnSwitchCamera.setVisibility(View.VISIBLE);
				btnSwitchCamera.setOnClickListener(v -> switchCamera());
			}
		} else {
			if (btnSwitchCamera != null) {
				btnSwitchCamera.setVisibility(View.GONE);
			}
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
				finish();
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

				ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
						.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
						.setTargetResolution(new android.util.Size(480, 360)) // Resolución baja para mejor rendimiento en dispositivos reales
						.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
						.build();

				imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
					// Configurar dimensiones de la imagen en el overlay
					int imageWidth = imageProxy.getWidth();
					int imageHeight = imageProxy.getHeight();
					
					if (!isImageSourceInfoSet) {
						Log.d("CameraActivity", "Configurando dimensiones de imagen: " + imageWidth + "x" + imageHeight);
						
						ContextCompat.getMainExecutor(CameraActivity.this).execute(() -> {
							graphicOverlay.setImageSourceInfo(imageWidth, imageHeight, false);
							
							// Esperar a que el overlay tenga dimensiones válidas antes de marcar como configurado
							graphicOverlay.post(() -> {
								int overlayWidth = graphicOverlay.getWidth();
								int overlayHeight = graphicOverlay.getHeight();
								Log.d("CameraActivity", "GraphicOverlay configurado - Imagen: " + imageWidth + "x" + imageHeight + 
									", Overlay: " + overlayWidth + "x" + overlayHeight);
								
								if (overlayWidth > 0 && overlayHeight > 0) {
							isImageSourceInfoSet = true;
								}
							});
						});
					}
					
					// Procesar según el modo actual
					if ("TEXTO".equals(currentMode) && textRecognitionHelper != null) {
						textRecognitionHelper.processImageProxy(imageProxy);
					} else if ("HUMANO".equals(currentMode) && faceDetectionHelper != null) {
						faceDetectionHelper.processImageProxy(imageProxy);
					} else if ("OBJETO".equals(currentMode)) {
						// Configurar dimensiones de la imagen en el overlay solo una vez
						if (!isImageSourceInfoSet) {
							ContextCompat.getMainExecutor(CameraActivity.this).execute(() -> {
								graphicOverlay.setImageSourceInfo(
										imageProxy.getWidth(),
										imageProxy.getHeight(),
										false
								);
								isImageSourceInfoSet = true;
							});
						}
						// Usar lógica simple de Appconescaneo
						if (objectDetectorHelper != null) {
							objectDetectorHelper.processImageProxy(imageProxy);
						} else {
							imageProxy.close();
						}
					} else {
						// Modo desconocido, cerrar imagen
						imageProxy.close();
					}
				});

				// Desvincular cámaras anteriores
				cameraProvider.unbindAll();
				
				camera = cameraProvider.bindToLifecycle(
						this, cameraSelector, preview, imageAnalysis);

			} catch (Exception e) {
				Log.e("CameraActivity", "❌ Error iniciando cámara: " + e.getMessage(), e);
			}
		}, ContextCompat.getMainExecutor(this));
	}

	private void showHelpDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_help, null);
		builder.setView(dialogView);
		builder.setPositiveButton("Entendido", null);
		builder.show();
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
	}
}

