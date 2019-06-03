package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import skupno.Container;
import skupno.ZEnterable;
import skupno.ZObject;
import skupno.figure.ZWalker;

/**
 * razred je razširitev JPanela zato, da lahko preuredimo metodo paintComponent
 * in tako direktno dostopamo do objekta tipa Graphics na katerega lahko rišemo
 * svoje grefiène elemente. Razred je tudi razširitev razreda ActionListener saj
 * lahko tako na eleganten naèin uporabimo èasovnik, ki bo ponovno izrisal
 * željene komponente na zaslon vsake x milisekund.
 * Implementiramo pa tudi MouseListener, da lahko zaznamo klike miške
 * 
 * @author znidi
 *
 */
public class ZPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -1435623014596264920L;
	/**Èasovnik namenjen ponovnemu izrisavanju
	 */
	private Timer timer = new Timer(32, this);
	private Container c;
	public int screenTransX;
	public int screenTransY;
	public static ZWalker nextClickTarget=null;
	/**
	 * Ko ustvarimo ta objekt zaženemo Èasovnik za ponovno izrisovanje.
	 * Prav tako si zabeležimo referenco na tabelo objektov.
	 */
	public ZPanel(Container c) {
		this.c = c;
		this.screenTransX = ScreenSettings.getScreenTransX();
		this.screenTransY = ScreenSettings.getScreenTransY();
		timer.start();//zaènemo render timer
		
		addMouseListener(this);//dodamo ta razred za poslušalec dogodkov miške.
		//preglednejše bi bilo za to narediti nov razred vendar je elegantneje
		//to narediti tukaj saj imamo direkten dostop do tabele polje
	}
	
	/**
	 * ko se ta objekt pojavi v Eventu preverimo, èe je storilec naš èasovnik in 
	 * v tem primeru ponovno narišemo vse na zaslon
	 */
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == timer) {
			TabViz.updateInfo();
			repaint();
		}
	}
	
	
	/**
	 * Metoda razreda JPanel, ki nam postreže z objektom razreda Graphics na katerega lahko rišemo
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoard((Graphics2D)(g));
	}

	/**
	 * Naša metoda, ki nariše dovoljene zadeve na ekran.
	 * Sprehodimo se èez tabelo elementov, ki so nam vidni in jih izrišemo na zaslon.
	 * Držimo se znanih omejitev in nastavitev
	 * @param g
	 */
	private synchronized void drawBoard(Graphics2D g) {
		
		c = TabViz.c;
		int pix = ScreenSettings.getPixPerSquare();
		for (int i = 0; i < c.polje.length; i++) {//narišemo nepremiène objekte
			for (int j = 0; j < c.polje[i].length; j++) {
				if(c.polje[i][j] == null){//èe imamo obrobo praznih polj naj bo èrna
					g.setColor(Color.BLACK);
					g.fillRect(i*pix-screenTransX, j*pix-screenTransY, pix, pix);
				}else{
					synchronized(c.polje[i][j]){//ker je ta operacija dokaj dolga se lahko vmes kaj spremeni in zato zaklenemo raje objekt
						c.polje[i][j].risi(g, i*pix-screenTransX, j*pix-screenTransY, pix);
					}
				}
			}
		}	
		c.walkers.forEach((key, walker)->{//narišemo še premiène zadeve
			walker.risi(g, screenTransX, screenTransY);
		});		
	}
	
	
	/**Metoda se bo izvedla ko bo nekdo z miško kliknil znotraj našega okna
	 * 
	 */
	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		final double x = e.getX()+screenTransX;//mora biti final, da lahko uporabljamo znotraj forEach bloka
		final double y = e.getY()+screenTransY;
		
		if(e.getClickCount() == 1){	
			if(SwingUtilities.isLeftMouseButton(e)){//leva tipka
				nextClickTarget = null;//èe je kliknjena hiša ne bomo premikali oseb...
				//najprej preverimo, èe je uporabnik želel izbrati drugega strièka
				c.walkers.forEach((key, walker)->{
					if(walker.getWho().equals(TabViz.whoAmI)){//èe je to res naša figura
						if(isFigClicked(x, y, walker.getX(), walker.getY())){//èe je bilo kliknjeno dovolj blizu...
							if(!walker.inMine && walker.woods==null){//èe je v rudniku ali seka drevje ga ne moremo klikniti
								walker.oneClick(e.getX(), e.getY());
								nextClickTarget = walker;
								return;
							}
						}
					}
					
				});
				
				if(nextClickTarget == null){//èe nismo kliknili na figuro pogledamo, èe smo kliknili na stavbo
					double xBPix = x/ScreenSettings.getPixPerSquare();//pixle pretvorimo v mesta v tabeli
					double ybPix = y/ScreenSettings.getPixPerSquare();
					//najprej upoštevamo koliko smo izhodišèe(0,0) našega polja že premaknili
					//tako dobljeno vrednost delimo s širino posameznega polja, da dobimo mesto
					//tega ZObjekta v tabeli
					ZObject target = c.polje[(int)xBPix][(int)ybPix];
					if(target != null){//èe na tem polju kaj stoji povemo objektu, da je bil kliknjen
						
						target.oneClick(xBPix, ybPix, x, y, TabViz.whoAmI);//tarèi podamo koordinate klika 
						
					}
				}		
				
			}
			if(SwingUtilities.isRightMouseButton(e)){//z desnim klikom premikamo
				
				if(nextClickTarget!=null){//èeje bila ta figura že prej izbrana 
					nextClickTarget.secondClick(e.getX()+screenTransX, e.getX()+screenTransX);
					TabViz.singelton.send("move_"+nextClickTarget.getID()+"_"+e.getX()+"_"+e.getY());
				
					//èe je bilo kliknjeno kaj drugega to napademo
					c.walkers.forEach((key, walker)->{
						if(!walker.getWho().equals(TabViz.whoAmI)){//èe ni naš
							if(isFigClicked(x, y, walker.getX(), walker.getY())){//èe je bilo kliknjeno dovolj blizu...
								TabViz.singelton.send("attackEntity_"+nextClickTarget.getID()+"_"+walker.getID());
								return;
							}
						}
						
					});
					
					if(true){//èe nismo kliknili na figuro pogledamo, èe smo kliknili na stavbo
						double xBPix = x/ScreenSettings.getPixPerSquare();//pixle pretvorimo v mesta v tabeli
						double yBPix = y/ScreenSettings.getPixPerSquare();
						ZObject target = c.polje[(int)xBPix][(int)yBPix];
						if(target != null){//èe na tem polju kaj stoji povemo objektu, da je bil kliknjen
							if(target instanceof ZEnterable) {//èe se da vstopit v stvbo
								TabViz.singelton.send("interactBuilding_"+nextClickTarget.getID()+"_"+(int)xBPix + "_" + (int)yBPix + "_enter");
							}else{
								if(!target.playerID.equals(TabViz.whoAmI)){//èe ni naše lahko napademo
									TabViz.singelton.send("interactBuilding_"+nextClickTarget.getID()+"_"+(int)xBPix + "_" + (int)yBPix + "_attack");
								}
							}
						}
					}
				}
				
			}
			
		}
		
	}
	
	public static boolean isFigClicked(double x, double y, double x2, double y2){
		double halfX = ScreenSettings.getWalkerSize()/2;
		double halfY = ScreenSettings.getWalkerSize()/2;//isto ampak lahko kdaj spremenimo in walkerji ne bodo kvadratni...
		x2 += halfX;
		y2 += halfY;
		double dist = Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
		
		return dist < ScreenSettings.getClickDistance();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}
