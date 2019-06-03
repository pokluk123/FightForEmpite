package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import skupno.Container;
import skupno.Img;
import skupno.ZObject;

/**
 * razred namenjem vizualizaciji igralnega polja
 * @author znidi
 *
 */
public class TabViz extends JFrame{

	private static final long serialVersionUID = 4341403121128185148L;

	transient static int lol = 10;
	
	private Socket soc;
	//private Socket socOut;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public static Container c = new Container();
	private ZPanel pan;
	private static JPanel bottom;//ker se elementi teh panelov pogosto spreminjajo je vredno
	private static JPanel bottomMenu;//obdržati kar statièno referenco na njih
	private static HashMap<String, JLabel> bottomInfo = new HashMap<String, JLabel>();
	public static String whoAmI;
	public static TabViz singelton;
	private static boolean bottomInitDone = false;
	private static boolean connected = false;
	
	public static void main(String[] args) throws InterruptedException {
		ScreenSettings.init();
		Img.loadAllImages();
		
		String ip = JOptionPane.showInputDialog("vnesi IP strežnika");
		int port = 0;
		try{
			port = Integer.parseInt(JOptionPane.showInputDialog("vnesi port"));
		}catch(Exception e){
			port = 0;
		}
		try {
			singelton = new TabViz(10, 50, ip.equals("")?"89.212.108.240":ip, port==0?5896:port);
			singelton.blockThread();
		} catch (IOException | ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public TabViz(int tabW, int tabH, String hostname, int port) throws InterruptedException, UnknownHostException, IOException, ClassNotFoundException{

		c.polje = new ZObject[tabW][tabH];
		
		pan = new ZPanel(c);//igralna plošèa
		
		bottom = new JPanel();//spodnji meni
		bottom.setMinimumSize(new Dimension(600, 150));//nastavimo fiksno višino spodnjega menija
		bottom.setPreferredSize(new Dimension(600, 150));
		bottom.setMaximumSize(new Dimension(999999, 150));
		bottom.setBackground(new Color(233, 234, 178));
		bottom.setLayout(new BorderLayout());
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(5, 2));
		JLabel goldStr = new JLabel("gold:");//tukaj naredimo tabelo informacij
		JLabel goldVal = new JLabel("0");
		JLabel woodStr = new JLabel("wood:");
		JLabel woodVal = new JLabel("0");
		JLabel proStr = new JLabel("prostor:");
		JLabel proVal = new JLabel("0");
		JLabel popStr = new JLabel("populacija:");
		JLabel popVal = new JLabel("0");
		infoPanel.add(goldStr);
		infoPanel.add(goldVal);
		infoPanel.add(woodStr);
		infoPanel.add(woodVal);
		infoPanel.add(proStr);
		infoPanel.add(proVal);
		infoPanel.add(popStr);
		infoPanel.add(popVal);
		bottomInfo.put("goldStr", goldStr);//JLabele shranimo v hash map, da jih lažje posodabljamo
		bottomInfo.put("gold", goldVal);
		bottomInfo.put("woodStr", woodStr);
		bottomInfo.put("wood", woodVal);
		bottomInfo.put("proStr", proStr);
		bottomInfo.put("pro", proVal);
		bottomInfo.put("popStr", popStr);
		bottomInfo.put("pop", popVal);
		infoPanel.setBorder(new EmptyBorder(10,10,10,10));//naredimo prazno obrobo okrog elementa
		bottomMenu = new JPanel();//tukaj inicializiramo, dodajamo pa spodaj v metodi changeBottom
		bottom.add(infoPanel, BorderLayout.LINE_START);
		bottom.add(bottomMenu, BorderLayout.CENTER);//dodamo komponenta na dno
		bottomInitDone = true;
		
		//ker smo razširili JFrame lahko direktno nastavljamo parametre
		setTitle("D game");
		setSize(ScreenSettings.getWidth(), ScreenSettings.getHeight());
		setMinimumSize(new Dimension(600, 600));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		BorderLayout mainLayout = new BorderLayout();
		getContentPane().setLayout(mainLayout);
		add(pan, BorderLayout.CENTER);
		add(bottom, BorderLayout.PAGE_END);

		soc = new Socket(hostname, port);//ustvarimo povazavo na strežnik
		oos = new ObjectOutputStream(new BufferedOutputStream(soc.getOutputStream()));
		oos.flush();//potegnemo vodo, da lahko nasprotna stran iz headerja prebere, da se 
		//je na tej strani ustvaril OutputStream. Èe tega ne storimo ali pa najprej ustvarimo
		//InputStream potem zaène Socket blokirati, saj nikoli ne pošlje headerjev...
		ois = new ObjectInputStream(new BufferedInputStream(soc.getInputStream()));
		System.out.println("na clientu konèano ustvarjanje Input/Output streamov");
		whoAmI = (String) ois.readObject();//prejmemo svoj playerID
		System.out.println("dobil sem id: " + whoAmI);
	}
	public void blockThread(){
		System.out.println("zaèenjamo poslušati za podatke");
		while(true){			
			try {
				c = (Container) ois.readObject();
				connected = true;
				if(!c.playerInfo.get(whoAmI).isAlive()){
					setContentPane(generateLostScreen());
				}
				//System.out.println(Arrays.deepToString(c.polje));
			} catch (ClassNotFoundException | IOException e) {		
				e.printStackTrace();
				return;
			}
		}
	}
	private JPanel generateLostScreen() {
		JPanel end = new JPanel();
		end.setLayout(new BorderLayout());
		JLabel youLost = new JLabel("You lost");
		youLost.setFont(new Font("Arial", Font.BOLD, 50));
		end.add(youLost, BorderLayout.CENTER);
		return end;
	}

	/**
	 * zamenjamo Meni jpanel na dnu 
	 * @param newBot
	 */
	public static void changeBottom(JPanel newBot){
		bottomMenu.removeAll();
		bottomMenu.add(newBot);
		bottomMenu.repaint();
		bottomMenu.revalidate();
	}
	
	/**
	 * pošljemo String èez socket.
	 * Lahko bi to poèeli s PrintWriterjem vendar nam zaenkrat String še zadostuje.
	 * Èe se v prihodnosti npr. odloèimo ustvariti razred Request...
	 * @param zaPoslat
	 */
	public void send(String zaPoslat){
		try{
			oos.writeObject(zaPoslat);
			oos.flush();//potegnemo vodo
		}catch(Exception e){
			System.out.println("Error pri pošiljanju");
			e.printStackTrace();
		}
	}
	
	/**
	 * posodobi finanèno stanje igralca (TODO: še za druge igralce)
	 */
	public static void updateInfo() {
		if(!bottomInitDone || !connected){//èe še ni dokonèano nalaganje in še nimamo podatkov ne moremo posodabljati polja.
			return;
		}
		//iz bottom info dobimopolje, ki ga želimo spreminjati
		//iz prejetega c.playerInfo pa vzamemo objekt z našim IDjem
		bottomInfo.get("gold").setText(""+(int)c.playerInfo.get(whoAmI).gold);
		bottomInfo.get("wood").setText(""+(int)c.playerInfo.get(whoAmI).wood);
		bottomInfo.get("pro").setText(""+c.playerInfo.get(whoAmI).houses);
		bottomInfo.get("pop").setText(""+c.playerInfo.get(whoAmI).stWalker);
		
	}
	
	
	
}
