package skupno.teren;

import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import skupno.Img;
import skupno.ZEnterable;

public class Mine extends ZEnterable implements ActionListener{

	private static final long serialVersionUID = -1665904348345936669L;

	
	
	public Mine(String playerID){
		this.playerID = playerID;

	}
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		g.drawImage(Img.getImage("mine"), x, y, null);
	}

	

	

}
