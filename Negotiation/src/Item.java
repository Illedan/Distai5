import java.util.ArrayList;
import java.util.Collections;

public class Item {

	private static final String[] itemList = { "hammer", "rocket", "sniper", "smallBook", "BigBook", "Chocolate", "Candy", "trolololo", "random" };

	private final String itemName;
	private int value;

	public Item(String name) {
		this.itemName = name;
		this.value = (int) (Math.random() * 10000);
	}

	public Item(String name, int value) {
		this.itemName = name;
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (this.itemName.equals(((Item) obj).getItemName())) {
			return true;
		}
		return false;
	}

	public static ArrayList<Item> createItems() {
		ArrayList<Item> temporaryList = new ArrayList<>();
		for (String item : itemList) {
			temporaryList.add(new Item(item));
		}
		return temporaryList;
	}

	public String getItemName() {
		return itemName;
	}

	public int amountOfItems() {
		return itemList.length;
	}

	public static ArrayList<Item>[] generateRandomItemlists() {
		ArrayList<Item>[] temp = new ArrayList[2];
		ArrayList<Item> wanted = new ArrayList<>();
		ArrayList<Item> owned = new ArrayList<>();
		temp[0] = wanted;
		temp[1] = owned;
		for (String item : itemList) {
			if (Math.random() > 0.8 && wanted.size() < 3) {
				wanted.add(new Item(item));
			} else if (owned.size() < itemList.length - 3) {
				owned.add(new Item(item));
			} else {
				wanted.add(new Item(item));
			}
		}
		return temp;
	}

	public String toString() {
		return itemName + ":" + value;
	}
}
