package skupno;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;

import client.ScreenSettings;
import client.TabViz;
import server.RunForestRun;
import skupno.Container.Player;
import skupno.teren.Empty;


/**
 * krovni objekt, ki predstavlja eno polje igralne površine
 * @author znidi
 *
 */
public abstract class ZObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 559801226285465345L;
	public boolean isVisible = true;
	public static Color backColor = new Color(190,250,175);
	public String playerID = "-1";//-1 pomeni, da je stvar skupna
	public double hp = 400;
	public double fullHP = 400;
	public boolean canBeAttacked = false;
	

	/**
	 * ta metoda doloèi kako naj se objekt na tem polju nariše 
	 * @param g kam se riše
	 * @param x offset risanja po X osi
	 * @param y offset risanja po Y osi
	 * @param pix koliko pixlov je velik posamezen kvadratek
	 */
	public void risi(Graphics2D g, int x, int y, int pix){
		double wSize = ScreenSettings.getPixPerSquare();	
		g.setColor(backColor);//od parent classa
		g.fillRect(x, y, pix, pix);
		if(hp<fullHP && hp>0){
			g.setColor(Color.RED);
			g.fillRect(x, y, (int)(wSize), (int)(wSize*0.1));
			g.setColor(Color.GREEN);
			g.fillRect(x, y, (int)(wSize * (hp/fullHP)), (int)(wSize*0.1));
		}
		Player p = TabViz.c.playerInfo.get(playerID);
		if(p != null){
			g.setColor(p.color);//dobimo barvo lastnika figure
			g.fillOval((int)x, (int)(y+wSize*0.6), (int)wSize, (int)(wSize*0.4));
		}
		risi2(g, x, y, pix);//poklièemo še risanje, ki je specifièno tipu stavbe
		
	}
	
	
	/**
	 * metoda pove kako se specifièni podarazredi narišejo
	 * @param g
	 * @param x
	 * @param y
	 * @param pix
	 */
	public abstract void risi2(Graphics2D g, int x, int y, int pix);
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public abstract void oneClick(double x, double y, double realX, double realY, String whoClicked);

	/**
	 * funkicija odšteje povzroèeno škodo s preraèunanimi utežmi
	 * èe se je stavba porušila potem vrnemo true in jo odstranimo iz tabele objektov
	 * @param dmg
	 * @param dmgType
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean johnnyCash(double dmg, String dmgType, int x, int y) {
		if (!canBeAttacked) {
			return false;
		}
		switch (dmgType) {
		case "siege":
			hp-=dmg*10;
			break;
		case "melee":
			hp-=dmg*3;
			break;
		case "horse":
			hp-=dmg*1;
			break;

		default:
			hp-=dmg;
		}
		if(hp<0){
			RunForestRun.c.polje[x][y] = new Empty();
			RunForestRun.c.playerInfo.get(playerID).halls.remove(this);//èe je bila stavba mestna hiša jo odstranimo...
			return true;
		}
		return false;
	}
	
	
	
	
	/**
	 * metoda ustvari gumb s sliko.
	 * @param ime
	 * @param iconName
	 * @param obj
	 * @param toolTip
	 * @return
	 */
	public static JButton makeButton(String actionCommand, String iconName, ActionListener obj, String toolTip){
		JButton butt = new JButton();
		butt.setActionCommand(actionCommand);
		butt.addActionListener(obj);
		butt.setIcon(Img.getIcon(iconName));
		butt.setOpaque(false);
		butt.setContentAreaFilled(false);
		butt.setBorderPainted(false);		
		butt.setBorder(null);
		butt.setToolTipText(toolTip);
		return butt;
	}
}
