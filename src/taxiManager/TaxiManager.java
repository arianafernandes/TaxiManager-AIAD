package taxiManager;

import java.lang.Object;
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
	public static void main(String args[]) throws StaleProxyException, InterruptedException {
		// Program Initialization
		startJade();
	}
	
	// FUNCTIONS    
	public static void startJade() throws StaleProxyException, InterruptedException{
		//Start Jade
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		cc = rt.createMainContainer(p);
		buildMap();
	}
	
	public static void buildMap() throws StaleProxyException, InterruptedException{	
		int NUMBER_TAXIS 	= 1;
		int NUMBER_CLIENTS 	= 2;
		
		centralAgent(NUMBER_TAXIS);
		taxiAgent(NUMBER_TAXIS);
		clientAgent(NUMBER_CLIENTS);
	}

	public static void centralAgent(int numberTaxis) throws StaleProxyException{
		//Central initialization
		String args[] = new String[1];
	    args[0] = Integer.toString(numberTaxis);
		central = cc.createNewAgent("Central", "agents.Central", args);
		central.start();
	}
	
	public static void taxiAgent(int numberTaxis) throws StaleProxyException{
		//Taxis initialization 
		for (int i = 1; i <= numberTaxis; i++) {
			AgentController taxi = cc.createNewAgent("Taxi" + i, "agents.Taxi",null);
			taxi.start();
		}
	}
	
	public static void clientAgent(int numberClients) throws StaleProxyException, InterruptedException{
		//Clients initialization 
		for (int i = 1; i <= numberClients; i++) {
			AgentController client = cc.createNewAgent("Cliente" + i, "agents.Client", null);
			Thread.sleep(50);
			client.start();
		}
	}

}
