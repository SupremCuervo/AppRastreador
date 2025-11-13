# 🔗 Enlaces Directos para Descargar Modelos

## 📥 COCO SSD MobileNet (Recomendado)

### **Opción 1: Enlace Directo de TensorFlow (Más Fácil)** ⭐

```
https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip
```

**Este es el enlace más directo y confiable.**

---

### **Opción 2: TensorFlow Model Zoo**

**Página principal:**
```
https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md
```

**Modelo específico:**
- Busca: **"SSD MobileNet V2 FPNLite 320x320"**
- Descarga la versión **"TFLite"** o **"Quantized TFLite"**

---

### **Opción 3: TensorFlow Hub**

```
https://tfhub.dev/tensorflow/ssd_mobilenet_v2/2
```

**Nota:** Puede requerir conversión a TensorFlow Lite.

---

## 📋 Pasos Rápidos

1. **Click en el enlace de Opción 1** (el más fácil)
2. **Descarga el archivo ZIP**
3. **Extrae el ZIP**
4. **Busca estos archivos:**
   - `detect.tflite` o `model.tflite` → Renómbralo a `ssd_mobilenet.tflite`
   - `labelmap.txt` o `labels.txt` → Renómbralo a `coco_labels.txt`

5. **Coloca en:**
   ```
   app/src/main/assets/ssd_mobilenet.tflite
   app/src/main/assets/coco_labels.txt
   ```

---

## 🎯 Enlace Directo (Copia y Pega)

**Para descargar directamente:**

```
https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip
```

**O haz click aquí:**
[Descargar COCO SSD MobileNet](https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip)

---

## 📦 Contenido del ZIP

Después de descargar y extraer, encontrarás:

- `detect.tflite` → **Renómbralo a:** `ssd_mobilenet.tflite`
- `labelmap.txt` → **Renómbralo a:** `coco_labels.txt`
- Posiblemente otros archivos de documentación

---

## ✅ Verificación

Después de colocar los archivos, la estructura debe ser:

```
app/src/main/assets/
├── ssd_mobilenet.tflite    ✅
└── coco_labels.txt         ✅
```

---

## 🚀 Listo!

Una vez que tengas los archivos en `assets/`, la app los detectará automáticamente al ejecutarse.

---

## 📝 Nota

Si el enlace no funciona, puedes buscar en:
- **GitHub TensorFlow Models:** https://github.com/tensorflow/models
- **TensorFlow Lite Models:** https://www.tensorflow.org/lite/models

