package taxiManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
import agents.Central;
import agents.Taxi;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import sajas.core.Agent;*/

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
		
		for( int i = 0; i < 6; i++){
			AgentController clt = cc.createNewAgent("client"+i,"agents.Client", args);
			clt.start();
		}
		
		// Fire up the agent and starts running the code
		central.start();
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
