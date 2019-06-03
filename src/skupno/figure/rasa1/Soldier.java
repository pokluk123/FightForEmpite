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

public class Soldier extends ZWalker implements ActionListener{

	private static final long serialVersionUID = -1751883667612527225L;

	public Soldier(String who, double x, double y, String uniqueID){
		super(who, x, y, uniqueID, 50, 0);//klièemo parent konstruktor

		this.speed = 1.75*speedMulti;
		this.imgName = "soldier";
		this.fullHP = 200;
		this.hp = this.fullHP;
		this.dmgType = "melee";
		this.dmg = 2;
	}
	
	@Override
	public void risiObj(Graphics2D g, int screeTransX, int screenTransY){
		return;
	}
	@Override
	public void oneClick(double x, double y) {

		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...		
		JLabel icon = new JLabel(Img.getIcon("soldier"));
		icon.setToolTipText("To je navaden vojak");
		JPanel stats = new JPanel();
		stats.setLayout(new GridLayout(5,2));//naredimo pokonèen seznam elementov
		stats.add(new JLabel("HP:"));
		stats.add(new JLabel(""+this.hp));//cast to string
		stats.add(new JLabel("DmgType:"));
		stats.add(new JLabel(this.dmgType));
		stats.add(new JLabel("Dmg:"));
		stats.add(new JLabel(""+this.dmg));
		
		
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
