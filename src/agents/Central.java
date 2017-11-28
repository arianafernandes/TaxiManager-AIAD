package agents;

import taxiManager.*;
import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Central extends Agent{
	
	class CentralBehaviour extends SimpleBehaviour {
	      private int n = 0;

	      // construtor do behaviour
	      public CentralBehaviour(Agent a) {
	         super(a);
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