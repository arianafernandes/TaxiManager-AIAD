package agents;
 
import java.util.Random;
 
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.core.behaviours.OneShotBehaviour;
import sajas.core.behaviours.SimpleBehaviour;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
 
@SuppressWarnings("serial")
public class Client extends Agent {
 
    int nClientes;
    int xi = 0;
    int xf;
    int yi;
    int yf;
    ContinuousSpace<Object> space;
    Grid<Object> grid;
   
    public Client() {
    }
    public Client(ContinuousSpace<Object> space, Grid<Object> grid, int nClientes) {
        this.nClientes = nClientes;
        Random r = new Random();
        this.xi = Math.abs(r.nextInt()) % 20;
        this.yi = Math.abs(r.nextInt()) % 20;
        this.xf = Math.abs(r.nextInt()) % 20;
        this.yf = Math.abs(r.nextInt()) % 20;
        this.nClientes = (Math.abs(r.nextInt()) % 4 + 1);
        this.space = space;
        this.grid = grid;
    }
 
    // client behaviour � one shot behaviour pois o agent so tem um
    // comportamento que � pedir o taxi
    class ClientBehaviour extends SimpleBehaviour {
        // construtor do behaviour
        Agent myAgent;
 
        ACLMessage m = null;
        public ClientBehaviour(Agent a) {
            super(a);
            myAgent = a;
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd1 = new ServiceDescription();
            sd1.setType("Central");
            template.addServices(sd1);
            try {
                // procura todos os agentes no df service
                DFAgentDescription[] result = DFService.search(a, template);
                // envia uma mensagem do tipo request para a central
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                for (int i = 0; i < result.length; ++i)
                    msg.addReceiver(result[i].getName());
 
                //Cliente pede taxi para nClientes pessoas
                String nC = Integer.toString(nClientes);
                String xiS = Integer.toString(xi);
                String yiS = Integer.toString(yi);
                String xfS = Integer.toString(xf);
                String yfS = Integer.toString(yf);
                msg.setContent(nC + "," + xiS + "," + yiS + "," + xfS + "," + yfS);
 
                System.out.println("---------------------------------");
                System.out.println(a.getLocalName() + ": Quero um taxi para " + nClientes + " pessoa(s) do sitio " +
                        xi + "-" + yi + " para o sitio " + xf + "-" + yf + ".");
                send(msg);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
 
        // m�todo action
        public void action() {
 
            ACLMessage msg = receive();
 
            if(msg != null){
                //se recebe mensagem inform da central
                if (msg.getPerformative() == ACLMessage.INFORM){
                    System.out.println(myAgent.getLocalName() + ": Obrigado CENTRAL, fico � espera do " + msg.getContent() +".");
                }
                if(msg.getPerformative() == ACLMessage.REFUSE){
                    System.out.println(myAgent.getLocalName() + ": OK. Dentro de minutos fa�o novo pedido.");
                }
 
                if(msg.getPerformative() == ACLMessage.FAILURE){
                    System.out.println("FAILURE");
                }
 
            }
            else{
                block();
            }
        }
 
        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return false;
        }
    }
 
    protected void setup() {
        //      Object[] args = getArguments();
        //      if (args != null) {
        //          // Extracting the integer.
        //          //this.nClientes = Integer.parseInt((String) args[0]);
        //      }
        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        // descreve servico
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Client");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        ClientBehaviour b = new ClientBehaviour(this);
        addBehaviour(b);
 
    }
 
}