package skupno.figure;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import client.ScreenSettings;
import client.TabViz;
import server.RunForestRun;
import skupno.Img;
import skupno.ZEnterable;
import skupno.figure.rasa1.Worker;
import skupno.teren.Trees;

public abstract class ZWalker implements Serializable {
	
	private static final long serialVersionUID = -3403458260769808212L;
	public double speedMulti = 1.5;//èe spreminjamo hitrost osvežitev moramo spremeniti tudi hitrost premikanja
	public String targetID;//id entitete, ki jo bomo napadli
	public int targetX;//koordinati stavbe v katero gremo/jo bomo napadli
	public int targetY;
	public String targetDo = "";//kaj naredimo ko pridemo do tarèe
	protected String who;//id lastnika te figure
	protected double x;//lokacija figure
	protected double y;//-||-
	protected double speed;//se meri v razdalja/frameRefresh
	protected volatile double kotSprehoda;
	public int kolikoKorakovSe;
	protected String imgName;
	protected String id;
	protected double hp;
	protected double fullHP;
	protected String dmgType;
	protected double dmg;
	public boolean inMine = false;
	public Trees woods = null;
		
	/**
	 * pri ustvarjanju podamo lastnika, koordinati kjer želimo novo figuro, njen ID in ceno lesa ter zlata
	 * @param who
	 * @param x
	 * @param y
	 * @param id
	 * @param price
	 */
	public ZWalker(String who, double x, double y, String id, int priceGold, int priceWood){
		this.who = who;
		this.id = id;
		this.x = x;
		this.y = y;
		RunForestRun.money(who, -priceGold, -priceWood);
	}
	public String getID(){
		return id;
	}
	
	public void risi(Graphics2D g, int screeTransX, int screenTransY){
		if(inMine || woods!=null) {
			return;
		}
		double wSize = ScreenSettings.getWalkerSize();
		g.setColor(Color.RED);
		g.fillRect((int)x, (int)(y), (int)(wSize), (int)(wSize*0.1));
		g.setColor(Color.GREEN);
		g.fillRect((int)x, (int)(y), (int)(wSize * (hp/fullHP)), (int)(wSize*0.1));
		g.setColor(TabViz.c.playerInfo.get(who).color);//dobimo barvo lastnika figure
		g.fillOval((int)x, (int)(y+wSize*0.8), (int)wSize, (int)(wSize*0.3));
		g.drawImage(Img.getImage(imgName), (int)(x-screeTransX), (int)(y-screenTransY), null);
		risiObj(g, screeTransX, screenTransY);//poklièemo še risanje, ki je specifièno tipu walkerja
	}
	
	/**
	 * èe bi sluèajno želili narisati še kaj objektu specifiènega poleg slike
	 * @param g
	 * @param screeTransX
	 * @param screenTransY
	 */
	public abstract void risiObj(Graphics2D g, int screeTransX, int screenTransY);

	public abstract void oneClick(double x, double y);

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public String getWho() {
		return who;
	}

	public void secondClick(double x2, double y2){
		double dist = Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
		kolikoKorakovSe = (int)(dist/speed);//kolikokrat se bomo morali premakniti da pridemo do cilja
		kotSprehoda = Math.atan2(y2-y, x-x2)+Math.toRadians(180);//kot med daljico sprehoda in osjo x;
		//atan2 nam vrne kot za polarne koordinate toèke èe mu podamo kartezièni koordinati
		//zato od kliknjene toèke odštejemo trenutno pozicijo in tako daljico sprehoda 
		//premaknemo v koordinatno izhodišèe, kjer lahko izraèunamo kot...
		targetID = null; //Èe se premikamo ne moremo napadat...
		//zato se ukaz za napad pošlje po ukazi za premikanje...
	}
	/**
	 * metoda premakne objekt glede na njegov kot premikanja in hitrost
	 */
	public void move() {
		if(kolikoKorakovSe>0){//se premikamo
			x += speed*Math.cos(kotSprehoda);
			y += -speed*Math.sin(kotSprehoda);//minus ker je y os invertirana...
			kolikoKorakovSe--;		
		}	
		if(kolikoKorakovSe<=0){
			if(targetID!=null){
				ZWalker tmp = RunForestRun.c.walkers.get(targetID);
				if(tmp == null){//èe je izginil ga ne rabimo veè poskušat ubit..
					targetID = null;
				}else{
					tmp.hp-=this.dmg;
					if(tmp.hp<=0){
						synchronized (RunForestRun.c.walkers) {
							RunForestRun.walkersToRemoveIDs.add(targetID);//èe smo ga ubili ga odstranimo...
						}
						targetID = null;
					}
				}
			}
			if (targetDo.equals("enter")) {//èe smo se namenili vstopit vstopimo
				if(this instanceof Worker){
					((ZEnterable)(RunForestRun.c.polje[targetX][targetY])).enter(this.id, (Worker)(this));
					targetX = -1;
					targetY = -1;
					targetDo = "";
				}
			}
			if (targetDo.equals("attack")) {//èe smo se namenili vstopit vstopimo	
				if(RunForestRun.c.polje[targetX][targetY].johnnyCash(dmg, dmgType, targetX, targetY)){//hurt 
					//èe smo jo podrli
					targetX = -1;
					targetY = -1;
					targetDo = "";
				}
			}
		}
		if(inMine) {
			RunForestRun.c.playerInfo.get(who).gold+=0.05;
		}
		
		if(woods!=null) {
			RunForestRun.c.playerInfo.get(who).wood+=0.03;
			woods.chop(0.03);
		}
	}
	
}
