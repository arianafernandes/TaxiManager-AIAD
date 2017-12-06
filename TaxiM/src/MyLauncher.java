import agents.Central;
import agents.Client;
import agents.Taxi;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;

public class MyLauncher extends RepastSLauncher {
	
	private ContainerController mainContainer;
	
	@Override
	public String getName() {
		return "SAJaS Project";
	}

	@Override
	protected void launchJADE() {
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		launchAgents();
	}
	
	private void launchAgents() {
		
		try {
			
			Central central = new Central(1,1);			
			mainContainer.acceptNewAgent("[CENTRAL] ", central).start();
			Client client = new Client(1);			
			mainContainer.acceptNewAgent("[CLIENT] ", client).start();
			Taxi taxi = new Taxi(1,2);			
			mainContainer.acceptNewAgent("[Taxi] ", taxi).start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Context build(Context<Object> context) {
		
		// ...
		
		return super.build(context);
	}

}
