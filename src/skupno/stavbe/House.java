package skupno.stavbe;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import client.TabViz;
import server.RunForestRun;
import skupno.Img;
import skupno.ZObject;

public class House extends ZObject implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5454326349351253562L;
	private static double lastClickX;
	private static double lastClickY;
	
	
	public House(String playerID){
		this.playerID = playerID;
		RunForestRun.c.playerInfo.get(playerID).houses+=5;
		this.canBeAttacked = true;
		this.fullHP = 500;
		this.hp = this.fullHP;
	}
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		g.drawImage(Img.getImage("hisa"), x, y, null);
	}

	/**
	 * ob kliku na to polje narišemo meni
	 */
	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {
		if(!whoClicked.equals(playerID)){//èe to ni naše
			return;
		}
		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...
		lastClickX = realX;
		lastClickY = realY;//statièni spremenljivki, ki se uporabljata za prenos vrednosti v actionPerformed
		
		
		jp.add(makeButton("delavec1", "worker", this, "Navaden delavec lahko pridobiva les ali rudo."));	
		jp.repaint();
		jp.revalidate();
	
		TabViz.changeBottom(jp);
	}
	
	
	/**
	 * Gumbom bi sicer lahko dodajali posamezne ActionListenerje 
	 * ampak smo za voljo èitljivejše kode vse klike na gumb združili v tej metodi
	 * in jih razlikujemo po ActionComaand Stringu...
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "delavec1":
			TabViz.singelton.send("new_Worker1_"+lastClickX+"_"+lastClickY);
			break;
		
		default:
			break;
		}
		
	}

}
