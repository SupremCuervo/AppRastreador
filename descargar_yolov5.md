# 📥 Descarga Rápida de YOLOv5

## 🚀 Descarga Directa (1 Click)

### **YOLOv5s (Recomendado para móviles)** ⭐

**Enlace directo:**
```
https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov5s.tflite
```

**O haz clic aquí:**
[Descargar YOLOv5s.tflite](https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov5s.tflite)

---

### **YOLOv5n (Más ligero y rápido)**

**Enlace directo:**
```
https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov5n.tflite
```

**O haz clic aquí:**
[Descargar YOLOv5n.tflite](https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov5n.tflite)

---

## 📋 Pasos Rápidos

1. **Descarga el modelo:**
   - Haz clic en uno de los enlaces de arriba
   - El archivo se descargará como `yolov5s.tflite` o `yolov5n.tflite`

2. **Renombra el archivo:**
   - `yolov5s.tflite` → `yolov5.tflite`
   - O déjalo con el nombre original (el código lo detectará)

3. **Coloca en la app:**
   - Copia a: `app/src/main/assets/yolov5.tflite`
   - O: `app/src/main/assets/yolov5s.tflite`

4. **Prepara las etiquetas:**
   - Si ya tienes `coco_labels.txt`, úsalo (pero **elimina la primera línea "background"** si existe)
   - O crea `yolov5_labels.txt` con 80 clases (ver GUIA_YOLOV5.md)

5. **Sincroniza y ejecuta:**
   - Sync Now en Android Studio
   - Ejecuta la app
   - ¡Listo! 🎉

---

## 🔍 Verificar Descarga

Después de descargar, verifica:
- ✅ El archivo `.tflite` tiene entre 6-30 MB
- ✅ El archivo no está corrupto (puedes abrirlo)
- ✅ Está en la ubicación correcta: `app/src/main/assets/`

---

## 📝 Nota Importante

**YOLOv5 NO usa "background" como primera clase.**

Si usas `coco_labels.txt`, asegúrate de que:
- ❌ NO tenga "background" como primera línea
- ✅ Empiece directamente con "person"
- ✅ Tenga exactamente 80 líneas

---

## 🆘 ¿Problemas?

Si los enlaces no funcionan:
1. Ve a: https://github.com/ultralytics/yolov5/releases
2. Busca la última versión
3. Descarga el modelo `.tflite` de los assets

---

¡Descarga completa! Ahora coloca el archivo en `assets/` y ejecuta la app. 🚀

