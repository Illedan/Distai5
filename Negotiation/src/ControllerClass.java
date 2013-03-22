import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class ControllerClass extends Agent{
	ArrayList<String> allAgents = new ArrayList<>();
	public void setup() {
		// Create the four agents
		createAgents();
		// Print the info of all the agents, to be sure
		printAgentInfo();
	}
	
	private boolean createAgents() {
		AgentContainer a = getContainerController();
		try {
			AgentController b = a.createNewAgent("agent1", "AbstractAgent", null);
			AgentController c = a.createNewAgent("agent2", "AbstractAgent", null);
//			AgentController d = a.createNewAgent("agent3", "AbstractAgent", null);
//			AgentController e = a.createNewAgent("agent4", "AbstractAgent", null);
			allAgents.add("agent1");
			allAgents.add("agent2");
//			allAgents.add("agent3");
//			allAgents.add("agent4");
			b.start();
			c.start();
//			d.start();
//			e.start();
			System.out.println("Created and started all agents. Total amount: " + allAgents.size());
			return true;
		} catch (Exception e) {
			System.out.println("Failed to create agensts!");
			System.err.println(e);
			takeDown();
			return false;
		}
	}
	
	private void printAgentInfo() {
		AMSAgentDescription[] agents = new AMSAgentDescription[10];
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		AID myID = getAID();
		System.out.println("Information about all agents currently running: ");
		for (int i = 0; i < agents.length; i++) {
			AID agentID = agents[i].getName();
			System.out.println((agentID.equals(myID) ? "*** " : "    ") + i + ": " + agentID.getName());
		}
	}
}
