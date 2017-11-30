package taxiManager;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class TaxiManager {

	Profile p;
	ContainerController cc;

	public static void main(String args[]) throws StaleProxyException {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile p = new ProfileImpl();
		// p.setParameter(Profile.GUI, "true");
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createMainContainer(p);

		//Central initialization 
		AgentController central = cc.createNewAgent("Central", "agents.Central", args);
		central.start();

		//Taxis initialization 
		for (int i = 0; i < 4; i++) {
			AgentController taxi = cc.createNewAgent("Taxi" + i, "agents.Taxi", args);
			taxi.start();
		}
		
		//Clients initialization 
		for (int i = 0; i < 1; i++) {
			AgentController client = cc.createNewAgent("Cliente" + i, "agents.Client", args);
			client.start();
		}
	}
}
