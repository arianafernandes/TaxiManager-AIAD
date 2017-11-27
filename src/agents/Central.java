package src.agents;

import sun.management.Agent;
import taxiManager.*;

@SuppressWarnings("serial")
public class Central extends Agent{
	
	class CentralBehaviour extends SimpleBehaviour {
	      private int n = 0;

	      // construtor do behaviour
	      public CentralBehaviour(Agent a) {
	         super();
	      }

	      // método action
	      public void action() {
	    	  
	    	  //ler a caixa de correio
	         ACLMessage msg = blockingReceive();
	         
	         //se receber uma mensagem do tipo inform(de outro agente)
	         if(msg.getPerformative() == ACLMessage.INFORM) {
	        	System.out.println("Central: " + msg.getContent());
	         }
	      }

	      private ACLMessage blockingReceive() {
			// TODO Auto-generated method stub
			return null;
		}

		// método done
	      public boolean done() {
	         return n==10;
	      }
	}   // fim da classe PingPongBehaviour
 
	protected void setup() {
		// regista agente no DF
	      DFAgentDescription dfd = new DFAgentDescription();
	      dfd.setName(getAID());
	      //descreve servico
	      ServiceDescription sd = new ServiceDescription();
	      sd.setName(getName());
	      sd.setType("Central");
	      dfd.addServices(sd);
	      try {
	         DFService.register(this, dfd);
	      } catch(FIPAException e) {
	         e.printStackTrace();
	      }
	      
	      CentralBehaviour b = new CentralBehaviour(this);
	      addBehaviour(b);
	}
}
