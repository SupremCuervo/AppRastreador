# 🎯 Guía Completa para Descargar YOLOv5 en TensorFlow Lite

> **Fuente Oficial:** [GitHub - ultralytics/yolov5](https://github.com/ultralytics/yolov5)  
> **Documentación:** [docs.ultralytics.com](https://docs.ultralytics.com)

## 📥 Opción 1: Convertir desde PyTorch (RECOMENDADO) ⭐

YOLOv5 no proporciona modelos TFLite pre-convertidos directamente. Debes convertir desde PyTorch usando el script oficial `export.py`.

### **Pasos para Convertir:**

#### **Paso 1: Clonar el Repositorio**

```bash
git clone https://github.com/ultralytics/yolov5.git
cd yolov5
```

#### **Paso 2: Instalar Dependencias**

```bash
pip install -r requirements.txt
```

#### **Paso 3: Descargar Modelo Pre-entrenado**

Los modelos están disponibles en los [Releases de YOLOv5](https://github.com/ultralytics/yolov5/releases):

- **YOLOv5n (Nano)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5n.pt
- **YOLOv5s (Small)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5s.pt
- **YOLOv5m (Medium)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5m.pt
- **YOLOv5l (Large)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5l.pt
- **YOLOv5x (XLarge)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5x.pt

#### **Paso 4: Exportar a TensorFlow Lite**

```bash
# Exportar YOLOv5s a TFLite
python export.py --weights yolov5s.pt --include tflite

# O exportar YOLOv5n (más ligero)
python export.py --weights yolov5n.pt --include tflite --imgsz 640
```

**Resultado:** Se generará `yolov5s.tflite` en el directorio actual.

#### **Opciones de Exportación:**

```bash
# Exportar con tamaño de imagen personalizado
python export.py --weights yolov5s.pt --include tflite --imgsz 320

# Exportar con cuantización (más pequeño, menor precisión)
python export.py --weights yolov5s.pt --include tflite --int8
```

---

## 📥 Opción 2: Descargar desde Repositorios de la Comunidad

Algunos usuarios de la comunidad han convertido y compartido modelos TFLite:

### **Repositorios con Modelos Pre-convertidos:**

1. **Ultralytics Assets** (no oficial, verificar):
   - https://github.com/ultralytics/assets/releases

2. **Hugging Face**:
   - Buscar: "yolov5 tflite" en https://huggingface.co/models

3. **GitHub Community**:
   - Buscar: "yolov5 tflite android" en GitHub

### **Pasos para Descargar:**

1. **Elige un modelo:**
   - **YOLOv5n**: ~6 MB, más rápido, buena para móviles
   - **YOLOv5s**: ~14 MB, balanceado (recomendado)
   - **YOLOv5m**: ~28 MB, mayor precisión

2. **Descarga el modelo:**
   - Haz clic en el enlace del modelo que quieras
   - O copia la URL y descarga con tu navegador
   - El archivo se descargará como `yolov5n.tflite` (o `yolov5s.tflite`, etc.)

3. **Descarga las etiquetas COCO:**
   - Usa el archivo `coco_labels.txt` que ya tienes en `assets/`
   - O descarga desde: https://raw.githubusercontent.com/ultralytics/yolov5/master/data/coco.yaml
   - Convierte el formato YAML a TXT (ver abajo)

---


---

## 📋 Crear Archivo de Etiquetas (yolov5_labels.txt)

### **Opción A: Usar el mismo que COCO (80 clases)**

El archivo `coco_labels.txt` que ya tienes funciona con YOLOv5, pero **NO debe incluir "background"** al inicio.

### **Opción B: Crear archivo específico para YOLOv5**

Crea un archivo `yolov5_labels.txt` con este contenido:

```
person
bicycle
car
motorcycle
airplane
bus
train
truck
boat
traffic light
fire hydrant
stop sign
parking meter
bench
bird
cat
dog
horse
sheep
cow
elephant
bear
zebra
giraffe
backpack
umbrella
handbag
tie
suitcase
frisbee
skis
snowboard
sports ball
kite
baseball bat
baseball glove
skateboard
surfboard
tennis racket
bottle
wine glass
cup
fork
knife
spoon
bowl
banana
apple
sandwich
orange
broccoli
carrot
hot dog
pizza
donut
cake
chair
couch
potted plant
bed
dining table
toilet
tv
laptop
mouse
remote
keyboard
cell phone
microwave
oven
toaster
sink
refrigerator
book
clock
vase
scissors
teddy bear
hair drier
toothbrush
```

**Nota:** YOLOv5 NO incluye "background" como primera clase (a diferencia de SSD MobileNet).

---

## 🔧 Instalación en la App

### **Paso 1: Colocar el Modelo**

1. Copia el archivo `.tflite` descargado a:
   ```
   app/src/main/assets/yolov5.tflite
   ```
   
   O renómbralo según el modelo:
   - `yolov5n.tflite` → `yolov5.tflite`
   - `yolov5s.tflite` → `yolov5.tflite`
   - `yolov5m.tflite` → `yolov5.tflite`

### **Paso 2: Colocar las Etiquetas**

1. Si usas `coco_labels.txt` existente, **elimina la primera línea "background"** si existe
2. O crea `yolov5_labels.txt` con el contenido de arriba
3. Coloca en:
   ```
   app/src/main/assets/yolov5_labels.txt
   ```
   O simplemente renombra `coco_labels.txt` a `yolov5_labels.txt` (sin "background")

### **Paso 3: Estructura Final**

```
app/src/main/assets/
├── yolov5.tflite          ✅ (o yolov5n.tflite, yolov5s.tflite, etc.)
└── yolov5_labels.txt      ✅ (o coco_labels.txt sin "background")
```

---

## 🚀 Verificación

### **Verificar que el Modelo se Carga:**

1. **Ejecuta la app**
2. **Abre Logcat** en Android Studio
3. **Filtra por:** `TFLiteObjectDetector`
4. **Busca estos mensajes:**
   ```
   ✅ Modelo YOLOv5 cargado exitosamente: yolov5.tflite
   Intérprete creado exitosamente
   Dimensiones de entrada: 640x640
   Número de etiquetas: 80
   ```

---

## 📊 Comparación de Modelos YOLOv5

| Modelo | Tamaño | Velocidad | Precisión | Uso Recomendado |
|--------|--------|-----------|-----------|-----------------|
| YOLOv5n | ~6 MB | ⚡⚡⚡ Muy rápido | ⭐⭐ Buena | Móviles, tiempo real |
| YOLOv5s | ~14 MB | ⚡⚡ Rápido | ⭐⭐⭐ Muy buena | **Recomendado** |
| YOLOv5m | ~28 MB | ⚡ Medio | ⭐⭐⭐⭐ Excelente | Tablets, análisis |

---

## 🔗 Enlaces Útiles

### **Repositorios y Documentación Oficial:**
- **YOLOv5 GitHub:** https://github.com/ultralytics/yolov5
- **YOLOv5 Releases:** https://github.com/ultralytics/yolov5/releases
- **YOLOv5 Docs:** https://docs.ultralytics.com/
- **Exportación a TFLite:** https://docs.ultralytics.com/modes/export/

### **Modelos Pre-entrenados (PyTorch):**
- **v7.0 Release:** https://github.com/ultralytics/yolov5/releases/tag/v7.0
- **Todos los modelos:** https://github.com/ultralytics/yolov5/releases

### **Herramientas de Conversión:**
- **Script export.py:** Incluido en el repositorio oficial
- **Documentación de Exportación:** https://docs.ultralytics.com/modes/export/
- **TensorFlow Lite Converter:** https://www.tensorflow.org/lite/models/convert

---

## ⚠️ Solución de Problemas

### **Problema: El modelo no se carga**
- ✅ Verifica que el archivo esté en `app/src/main/assets/`
- ✅ Verifica que el nombre sea correcto (`yolov5.tflite` o variantes)
- ✅ Verifica los logs en Logcat para ver el error específico

### **Problema: Las etiquetas no coinciden**
- ✅ Asegúrate de que `yolov5_labels.txt` tenga 80 líneas (sin "background")
- ✅ Verifica que no haya líneas vacías al final

### **Problema: La detección no funciona**
- ✅ Verifica que el modelo se haya cargado correctamente (logs)
- ✅ Asegúrate de que el formato de salida del modelo sea compatible
- ✅ Revisa los logs de inferencia en Logcat

---

## ✅ Checklist Final

- [ ] Descargar modelo YOLOv5 en formato `.tflite`
- [ ] Renombrar a `yolov5.tflite` (o dejar nombre original)
- [ ] Crear o actualizar `yolov5_labels.txt` (80 clases, sin "background")
- [ ] Colocar ambos archivos en `app/src/main/assets/`
- [ ] Sincronizar proyecto (Sync Now)
- [ ] Ejecutar la app
- [ ] Verificar en Logcat que el modelo se carga correctamente
- [ ] Probar detección de objetos

---

## 🎯 Recomendación Final

**Para tu app móvil, te recomiendo:**

1. **YOLOv5n (Nano)** si priorizas velocidad y tamaño (~6 MB)
2. **YOLOv5s (Small)** si quieres el mejor balance (⭐ RECOMENDADO, ~14 MB)

**Pasos rápidos:**
1. Clona el repositorio: `git clone https://github.com/ultralytics/yolov5.git`
2. Instala dependencias: `pip install -r requirements.txt`
3. Descarga modelo: Descarga `yolov5s.pt` desde releases
4. Exporta a TFLite: `python export.py --weights yolov5s.pt --include tflite`
5. Renombra a: `yolov5.tflite`
6. Coloca en: `app/src/main/assets/yolov5.tflite`
7. Usa `yolov5_labels.txt` (ya creado)
8. ¡Listo! 🚀

**Alternativa rápida (sin Python):**
- Busca modelos TFLite pre-convertidos en la comunidad de GitHub
- Verifica que sean compatibles con la versión de YOLOv5 que necesitas

---

¡Con YOLOv5 tendrás mayor precisión en la detección de objetos! 🎉

