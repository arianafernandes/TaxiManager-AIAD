import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
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
	     
	         }
	      }

	      // método done
	      public boolean done() {
	         return n==10;
	      }
	}   // fim da classe PingPongBehaviour
 
	protected void setup() {
			
	}
}
