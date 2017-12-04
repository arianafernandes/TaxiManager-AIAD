package agents;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
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
	

	public Taxi() {
				
	}

	public double calcDist(int xi,int xf, int yi, int yf){
		double distF;
		
		int dx = xf - xi;
		int dy = yf - yi;
		int dx2 = dx * dx;
		int dy2 = dy*dy;
		
		distF = Math.sqrt(dx2 + dy2);
		return distF ;
	}
	public void setAvalable(int val){
		this.available = val;
	}
	
	public int getAvailable() {
		return available;
	}
	
	public void setCapacity(int val){
		this.capacity = val;
	}
	
	public int getCapacity(){
		return capacity;
	}

	// client behaviour é simple behaviour
	class TaxiBehaviour extends SimpleBehaviour {
		private int n = 0;
		Agent myAgent;

		// construtor do behaviour
		public TaxiBehaviour(Agent a) {
			super(a);
			myAgent = a;
		}

		// método action
		public void action() {
			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber mensagem do tipo cfp (da central)
			if (msg.getPerformative() == ACLMessage.CFP) {

				
				String[] parts = msg.getContent().split(",");
				String msgSender = parts[0];
				String xf = parts[1];
				String yf = parts[2];//
				int xfi = Integer.parseInt(xf);
				int yfi = Integer.parseInt(yf);
				
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd1 = new ServiceDescription();
				sd1.setType("Central");
				template.addServices(sd1);

				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					// envia uma mensagem do tipo propose para a central

					ACLMessage proposta = new ACLMessage(ACLMessage.PROPOSE);

					for (int i = 0; i < result.length; ++i) {
						proposta.addReceiver(result[i].getName());
					}

					String agentName = getAID().getLocalName();
					// proposta do taxi para a central
					
					double time = (calcDist(x,y,xfi,yfi) * 2);
					
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
					System.out.println(agentName + ": estou a " + String.format( "%.2f", time) + " minutos do " + msgSender + ". Tenho "
							+ cap + " lugar(es) livre(s) " + "e estou " + av);
					
					send(proposta);
				} catch (FIPAException e) {
					e.printStackTrace();
				}

			}
			
			/*
			 * RECEBE MENSAGEM CENTRAL se receber uma mensagem do tipo
			 * proposal(da central) significa que é o taxi que vai efectuar o
			 * serviço
			 */
			if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				
				String nPessoas = msg.getContent();
				int nP = Integer.parseInt(nPessoas);

				// System.out.println("CENTRAL envia resposta para o taxi que
				// vai efectuar o serviço.");
				setCapacity( getCapacity() - nP);
				
				if(capacity == 0){
					setAvalable(0);
				}
				
				System.out.println(myAgent.getLocalName() + ": Ok. Já vou buscar o Cliente.");
			}
			// se receber uma mensagem do tipo reject(da central)
			// significa que é o taxi que nao vai efectuar o serviço
			if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
				//System.out.println("Depois resposta do taxi");
				System.out.println(myAgent.getLocalName() + ": Ok Central, aguardo por novos clientes.");
			}

		}

		// método done
		public boolean done() {
			return n == 10;
		}
	}

	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			// Extracting the integer.
			int val = Integer.parseInt((String) args[0]);	
			setCapacity(val);
			setAvalable(1);
			Random r = new Random();
			this.x = Math.abs(r.nextInt()) % 20;
			this.y = Math.abs(r.nextInt()) % 20;
		}
		
		
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