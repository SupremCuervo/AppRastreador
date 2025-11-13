#!/bin/bash
# Script para convertir YOLOv5 a TensorFlow Lite
# Basado en el repositorio oficial: https://github.com/ultralytics/yolov5

echo "═══════════════════════════════════════════════════════════════"
echo "  🚀 Script de Conversión: YOLOv5 PyTorch → TensorFlow Lite"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Paso 1: Clonar repositorio
echo -e "${YELLOW}Paso 1: Clonando repositorio YOLOv5...${NC}"
if [ ! -d "yolov5" ]; then
    git clone https://github.com/ultralytics/yolov5.git
    echo -e "${GREEN}✅ Repositorio clonado${NC}"
else
    echo -e "${GREEN}✅ Repositorio ya existe${NC}"
fi

cd yolov5

# Paso 2: Instalar dependencias
echo -e "${YELLOW}Paso 2: Instalando dependencias...${NC}"
pip install -r requirements.txt
echo -e "${GREEN}✅ Dependencias instaladas${NC}"

# Paso 3: Descargar modelo (si no existe)
echo -e "${YELLOW}Paso 3: Descargando modelo YOLOv5s...${NC}"
MODEL_FILE="yolov5s.pt"
if [ ! -f "$MODEL_FILE" ]; then
    wget https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5s.pt
    echo -e "${GREEN}✅ Modelo descargado${NC}"
else
    echo -e "${GREEN}✅ Modelo ya existe${NC}"
fi

# Paso 4: Exportar a TFLite
echo -e "${YELLOW}Paso 4: Exportando a TensorFlow Lite...${NC}"
python export.py --weights yolov5s.pt --include tflite --imgsz 640

if [ -f "yolov5s.tflite" ]; then
    echo -e "${GREEN}✅ Conversión exitosa!${NC}"
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "  Archivo generado: yolov5s.tflite"
    echo "  Tamaño: $(du -h yolov5s.tflite | cut -f1)"
    echo ""
    echo "  Próximos pasos:"
    echo "  1. Renombra: yolov5s.tflite → yolov5.tflite"
    echo "  2. Copia a: ../app/src/main/assets/yolov5.tflite"
    echo "  3. Verifica que yolov5_labels.txt esté en assets/"
    echo "  4. Sincroniza y ejecuta la app"
    echo "═══════════════════════════════════════════════════════════════"
else
    echo -e "${YELLOW}⚠️ Error en la conversión. Verifica los logs arriba.${NC}"
fi

cd ..

