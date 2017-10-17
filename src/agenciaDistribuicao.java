import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*; 

public class agenciaDistribuicao {

	public static void main(String args[]) throws StaleProxyException {

		// Get a hold on JADE runtime 
		Runtime rt = Runtime.instance(); 
		// Create a default profile    
		Profile p = new ProfileImpl();     
		p.setParameter(Profile.GUI, "true");
		// Create a new non-main container, connecting to the default 
		// main container (i.e. on this host, port 1099) 
		ContainerController cc = rt.createMainContainer(p); 
		// Create a new agent, a DummyAgent 
		// and pass it a reference to an Object 
		//Object reference = new Object(); 
		//Object args1[] = new Object[1]; 
		 
		AgentController taxiPorto = cc.createNewAgent("taxiPorto","Taxi", args);
		AgentController taxiCaminha = cc.createNewAgent("taxiCaminha","Taxi", args);
		// Fire up the agent and starts running the code
		taxiPorto.start();
		
	}
	
	
}
