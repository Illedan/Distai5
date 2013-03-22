import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractAgent extends Agent {
	public static boolean done = false;
	public static final int startingMoney = 10000;
	private int money;
	private ArrayList<Item> ownedItems;
	private ArrayList<Item> wantedItems;
	private Map<AID, Set<Item>> otherAgentsItem;
	private AID currentTradePartner;

	public AbstractAgent() {
		this.money = startingMoney;
		ownedItems = new ArrayList<>();
		wantedItems = new ArrayList<>();
		otherAgentsItem = new HashMap<AID, Set<Item>>();
		currentTradePartner = null;
	}

	private void handleRequest(ACLMessage m) {
		String[] message = m.getContent().trim().split("-");
		if (Integer.parseInt(message[0]) == TradeProtocol.RequestInventory.value) {
			sendReply(m, ACLMessage.INFORM, TradeProtocol.RequestInventory.value + "-" + this.ownedItems.toString());
		} else if (Integer.parseInt(message[0]) == TradeProtocol.HaveItem.value) {
			String[] splitItemName = message[1].split(":");
			Item item = new Item(splitItemName[0], Integer.parseInt(splitItemName[1]));
			sendReply(m, ACLMessage.INFORM, TradeProtocol.HaveItem.value + "-" + item.toString() + "-" + ownedItems.contains(item));
		}
	}

	private void handleInform(ACLMessage m) {
		if (m.getSender() == getAID()) {
			return;
		}
		String[] message = m.getContent().trim().split("-");
		if (Integer.parseInt(message[0]) == TradeProtocol.RequestInventory.value) {
			Set<Item> otherInventory = new HashSet<Item>();
			String[] items = message[1].replaceAll("\\[\\]", "").split(",");
			for (String item : items) {
				String[] splitItemName = item.split(":");
				otherInventory.add(new Item(splitItemName[0].substring(1, splitItemName[0].length()),
						Integer.parseInt(splitItemName[1].split("]")[0])));
			}
			this.otherAgentsItem.put(m.getSender(), otherInventory);
		} else if (Integer.parseInt(message[0]) == TradeProtocol.HaveItem.value) {
			String[] splitItemName = message[1].split(":");
			Item item = new Item(splitItemName[0], Integer.parseInt(splitItemName[1]));
			boolean agentHas = Boolean.parseBoolean(message[2]);
			if (!this.otherAgentsItem.containsKey(m.getSender())) {
				this.otherAgentsItem.put(m.getSender(), new HashSet<Item>());
			}
			if (agentHas) {
				this.otherAgentsItem.get(m.getSender()).add(item);
			} else {
				this.otherAgentsItem.get(m.getSender()).remove(item);
			}
		}
		decideWhatToDo();
	}

	private void sendReply(ACLMessage msg, int performative, String content) {
		ACLMessage reply = msg.createReply();
		reply.setSender(this.getAID());
		reply.setPerformative(performative);
		reply.setContent(content);
		this.send(reply);
	}

	private boolean isSenderTradePartner(ACLMessage msg) {
		if (msg.getSender().equals(this.currentTradePartner)) {
			return true;
		}
		return false;
	}

	protected void propose(AID receiver, Item want, Item give, int money) {
		String content = ((want == null) ? "" : want.toString()) + "-" + ((give == null) ? "" : give.toString()) + "-" + money;
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setSender(this.getAID());
		msg.addReceiver(receiver);
		msg.setContent(content);
		send(msg);
	}

	protected void handleAcceptedProposal(ACLMessage msg) {
		System.out.println("\n\n" + getAID().getName() + ":: \t" + "HANDLE ACCEPTED PROPOSAL!");
		// if (!isSenderTradePartner(msg)) {
		// System.out.println("AM i agreeing with myself??");
		// return;
		// }
		this.sendReply(msg, ACLMessage.AGREE, msg.getContent());
		System.out.println(getAID().getName() + ":: \t" + "Sending agree");

		String[] content = msg.getContent().split("-");
		Item item1, item2;
		String[] firstItem = content[1].split(":");
		System.out.println("Item: " + Arrays.toString(firstItem));
		if (firstItem[0].length() > 0) {
			item1 = new Item(firstItem[0], Integer.parseInt(firstItem[1]));
		} else {
			item1 = null;
		}
		String[] secondItem = content[2].split(":");
		System.out.println("Item2: " + Arrays.toString(secondItem));
		System.out.println(msg.getContent());
		if (secondItem[0].length() > 0) {
			item2 = new Item(secondItem[0], Integer.parseInt(secondItem[1]));
		} else {
			item2 = null;
		}

		money -= Integer.parseInt(content[3]);
		if (item2 != null)
			this.ownedItems.remove(item2);
		if (item1 != null)
			this.ownedItems.add(item1);
		if (item1 != null)
			otherAgentsItem.get(msg.getSender()).remove(item1);

		this.currentTradePartner = null;
		System.out.println(getAID().getName() + ":: \t" + "Added stuff to my inven, let's see if I'm done.");
		checkIfDone();
	}

	protected void handleAgreedProposal(ACLMessage msg) {
		System.out.println("\n\n" + getAID().getName() + ":: \t" + "HANDLE AGREED PROPOSAL!");
		System.out.println("\n\n" + getAID().getName() + ":: \t" + msg.getContent());
		System.out.println("\n\n" + getAID().getName() + ":: \t" + msg.getPerformative());
		if (!isSenderTradePartner(msg))
			return;

		String[] content = msg.getContent().split("-");
		Item item1, item2;
		String[] firstItem = content[1].split(":");
		if (firstItem[0].length() > 0) {
			item1 = new Item(firstItem[0], Integer.parseInt(firstItem[1]));
		} else {
			item1 = null;
		}
		String[] secondItem = content[2].split(":");
		if (secondItem[0].length() > 0) {
			item2 = new Item(secondItem[0], Integer.parseInt(secondItem[1]));
		} else {
			item2 = null;
		}

		money += Integer.parseInt(content[3]);
		if (item1 != null)
			this.ownedItems.remove(item1);
		if (item2 != null)
			this.ownedItems.add(item2);
		if (item2 != null)
			otherAgentsItem.get(msg.getSender()).remove(item2);

		this.currentTradePartner = null;
		checkIfDone();
	}

	protected void decideWhatToDo() {
		System.out.println("\n\n\n");
		System.out.println(getAID().getName() + ":: \t" + "DECIDE WHAT TO DO!!!");
		if (currentTradePartner != null) {
			return;
		}
		List<Item> itemsIDoNotHave = new ArrayList<>();
		for (Item item : wantedItems) {
			if (!ownedItems.contains(item))
				itemsIDoNotHave.add(item);
		}
		Item itemIWant = itemsIDoNotHave.get(0);
		System.out.println(getAID().getName() + ":: \t" + "I WANT: " + itemIWant);
		System.out.println(getAID().getName() + ":: \t" + "ME: " + getAID());
		for (AID agent : otherAgentsItem.keySet()) {
			System.out.println(getAID().getName() + ":: \t" + "OTHER AGENT: " + agent);
			System.out.println(getAID().getName() + ":: \t" + otherAgentsItem.get(agent));
			for (Item item : otherAgentsItem.get(agent)) {
				if (item.equals(itemIWant)) {
					Item give = null;
					int money = whatIWannaGive(itemIWant, give);
					System.out.println(getAID().getName() + ":: \t" + "I PROPOSED!!!");
					propose(agent, itemIWant, give, money);
					return;
				}
			}
			System.out.println(getAID().getName() + ":: \t" + "MY ITEMS: " + ownedItems);

		}
		System.out.println(getAID().getName() + ":: \t" + "DIDNT FIND ANYTHING =(");
	}

	protected void decideWhatToDoAfterRejection() {
		System.out.println("\n\n" + getAID().getName() + ":: \t" + "DECIDE WHAT TO DO - REJECTION STYLE!");
		List<Item> itemsIDoNotHave = new ArrayList<>();
		for (Item item : wantedItems) {
			if (!ownedItems.contains(item))
				itemsIDoNotHave.add(item);
		}
		Item itemIWant = itemsIDoNotHave.get(0);
		for (AID agent : otherAgentsItem.keySet()) {
			for (Item item : otherAgentsItem.get(agent)) {
				if (item.equals(itemIWant)) {
					Item give = null;
					int money = whatIWannaGive(itemIWant, give);
					System.out.println(getAID().getName() + ":: \t" + "I PROPOSED!!!");
					propose(agent, itemIWant, give, money);
					return;
				}
			}
		}
	}

	// Passing give by reference
	protected int whatIWannaGive(Item want, Item give) {
		int costForItemIWant = want.getValue();
		if (money >= costForItemIWant) {
			return costForItemIWant;
		}
		int tries = 0;
		while (25 > tries++) {
			give = ownedItems.get((int) (Math.random() * ownedItems.size() - 1));
			if (wantedItems.contains(give)) {
				give = null;
				continue;
			}
			int money = Math.max(want.getValue() - give.getValue(), 0);
			if (money > this.money)
				continue;
			return money;
		}
		return 0;
	}

	protected void handleRejectProposal(ACLMessage msg) {
		System.out.println("\n\n" + getAID().getName() + ":: \t" + "HANDLE REJECTION PROPOSAL!");
		if (!isSenderTradePartner(msg))
			return;

		String[] content = msg.getContent().split("-");
		Item item1;
		String[] firstItem = content[0].split(":");
		if (firstItem[0].length() > 0) {
			item1 = new Item(firstItem[0], Integer.parseInt(firstItem[1]));
			// Seems like he wants it for himself (we are not gonna offer anything better anyways)
			otherAgentsItem.get(msg.getSender()).remove(item1);
		} else {
			item1 = null;
		}

		this.currentTradePartner = null;
		decideWhatToDoAfterRejection();
	}

	protected boolean wantDeal(Item proposedItem, Item toGiveAway, int offeredMoney) {
		if (wantedItems.contains(toGiveAway)) {
			return false;
		}
		if (wantedItems.contains(proposedItem) && money >= offeredMoney) {
			return true;
		} else if (offeredMoney <= money && offeredMoney >= toGiveAway.getValue()) {
			return true;
		}
		return false;
	}

	protected void handlePropose(ACLMessage msg) {
		System.out.println("\n\n" + getAID().getName() + ":: \t" + "HANDLE PROPOSAL!");
		String[] content = msg.getContent().split("-");
		Item item1, item2;
		String[] firstItem = content[0].split(":");
		if (firstItem[0].length() > 0) {
			item1 = new Item(firstItem[0], Integer.parseInt(firstItem[1]));
		} else {
			item1 = null;
		}
		String[] secondItem = content[0].split(":");
		if (secondItem[0].length() > 0) {
			item2 = new Item(secondItem[0], Integer.parseInt(secondItem[1]));
		} else {
			item2 = null;
		}
		int money = Integer.parseInt(content[2]);
		if (this.currentTradePartner == null) {
			this.currentTradePartner = msg.getSender();
			if (ownedItems.contains(item1) && wantDeal(item1, item2, money)) {
				System.out.println(getAID().getName() + ":: \t" + "HANDLE PROPOSAL! - Accepting deal");
				this.sendReply(msg, ACLMessage.ACCEPT_PROPOSAL, TradeProtocol.AcceptNegotiation.value + "-" + msg.getContent());
			} else {
				System.out.println(getAID().getName() + ":: \t" + "HANDLE PROPOSAL! - Rejecting deal");
				this.sendReply(msg, ACLMessage.REJECT_PROPOSAL, TradeProtocol.RejectNegotiation.value + "-" + msg.getContent());
			}
		} else if (!msg.getSender().equals(this.currentTradePartner)) {
			// Already conducting trade, come back later
			System.out.println(getAID().getName() + ":: \t" + "HANDLE PROPOSAL! - Rejecting deal!");
			this.sendReply(msg, ACLMessage.REJECT_PROPOSAL, TradeProtocol.RejectNegotiation.value + "-" + msg.getContent());
		}
	}

	protected void checkIfDone() {
		System.out.println("Checking if done!");
		for (Item item : wantedItems) {
			if (!ownedItems.contains(item)) {
				decideWhatToDo();
				return;
			}
		}
		System.out.println("I WON! I AM " + this.getAID() + "\nI have " + money + " money\nMy items are " + ownedItems + " \nAnd i wanted "
				+ wantedItems);
		System.exit(0);
	}

	protected AID[] getAllAgentAids() {
		AMSAgentDescription[] agents = null;

		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
		}
		if (agents == null)
			return null;
		AID[] aids = new AID[agents.length];
		for (int i = 0; i < agents.length; i++) {
			aids[i] = agents[i].getName();
		}
		return aids;
	}

	protected void broadCastRequestForInventories() {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		for (AID agent : getAllAgentAids()) {
			if (getAID() == agent) {
				continue;
			}
			message.addReceiver(agent);
		}
		message.setContent(TradeProtocol.RequestInventory.value + "");
		message.setSender(this.getAID());
		send(message);
	}

	@SuppressWarnings("serial")
	@Override
	protected void setup() {
		super.setup();

		ArrayList<Item>[] items = Item.generateRandomItemlists();
		this.ownedItems = items[1];
		this.wantedItems = items[0];
		System.out.println("Hello, I am a new agent!");
		System.out.println("I own " + ownedItems);
		System.out.println("And I want " + wantedItems);

		this.addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				if (!done) {
					done = true;
					broadCastRequestForInventories();
				}
			}
		});
		this.addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					switch (msg.getPerformative()) {
					case ACLMessage.REQUEST:
						handleRequest(msg);
						break;
					case ACLMessage.INFORM:
						handleInform(msg);
						break;
					case ACLMessage.PROPOSE:
						handlePropose(msg);
						break;
					case ACLMessage.ACCEPT_PROPOSAL:
						handleAcceptedProposal(msg);
						break;
					case ACLMessage.REJECT_PROPOSAL:
						handleRejectProposal(msg);
						break;
					case ACLMessage.AGREE:
						handleAgreedProposal(msg);
						break;
					default:
						break;
					}
				}
				block();
			}
		});
	}
}
