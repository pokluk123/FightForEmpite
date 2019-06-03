package client;

/**
 * razred namenjen shranjevanju nastavitev zaslona.
 * Bi se dalo pohitrit amapak smo leta 2019 in se 
 * zato lahko "preseravamo" z viri, ki so nam na voljo
 * @author znidi
 *
 */
public class ScreenSettings {
	private static int height;//višina okna
	private static int width;//širina okna
	private static int pixPerSquare;//koliko slikovnih pik bi znašali višina in širina posameznega narisanega polja (èe bi gledali iz ptièje perspektive)
	//private int allowedDarkness;//koliko polj v vsako smer lahko gremo ko je konec igralne površine
	private static double alfaTilt;//nagib kamere glede na polje po x osi
	private static double betaTilt;//nagib kamere glede na polje po y osi
	private static int screenTransX;
	private static int screenTransY;
	private static int menuImageSize;
	private static int walkerSize;
	private static double clickDistance;
	
	public static void init(){
		height = 800;
		width = 1000;
		pixPerSquare = 50;
		//allowedDarkness = 10;
		screenTransX = pixPerSquare*2*0;
		screenTransY = pixPerSquare*2*0;
		menuImageSize = 120;
		walkerSize = (int)(pixPerSquare*0.6f);
		clickDistance = walkerSize;//v pixlih
	}
	
	public static int getMenuImageSize(){
		return menuImageSize;
	}
	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		ScreenSettings.height = height;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		ScreenSettings.width = width;
	}

	public static int getPixPerSquare() {
		return pixPerSquare;
	}

	public static void setPixPerSquare(int pixPerSquare) {
		ScreenSettings.pixPerSquare = pixPerSquare;
	}

	public static double getAlfaTilt() {
		return alfaTilt;
	}

	public static void setAlfaTilt(double alfaTilt) {
		ScreenSettings.alfaTilt = alfaTilt;
	}

	public static double getBetaTilt() {
		return betaTilt;
	}

	public static void setBetaTilt(double betaTilt) {
		ScreenSettings.betaTilt = betaTilt;
	}

	public static int getScreenTransX() {
		return screenTransX;
	}

	public static void setScreenTransX(int screenTransX) {
		ScreenSettings.screenTransX = screenTransX;
	}

	public static int getScreenTransY() {
		return screenTransY;
	}

	public static void setScreenTransY(int screenTransY) {
		ScreenSettings.screenTransY = screenTransY;
	}

	public static int getWalkerSize() {
		return walkerSize;
	}

	public static double getClickDistance() {
		return clickDistance;
	}

}
