package skupno.figure.rasa1;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import client.TabViz;
import skupno.Img;
import skupno.figure.ZWalker;

public class Worker extends ZWalker implements ActionListener{

	private static final long serialVersionUID = -3453814533838059681L;
	private String loadType;
	private int load;
	
	public Worker(String who, double x, double y, String uniqueID){
		super(who, x, y, uniqueID, 30, 0);//klièemo parent konstruktor
		
		this.speed = 2*speedMulti;
		this.imgName = "worker";
		this.fullHP = 50;
		this.hp = this.fullHP;
		this.loadType = "";
		this.load = 0;
	}
	
	@Override
	public void risiObj(Graphics2D g, int screeTransX, int screenTransY){
		return;
	}
	@Override
	public void oneClick(double x, double y) {
		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...
		
		
		JLabel icon = new JLabel(Img.getIcon("worker"));
		icon.setToolTipText("To je navaden delavec");
		JPanel stats = new JPanel();
		stats.setLayout(new GridLayout(5,2));//naredimo pokonèen seznam elementov
		stats.add(new JLabel("HP:"));
		stats.add(new JLabel(""+this.hp));//cast to string
		stats.add(new JLabel("LoadType:"));
		stats.add(new JLabel(this.loadType));
		stats.add(new JLabel("Load:"));
		stats.add(new JLabel(""+this.load));
		
		
		jp.add(icon);	
		jp.add(stats);
		jp.repaint();
		jp.revalidate();
	
		TabViz.changeBottom(jp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// zaenkrat še nimamo niè za klikat
		
	}

}
