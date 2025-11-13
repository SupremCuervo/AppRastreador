#!/usr/bin/env python3
"""
Script para crear el archivo yolov5_labels.txt
YOLOv5 usa 80 clases COCO sin "background"
"""

# Lista de 80 clases COCO (sin "background")
COCO_CLASSES = [
    "person",
    "bicycle",
    "car",
    "motorcycle",
    "airplane",
    "bus",
    "train",
    "truck",
    "boat",
    "traffic light",
    "fire hydrant",
    "stop sign",
    "parking meter",
    "bench",
    "bird",
    "cat",
    "dog",
    "horse",
    "sheep",
    "cow",
    "elephant",
    "bear",
    "zebra",
    "giraffe",
    "backpack",
    "umbrella",
    "handbag",
    "tie",
    "suitcase",
    "frisbee",
    "skis",
    "snowboard",
    "sports ball",
    "kite",
    "baseball bat",
    "baseball glove",
    "skateboard",
    "surfboard",
    "tennis racket",
    "bottle",
    "wine glass",
    "cup",
    "fork",
    "knife",
    "spoon",
    "bowl",
    "banana",
    "apple",
    "sandwich",
    "orange",
    "broccoli",
    "carrot",
    "hot dog",
    "pizza",
    "donut",
    "cake",
    "chair",
    "couch",
    "potted plant",
    "bed",
    "dining table",
    "toilet",
    "tv",
    "laptop",
    "mouse",
    "remote",
    "keyboard",
    "cell phone",
    "microwave",
    "oven",
    "toaster",
    "sink",
    "refrigerator",
    "book",
    "clock",
    "vase",
    "scissors",
    "teddy bear",
    "hair drier",
    "toothbrush"
]

def create_yolov5_labels(output_file="yolov5_labels.txt"):
    """Crea el archivo yolov5_labels.txt con las 80 clases COCO"""
    
    print(f"Creando archivo: {output_file}")
    print(f"Total de clases: {len(COCO_CLASSES)}")
    
    with open(output_file, "w", encoding="utf-8") as f:
        for class_name in COCO_CLASSES:
            f.write(class_name + "\n")
    
    print(f"✅ Archivo creado exitosamente: {output_file}")
    print(f"✅ Total de líneas escritas: {len(COCO_CLASSES)}")
    print(f"\nColoca este archivo en: app/src/main/assets/yolov5_labels.txt")

if __name__ == "__main__":
    import sys
    
    output_file = "yolov5_labels.txt"
    if len(sys.argv) > 1:
        output_file = sys.argv[1]
    
    create_yolov5_labels(output_file)

