# 🎯 YOLOv5 para Android - Guía Completa

> **Repositorio Oficial:** [https://github.com/ultralytics/yolov5](https://github.com/ultralytics/yolov5)  
> **Documentación:** [https://docs.ultralytics.com](https://docs.ultralytics.com)

## 📋 Resumen

Esta guía te ayudará a convertir y usar modelos YOLOv5 en formato TensorFlow Lite para tu aplicación Android.

## 🚀 Método Rápido (Recomendado)

### 1. Clonar y Preparar

```bash
git clone https://github.com/ultralytics/yolov5.git
cd yolov5
pip install -r requirements.txt
```

### 2. Descargar Modelo Pre-entrenado

Descarga desde los [Releases oficiales](https://github.com/ultralytics/yolov5/releases):

- **YOLOv5s (Recomendado)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5s.pt
- **YOLOv5n (Más ligero)**: https://github.com/ultralytics/yolov5/releases/download/v7.0/yolov5n.pt

### 3. Convertir a TFLite

```bash
python export.py --weights yolov5s.pt --include tflite --imgsz 640
```

### 4. Instalar en la App

1. Renombra `yolov5s.tflite` a `yolov5.tflite`
2. Copia a `app/src/main/assets/yolov5.tflite`
3. Verifica que `yolov5_labels.txt` esté en `assets/`
4. ¡Listo! 🎉

## 📚 Documentación Adicional

- **GUIA_YOLOV5.md**: Guía completa detallada
- **DESCARGA_YOLOV5_RAPIDA.txt**: Instrucciones rápidas
- **convertir_yolov5_a_tflite.sh**: Script automatizado (Linux/Mac)
- **convertir_yolov5_a_tflite.bat**: Script automatizado (Windows)

## 🔗 Enlaces Útiles

- [Repositorio YOLOv5](https://github.com/ultralytics/yolov5)
- [Releases](https://github.com/ultralytics/yolov5/releases)
- [Documentación de Exportación](https://docs.ultralytics.com/modes/export/)
- [PyTorch Hub](https://pytorch.org/hub/ultralytics_yolov5/)

## ⚠️ Notas Importantes

1. **YOLOv5 NO proporciona modelos TFLite pre-convertidos** - Debes convertirlos tú mismo
2. **Necesitas Python 3.8+ y PyTorch** para la conversión
3. **El proceso de conversión tarda 1-2 minutos**
4. **YOLOv5 usa 80 clases COCO sin "background"**

## ✅ Verificación

Después de instalar, verifica en Logcat:

```
✅ Modelo YOLOv5 cargado exitosamente: yolov5.tflite
Intérprete creado exitosamente
Dimensiones de entrada: 640x640
Número de etiquetas: 80
```

## 🆘 Solución de Problemas

Si encuentras problemas:

1. Verifica que Python 3.8+ esté instalado
2. Instala todas las dependencias: `pip install -r requirements.txt`
3. Verifica que el modelo .pt se descargó correctamente
4. Revisa los logs de exportación para errores
5. Asegúrate de que el modelo TFLite generado no esté corrupto

---

¡Buena suerte con tu implementación! 🚀

