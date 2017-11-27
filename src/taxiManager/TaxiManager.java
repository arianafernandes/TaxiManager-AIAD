package taxiManager;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;


public class TaxiManager {

	////////////////////////
	//
	// MEMBER VARIABLES
	//
	////////////////////////
	static Profile p;
	static ContainerController cc;

	public static void main(String args[]) throws StaleProxyException {
		// protected void launchJADE() throws StaleProxyException {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		p = new ProfileImpl();
		p.setParameter(Profile.GUI, "true");
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		cc = rt.createMainContainer(p);
		// Create a new agent, a DummyAgent
		// and pass it a reference to an Object
		//Object reference = new Object();
		//Object args1[] = new Object[1];

		AgentController central = cc.createNewAgent("Central", "agents.Central", null);
		AgentController taxi = cc.createNewAgent("Taxi", "agents.Taxi", null);
		AgentController client = cc.createNewAgent("Client", "agents.Client", null);

		

		// Fire up the agent and starts running the code
		central.start();
		taxi.start();
		client.start();
	}

}
