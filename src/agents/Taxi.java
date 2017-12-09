package agents;
 
import java.util.Random;
 
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.core.behaviours.SimpleBehaviour;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
 
@SuppressWarnings("serial")
public class Taxi extends Agent {
 
    public int available;
    public int capacity;
    public int x;
    public int y;
    ContinuousSpace<Object> space;
    Grid<Object> grid;
   
 
    public Taxi() {
 
    }
 
    public Taxi(ContinuousSpace<Object> space, Grid<Object> grid, int avaliable, int capacity) {
        this.available = avaliable;
        this.capacity = capacity;
        Random r = new Random();
        this.x = Math.abs(r.nextInt()) % 20;
        this.y = Math.abs(r.nextInt()) % 20;
        this.space = space;
        this.grid = grid;
    }
 
    public double calcDist(int xi, int xf, int yi, int yf) {
        double distF;
 
        int dx = xf - xi;
        int dy = yf - yi;
        int dx2 = dx * dx;
        int dy2 = dy * dy;
 
        distF = Math.sqrt(dx2 + dy2);
        return distF;
    }
 
    public void setAvalable(int val) {
        this.available = val;
    }
 
    public int getAvailable() {
        return available;
    }
 
    public void setCapacity(int val) {
        this.capacity = val;
    }
 
    public int getCapacity() {
        return capacity;
    }
    
    public void setX(int x) {
        this.x = x;
    }
 
    public void setY(int y) {
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
 
    // client behaviour é simple behaviour
    class TaxiBehaviour extends SimpleBehaviour {
        Agent myAgent;
 
        // construtor do behaviour
        public TaxiBehaviour(Agent a) {
            super(a);
            myAgent = a;
        }
 
        // método action
        public void action() {
            // ler a caixa de correio
            ACLMessage msg = receive();
 
            if (msg != null){
                // se receber mensagem do tipo cfp (da central)
                if (msg.getPerformative() == ACLMessage.CFP) {
 
                    String[] parts = msg.getContent().split(",");
                    String msgSender = parts[0];
                    String xf = parts[1];
                    String yf = parts[2];//
                    int xfi = Integer.parseInt(xf);
                    int yfi = Integer.parseInt(yf);
 
//                  DFAgentDescription template = new DFAgentDescription();
//                  ServiceDescription sd1 = new ServiceDescription();
//                  sd1.setType("Central");
//                  template.addServices(sd1);
 
                    ACLMessage proposta = msg.createReply();
                    proposta.setPerformative(ACLMessage.PROPOSE);
 
//                      for (int i = 0; i < result.length; ++i) {
//                          proposta.addReceiver(result[i].getName());
//                      }
 
                    String agentName = getAID().getLocalName();
                    // proposta do taxi para a central
 
                    double time = (calcDist(x, y, xfi, yfi) * 2);
 
                    String timeS = String.valueOf(time);
                    String avl = Integer.toString(getAvailable());
                    String cap = Integer.toString(getCapacity());
 
                    String args = timeS + "," + avl + "," + cap;
                    proposta.setContent(args);
 
                    String av;
                    if (getAvailable() == 1) {
                        av = "disponivel";
                    } else {
                        av = "indisponivel";
                    }
                    System.out.println(agentName + ": estou a " + String.format("%.2f", time) + " minutos do "
                            + msgSender + ". Tenho " + cap + " lugar(es) livre(s) " + "e estou " + av);
 
                    send(proposta);
 
                }
 
                /*
                 * RECEBE MENSAGEM CENTRAL se receber uma mensagem do tipo proposal(da central)
                 * significa que é o taxi que vai efectuar o serviço
                 */
                if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
 
                    String[] parts = msg.getContent().split(",");
                    int nP = Integer.parseInt(parts[0]);
                    int checked_shared = Integer.parseInt(parts[1]);
                    int destinoX = Integer.parseInt(parts[2]);
                    int destinoY = Integer.parseInt(parts[3]);
                     
//                    System.out.println("X inicial" + getX());
//                    System.out.println("Y inicial" + getY());
                    setX(destinoX);
                    setY(destinoY);
//                    System.out.println("X final" + getX());
//                    System.out.println("Y final" + getY());
//                    System.out.println(space + "space - grid" + grid);
                    // System.out.println("CENTRAL envia resposta para o taxi que
                    // vai efectuar o serviço.");
                    //VERIFICAÇÃO DE SER TAXI PARTILHADO OU NÃO
                    setCapacity(getCapacity() - nP);
                    if (checked_shared == 1) {
                        if (capacity == 0) {
                            setAvalable(0);
                        }
                    }
                    else
                        setAvalable(0);
 
                    System.out.println(myAgent.getLocalName() + ": Ok. Já vou buscar o Cliente.");
                }
                // se receber uma mensagem do tipo reject(da central)
                // significa que é o taxi que nao vai efectuar o serviço
                if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    // System.out.println("Depois resposta do taxi");
                    if (available != 0) {
                        System.out.println(myAgent.getLocalName() + ": Ok Central, aguardo por novos clientes.");
                    }
                }
 
            }
            else{
                block();
            }
        }
       
        // método done
        public boolean done() {
            return false;
        }
    }
 
    protected void setup() {
        //      Object[] args = getArguments();
        //      if (args != null) {
        //          // Extracting the integer.
        //          int val = Integer.parseInt((String) args[0]);
        //          setCapacity(val);
        //          setAvalable(1);
        //          Random r = new Random();
        //          this.x = Math.abs(r.nextInt()) % 20;
        //          this.y = Math.abs(r.nextInt()) % 20;
        //      }
 
        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        // descreve servico
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Taxi");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
 
        addBehaviour(new TaxiBehaviour(this));
    }
}