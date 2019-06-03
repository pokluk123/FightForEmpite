package skupno;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import skupno.figure.ZWalker;
import skupno.stavbe.TownHall;

/**
 * ta razred vsebuje vse objekte, ki se ob posodobitvah razpo�iljajo vsem uporabnikom.
 *
 */
public class Container implements Serializable{

	private static final long serialVersionUID = 3809350580406874990L;
	
	//spremenjlivke se dostopajo v razli�nih nitih in jih zato ozna�imo z volatile,
	//da zagotovimo enako vrednost spremenljivke v vseh nitih.
	//znajo biti kak�ni problemi s sinhronizacijo vendar sem premalo pla�an, da bi se ukvarjal �e s tem...
	public volatile ZObject[][] polje;
	public volatile Map<String, ZWalker> walkers = Collections.synchronizedMap(new HashMap<String, ZWalker>());
	public volatile Map<String, Player> playerInfo = Collections.synchronizedMap(new HashMap<String, Player>());
	
	//ker se ta razred obna�a bolj kot ne kot struct ga je bolj smoterno imeti kar tukaj
	//kot pa da bi ljudi medli s �e eno datoteko...
	public static class Player implements Serializable{
		private static final long serialVersionUID = -751418963995314458L;
		public ArrayList<TownHall> halls = new ArrayList<TownHall>();
		public String id;
		public double wood;
		public double gold;
		public Color color;
		public int houses;
		public int stWalker;
		public Player(String id, int startWood, int startGold, Color color){
			this.id = id;
			this.wood = startWood;
			this.gold = startGold;
			this.color = color;
			this.houses = 0;
			this.stWalker = 0;
		}
		
		/**
		 * metoda preveri, �e ima igralec vsaj eno stoje�o mestno hi�o
		 */
		public boolean isAlive() {
			boolean alive = true;
			for (TownHall townHall : halls) {
				alive &= townHall.hp>0;
			}
			return alive;
		}
	}
}
