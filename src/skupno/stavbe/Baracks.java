package skupno.stavbe;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import client.TabViz;
import skupno.Img;
import skupno.ZObject;

public class Baracks extends ZObject implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7050053241673103992L;
	private static double lastClickX;
	private static double lastClickY;
	
	public Baracks(String playerID){
		this.playerID = playerID;
		this.fullHP = 800;
		this.hp = this.fullHP;
		this.canBeAttacked = true;
	}
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		g.drawImage(Img.getImage("barake"), x, y, null);
	}

	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {
		if(!whoClicked.equals(playerID)){//èe to ni naše
			return;
		}
		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...
		lastClickX = realX;
		lastClickY = realY;//statièni spremenljivki, ki se uporabljata za prenos vrednosti v actionPerformed
		
		
		jp.add(makeButton("soldier1", "soldier", this, "Navaden vojak je poèasen a vzdržljiv"));
		jp.add(makeButton("horse1", "horse", this, "Vojak na konju je hiter in povzroèa veè škode"));
		jp.repaint();
		jp.revalidate();
	
		TabViz.changeBottom(jp);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "soldier1":
			TabViz.singelton.send("new_Soldier1_"+lastClickX+"_"+lastClickY);
			break;
			
		case "horse1":
			TabViz.singelton.send("new_Horse1_"+lastClickX+"_"+lastClickY);
			break;
		
		default:
			break;
		}
		
	}

}
