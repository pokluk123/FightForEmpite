package skupno;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import client.ScreenSettings;


/**
 * razred je namenjen enotnemu sistemu za nalaganje slik
 */
public class Img {
	private static HashMap<String, Image> images = new HashMap<String, Image>();
	private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	private static Image error;
	private static ImageIcon errorIcon;
	
	public static void loadAllImages(){
		int imgSize = ScreenSettings.getPixPerSquare();
		int walkerSize = ScreenSettings.getWalkerSize();
		int iconSize = ScreenSettings.getMenuImageSize();
		BufferedImage napaka = new BufferedImage(1000,1000, BufferedImage.TYPE_INT_BGR);
		Graphics2D g2d = napaka.createGraphics();
		g2d.setColor(new Color(255,0,0));//narišemo rdeè kvadrat
		g2d.fillRect(0, 0, 1000, 1000);
		g2d.dispose();//sprostimo ram
		error =  napaka.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH);
		errorIcon = new ImageIcon(napaka.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
		
		//zaènemo z dodajanjem vseh slik. Bi se dalo elegantnejše ampak...
		addImageByFilePath("zgradbe/hisa2.png", "hisa", imgSize, iconSize); 
		addImageByFilePath("zgradbe/barake.png", "barake", imgSize, iconSize);
		addImageByFilePath("zgradbe/magistrat.png", "magistrat", imgSize, iconSize);
		addImageByFilePath("teren/mine.png", "mine", imgSize, iconSize);
		addImageByFilePath("teren/tree.png", "tree", imgSize, iconSize);
		addImageByFilePath("teren/pine.png", "pine", imgSize, iconSize);
		addImageByFilePath("teren/stump.png", "stump", imgSize, iconSize);
		addImageByFilePath("teren/stone1.png", "stone1", imgSize, iconSize);
		addImageByFilePath("teren/stone2.png", "stone2", imgSize, iconSize);
		addImageByFilePath("teren/stone3.png", "stone3", imgSize, iconSize);
		addImageByFilePath("ljudje/walker1.png", "worker", walkerSize, iconSize); 
		addImageByFilePath("ljudje/soldier1.png", "soldier", walkerSize, iconSize); 
		addImageByFilePath("ljudje/horse1.png", "horse", walkerSize, iconSize); 
	}
	
	/**
	 * Funkcija naloži sliko iz mape /ikone/ + filaName
	 * in prilagodi na željene velikosti ter doda v primeren HashMap
	 * @param fileName
	 * @throws IOException 
	 */
	private static void addImageByFilePath(String fileName, String name,int imgSize, int iconSize){
		Image img;
		try{
			img = ImageIO.read(new File("ikone/" + fileName));//preberemo sliko iz datoteke in prikažemo na zaslon		
		}catch(IOException e){//èe slike zaradi nekega razloga ne moremo prebrati jo zamenjamo z rdeèim kvadratom...
			System.out.println("napaka pri branju slike iz datoteke: "+fileName);
			return;
		}
		images.put(name, img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH));
		icons.put(name, new ImageIcon(img.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
	}
	/**
	 * vrnemo že naloženo sliko
	 * @param key
	 * @return
	 */
	public static Image getImage(String key){
		Image tmp = images.get(key);
		if(tmp == null){
			return error;
		}
		return tmp;
	}
	
	/**
	 * vrnemo že naloženo ikono
	 * @param key
	 * @return
	 */
	public static ImageIcon getIcon(String key){
		ImageIcon tmp = icons.get(key);
		if(tmp == null){
			return errorIcon;
		}
		return tmp;
	}
}
