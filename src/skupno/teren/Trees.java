package skupno.teren;

import java.awt.Graphics2D;
import java.util.HashMap;

import skupno.Img;
import skupno.ZEnterable;
import skupno.figure.rasa1.Worker;

/**
 * Ta razred predstavlja drevo
 * @author znidi
 *
 */
public abstract class Trees extends ZEnterable{
	
	private HashMap<String,Worker> inside = new HashMap<String,Worker>();
	
	
	private static final long serialVersionUID = -5511229679182544353L; 
	
	
	/**
	 * da lahko narišemo štor kjer je prej stalo drevo
	 */
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		if(hp>0){
			risi3(g, x, y, pix);
		}else{
			g.drawImage(Img.getImage("stump"), x, y, null);
		}
	}
	
	/**
	 * metoda namenjena temu, da lahko posamezen subclass nariše svoje specifiène lastnosti
	 * @param g
	 * @param x
	 * @param y
	 * @param pix
	 */
	public abstract void risi3(Graphics2D g, int x, int y, int pix);
	
	
	/**
	 * posekamo del lesa in preverimo, èe drevo še stoji...
	 * @param koliko
	 * @return
	 */
	public boolean chop(double koliko) {
		hp -= koliko;
		if(hp<=0){
			removeAll();
			return false;
		}
		return true;
	}
	
	
	

	public HashMap<String,Worker> getInside() {
		return inside;
	}

	public void setInside(HashMap<String,Worker> inside) {
		this.inside = inside;
	}




	public static class Smreka extends Trees{

		private static final long serialVersionUID = 8017951847994427527L;
		static int[] tockeX = {50,15,35,18,40,20,50,80,60,82,65,85,50};
		static int[] tockeY = {85,85,65,65,45,45,20,45,45,65,65,85,85};
		public Smreka(){
			fullHP = 50;
			hp = fullHP;
		}

		@Override
		public void risi3(Graphics2D g, int x, int y, int pix) {
			g.drawImage(Img.getImage("pine"), x, y, null);
		}
		
	}
	
	/**
	 *drevo z veè lesa
	 */
	public static class Hrast extends Trees{

		private static final long serialVersionUID = 8017951847994427527L;
		
		
		public Hrast(){
			fullHP = 100;
			hp = fullHP;
		}


		@Override
		public void risi3(Graphics2D g, int x, int y, int pix) {
			g.drawImage(Img.getImage("tree"), x, y, null);
		}

	}
	
	
}
