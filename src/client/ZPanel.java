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
 * razred je raz�iritev JPanela zato, da lahko preuredimo metodo paintComponent
 * in tako direktno dostopamo do objekta tipa Graphics na katerega lahko ri�emo
 * svoje grefi�ne elemente. Razred je tudi raz�iritev razreda ActionListener saj
 * lahko tako na eleganten na�in uporabimo �asovnik, ki bo ponovno izrisal
 * �eljene komponente na zaslon vsake x milisekund.
 * Implementiramo pa tudi MouseListener, da lahko zaznamo klike mi�ke
 * 
 * @author znidi
 *
 */
public class ZPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -1435623014596264920L;
	/**�asovnik namenjen ponovnemu izrisavanju
	 */
	private Timer timer = new Timer(32, this);
	private Container c;
	public int screenTransX;
	public int screenTransY;
	public static ZWalker nextClickTarget=null;
	/**
	 * Ko ustvarimo ta objekt za�enemo �asovnik za ponovno izrisovanje.
	 * Prav tako si zabele�imo referenco na tabelo objektov.
	 */
	public ZPanel(Container c) {
		this.c = c;
		this.screenTransX = ScreenSettings.getScreenTransX();
		this.screenTransY = ScreenSettings.getScreenTransY();
		timer.start();//za�nemo render timer
		
		addMouseListener(this);//dodamo ta razred za poslu�alec dogodkov mi�ke.
		//preglednej�e bi bilo za to narediti nov razred vendar je elegantneje
		//to narediti tukaj saj imamo direkten dostop do tabele polje
	}
	
	/**
	 * ko se ta objekt pojavi v Eventu preverimo, �e je storilec na� �asovnik in 
	 * v tem primeru ponovno nari�emo vse na zaslon
	 */
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == timer) {
			TabViz.updateInfo();
			repaint();
		}
	}
	
	
	/**
	 * Metoda razreda JPanel, ki nam postre�e z objektom razreda Graphics na katerega lahko ri�emo
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoard((Graphics2D)(g));
	}

	/**
	 * Na�a metoda, ki nari�e dovoljene zadeve na ekran.
	 * Sprehodimo se �ez tabelo elementov, ki so nam vidni in jih izri�emo na zaslon.
	 * Dr�imo se znanih omejitev in nastavitev
	 * @param g
	 */
	private synchronized void drawBoard(Graphics2D g) {
		
		c = TabViz.c;
		int pix = ScreenSettings.getPixPerSquare();
		for (int i = 0; i < c.polje.length; i++) {//nari�emo nepremi�ne objekte
			for (int j = 0; j < c.polje[i].length; j++) {
				if(c.polje[i][j] == null){//�e imamo obrobo praznih polj naj bo �rna
					g.setColor(Color.BLACK);
					g.fillRect(i*pix-screenTransX, j*pix-screenTransY, pix, pix);
				}else{
					synchronized(c.polje[i][j]){//ker je ta operacija dokaj dolga se lahko vmes kaj spremeni in zato zaklenemo raje objekt
						c.polje[i][j].risi(g, i*pix-screenTransX, j*pix-screenTransY, pix);
					}
				}
			}
		}	
		c.walkers.forEach((key, walker)->{//nari�emo �e premi�ne zadeve
			walker.risi(g, screenTransX, screenTransY);
		});		
	}
	
	
	/**Metoda se bo izvedla ko bo nekdo z mi�ko kliknil znotraj na�ega okna
	 * 
	 */
	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		final double x = e.getX()+screenTransX;//mora biti final, da lahko uporabljamo znotraj forEach bloka
		final double y = e.getY()+screenTransY;
		
		if(e.getClickCount() == 1){	
			if(SwingUtilities.isLeftMouseButton(e)){//leva tipka
				nextClickTarget = null;//�e je kliknjena hi�a ne bomo premikali oseb...
				//najprej preverimo, �e je uporabnik �elel izbrati drugega stri�ka
				c.walkers.forEach((key, walker)->{
					if(walker.getWho().equals(TabViz.whoAmI)){//�e je to res na�a figura
						if(isFigClicked(x, y, walker.getX(), walker.getY())){//�e je bilo kliknjeno dovolj blizu...
							if(!walker.inMine && walker.woods==null){//�e je v rudniku ali seka drevje ga ne moremo klikniti
								walker.oneClick(e.getX(), e.getY());
								nextClickTarget = walker;
								return;
							}
						}
					}
					
				});
				
				if(nextClickTarget == null){//�e nismo kliknili na figuro pogledamo, �e smo kliknili na stavbo
					double xBPix = x/ScreenSettings.getPixPerSquare();//pixle pretvorimo v mesta v tabeli
					double ybPix = y/ScreenSettings.getPixPerSquare();
					//najprej upo�tevamo koliko smo izhodi��e(0,0) na�ega polja �e premaknili
					//tako dobljeno vrednost delimo s �irino posameznega polja, da dobimo mesto
					//tega ZObjekta v tabeli
					ZObject target = c.polje[(int)xBPix][(int)ybPix];
					if(target != null){//�e na tem polju kaj stoji povemo objektu, da je bil kliknjen
						
						target.oneClick(xBPix, ybPix, x, y, TabViz.whoAmI);//tar�i podamo koordinate klika 
						
					}
				}		
				
			}
			if(SwingUtilities.isRightMouseButton(e)){//z desnim klikom premikamo
				
				if(nextClickTarget!=null){//�eje bila ta figura �e prej izbrana 
					nextClickTarget.secondClick(e.getX()+screenTransX, e.getX()+screenTransX);
					TabViz.singelton.send("move_"+nextClickTarget.getID()+"_"+e.getX()+"_"+e.getY());
				
					//�e je bilo kliknjeno kaj drugega to napademo
					c.walkers.forEach((key, walker)->{
						if(!walker.getWho().equals(TabViz.whoAmI)){//�e ni na�
							if(isFigClicked(x, y, walker.getX(), walker.getY())){//�e je bilo kliknjeno dovolj blizu...
								TabViz.singelton.send("attackEntity_"+nextClickTarget.getID()+"_"+walker.getID());
								return;
							}
						}
						
					});
					
					if(true){//�e nismo kliknili na figuro pogledamo, �e smo kliknili na stavbo
						double xBPix = x/ScreenSettings.getPixPerSquare();//pixle pretvorimo v mesta v tabeli
						double yBPix = y/ScreenSettings.getPixPerSquare();
						ZObject target = c.polje[(int)xBPix][(int)yBPix];
						if(target != null){//�e na tem polju kaj stoji povemo objektu, da je bil kliknjen
							if(target instanceof ZEnterable) {//�e se da vstopit v stvbo
								TabViz.singelton.send("interactBuilding_"+nextClickTarget.getID()+"_"+(int)xBPix + "_" + (int)yBPix + "_enter");
							}else{
								if(!target.playerID.equals(TabViz.whoAmI)){//�e ni na�e lahko napademo
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
