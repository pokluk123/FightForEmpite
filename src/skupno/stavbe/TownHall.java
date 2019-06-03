package skupno.stavbe;

import java.awt.Graphics2D;

import skupno.Img;
import skupno.ZObject;

public class TownHall extends ZObject{

	private static final long serialVersionUID = -8345047701772391105L;
	
	public TownHall(String who){
		this.fullHP = 10000;
		this.hp = this.fullHP;
		this.playerID = who;
		this.canBeAttacked = true;
	}
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		g.drawImage(Img.getImage("magistrat"), x, y, null);
	}

	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {
		if(!whoClicked.equals(playerID)){//èe to ni naše
			return;
		}
		
	}
	
}
