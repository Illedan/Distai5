import java.util.ArrayList;

import jade.core.Agent;


public class AbstractAgent extends Agent{
	public static final int startingMoney = 10000;
	protected int money;
	protected ArrayList<Item> owndItems;
	protected ArrayList<Item> wantedItems;
	
	public AbstractAgent(){
		this.money = startingMoney;
		owndItems = new ArrayList<>();
		wantedItems = new ArrayList<>();
		
	}
	
	public void giveItem(Item item){
		this.owndItems.add(item);
	}
	public void wantThisItem(Item item){
		this.wantedItems.add(item);
	}
	
	@Override
	protected void setup() {
		//start here.
	}
}
