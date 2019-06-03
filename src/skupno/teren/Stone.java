package skupno.teren;

import java.awt.Graphics2D;

import skupno.Img;
import skupno.ZObject;

/**
 * Ta razred predstavlja skale
 * 
 * @author znidi
 *
 */
public class Stone extends ZObject {

	private static final long serialVersionUID = -2683058462568243415L;
	private final int type;
	public Stone(){
		type = (int)(Math.random()*3 + 1);
	}
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		g.drawImage(Img.getImage("stone" + type), x, y, null);
	}

	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {

	}

}
