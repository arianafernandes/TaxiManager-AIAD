package agents;

import jade.core.Agent;
import taxiManager.*;
import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Taxi extends Agent{
	
	class TaxiBehaviour extends SimpleBehaviour {
	      private int n = 0;

	      // construtor do behaviour
	      public TaxiBehaviour(Agent a) {
	         super(a);
	      }

	      // método action
	      public void action() {
	    	  
	    	  //ler a caixa de correio
	         ACLMessage msg = blockingReceive();
	         
	         //se receber uma mensagem do tipo inform(de outro agente)
	         if(msg.getPerformative() == ACLMessage.INFORM) {
	     
	         }
	      }

	      // método done
	      public boolean done() {
	         return n==10;
	      }
	   } 
	
	class SetPrice extends SimpleBehaviour {
	      private int n = 0;

	      // construtor do behaviour
	      public SetPrice(Agent a) {
	         super(a);
	      }

	      // método action
	      public void action() {
	    	  
	      }

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	protected void setup() {
		
	}
}
