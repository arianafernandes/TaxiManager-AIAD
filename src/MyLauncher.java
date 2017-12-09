import agents.Central;
import agents.Client;
import agents.Taxi;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class MyLauncher extends RepastSLauncher {

	public ContainerController mainContainer;
	public ContinuousSpaceFactory spaceFactory;
	public GridFactory gridFactory;
	public ContinuousSpace<Object> space;
	public Grid<Object> grid;
	public Context<Object> context;
	private Network<Object> network;
	public Agent[] agents;
	
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		this.context.setId("taxiM");

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"network",
				this.context,
				true);
		netBuilder.buildNetwork();
		network = (Network<Object>) this.context.getProjection("network");
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space",
				this.context,
				new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 
				50,50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), false, 50, 50));

		Parameters params = RunEnvironment.getInstance().getParameters();
		int taxisCount = (Integer) params.getValue("number_taxis");
		for (int i = 0; i < taxisCount; i++) {
			context.add(new Taxi(space, grid, 1, 4));
			
		}

		int clientsCount = (Integer) params.getValue("number_clients");
		for (int i = 0; i < clientsCount; i++) {
			Client client = new Client(space, grid, i);
			context.add(client);
		}

		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}

		if (RunEnvironment.getInstance().isBatch()) {
			RunEnvironment.getInstance().endAt(20);
		}

		// Clean everything before the simulation ends
		Schedule scheduler = new Schedule();
		ScheduleParameters stop = ScheduleParameters
				.createAtEnd(ScheduleParameters.END);
		scheduler.schedule(stop, this, "endSimulation");

		this.context = context;

		return super.build(context);
	}
	public void endSimulation(){
		System.out.println("END OF SIMULATION");
	}
	
	@Override
	public String getName() {
		return "taximanager";
	}

	@Override
	protected void launchJADE() {

		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		try {
			launchAgents();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void launchAgents() throws InterruptedException {

		Parameters params = RunEnvironment.getInstance().getParameters();
		try {
			Central central = new Central(1, 1);
			mainContainer.acceptNewAgent("[CENTRAL]", central).start();

			int taxiCount = (Integer) params.getValue("number_taxis");
			for (int i = 0; i < taxiCount; i++) {
				Taxi taxi = new Taxi(space, grid, 1, 4);
				context.add(taxi);
				mainContainer.acceptNewAgent("[Taxi " + i + "]", taxi).start();
			}

			int clientCount = (Integer) params.getValue("number_clients");
			for (int i = 0; i < clientCount; i++) {
				Client client = new Client(space, grid, clientCount);
				context.add(client);
				mainContainer.acceptNewAgent("[CLIENT " + i + "]", client).start();

			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}