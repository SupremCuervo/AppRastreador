package com.mhrc.apprastreador;

import android.graphics.Color;
import com.google.mlkit.vision.objects.DetectedObject;
import java.util.HashMap;
import java.util.Map;

public class ObjectDetectionInfo {
	// Colores (públicos para uso en TFLiteObjectGraphic)
	public static final int COLOR_HUMANO = Color.BLUE;
	public static final int COLOR_OBJETO_CONOCIDO = Color.GREEN;
	public static final int COLOR_OBJETO_DESCONOCIDO = Color.RED;
	
	// Categorías conocidas de ML Kit (PredefinedCategory)
	private static final String CATEGORIA_PERSON = "PERSON";
	private static final String CATEGORIA_FOOD = "FOOD";
	private static final String CATEGORIA_FURNITURE = "FURNITURE";
	private static final String CATEGORIA_PLACE = "PLACE";
	private static final String CATEGORIA_PLANT = "PLANT";
	private static final String CATEGORIA_VEHICLE = "VEHICLE";
	private static final String CATEGORIA_ELECTRONICS = "ELECTRONICS";
	private static final String CATEGORIA_CLOTHING = "CLOTHING";
	private static final String CATEGORIA_BAG = "BAG";
	private static final String CATEGORIA_BOOK = "BOOK";
	private static final String CATEGORIA_TOY = "TOY";
	private static final String CATEGORIA_SPORT = "SPORT";
	private static final String CATEGORIA_KITCHEN = "KITCHEN";
	
	// Mapa de traducciones y objetos conocidos
	private static final Map<String, String> TRADUCCIONES_OBJETOS = new HashMap<String, String>() {{
		put("PERSON", "Humano");
		put("FOOD", "Comida");
		put("FURNITURE", "Mueble");
		put("PLACE", "Lugar");
		put("PLANT", "Planta");
		put("VEHICLE", "Vehículo");
		put("ELECTRONICS", "Electrónico");
		put("CLOTHING", "Ropa");
		put("BAG", "Bolso/Mochila");
		put("BOOK", "Libro");
		put("TOY", "Juguete");
		put("SPORT", "Deporte");
		put("KITCHEN", "Cocina");
	}};
	
	// Objetos específicos conocidos (expandido con más objetos de COCO)
	private static final Map<String, String> OBJETOS_ESPECIFICOS = new HashMap<String, String>() {{
		// Dispositivos electrónicos
		put("LAPTOP", "Computadora");
		put("COMPUTER", "Computadora");
		put("DESKTOP", "Computadora de escritorio");
		put("PHONE", "Teléfono");
		put("MOBILE", "Teléfono");
		put("CELL PHONE", "Teléfono móvil");
		put("SMARTPHONE", "Teléfono inteligente");
		put("TABLET", "Tablet");
		put("KEYBOARD", "Teclado");
		put("MOUSE", "Mouse");
		put("MONITOR", "Monitor");
		put("SCREEN", "Pantalla");
		put("PRINTER", "Impresora");
		put("CAMERA", "Cámara");
		put("HEADPHONES", "Audífonos");
		put("SPEAKER", "Altavoz");
		put("TV", "Televisor");
		put("TELEVISION", "Televisor");
		put("REMOTE", "Control remoto");
		put("MICROWAVE", "Microondas");
		put("OVEN", "Horno");
		put("TOASTER", "Tostadora");
		put("REFRIGERATOR", "Refrigerador");
		put("HAIR DRIER", "Secador de pelo");
		put("TOOTHBRUSH", "Cepillo de dientes");
		
		// Escritura y papelería
		put("PEN", "Lápiz");
		put("PENCIL", "Lápiz");
		put("BOLIGRAFO", "Bolígrafo");
		put("MARKER", "Marcador");
		put("BOOK", "Libro");
		put("NOTEBOOK", "Cuaderno");
		put("PAPER", "Papel");
		put("ERASER", "Borrador");
		put("SCISSORS", "Tijeras");
		
		// Muebles
		put("BED", "Cama");
		put("CHAIR", "Silla");
		put("TABLE", "Mesa");
		put("DINING TABLE", "Mesa de comedor");
		put("DESK", "Escritorio");
		put("SOFA", "Sofá");
		put("COUCH", "Sofá");
		put("CABINET", "Armario");
		put("SHELF", "Estante");
		put("BENCH", "Banco");
		put("TOILET", "Inodoro");
		put("SINK", "Lavabo");
		
		// Contenedores y objetos cotidianos
		put("CUP", "Taza");
		put("MUG", "Taza");
		put("BOTTLE", "Botella");
		put("GLASS", "Vaso");
		put("WINE GLASS", "Copa de vino");
		put("BOWL", "Tazón");
		put("PLATE", "Plato");
		put("VASE", "Jarrón");
		
		// Utensilios de cocina
		put("FORK", "Tenedor");
		put("KNIFE", "Cuchillo");
		put("SPOON", "Cuchara");
		
		// Ropa y accesorios
		put("BAG", "Bolso");
		put("BACKPACK", "Mochila");
		put("HANDBAG", "Bolso de mano");
		put("SUITCASE", "Maleta");
		put("WALLET", "Billetera");
		put("WATCH", "Reloj");
		put("GLASSES", "Gafas");
		put("SUNGLASSES", "Gafas de sol");
		put("SHOES", "Zapatos");
		put("SNEAKERS", "Zapatillas");
		put("TIE", "Corbata");
		
		// Vehículos
		put("CAR", "Auto");
		put("BIKE", "Bicicleta");
		put("BICYCLE", "Bicicleta");
		put("MOTORCYCLE", "Motocicleta");
		put("BUS", "Autobús");
		put("TRUCK", "Camión");
		put("AIRPLANE", "Avión");
		put("TRAIN", "Tren");
		put("BOAT", "Barco");
		
		// Señales de tráfico e infraestructura
		put("TRAFFIC LIGHT", "Semáforo");
		put("FIRE HYDRANT", "Hidrante");
		put("STOP SIGN", "Señal de alto");
		put("PARKING METER", "Parquímetro");
		
		// Animales
		put("BIRD", "Pájaro");
		put("CAT", "Gato");
		put("DOG", "Perro");
		put("HORSE", "Caballo");
		put("SHEEP", "Oveja");
		put("COW", "Vaca");
		put("ELEPHANT", "Elefante");
		put("BEAR", "Oso");
		put("ZEBRA", "Cebra");
		put("GIRAFFE", "Jirafa");
		put("TEDDY BEAR", "Osito de peluche");
		
		// Deportes y recreación
		put("BALL", "Pelota");
		put("SPORTS BALL", "Pelota deportiva");
		put("FOOTBALL", "Balón de fútbol");
		put("BASKETBALL", "Balón de baloncesto");
		put("TENNIS", "Pelota de tenis");
		put("TENNIS RACKET", "Raqueta de tenis");
		put("BASEBALL BAT", "Bate de béisbol");
		put("BASEBALL GLOVE", "Guante de béisbol");
		put("SKIS", "Esquís");
		put("SNOWBOARD", "Tabla de snowboard");
		put("SKATEBOARD", "Patineta");
		put("SURFBOARD", "Tabla de surf");
		put("FRISBEE", "Frisbee");
		put("KITE", "Cometa");
		
		// Alimentos
		put("APPLE", "Manzana");
		put("BANANA", "Plátano");
		put("ORANGE", "Naranja");
		put("SANDWICH", "Sandwich");
		put("PIZZA", "Pizza");
		put("HOT DOG", "Perro caliente");
		put("DONUT", "Donut");
		put("CAKE", "Pastel");
		put("BROCCOLI", "Brócoli");
		put("CARROT", "Zanahoria");
		
		// Plantas
		put("POTTED PLANT", "Planta en maceta");
		
		// Otros
		put("UMBRELLA", "Paraguas");
		put("CLOCK", "Reloj");
		put("LAMP", "Lámpara");
	}};
	
	/**
	 * Obtiene el color según el tipo de objeto detectado
	 */
	public static int getColorForObject(DetectedObject object) {
		if (isHuman(object)) {
			return COLOR_HUMANO;
		} else if (isKnownObject(object)) {
			return COLOR_OBJETO_CONOCIDO;
		} else {
			return COLOR_OBJETO_DESCONOCIDO;
		}
	}
	
	/**
	 * Obtiene el texto descriptivo del objeto
	 */
	public static String getTextForObject(DetectedObject object) {
		if (object.getLabels().isEmpty()) {
			return "Objeto Desconocido";
		}
		
		String label = object.getLabels().get(0).getText();
		float confidence = object.getLabels().get(0).getConfidence();
		
		// Verificar si es humano
		if (CATEGORIA_PERSON.equals(label) || "PERSON".equalsIgnoreCase(label)) {
			return "Humano (" + String.format("%.0f%%", confidence * 100) + ")";
		}
		
		// Normalizar label (mayúsculas y espacios)
		String labelUpper = label.toUpperCase().trim();
		
		// Buscar en objetos específicos conocidos (primer intento con label exacto)
		String objetoEspecifico = OBJETOS_ESPECIFICOS.get(labelUpper);
		if (objetoEspecifico != null) {
			return objetoEspecifico + " (" + String.format("%.0f%%", confidence * 100) + ")";
		}
		
		// Buscar con reemplazo de espacios por guiones y viceversa
		String labelWithUnderscore = labelUpper.replace(" ", "_");
		objetoEspecifico = OBJETOS_ESPECIFICOS.get(labelWithUnderscore);
		if (objetoEspecifico != null) {
			return objetoEspecifico + " (" + String.format("%.0f%%", confidence * 100) + ")";
		}
		
		// Buscar en traducciones generales (categorías ML Kit)
		String traduccion = TRADUCCIONES_OBJETOS.get(labelUpper);
		if (traduccion != null) {
			return traduccion + " (" + String.format("%.0f%%", confidence * 100) + ")";
		}
		
		// Si tiene etiqueta pero no está en nuestros mapas, mostrar label original
		// con formato más limpio (capitalizar primera letra)
		String formattedLabel = label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase();
		return formattedLabel + " (" + String.format("%.0f%%", confidence * 100) + ")";
	}
	
	/**
	 * Verifica si el objeto detectado es un humano
	 */
	public static boolean isHuman(DetectedObject object) {
		if (object.getLabels().isEmpty()) {
			return false;
		}
		String label = object.getLabels().get(0).getText();
		// Verificar si es PERSON (ML Kit) o person (COCO/TFLite)
		return CATEGORIA_PERSON.equalsIgnoreCase(label) || "person".equalsIgnoreCase(label);
	}
	
	/**
	 * Verifica si el objeto es conocido (está en nuestros mapas o es una categoría conocida)
	 */
	public static boolean isKnownObject(DetectedObject object) {
		if (object.getLabels().isEmpty()) {
			return false;
		}
		
		String label = object.getLabels().get(0).getText();
		
		// NO verificar confianza aquí - eso se hace en los filtros
		// Solo verificar si el objeto está en nuestros mapas de objetos conocidos
		
		// Verificar si es una categoría conocida de ML Kit
		String labelUpper = label.toUpperCase().trim();
		if (TRADUCCIONES_OBJETOS.containsKey(labelUpper)) {
			return true;
		}
		
		// Verificar si está en objetos específicos conocidos
		if (OBJETOS_ESPECIFICOS.containsKey(labelUpper)) {
			return true;
		}
		
		// Verificar con reemplazo de espacios por guiones bajos
		String labelWithUnderscore = labelUpper.replace(" ", "_");
		if (OBJETOS_ESPECIFICOS.containsKey(labelWithUnderscore)) {
			return true;
		}
		
		// Verificar con reemplazo de espacios por guiones
		String labelWithDash = labelUpper.replace(" ", "-");
		if (OBJETOS_ESPECIFICOS.containsKey(labelWithDash)) {
			return true;
		}
		
		// Si no está en nuestros mapas, es DESCONOCIDO
		// (no considerar confianza aquí - eso se hace en los filtros)
		return false;
	}
	
	/**
	 * Obtiene el tipo de objeto (HUMANO, CONOCIDO, DESCONOCIDO)
	 */
	public static ObjectType getObjectType(DetectedObject object) {
		if (isHuman(object)) {
			return ObjectType.HUMANO;
		} else if (isKnownObject(object)) {
			return ObjectType.CONOCIDO;
		} else {
			return ObjectType.DESCONOCIDO;
		}
	}
	
	public enum ObjectType {
		HUMANO,
		CONOCIDO,
		DESCONOCIDO
	}
	
	/**
	 * Obtiene el texto descriptivo para una etiqueta (para uso con TFLite)
	 */
	public static String getTextForLabel(String label) {
		if (label == null || label.trim().isEmpty()) {
			return "Objeto Desconocido";
		}
		
		String labelUpper = label.toUpperCase().trim();
		
		// Verificar si es humano
		if (CATEGORIA_PERSON.equalsIgnoreCase(label) || "person".equalsIgnoreCase(label)) {
			return "Humano";
		}
		
		// Buscar en objetos específicos conocidos
		String objetoEspecifico = OBJETOS_ESPECIFICOS.get(labelUpper);
		if (objetoEspecifico != null) {
			return objetoEspecifico;
		}
		
		// Buscar con reemplazo de espacios
		String labelWithUnderscore = labelUpper.replace(" ", "_");
		objetoEspecifico = OBJETOS_ESPECIFICOS.get(labelWithUnderscore);
		if (objetoEspecifico != null) {
			return objetoEspecifico;
		}
		
		// Buscar en traducciones generales (categorías ML Kit)
		String traduccion = TRADUCCIONES_OBJETOS.get(labelUpper);
		if (traduccion != null) {
			return traduccion;
		}
		
		// Si no está en nuestros mapas, capitalizar primera letra
		if (label.length() > 0) {
			return label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase();
		}
		return label;
	}
	
	/**
	 * Verifica si una etiqueta es conocida (para uso con TFLite)
	 */
	public static boolean isKnownLabel(String label) {
		if (label == null || label.trim().isEmpty()) {
			return false;
		}
		
		String labelUpper = label.toUpperCase().trim();
		
		// Verificar si es humano
		if (CATEGORIA_PERSON.equalsIgnoreCase(label) || "person".equalsIgnoreCase(label)) {
			return true;
		}
		
		// Verificar si es una categoría conocida de ML Kit
		if (TRADUCCIONES_OBJETOS.containsKey(labelUpper)) {
			return true;
		}
		
		// Verificar si está en objetos específicos conocidos
		if (OBJETOS_ESPECIFICOS.containsKey(labelUpper)) {
			return true;
		}
		
		// Verificar con reemplazo de espacios
		String labelWithUnderscore = labelUpper.replace(" ", "_");
		if (OBJETOS_ESPECIFICOS.containsKey(labelWithUnderscore)) {
			return true;
		}
		
		return false;
	}
}

