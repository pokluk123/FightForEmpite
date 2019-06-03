package skupno.teren;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import client.TabViz;
import skupno.ZObject;
/**
 * razred predstavlja prazno polje na katerem je možno graditi.
 * Ker imamo ob kliku na polje v meniju veè možnosti, ta razred razširja ActionListener,
 * ki se proži vsakiè ko kliknemo na eno izmed možnosti v meniju.
 * @author znidi
 *
 */
public class Empty extends ZObject implements ActionListener{

	private static final long serialVersionUID = -798308575172807131L;
	private static double lastClickX;
	private static double lastClickY;
	
	@Override
	public void risi2(Graphics2D g, int x, int y, int pix) {
		
	}

	/**
	 * ob kliku na to polje narišemo meni
	 */
	@Override
	public void oneClick(double x, double y, double realX, double realY, String whoClicked) {

		JPanel jp = new JPanel();//tukaj zgeneriramo meni. Bi se dalo narediti uèinkovitejše ampak 2019 ipd...
		lastClickX = x;
		lastClickY = y;//statièni spremenljivki, ki se uporabljata za 
		

		
		if(TabViz.c.playerInfo.get(whoClicked).halls.size() != 0){//èe že imamo mestno hišo potem lahko gradimo še druge hiše			
			jp.add(makeButton("hiša", "hisa", this, "V hišah stanujejo delavci. Vsaka hiša lahko nastani do 5 delavcev"));	
			jp.add(makeButton("barake", "barake", this, "V barakah lahko izurimo vojšèake"));
		}
		jp.add(makeButton("townHall", "magistrat", this, "mestna hiša je srce naselbine"));
		
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
		case "hiša":
			TabViz.singelton.send("new_House_"+lastClickX+"_"+lastClickY);
			break;
			
		case "barake":
			TabViz.singelton.send("new_Baracks_"+lastClickX+"_"+lastClickY);
			break;
			
		case "townHall":
			TabViz.singelton.send("new_TownHall_"+lastClickX+"_"+lastClickY);
			break;
		default:
			break;
		}
		
	}
	
}
