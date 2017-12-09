import java.util.Random;

import agents.Central;
import agents.Client;
import agents.Taxi;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.ui.RunOptionsModel;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;

public class MyLauncher extends RepastSLauncher implements ContextBuilder<Object> {

	public ContainerController mainContainer;
	public Grid<Object> grid;
	private boolean launchedJADE = false;
	public ContinuousSpace<Object> space;
	
	@Override
	public Context build(Context<Object> context) {

		context.setId("taxiM");
		
		
		if(!launchedJADE)
			launchJADE();

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space",
				context, 
				new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(),
				50,50);
		this.space = space;
	
				
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("network", context, true);
		netBuilder.buildNetwork();
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), false, 50, 50));

		

		Random rand = new Random();
		Parameters params = RunEnvironment.getInstance().getParameters();
		int taxisCount = params.getInteger("number_taxis");
		int clientsCount =  params.getInteger("number_clients");
		
		
		
		
		for (int i = 0; i < taxisCount; i++) {
			
			Taxi taxi = new Taxi(space, grid, 1, 4);
			context.add(taxi);
			try {
				mainContainer.acceptNewAgent("Taxi"+i, taxi).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < clientsCount; i++) {
			Client client = new Client(space, grid, i);
			context.add(client);
			try {
				mainContainer.acceptNewAgent("Client"+i, client).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
				
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}
		
	
		return context;

	}

	@Override
	public void launchJADE(){		
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		Central central = new Central(1,1,space,grid);
		try {
			System.out.println("1");
			mainContainer.acceptNewAgent("Central", central);
			System.out.println("2");
		} catch (StaleProxyException e) {
			System.out.println("3");
			e.printStackTrace();
		}
		System.out.println("4");
		launchedJADE = true;
	}

	@Override
	public String getName() {
		return "taximanager";
	}

}