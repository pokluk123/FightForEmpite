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

public class Horse extends ZWalker implements ActionListener{

	private static final long serialVersionUID = -1712314983676797398L;

	public Horse(String who, double x, double y, String uniqueID){
		super(who, x, y, uniqueID, 90, 0);//kli�emo parent konstruktor

		this.speed = 3*speedMulti;
		this.imgName = "horse";
		this.fullHP = 500;
		this.hp = this.fullHP;
		this.dmgType = "horse";
		this.dmg = 3;
	}
	
	@Override
	public void risiObj(Graphics2D g, int screeTransX, int screenTransY){
		return;
	}
	@Override
	public void oneClick(double x, double y) {

		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti u�inkovitej�e ampak 2019 ipd...		
		JLabel icon = new JLabel(Img.getIcon("horse"));
		icon.setToolTipText("To je konjenik");
		JPanel stats = new JPanel();
		stats.setLayout(new GridLayout(5,2));//naredimo pokon�en seznam elementov
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
		// zaenkrat �e nimamo ni� za klikat
		
	}

}
