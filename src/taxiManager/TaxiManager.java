package taxiManager;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class TaxiManager {
	
	//Global Vars
	public static Profile p;
	public static ContainerController cc;
	public static AgentController central;
	
	//Main
	public static void main(String args[]) throws StaleProxyException {
		// Program Initialization
		startJade();
	}
	
	// FUNCTIONS    
	public static void startJade() throws StaleProxyException{
		//Start Jade
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		cc = rt.createMainContainer(p);
		buildMap();
	}
	
	public static void buildMap() throws StaleProxyException{
		centralAgent();
		taxiAgent(1);
		clientAgent(1);
	}
		
	public static void centralAgent() throws StaleProxyException{
		//Central initialization
		central = cc.createNewAgent("Central", "agents.Central", null);
		central.start();
	}
	
	public static void taxiAgent(int numberTaxis) throws StaleProxyException{
		//Taxis initialization 
		for (int i = 1; i <= numberTaxis; i++) {
			AgentController taxi = cc.createNewAgent("Taxi" + i, "agents.Taxi",null);
			taxi.start();
		}
	}
	
	public static void clientAgent(int numberClients) throws StaleProxyException{
		//Clients initialization 
		for (int i = 1; i <= numberClients; i++) {
			AgentController client = cc.createNewAgent("Cliente" + i, "agents.Client", null);
			client.start();
		}
	}

}
