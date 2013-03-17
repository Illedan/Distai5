import java.util.ArrayList;


public class Item {

	public static final String[] itemList={"hammer", 
		"rocket", 
		"sniper", 
		"smallBook", 
		"BigBook", 
		"Chocolate",
		"Candy", 
		"trolololo",
		"random",
		""};
	
	public final String itemName;
	public Item(String name){
		this.itemName=name;
	}
	
	public static ArrayList<Item> createItems(){
		ArrayList<Item> tempItemList = new ArrayList<>();
		for (int i = 0; i < itemList.length; i++) {
			for (int j = i; j < itemList.length; j++) {
				tempItemList.add(new Item(itemList[i]+itemList[j]));
			}
		}
		return tempItemList;
	}
}
