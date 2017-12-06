package taxiManager;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Random;
//import java.util.Scanner;
//
//import agents.Central;
//import agents.Taxi;
//import jade.core.AID;
//import jade.core.Profile;
//import jade.core.ProfileImpl;
//import jade.wrapper.StaleProxyException;
//import sajas.sim.repasts.RepastSLauncher;
//import sajas.wrapper.AgentController;
//import sajas.wrapper.ContainerController;
//import sajas.core.Agent;
//import sajas.core.Runtime;

import agents.Central;
import agents.Client;
import agents.Taxi;

import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import sajas.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
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


public class TaxiManager extends RepastSLauncher implements ContextBuilder<Object>  {
	
	//Global Vars
	public static Profile p;
	public static ContainerController cc;
	public static AgentContainer central;
	public static int NUMBER_TAXIS 					= 0;
	public static int TAXI_CAPACITY 				= 4;
	public static int NUMBER_CLIENTS 				= 0;
	public static int SHARED 						= 0;
	public static int NUMBER_PATIENTS_PER_CLIENT	= 1;
	private Context<Object> context;
	private ContinuousSpace<Object> space;
	
	//Main
//	public static void main(String args[]) throws StaleProxyException, InterruptedException, extends RepastLauncher {
//		// Program Initialization
//		launchJADE();
//	}
	
	// FUNCTIONS    
//	public static void startJade() throws StaleProxyException, InterruptedException{
//		//Start Jade
//		Runtime rt = Runtime.instance();
//		Profile p = new ProfileImpl();
//		cc = rt.createMainContainer(p);
//		
//	}
/*	
public static void printDisplay(){
		
		Scanner input = new Scanner(System.in);  // Reading from System.in
		
		System.out.println("###############################################");
		System.out.println("#####                                    #####");
		System.out.println("#####     | BEM VINDO TAXI MANAGER |     #####");
		System.out.println("#####                                    #####");
		System.out.println("###############################################");
		System.out.println("Introduza o numero que desejar pf.");
		System.out.println();
		System.out.print("Quantos Clientes?" );
		System.out.println();
		NUMBER_CLIENTS = input.nextInt();
		System.out.print("Quantos Taxis?" );
		System.out.println();
		NUMBER_TAXIS = input.nextInt();
		System.out.print("Deseja uma central Partilhada? 1(Sim) 0(Nao). " );
		System.out.println();
		SHARED = input.nextInt();
	} 
	*/

	protected void launchJADE()  {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		cc = rt.createMainContainer(p1);
		launchAgents();
	}
	
	private void launchAgents(){	
		System.out.println("INICIADO");
		//printDisplay();
		
		if(SHARED == 1 || SHARED == 0){
			centralAgent(NUMBER_TAXIS, SHARED);
			taxiAgent(NUMBER_TAXIS,TAXI_CAPACITY);
			if(NUMBER_CLIENTS != 0){
				clientAgent(NUMBER_CLIENTS, NUMBER_PATIENTS_PER_CLIENT);
			}
			else{
				System.out.println("Atualmente nao ha clientes e por isso nao ha pedidos em curso.");
			}
		}
		else{
			System.out.println("Nao inseriu uma opcao valida para central partilhada (0 ou 1).");
		}
	}
	
	public static void centralAgent(int numberTaxis, int shared){
		//Central initialization
		String args[] = new String[2];
	    args[0] = Integer.toString(numberTaxis);
	    args[1] = Integer.toString(shared);
	    Central central = new Central(numberTaxis,shared);
	   
			try {
				cc.acceptNewAgent("[CENTRAL]", central).start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	
		
	}
	
	public static void taxiAgent(int numberTaxis , int cap) {
		//Taxis initialization 
		String args[] = new String[1];
	    args[0] = Integer.toString(cap);
	    
		for (int i = 1; i <= numberTaxis; i++) {
			Taxi taxi = new Taxi(cap);
			try {
				cc.acceptNewAgent("[TAXI " + i + "]", taxi).start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void clientAgent(int numberClients , int patiens){
		//Clients initialization 
		String args[] = new String[1];
	    args[0] = Integer.toString(patiens);
		
		for (int i = 1; i <= numberClients; i++) {
			//AgentController client = cc.acceptNewAgent("[CLIENTE " + i + "]", "agents.Client", args);
			Client client = new Client(patiens);
		
		
			try {
				cc.acceptNewAgent("[CLIENTE " + i + "]", client).start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//client.start();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		this.context = context;
		this.context.setId("TaxiManager");
		
		// GENERATE CONTINUOUS SPACE
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory( null );
		space = spaceFactory.createContinuousSpace (
				"space", 
				this.context ,
				new RandomCartesianAdder<Object>() ,
				new repast.simphony.space.continuous.WrapAroundBorders(),
				50, 50);
		return this.build(context);
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
