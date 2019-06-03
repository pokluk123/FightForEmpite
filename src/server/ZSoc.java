package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ZSoc implements Runnable{
	private Socket soc;
	private ObjectOutputStream oout;
	private ObjectInputStream in;
	private int errCount = 0;
	private String playerID;
	private Thread t;
	
	public ZSoc(Socket soc, String genID) throws IOException{
		this.soc = soc;
		oout = new ObjectOutputStream(new BufferedOutputStream(soc.getOutputStream()));
		oout.flush();
		in = new ObjectInputStream(new BufferedInputStream(soc.getInputStream()));
		System.out.println("Input/OutputStream ustvarjen na strežniku");
		
		oout.writeObject(genID);//poljemo èez id
		oout.flush();
		this.playerID = genID;
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * @return the soc
	 */
	public Socket getSoc() {
		return soc;
	}
	
	public String getPlayerID(){
		return playerID;
	}
	
	/**
	 * @return the oout
	 */
	public ObjectOutputStream getOout() {
		return oout;
	}

	/**
	 * @return the errCount
	 */
	public int getErrCount() {
		return errCount;
	}
	@Override
	public void run() {
		try{
			while (true) {
				String prejeto = (String) in.readObject();
				String[] split = prejeto.split("_");
				String request1 = split[0];
				System.out.println("request1: " + request1);
				switch (request1) {
				case "new":
					RunForestRun.create(split, playerID);
					break;
					
				case "move":
					RunForestRun.moveEntity(split, playerID);
					break;
					
				case "attackEntity":
					RunForestRun.attackEntity(split, playerID);
					break;
				
				case "interactBuilding":
					RunForestRun.interactBuilding(split, playerID);
					break;
				
				case "takeOut":
					RunForestRun.takeOut(split, playerID);
					break;
					
				default:
					System.out.println("Nepoznan ukaz");
					break;
				}
			}
		}catch(Exception e){//èe se socket zapre
			System.out.println("težava pri branju");
			e.printStackTrace();
		}
		
	}

}
