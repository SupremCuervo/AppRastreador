package com.mhrc.apprastreador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		Button btnHumano = findViewById(R.id.btnHumano);
		Button btnObjeto = findViewById(R.id.btnObjeto);
		Button btnTexto = findViewById(R.id.btnTexto);
		Button btnImagen = findViewById(R.id.btnImagen);

		// Modo Humano (con edad y género)
		btnHumano.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, CameraActivity.class);
			intent.putExtra("MODE", "HUMANO");
			startActivity(intent);
		});

		// Modo Objeto (con filtros)
		btnObjeto.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, CameraActivity.class);
			intent.putExtra("MODE", "OBJETO");
			startActivity(intent);
		});

		// Modo Texto (reconocimiento de texto)
		btnTexto.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, CameraActivity.class);
			intent.putExtra("MODE", "TEXTO");
			startActivity(intent);
		});

		// Modo Análisis de Imagen Estática
		btnImagen.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, ImageAnalysisActivity.class);
			startActivity(intent);
		});
	}
}

