import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Client extends Agent {
	
	class ClientBehaviour extends SimpleBehaviour {
	      private int n = 0;

	      // construtor do behaviour
	      public ClientBehaviour(Agent a) {
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
	
	protected void setup() {
		
	}
}
