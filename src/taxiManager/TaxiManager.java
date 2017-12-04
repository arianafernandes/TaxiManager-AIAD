package taxiManager;

import java.lang.Object;
import java.util.Scanner;

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
	public static int NUMBER_TAXIS 					= 0;
	public static int TAXI_CAPACITY 				= 4;
	public static int NUMBER_CLIENTS 				= 0;
	public static int SHARED 						= 0;
	public static int NUMBER_PATIENTS_PER_CLIENT	= 1;
	
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
	
	public static void printDisplay(){
		
		Scanner input = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter a number: ");
		
		System.out.println("###############################################");
		System.out.println("#####                                    #####");
		System.out.println("#####     | BEM VENDO TAXI MANAGER |     #####");
		System.out.println("#####                                    #####");
		System.out.println("###############################################");
		System.out.println();
		System.out.print("Quantos Clientes: " );
		NUMBER_CLIENTS = input.nextInt();
		System.out.print("Quantos Taxis: " );
		NUMBER_TAXIS = input.nextInt();
		System.out.print("Deseja uma central Partilhada 1(Yes) 0(No): " );
		SHARED = input.nextInt();
	}
	
	public static void buildMap() throws StaleProxyException, InterruptedException{			
		printDisplay();
		centralAgent(NUMBER_TAXIS);
		taxiAgent(NUMBER_TAXIS,TAXI_CAPACITY);
		clientAgent(NUMBER_CLIENTS, NUMBER_PATIENTS_PER_CLIENT);
	}

	public static void centralAgent(int numberTaxis) throws StaleProxyException{
		//Central initialization
		String args[] = new String[1];
	    args[0] = Integer.toString(numberTaxis);
		central = cc.createNewAgent("[CENTRAL] ", "agents.Central", args);
		central.start();
	}
	
	public static void taxiAgent(int numberTaxis , int cap) throws StaleProxyException{
		//Taxis initialization 
		String args[] = new String[1];
	    args[0] = Integer.toString(cap);
	    
		for (int i = 1; i <= numberTaxis; i++) {
			AgentController taxi = cc.createNewAgent("[TAXI " + i + "]", "agents.Taxi",args);
			taxi.start();
		}
	}
	
	public static void clientAgent(int numberClients , int patiens) throws StaleProxyException, InterruptedException{
		//Clients initialization 
		String args[] = new String[1];
	    args[0] = Integer.toString(patiens);
		
		for (int i = 1; i <= numberClients; i++) {
			AgentController client = cc.createNewAgent("[CLIENTE " + i + "]", "agents.Client", args);
			Thread.sleep(50);
			client.start();
		}
	}

}
