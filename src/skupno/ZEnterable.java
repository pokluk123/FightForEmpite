package skupno;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import client.ScreenSettings;
import client.TabViz;
import skupno.figure.rasa1.Worker;
import skupno.teren.Mine;
import skupno.teren.Trees;

public abstract class ZEnterable extends ZObject implements ActionListener {

	private static final long serialVersionUID = 1224436068745173251L;

	protected HashMap<String,Worker> inside = new HashMap<String,Worker>();
	private static double lastClickX;
	private static double lastClickY;
	
	/**
	 * ob kliku na to polje narišemo meni
	 */
	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {

		
		lastClickX = realX;
		lastClickY = realY;//statièni spremenljivki, ki se uporabljata za prenos vrednosti v actionPerformed
		
		showMenu();
	}
	
	/**
	 * metoda osveži meni na podatke te zgradbe
	 */
	public void showMenu(){
		
		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...
		inside.forEach((key, worker)->{
			JButton del = new JButton();//ustvarimo gumbe in jih dodamo na primeren jpanel
			del.setActionCommand(key);
			del.addActionListener(this);
			del.setIcon(Img.getIcon("worker"));
			del.setOpaque(false);
			del.setContentAreaFilled(false);
			del.setToolTipText(worker.getWho().equals(playerID)?"Tvoj delavec pridno dela":"Nemarni sovražni zabušant, ki nam krade dobrine");	
			del.setBorder(new LineBorder(TabViz.c.playerInfo.get(worker.getWho()).color, 10));
			jp.add(del);	
			
		});
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
		Worker w = inside.get(e.getActionCommand());
		if (w!=null && w.getWho().equals(TabViz.whoAmI)) {
			double xBPix = lastClickX/ScreenSettings.getPixPerSquare();//pixle pretvorimo v mesta v tabeli
			double yBPix = lastClickY/ScreenSettings.getPixPerSquare();
			TabViz.singelton.send("takeOut_"+w.getID()+"_"+xBPix+"_"+yBPix);
		}		
	}
	
	public void enter(String whoID, Worker w) {
		inside.put(whoID, w);
		if(this instanceof Trees){
			w.woods = (Trees) this;
		}
		if(this instanceof Mine){
			w.inMine = true;
		}
		
	}


	public void remove(String whoID, Worker worker) {
		inside.remove(whoID);
		worker.inMine=false;
		worker.woods=null;
		showMenu();//osvežimo meni, ker je zdaj notri en delavec manj
	}
	
	public void removeAll(){
		inside.forEach((id, w)->{
			w.inMine=false;
			w.woods=null;
		});
		inside = new HashMap<String, Worker>();	
	}
}
