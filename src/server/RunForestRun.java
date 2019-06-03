package server;
import skupno.Container;
import skupno.Container.Player;
import skupno.ZEnterable;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import skupno.ZObject;
import skupno.figure.ZWalker;
import skupno.figure.rasa1.Horse;
import skupno.figure.rasa1.Soldier;
import skupno.figure.rasa1.Worker;
import skupno.stavbe.Baracks;
import skupno.stavbe.House;
import skupno.stavbe.TownHall;
import skupno.teren.Empty;
import skupno.teren.Mine;
import skupno.teren.Stone;
import skupno.teren.Trees;

public class RunForestRun extends JFrame implements ActionListener {
	private static final long serialVersionUID = -2613361449324478632L;
	private Timer timer = new Timer(32, this);
	private Timer entityUpdate = new Timer(32, this);
	private static List<ZSoc> cons = Collections.synchronizedList(new ArrayList<ZSoc>());
	private static ServerSocket sSoc = null;
	public static Container c = new Container();
	//zaradi multiThreading ne moremo direktno odstranjevati zadev iz HashMapa torej posrbimo, da jih odtranimo,
	//ko je za to èas najbolj primeren
	public static List<String> walkersToRemoveIDs = Collections.synchronizedList(new ArrayList<String>());
	private static Color[] colors = {
			new Color(160, 155, 255),
			new Color(226, 128, 88),
			new Color(28, 128, 88),
			new Color(213, 44, 255)
	};
	
	
	public static void main(String[] args) {
		int obroba = 0;
		c.polje = new ZObject[40][20];
		for (int i = obroba; i < c.polje.length - obroba; i++) {
			for (int j = obroba; j < c.polje[i].length - obroba; j++) {
				c.polje[i][j] = new Empty();
			}
		}
		c.playerInfo.put("noone", new Player("noone", 5000, 5000, new Color(50, 150, 50)));//spodaj zgeneriramo teren
		for (int i = 0; i < 40; i++) {
			c.polje[(int)(Math.random()*c.polje.length)][(int)(Math.random()*c.polje[0].length)] = new Trees.Hrast();
		}
		for (int i = 0; i < 30; i++) {
			c.polje[(int)(Math.random()*c.polje.length)][(int)(Math.random()*c.polje[0].length)] = new Trees.Smreka();
		}
		for (int i = 0; i < 30; i++) {
			c.polje[(int)(Math.random()*c.polje.length)][(int)(Math.random()*c.polje[0].length)] = new Stone();
		}
		for (int i = 0; i < 10; i++) {
			c.polje[(int)(Math.random()*c.polje.length)][(int)(Math.random()*c.polje[0].length)] = new Mine("noone");
		}
		


		try {
			int port = 0;
			try{
				port = Integer.parseInt(JOptionPane.showInputDialog("localhost"));
			}catch(Exception e){
				port = 0;
			}
			sSoc = new ServerSocket(port==0?5896:port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "vrata, ki jih želite uporabiti so že zasedena.");
			e.printStackTrace();
			return;
		}

		int zapStPlayer = 1;
		new RunForestRun();
		while (true) {
			try {
				Socket soc = sSoc.accept();//ko se nekdo želi povezat
				String genID = "player_" + zapStPlayer;//dobi ID
				ZSoc tmp = new ZSoc(soc, genID);
				c.playerInfo.put(genID, new Player(genID, 700, 200, colors[zapStPlayer%colors.length]));//ustvarimo mu podatke
				cons.add(tmp);
				System.out.println("Con from: " + soc.getInetAddress());
				zapStPlayer++;
			} catch (IOException e) {
				System.out.println("težave pri povezovanju: ");
				e.printStackTrace();
			}
		}
	}

	public RunForestRun() {
		timer.start();
		entityUpdate.start();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			for (int i = cons.size()-1; i >= 0; i--) {		
			// èe gremo od zadaj proti niè se
			// izognemo težavam, ki nastanejo èe
			// vmes brišemo elemente
				ZSoc tmpZ = cons.get(i);
				Socket tmp = tmpZ.getSoc();
				if (tmp.isClosed() || (!tmp.isConnected()) || (!tmp.isBound())) {
					cons.remove(tmpZ);//èe povezava ni aktivna jo odstranimo
				} else {
					try {
						//System.out.println(Arrays.deepToString(polje));
						tmpZ.getOout().reset();
						tmpZ.getOout().writeObject(c);
						tmpZ.getOout().flush();
						
					} catch (Exception e2) {
						System.out.println("Error pri pošiljanju objekta "+tmpZ.getPlayerID());
						//e2.printStackTrace();
						cons.remove(tmpZ);
					}
				}
			}
			
		}
		if (e.getSource() == entityUpdate) {//premaknemo kar je za premaknit
			c.walkers.forEach((key,walker)->{//lamda ftw				
				walker.move();
			});
			for (int i = 0; i < walkersToRemoveIDs.size(); i++) {
				c.walkers.remove(walkersToRemoveIDs.get(i));
			}
			
			c.playerInfo.forEach((key,data)->{
				if(!data.isAlive()){//èe je igralec konaèal z igro mu ne pošiljamo veè podatkov
					for (ZSoc zSoc : cons) {
						if(zSoc.getPlayerID().equals(key)){
							cons.remove(zSoc);
							break;
						}
					}
				};
			});
		}
		
	}
	
	/**
	 * funkcija v polje doda nov objekt glede na 2. del requesta poslaneg na socket.
	 * @param data
	 * @param who
	 */
	public static void create(String[] data, String who){
		if(c.playerInfo.get(who).gold<0 || c.playerInfo.get(who).wood<0){
			return;//èe je igralec v minusu ne more kupiti nièesar
		}
		String request2 = data[1];
		double x = Double.parseDouble(data[2]);
		double y = Double.parseDouble(data[3]);
		String uniqueID = UUID.randomUUID().toString();//Overkill but still
		switch (request2) {
		case "House":
			if(c.playerInfo.get(who).wood>40){//cena hiše
				if(c.polje[(int)x][(int)y] instanceof Empty){//èe tukaj sploh lahko zidamo
					c.polje[(int)x][(int)y] = new House(who);
					c.playerInfo.get(who).wood-=40;
				}
			}

			break;
			
		case "Baracks":	
			if(c.playerInfo.get(who).wood>70){//cena barak
				if(c.polje[(int)x][(int)y] instanceof Empty){
					c.polje[(int)x][(int)y] = new Baracks(who);
					c.playerInfo.get(who).wood-=70;
				}
			}
			break;
			
		case "TownHall":	
			if(c.playerInfo.get(who).wood>500){//cena
				if(c.polje[(int)x][(int)y] instanceof Empty){
					TownHall tmp = new TownHall(who);
					c.polje[(int)x][(int)y] = tmp;
					c.playerInfo.get(who).halls.add(tmp);
					c.playerInfo.get(who).wood-=500;
				}
			}
			break;
			
		case "Worker1":	
			if(c.playerInfo.get(who).houses-1>=c.playerInfo.get(who).stWalker){//preverimo, èe ima igralec dovolj prostora za nove vojake
				c.walkers.put(uniqueID, new Worker(who, x, y, uniqueID));
				c.playerInfo.get(who).stWalker++;
			}
			break;	
			
		case "Soldier1":
			if(c.playerInfo.get(who).houses-2>=c.playerInfo.get(who).stWalker){//preverimo, èe ima igralec dovolj prostora za nove vojake
				c.walkers.put(uniqueID, new Soldier(who, x, y, uniqueID));
				c.playerInfo.get(who).stWalker+=2;
			}
			break;
			
		case "Horse1":
			if(c.playerInfo.get(who).houses-4>=c.playerInfo.get(who).stWalker){//preverimo, èe ima igralec dovolj prostora za nove vojake
				c.walkers.put(uniqueID, new Horse(who, x, y, uniqueID));
				c.playerInfo.get(who).stWalker+=4;
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * metoda premakne figuro s tem IDjem glede na podatke prejete preko socketa
	 * @param split
	 * @param playerID
	 */
	public static void moveEntity(String[] split, String playerID) {
		String whoID = split[1];
		double x = Double.parseDouble(split[2]);
		double y = Double.parseDouble(split[3]);
		ZWalker tmp = c.walkers.get(whoID);
		if(tmp!=null){
			tmp.secondClick(x, y);
		}
		
	}
	
	/**
	 * metoda uredi, da bo figura napadla nasprotnika, ko bo prehodila svojo pot
	 * @param split
	 * @param playerID
	 */
	public static void attackEntity(String[] split, String playerID) {
		String whoID = split[1];
		String defenderID = split[2];
		ZWalker tmp = c.walkers.get(whoID);
		if(tmp!=null){
			tmp.targetID = defenderID;
		}
		
	}
	
	/**
	 * metoda doda/odvzame primerne kolièine denarja in lesa isgralcu s tem IDjem
	 * @param who
	 * @param gold
	 * @param wood
	 */
	public static void money(String who, int gold, int wood) {
		c.playerInfo.get(who).gold+=gold;
		c.playerInfo.get(who).wood+=wood;
		
	}
	
	/**
	 * neka entiteta želi vstopiti ali pa napasti stavbo
	 * @param split
	 * @param playerID
	 */
	public static void interactBuilding(String[] split, String playerID) {
		String whoID = split[1];
		double x = Double.parseDouble(split[2]);
		double y = Double.parseDouble(split[3]);
		String whatToDo = split[4];
		ZWalker tmp = c.walkers.get(whoID);
		if(tmp!=null){
			tmp.targetX = (int)x;
			tmp.targetY = (int)y;
			tmp.targetDo = whatToDo;
		}
		
	}
	
	/**
	 * delavca želimo vzeti ven iz stavbe
	 * @param split
	 * @param playerID
	 */
	public static void takeOut(String[] split, String playerID) {
		String whoID = split[1];
		double x = Double.parseDouble(split[2]);
		double y = Double.parseDouble(split[3]);
		ZWalker tmp = c.walkers.get(whoID);
		System.out.println("odstranili bomo" + tmp);
		if(tmp!=null){
			((ZEnterable)(c.polje[(int)x][(int)y])).remove(whoID, (Worker)(tmp));
		}
		
	}
}
