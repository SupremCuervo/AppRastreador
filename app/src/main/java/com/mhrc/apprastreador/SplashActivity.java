package com.mhrc.apprastreador;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

	private static final int SPLASH_DURATION = 5000; // 5 segundos como en Rastreador

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Iniciar animación de rotación del engranaje
		ImageView engranaje = findViewById(R.id.ivEngranaje);
		if (engranaje != null) {
			Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
			engranaje.startAnimation(rotation);
		}

		// Esperar 5 segundos y luego ir al menú principal (como en Rastreador)
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
				startActivity(intent);
				finish(); // Cerrar SplashActivity para que no se pueda volver atrás
			}
		}, SPLASH_DURATION);
	}
}

