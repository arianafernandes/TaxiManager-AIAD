package serviceConsumerProviderVis;



import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import serviceConsumerProviderVis.onto.Central;
import serviceConsumerProviderVis.onto.Client;

public class RepastSServiceConsumerProviderLauncher extends RepastSLauncher {
	public static Profile p;
	public static ContainerController cc;
	public static AgentController central;
	public static int NUMBER_TAXIS 					= 0;
	public static int TAXI_CAPACITY 				= 4;
	public static int NUMBER_CLIENTS 				= 0;
	public static int SHARED 						= 0;
	public static int NUMBER_PATIENTS_PER_CLIENT	= 1;
	public static ContainerController agentContainer;
	public static ContainerController mainContainer;

//	public static Agent getAgent(Context<?> context, AID aid) {
//		System.out.println("PARTE 1");
//		for(Object obj : context.getObjects(Agent.class)) {
//			if(((Agent) obj).getAID().equals(aid)) {
//				return (Agent) obj;
//			}
//		}
//		return null;
//	}
//
//	public int getN() {
//		return N;
//	}
//
//	public void setN(int N) {
//		this.N = N;
//	}
//
//	public int getFILTER_SIZE() {
//		return FILTER_SIZE;
//	}
//
//	public void setFILTER_SIZE(int FILTER_SIZE) {
//		this.FILTER_SIZE = FILTER_SIZE;
//	}
//
//	public double getFAILURE_PROBABILITY_GOOD_PROVIDER() {
//		return FAILURE_PROBABILITY_GOOD_PROVIDER;
//	}
//
//	public void setFAILURE_PROBABILITY_GOOD_PROVIDER(double FAILURE_PROBABILITY_GOOD_PROVIDER) {
//		this.FAILURE_PROBABILITY_GOOD_PROVIDER = FAILURE_PROBABILITY_GOOD_PROVIDER;
//	}
//
//	public double getFAILURE_PROBABILITY_BAD_PROVIDER() {
//		return FAILURE_PROBABILITY_BAD_PROVIDER;
//	}
//
//	public void setFAILURE_PROBABILITY_BAD_PROVIDER(double FAILURE_PROBABILITY_BAD_PROVIDER) {
//		this.FAILURE_PROBABILITY_BAD_PROVIDER = FAILURE_PROBABILITY_BAD_PROVIDER;
//	}
//
//	public int getN_CONTRACTS() {
//		return N_CONTRACTS;
//	}
//
//	public void setN_CONTRACTS(int N_CONTRACTS) {
//		this.N_CONTRACTS = N_CONTRACTS;
//	}
	
	
	@Override
	public Context build(Context<Object> context) {
		System.out.println("#################################################################");
		print();
		 //http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf
		System.out.println("PARTE 5");
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("Service Consumer/Provider network", context, true);
		netBuilder.buildNetwork();
		
		return super.build(context);
	}

	@Override
	public String getName() {
		return "Service Consumer/Provider -- SAJaS RepastS Test";
	}

	@Override
	protected void launchJADE() {
		System.out.println("PARTE 2");
		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SHARED == 1) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		System.out.println("LAUNCHE AGENTS");
		launchAgents();
	}
	
	private void launchAgents() {
		
		try {
			
			// -------------------- Central -----------------------------
			Central central = new Central(2,1);
			agentContainer.acceptNewAgent("Central", central).start();
			System.out.println("CENTRAL CRIADA");
			
			
			// -------------------- Cliente -----------------------------
			Client client = new Client(1);
			agentContainer.acceptNewAgent("Client", client).start();
			System.out.println("CLIENTE CRIADA");
						
			
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		print();
		System.out.println("#################################################################");
	}
	
	public void print() {
		for(int i = 0; i<5; i++) {
			System.out.println();
		}
	}


}
