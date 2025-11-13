@echo off
REM Script para convertir YOLOv5 a TensorFlow Lite (Windows)
REM Basado en el repositorio oficial: https://github.com/ultralytics/yolov5

echo =══════════════════════════════════════════════════════════════
echo   🚀 Script de Conversión: YOLOv5 PyTorch → TensorFlow Lite
echo =══════════════════════════════════════════════════════════════
echo.

REM Paso 1: Clonar repositorio
echo Paso 1: Clonando repositorio YOLOv5...
if not exist "yolov5" (
    git clone https://github.com/ultralytics/yolov5.git
    echo ✅ Repositorio clonado
) else (
    echo ✅ Repositorio ya existe
)

cd yolov5

REM Paso 2: Instalar dependencias
echo.
echo Paso 2: Instalando dependencias...
pip install -r requirements.txt
echo ✅ Dependencias instaladas

REM Paso 3: Descargar modelo (si no existe)
echo.
echo Paso 3: Descargando modelo YOLOv5s...
if not exist "yolov5s.pt" (
    echo Descargando desde GitHub...
    curl -L -o yolov5s.pt https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5s.pt
    echo ✅ Modelo descargado
) else (
    echo ✅ Modelo ya existe
)

REM Paso 4: Exportar a TFLite
echo.
echo Paso 4: Exportando a TensorFlow Lite...
python export.py --weights yolov5s.pt --include tflite --imgsz 640

if exist "yolov5s.tflite" (
    echo.
    echo =══════════════════════════════════════════════════════════════
    echo   ✅ Conversión exitosa!
    echo.
    echo   Archivo generado: yolov5s.tflite
    echo.
    echo   Próximos pasos:
    echo   1. Renombra: yolov5s.tflite → yolov5.tflite
    echo   2. Copia a: ..\app\src\main\assets\yolov5.tflite
    echo   3. Verifica que yolov5_labels.txt esté en assets/
    echo   4. Sincroniza y ejecuta la app
    echo =══════════════════════════════════════════════════════════════
) else (
    echo.
    echo ⚠️ Error en la conversión. Verifica los logs arriba.
)

cd ..

pause

