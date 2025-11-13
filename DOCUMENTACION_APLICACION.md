# Documentación Técnica - AppRastreador

## 📱 Descripción General

**AppRastreador** es una aplicación Android de detección y análisis de objetos en tiempo real que utiliza tecnologías de Machine Learning y visión por computadora. La aplicación permite detectar objetos, reconocer texto, identificar caras humanas y analizar imágenes estáticas mediante el uso de la cámara del dispositivo o imágenes de la galería.

---

## 🛠️ Tecnologías Utilizadas

### Framework y Lenguajes
- **Android SDK**: Framework principal para desarrollo Android
- **Java**: Lenguaje principal de programación (Java 11)
- **Kotlin**: Lenguaje secundario para algunas funcionalidades modernas
- **XML**: Para definición de layouts y recursos

### Machine Learning y Visión por Computadora
- **Google ML Kit**: Suite de herramientas de Machine Learning de Google
  - **ML Kit Object Detection** (v17.0.2): Detección de objetos en tiempo real
  - **ML Kit Text Recognition** (v16.0.1): Reconocimiento óptico de caracteres (OCR)
  - **ML Kit Face Detection** (v16.1.7): Detección facial con clasificación de edad y género
- **TensorFlow Lite** (v2.14.0): Framework para modelos de Machine Learning personalizados
  - **TensorFlow Lite Support** (v0.4.4): Utilidades de soporte
  - **TensorFlow Lite Metadata** (v0.4.4): Metadatos de modelos

### Cámara y Multimedia
- **CameraX** (v1.3.1): Biblioteca moderna de Android para manejo de cámara
  - `camera-core`: Funcionalidades principales
  - `camera-camera2`: Integración con Camera2 API
  - `camera-lifecycle`: Gestión del ciclo de vida
  - `camera-view`: Vista previa de la cámara

### Interfaz de Usuario
- **Material Design Components** (v1.11.0): Componentes de Material Design
- **AppCompat** (v1.6.1): Compatibilidad con versiones anteriores de Android
- **ConstraintLayout** (v2.1.4): Sistema de layout flexible
- **Jetpack Compose**: Framework moderno de UI (parcialmente implementado)

### Otras Dependencias
- **AndroidX Core KTX**: Extensiones Kotlin para AndroidX
- **AndroidX Lifecycle**: Gestión del ciclo de vida de componentes
- **Guava**: Utilidades de Google (para ListenableFuture)

---

## 📐 Arquitectura de la Aplicación

### Estructura de Paquetes
```
com.mhrc.apprastreador/
├── Activities (Pantallas principales)
│   ├── SplashActivity.java
│   ├── MainMenuActivity.java
│   ├── CameraActivity.java
│   └── ImageAnalysisActivity.java
├── Helpers (Clases auxiliares de ML)
│   ├── ObjectDetectorHelper.java
│   ├── FaceDetectionHelper.java
│   └── TextRecognitionHelper.java
├── Graphics (Visualización de detecciones)
│   ├── GraphicOverlay.java
│   ├── ObjectGraphic.java
│   ├── FaceGraphic.java
│   └── TextGraphic.java
└── UI Theme (Temas de Compose)
    └── ui/theme/
```

### Permisos Requeridos
- `CAMERA`: Acceso a la cámara del dispositivo
- `READ_EXTERNAL_STORAGE`: Lectura de imágenes (Android ≤ 12)
- `READ_MEDIA_IMAGES`: Lectura de imágenes (Android ≥ 13)

---

## 🖥️ Pantallas y Funcionalidades

### 1. SplashActivity (Pantalla de Inicio)

**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/SplashActivity.java`  
**Layout**: `app/src/main/res/layout/activity_splash.xml`

#### Propósito
Pantalla de bienvenida que se muestra al iniciar la aplicación. Presenta el logo y una animación de carga mientras la aplicación se inicializa.

#### Componentes Visuales
- **Logo de la aplicación**: Imagen centrada (`logo_write`)
- **Texto descriptivo**: "Detección y Seguimiento de Objetos"
- **Animación de engranaje**: Indicador visual de carga con rotación continua

#### Funcionalidad
1. Al iniciar, muestra el logo y el texto descriptivo
2. Inicia una animación de rotación del engranaje
3. Espera **5 segundos** (SPLASH_DURATION)
4. Navega automáticamente a `MainMenuActivity`
5. Cierra la actividad para evitar volver atrás

#### Características Técnicas
- Tema personalizado: `SplashTheme` (sin ActionBar)
- Duración fija: 5000ms
- Handler con Looper principal para la transición

---

### 2. MainMenuActivity (Menú Principal)

**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/MainMenuActivity.java`  
**Layout**: `app/src/main/res/layout/activity_main_menu.xml`

#### Propósito
Pantalla principal que actúa como centro de navegación. Permite al usuario seleccionar el tipo de detección que desea realizar.

#### Componentes Visuales
- **Título**: "Selecciona un modo"
- **Logo**: Imagen del logo de la aplicación (248x248dp)
- **Botones de navegación**:
  1. **Detectar Humanos**: Inicia detección facial
  2. **Detectar Objetos**: Inicia detección de objetos
  3. **Reconocimiento de Texto**: Inicia OCR
  4. **Análisis de imagen**: Abre análisis de imágenes estáticas

#### Funcionalidad
Cada botón navega a una actividad diferente:
- **Botón "Detectar Humanos"**: 
  - Abre `CameraActivity` con modo `"HUMANO"`
  - Activa detección facial con ML Kit
  
- **Botón "Detectar Objetos"**: 
  - Abre `CameraActivity` con modo `"OBJETO"`
  - Activa detección de objetos con ML Kit
  
- **Botón "Reconocimiento de Texto"**: 
  - Abre `CameraActivity` con modo `"TEXTO"`
  - Activa reconocimiento de texto con ML Kit
  
- **Botón "Análisis de imagen"**: 
  - Abre `ImageAnalysisActivity`
  - Permite análisis de imágenes estáticas desde cámara o galería

#### Características Técnicas
- Layout: LinearLayout vertical con elementos centrados
- Fondo: Color personalizado (`splash_background`)
- Botones: Estilo Material Design con color personalizado (`button_color`)

---

### 3. CameraActivity (Cámara en Tiempo Real)

**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/CameraActivity.java`  
**Layout**: `app/src/main/res/layout/activity_camera.xml`

#### Propósito
Pantalla principal de detección en tiempo real. Muestra la vista previa de la cámara y superpone los resultados de detección directamente sobre la imagen.

#### Modos de Operación
La actividad puede funcionar en tres modos según el parámetro recibido:

1. **Modo HUMANO**: Detección facial
2. **Modo OBJETO**: Detección de objetos
3. **Modo TEXTO**: Reconocimiento de texto

#### Componentes Visuales

**Vista Principal**:
- **PreviewView**: Vista previa de la cámara en tiempo real
- **GraphicOverlay**: Capa transparente para dibujar bounding boxes y anotaciones

**Controles**:
- **Botón Regresar** (superior izquierda):
  - Icono de flecha hacia atrás
  - Regresa al menú principal
  - Tamaño: 80x80dp
  
- **Botón Ayuda** (superior derecha):
  - Botón circular amarillo con "?"
  - Muestra diálogo de ayuda
  - Tamaño: wrap_content
  
- **Botón Cambiar Cámara** (inferior central):
  - Icono de cámara
  - Alterna entre cámara frontal y trasera
  - Solo visible en modos HUMANO y OBJETO
  - Tamaño: 50x50dp

#### Funcionalidad Detallada

**Inicialización**:
1. Recibe el modo de operación desde el Intent
2. Inicializa los helpers de ML según el modo:
   - `FaceDetectionHelper` para modo HUMANO
   - `ObjectDetectorHelper` para modo OBJETO
   - `TextRecognitionHelper` para modo TEXTO
3. Configura los botones y sus listeners
4. Solicita permisos de cámara si es necesario

**Procesamiento de Imágenes**:
1. **CameraX** captura frames continuamente
2. Cada frame se procesa con el helper correspondiente:
   - **Modo HUMANO**: Detecta caras, muestra bounding boxes y clasificaciones
   - **Modo OBJETO**: Detecta objetos, muestra bounding boxes con etiquetas
   - **Modo TEXTO**: Detecta texto, muestra bounding boxes alrededor de bloques de texto
3. Los resultados se dibujan en `GraphicOverlay` en tiempo real
4. Resolución de análisis: 480x360 píxeles (optimizado para rendimiento)

**Gestión de Cámara**:
- Soporte para cámara frontal y trasera
- Cambio dinámico de cámara sin reiniciar la actividad
- Gestión automática del ciclo de vida de la cámara
- Rotación automática según orientación del dispositivo

**Características Técnicas**:
- **ImageAnalysis**: Estrategia `KEEP_ONLY_LATEST` para mejor rendimiento
- **ExecutorService**: Thread dedicado para procesamiento de imágenes
- **GraphicOverlay**: Sistema de coordenadas para mapear detecciones a la pantalla
- **Lifecycle-aware**: La cámara se detiene automáticamente cuando la actividad se pausa

#### Diálogo de Ayuda
Muestra información sobre cómo usar la detección:
- Mantener objetos bien iluminados
- Centralizar objetos en la pantalla
- Evitar movimientos bruscos
- Asegurar características visuales claras

---

### 4. ImageAnalysisActivity (Análisis de Imágenes Estáticas)

**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/ImageAnalysisActivity.java`  
**Layout**: `app/src/main/res/layout/activity_image_analysis.xml`

#### Propósito
Permite analizar imágenes estáticas desde la cámara o la galería. A diferencia de `CameraActivity`, aquí el usuario puede tomar una foto o seleccionar una imagen y luego analizarla manualmente.

#### Componentes Visuales

**Vista Principal**:
- **PreviewView**: Vista previa de la cámara (oculta cuando hay imagen seleccionada)
- **ImageView**: Muestra la imagen seleccionada de galería o capturada
- **GraphicOverlay**: Capa para dibujar resultados de detección

**Controles Superiores**:
- **Botón Regresar** (superior izquierda): Vuelve al menú principal
- **Spinner de Modo** (superior derecha): 
  - Permite cambiar entre modos: OBJETO, HUMANO, TEXTO
  - Estilo personalizado con fondo oscuro

**Controles Inferiores** (barra horizontal):
- **Cambiar Cámara**: Alterna entre cámara frontal y trasera
- **Cámara**: Captura una foto
- **Galería**: Selecciona imagen de la galería
- **Analizar**: Procesa la imagen actual

#### Funcionalidad Detallada

**Flujo de Trabajo**:
1. Usuario selecciona o captura una imagen
2. La imagen se muestra en pantalla completa
3. Usuario selecciona el modo de análisis (si no lo ha hecho)
4. Usuario presiona "Analizar"
5. La imagen se procesa con el helper correspondiente
6. Los resultados se dibujan sobre la imagen

**Captura de Foto**:
- Utiliza `ImageCapture` de CameraX
- Guarda la foto en el almacenamiento externo
- Formato: JPG con timestamp en el nombre
- Procesa automáticamente después de capturar

**Selección de Galería**:
- Utiliza `ACTION_PICK` para seleccionar imágenes
- Soporta permisos de almacenamiento (legacy y moderno)
- Carga la imagen como Bitmap
- Configura el overlay con las dimensiones de la imagen

**Análisis Manual**:
- El usuario controla cuándo analizar
- Guarda el último frame de la cámara para análisis
- Permite múltiples análisis de la misma imagen
- Limpia resultados anteriores antes de cada análisis

**Características Técnicas**:
- **ImageCapture**: Modo `MINIMIZE_LATENCY` para captura rápida
- **Gestión de ImageProxy**: Guarda el último frame para análisis manual
- **Sincronización**: Thread-safe para manejo de frames
- **Soporte multi-modo**: Cambio dinámico de modo sin reiniciar

---

## 🔧 Componentes Auxiliares

### Helpers de Machine Learning

#### ObjectDetectorHelper
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/ObjectDetectorHelper.java`

**Propósito**: Encapsula la lógica de detección de objetos con ML Kit.

**Configuración**:
- Modo: `STREAM_MODE` (tiempo real)
- Múltiples objetos: Habilitado
- Clasificación: Habilitada

**Funcionalidad**:
- Convierte `ImageProxy` a `InputImage`
- Procesa con ML Kit Object Detection
- Notifica resultados mediante listener
- Maneja errores y cierra recursos

#### FaceDetectionHelper
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/FaceDetectionHelper.java`

**Propósito**: Encapsula la lógica de detección facial con ML Kit.

**Configuración**:
- Modo de rendimiento: `ACCURATE` (máxima precisión)
- Landmarks: Todos habilitados
- Clasificación: Todas habilitadas (sonrisa, ojos abiertos, etc.)
- Tamaño mínimo de cara: 0.1 (10% de la imagen)
- Tracking: Habilitado

**Funcionalidad**:
- Detecta caras en imágenes
- Proporciona información de landmarks faciales
- Clasifica características (sonrisa, ojos, etc.)
- Rastrea caras entre frames

#### TextRecognitionHelper
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/TextRecognitionHelper.java`

**Propósito**: Encapsula la lógica de reconocimiento de texto (OCR) con ML Kit.

**Configuración**:
- Opciones: Latin Text Recognizer (reconocimiento de texto latino)

**Funcionalidad**:
- Reconoce texto en imágenes
- Organiza texto en bloques, líneas y elementos
- Proporciona coordenadas de cada bloque de texto
- Soporta múltiples idiomas latinos

### Componentes de Visualización

#### GraphicOverlay
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/GraphicOverlay.java`

**Propósito**: Vista personalizada que superpone gráficos sobre la imagen de la cámara.

**Funcionalidad**:
- Sistema de coordenadas para mapear detecciones
- Gestión de múltiples gráficos simultáneos
- Actualización automática del canvas
- Sincronización con dimensiones de la imagen

#### ObjectGraphic
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/ObjectGraphic.java`

**Propósito**: Dibuja bounding boxes y etiquetas para objetos detectados.

**Visualización**:
- Rectángulo alrededor del objeto
- Etiqueta con nombre de la clase
- Probabilidad de detección
- Color personalizado por tipo

#### FaceGraphic
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/FaceGraphic.java`

**Propósito**: Dibuja información sobre caras detectadas.

**Visualización**:
- Rectángulo alrededor de la cara
- Landmarks faciales (ojos, nariz, boca)
- Información de clasificación (edad estimada, género, sonrisa, etc.)
- ID de tracking

#### TextGraphic
**Ubicación**: `app/src/main/java/com/mhrc/apprastreador/TextGraphic.java`

**Propósito**: Dibuja bounding boxes alrededor de bloques de texto detectados.

**Visualización**:
- Rectángulo alrededor del bloque de texto
- Texto reconocido
- Coordenadas de líneas individuales

---

## 🔄 Flujo de la Aplicación

### Flujo Principal

```
1. Inicio de la App
   ↓
2. SplashActivity (5 segundos)
   ↓
3. MainMenuActivity
   ├─→ CameraActivity (Modo HUMANO)
   ├─→ CameraActivity (Modo OBJETO)
   ├─→ CameraActivity (Modo TEXTO)
   └─→ ImageAnalysisActivity
```

### Flujo de Detección en Tiempo Real

```
CameraActivity
   ↓
Inicializar CameraX
   ↓
Configurar ImageAnalysis
   ↓
Procesar cada frame
   ↓
Helper correspondiente (Object/Face/Text)
   ↓
ML Kit procesa imagen
   ↓
Callback con resultados
   ↓
Dibujar en GraphicOverlay
   ↓
Actualizar UI
```

### Flujo de Análisis de Imagen Estática

```
ImageAnalysisActivity
   ↓
Usuario selecciona/captura imagen
   ↓
Imagen se muestra en pantalla
   ↓
Usuario selecciona modo
   ↓
Usuario presiona "Analizar"
   ↓
Helper procesa imagen
   ↓
Resultados se dibujan sobre imagen
```

---

## 📊 Características Técnicas Avanzadas

### Optimización de Rendimiento
- **Resolución reducida**: 480x360 para análisis (mejor rendimiento)
- **Estrategia de backpressure**: `KEEP_ONLY_LATEST` (solo último frame)
- **Thread dedicado**: ExecutorService para procesamiento
- **Gestión de memoria**: Cierre automático de ImageProxy

### Gestión de Permisos
- **Cámara**: Requerido para todas las funcionalidades
- **Almacenamiento**: Solo para selección de imágenes
- **Solicitud dinámica**: Permisos en tiempo de ejecución
- **Compatibilidad**: Soporte para Android 5.0+ (API 21+)

### Gestión del Ciclo de Vida
- **Lifecycle-aware**: CameraX se detiene automáticamente
- **Limpieza de recursos**: Helpers se cierran en onDestroy
- **Prevención de memory leaks**: Cierre adecuado de recursos

---

## 🎨 Temas y Estilos

### Temas Definidos
- **Theme.AppRastreador**: Tema principal (NoActionBar)
- **SplashTheme**: Tema para pantalla de inicio

### Colores Personalizados
- `button_color`: Color de los botones principales
- `splash_background`: Fondo de la pantalla de inicio
- `splash_text_color`: Color del texto en splash

---

## 📱 Requisitos del Sistema

- **Versión mínima de Android**: 5.0 (API 21 - Lollipop)
- **Versión objetivo**: Android 14 (API 36)
- **Cámara**: Requerida (hardware)
- **Almacenamiento**: Opcional (solo para galería)

---

## 🔐 Seguridad y Privacidad

- **Permisos mínimos**: Solo los necesarios
- **Procesamiento local**: Todo el ML se ejecuta en el dispositivo
- **Sin conexión a internet**: No requiere conexión para funcionar
- **Datos locales**: Las imágenes capturadas se guardan localmente

---

## 📝 Notas de Desarrollo

### Estructura de Código
- **Java**: Lenguaje principal para lógica de negocio
- **Kotlin**: Parcialmente implementado (MainActivity, temas)
- **XML**: Layouts y recursos

### Modelos de ML
- **ML Kit**: Modelos pre-entrenados de Google
- **TensorFlow Lite**: Preparado para modelos personalizados (archivos .tflite en assets)

### Archivos de Assets
- `ssd_mobilenet.tflite`: Modelo de detección de objetos
- `coco_labels.txt`: Etiquetas para detección COCO
- `yolov5_labels.txt`: Etiquetas para modelo YOLOv5

---

## 🚀 Mejoras Futuras Sugeridas

1. **Persistencia**: Guardar resultados de detección
2. **Exportación**: Compartir imágenes con detecciones
3. **Filtros avanzados**: Filtrado por tipo de objeto
4. **Historial**: Ver detecciones anteriores
5. **Configuración**: Ajustes de sensibilidad y precisión
6. **Modelos personalizados**: Integración completa de TensorFlow Lite

---

## 📄 Licencia y Créditos

**Desarrollado por**: MHRC  
**Versión**: 1.0  
**Tecnologías**: Google ML Kit, CameraX, TensorFlow Lite

---

*Documentación generada para AppRastreador - Sistema de Detección y Análisis de Objetos*

