import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;


// classe do agente
public class PingPong extends Agent {

   // classe do behaviour
   class PingPongBehaviour extends SimpleBehaviour {
      private int n = 0;

      // construtor do behaviour
      public PingPongBehaviour(Agent a) {
         super(a);
      }

      // método action
      public void action() {
    	  
    	  //ler a caixa de correio
         ACLMessage msg = blockingReceive();
         
         //se receber uma mensagem do tipo inform(de outro agente)
         if(msg.getPerformative() == ACLMessage.INFORM) {
            System.out.println(++n + " " + getLocalName() + ": recebi " + msg.getContent());
            // cria resposta
            ACLMessage reply = msg.createReply();
            // preenche conteúdo da mensagem
            if(msg.getContent().equals("ping"))
               reply.setContent("pong");
            else reply.setContent("ping");
            // envia mensagem
            send(reply);
         }
      }

      // método done
      public boolean done() {
         return n==10;
      }

   }   // fim da classe PingPongBehaviour

   // método setup
   protected void setup() {
	   
	   // (1) REGISTA O AGENTE
	   // (2) ADICIONA AOS COMPORTAMENTOS
	   
      String tipo = "";
      // obtém argumentos
      Object[] args = getArguments();
      if(args != null && args.length > 0) {
         tipo = (String) args[0];
      } else {
         System.out.println("Não especificou o tipo");
      }
      
      // regista agente no DF
      DFAgentDescription dfd = new DFAgentDescription();
      dfd.setName(getAID());
      ServiceDescription sd = new ServiceDescription();
      sd.setName(getName());
      sd.setType("Agente " + tipo);
      dfd.addServices(sd);
      try {
         DFService.register(this, dfd);
      } catch(FIPAException e) {
         e.printStackTrace();
      }

      // cria//ADICIONA behaviour
      PingPongBehaviour b = new PingPongBehaviour(this);
      addBehaviour(b);
	  
      // toma a iniciativa se for agente "pong"
      if(tipo.equals("pong")) {
         // pesquisa DF por agentes "ping"
    	  //vai buscar todos aqueles que teem agente ping
    	  //uma var que vai ter um conjunto de esp
         DFAgentDescription template = new DFAgentDescription();
         //servico para dentro do template
         ServiceDescription sd1 = new ServiceDescription();
         sd1.setType("Agente ping");
         template.addServices(sd1);
         try {
            DFAgentDescription[] result = DFService.search(this, template);
            // envia mensagem "pong" inicial a todos os agentes "ping"
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            for(int i=0; i<result.length; ++i)
               msg.addReceiver(result[i].getName());
            msg.setContent("pong");
            send(msg);
         } catch(FIPAException e) { e.printStackTrace(); }
      }

   }   // fim do metodo setup

   // método takeDown
   protected void takeDown() {
      // retira registo no DF
      try {
         DFService.deregister(this);  
      } catch(FIPAException e) {
         e.printStackTrace();
      }
   }

}   // fim da classe PingPong

