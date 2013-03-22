public enum TradeProtocol {
	RequestInventory(0), AcceptNegotiation(1), RejectNegotiation(2), RejectProposal(3), InNegotiation(4), HaveItem(5);
	public final int value;

	TradeProtocol(int i) {
		value = i;
	}

	public static TradeProtocol get(int i) {
		return i == 0 ? RequestInventory : i == 1 ? AcceptNegotiation : i == 2 ? RejectNegotiation : i == 3 ? RejectProposal : i == 4 ? InNegotiation
				: HaveItem;
	}
}
