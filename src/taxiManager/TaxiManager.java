package taxiManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class TaxiManager{

////////////////////////
//						
//	MEMBER VARIABLES  
//						
////////////////////////
	Profile p;
	ContainerController cc;
	

	public static void main(String args[]) throws StaleProxyException {
		// Get a hold on JADE runtime 
		Runtime rt = Runtime.instance(); 
		// Create a default profile    
		Profile p = new ProfileImpl();     
		//p.setParameter(Profile.GUI, "true");
		// Create a new non-main container, connecting to the default 
		// main container (i.e. on this host, port 1099) 
		ContainerController cc = rt.createMainContainer(p); 
		// Create a new agent, a DummyAgent 
		// and pass it a reference to an Object 
		//Object reference = new Object(); 
		//Object args1[] = new Object[1]; 
		
				
		AgentController central = cc.createNewAgent("central","agents.Central", args);
		AgentController taxiPorto1 = cc.createNewAgent("taxiPorto","agents.Taxi", args);
		AgentController client = cc.createNewAgent("Cliente André Maia","agents.Client", args);

		
		for( int i = 0; i < 6; i++){
			//AgentController clt = cc.createNewAgent("client"+i,"agents.Client", args);
			//clt.start();
		}
		
		// Fire up the agent and starts running the code
		central.start();
		client.start();
		taxiPorto1.start();
		
	}
	
	/*public static void main(String args[]) throws StaleProxyException {
		launchJADE();
	}
	
		protected void launchJADE() throws StaleProxyException {
			// Get a hold on JADE runtime 
			Runtime rt = Runtime.instance(); 
			// Create a default profile    
			p = new ProfileImpl();     
			//p.setParameter(Profile.GUI, "true");
			// Create a new non-main container, connecting to the default 
			// main container (i.e. on this host, port 1099) 
			cc = rt.createMainContainer(p); 
			// Create a new agent, a DummyAgent 
			// and pass it a reference to an Object 
			//Object reference = new Object(); 
			//Object args1[] = new Object[1]; 
			launchAgents();
		}
		
		private void launchAgents() throws StaleProxyException{
			AgentController central = cc.createNewAgent("central","agents.Central",null);
			AgentController taxiPorto1 = cc.createNewAgent("taxiPorto","agents.Taxi", null);
			
			for( int i = 0; i < 6; i++){
				AgentController clt = cc.createNewAgent("client"+i,"agents.Client", null);
				clt.start();
			}
			
			//AgentController snif = cc.createNewAgent("sniffer","jade.tools.sniffer.Sniffer",args); 
			//snif.start();
			
			
			// Fire up the agent and starts running the code
			central.start();
			taxiPorto1.start();
		}
	*/
}
