# 📥 Instrucciones para Descargar Modelos Pre-entrenados

## 🎯 Comparación: COCO SSD MobileNet vs YOLOv5

### **COCO SSD MobileNet** (Recomendado para móviles) ⭐

**Ventajas:**
- ✅ **Más ligero** (~6-8 MB)
- ✅ **Más rápido** en dispositivos móviles
- ✅ **Menor consumo de batería**
- ✅ **Fácil integración** con TensorFlow Lite
- ✅ **Detecta 80 objetos** comunes (laptop, mouse, keyboard, bed, chair, etc.)

**Desventajas:**
- ⚠️ Precisión ligeramente menor que YOLOv5
- ⚠️ Puede tener problemas con objetos muy pequeños

**Recomendado para:** Dispositivos móviles, tiempo real, batería limitada

---

### **YOLOv5** (Mayor precisión) 🎯

**Ventajas:**
- ✅ **Mayor precisión** en detección
- ✅ **Mejor con objetos pequeños**
- ✅ **Detecta más objetos** simultáneamente
- ✅ **Mejor en entornos complejos**

**Desventajas:**
- ⚠️ **Más pesado** (~15-30 MB)
- ⚠️ **Más lento** en dispositivos móviles
- ⚠️ **Mayor consumo de batería**
- ⚠️ Puede requerir conversión de PyTorch a TensorFlow Lite

**Recomendado para:** Mayor precisión, dispositivos potentes, análisis de imágenes

---

## 🏆 Recomendación Final

**Para tu app móvil: COCO SSD MobileNet** ⭐

**Razones:**
1. ✅ Funciona mejor en tiempo real en móviles
2. ✅ Detecta los objetos que necesitas (computadora, mouse, lápiz, cama)
3. ✅ Más fácil de integrar
4. ✅ Menor tamaño de app
5. ✅ Mejor rendimiento en dispositivos con recursos limitados

---

## 📥 Opción 1: COCO SSD MobileNet (Recomendado)

### Paso 1: Descargar el modelo

**Opción A: Desde TensorFlow Hub (Recomendado)**
1. Ve a: https://tfhub.dev/tensorflow/ssd_mobilenet_v2/2
2. Descarga el modelo en formato TensorFlow Lite
3. O usa este enlace directo:
   ```
   https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip
   ```

**Opción B: Desde TensorFlow Model Zoo**
1. Ve a: https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md
2. Busca: "SSD MobileNet V2 FPNLite 320x320"
3. Descarga la versión "TFLite" o "Quantized TFLite"

### Paso 2: Extraer archivos

Después de descargar el ZIP, extrae:
- `detect.tflite` o `model.tflite` → Renómbralo a `ssd_mobilenet.tflite`
- `labelmap.txt` o `labels.txt` → Renómbralo a `coco_labels.txt`

### Paso 3: Colocar en la app

1. **Crear carpeta assets** (si no existe):
   - Click derecho en `app/src/main/`
   - **New → Folder → Assets Folder**
   - Click en **Finish**

2. **Copiar archivos:**
   - `ssd_mobilenet.tflite` → `app/src/main/assets/ssd_mobilenet.tflite`
   - `coco_labels.txt` → `app/src/main/assets/coco_labels.txt`

### Paso 4: Verificar estructura

```
app/src/main/assets/
├── ssd_mobilenet.tflite
└── coco_labels.txt
```

### Paso 5: Sincronizar y ejecutar

1. **Sync Now** en Android Studio
2. Ejecuta la app
3. El modelo se cargará automáticamente

---

## 📥 Opción 2: YOLOv5 (Mayor precisión)

### Paso 1: Descargar modelo YOLOv5

**Opción A: Desde Ultralytics (Recomendado)**
1. Ve a: https://github.com/ultralytics/yolov5
2. Descarga el modelo YOLOv5s (small) o YOLOv5n (nano) para móviles
3. Convierte a TensorFlow Lite usando el script de conversión

**Opción B: Modelo pre-convertido**
1. Busca "YOLOv5 TensorFlow Lite Android" en GitHub
2. Descarga un modelo ya convertido
3. Ejemplo: https://github.com/zldrobit/yolov5

### Paso 2: Convertir a TensorFlow Lite (si es necesario)

Si tienes el modelo en PyTorch:

```python
# Script de conversión
import torch
from yolov5 import YOLOv5

# Cargar modelo
model = YOLOv5('yolov5s.pt')

# Exportar a TensorFlow Lite
model.export(format='tflite')
```

### Paso 3: Colocar en la app

1. **Copiar archivos:**
   - `yolov5.tflite` → `app/src/main/assets/yolov5.tflite`
   - `yolov5_labels.txt` → `app/src/main/assets/yolov5_labels.txt`

### Paso 4: Verificar estructura

```
app/src/main/assets/
├── yolov5.tflite
└── yolov5_labels.txt
```

---

## 📋 Formato de labels.txt

### Para COCO SSD MobileNet (coco_labels.txt):

```
background
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

### Para YOLOv5 (yolov5_labels.txt):

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

---

## 🔗 Enlaces de Descarga Directos

### COCO SSD MobileNet:

1. **TensorFlow Lite Model Zoo:**
   ```
   https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip
   ```

2. **TensorFlow Hub:**
   ```
   https://tfhub.dev/tensorflow/ssd_mobilenet_v2/2
   ```

3. **GitHub TensorFlow Models:**
   ```
   https://github.com/tensorflow/models/tree/master/research/object_detection
   ```

### YOLOv5:

1. **Ultralytics GitHub:**
   ```
   https://github.com/ultralytics/yolov5
   ```

2. **YOLOv5 TensorFlow Lite (pre-convertido):**
   ```
   https://github.com/zldrobit/yolov5
   ```

---

## ✅ Checklist de Implementación

- [ ] Decidir qué modelo usar (COCO SSD MobileNet recomendado)
- [ ] Descargar el modelo .tflite
- [ ] Descargar o crear el archivo labels.txt
- [ ] Crear carpeta assets en app/src/main/
- [ ] Copiar modelo a app/src/main/assets/
- [ ] Copiar labels.txt a app/src/main/assets/
- [ ] Sincronizar proyecto (Sync Now)
- [ ] Ejecutar la app
- [ ] Verificar que el modelo se carga correctamente (revisar Logcat)

---

## 🚀 Resultado Esperado

Una vez implementado el modelo:

1. **La app detectará automáticamente el modelo** en assets/
2. **Usará el modelo TensorFlow Lite** en lugar de solo ML Kit
3. **Detectará objetos específicos** como:
   - ✅ Computadora (laptop)
   - ✅ Mouse
   - ✅ Teclado (keyboard)
   - ✅ Lápiz (pencil)
   - ✅ Cama (bed)
   - ✅ Silla (chair)
   - ✅ Mesa (dining table)
   - ✅ Y 73 objetos más

4. **Mayor precisión** en la detección
5. **Mejor identificación** de objetos específicos

---

## 📝 Notas Importantes

1. **Tamaño del modelo:**
   - COCO SSD MobileNet: ~6-8 MB
   - YOLOv5: ~15-30 MB
   - Esto aumentará el tamaño de tu APK

2. **Rendimiento:**
   - El modelo se carga al iniciar la app
   - Puede tardar 1-2 segundos la primera vez
   - Se mantiene en memoria mientras la app está activa

3. **Fallback:**
   - Si no hay modelo, la app usa `EnhancedObjectDetector`
   - La app funciona sin modelo (con menor precisión)

---

## 🎯 Recomendación Final

**Para tu app: Usa COCO SSD MobileNet**

1. ✅ Descarga desde el enlace de TensorFlow
2. ✅ Coloca en assets/
3. ✅ La app lo detectará automáticamente
4. ✅ Disfruta de mejor detección de objetos específicos

---

¡Con el modelo implementado, tu app detectará computadoras, mouse, lápices y camas con mucha mayor precisión! 🚀

