import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
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
		// regista agente no DF
	      DFAgentDescription dfd = new DFAgentDescription();
	      dfd.setName(getAID());
	      //descreve servico
	      ServiceDescription sd = new ServiceDescription();
	      sd.setName(getName());
	      sd.setType("Cliente");
	      dfd.addServices(sd);
	      try {
	         DFService.register(this, dfd);
	      } catch(FIPAException e) {
	         e.printStackTrace();
	      }
	      
         DFAgentDescription template = new DFAgentDescription();
         ServiceDescription sd1 = new ServiceDescription();
         sd1.setType("Central");
         template.addServices(sd1);
         
         try {
            DFAgentDescription[] result = DFService.search(this, template);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            for(int i=0; i<result.length; ++i)
               msg.addReceiver(result[i].getName());
            msg.setContent("Need a Taxi.");
            send(msg);
         } catch(FIPAException e) { e.printStackTrace(); }
	}
}
