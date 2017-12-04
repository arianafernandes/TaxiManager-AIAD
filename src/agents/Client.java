package agents;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
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
	
	public Client() {
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

	// client behaviour é one shot behaviour pois o agent so tem um
	// comportamento que é pedir o taxi
	class ClientBehaviour extends OneShotBehaviour {
		// construtor do behaviour
		Agent myAgent;

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
				msg.setContent(nC + "," + xiS + "," + yiS);
				
				System.out.println("---------------------------------");
				System.out.println(a.getLocalName() + ": Quero um taxi para " + nClientes + " pessoa(s) do sitio " + 
				xi + "-" + yi + " para o sitio " + xf + "-" + yf + ".");
				send(msg);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

		// método action
		public void action() {
			
			ACLMessage msg = blockingReceive();
			
			//se recebe mensagem inform da central
			if (msg.getPerformative() == ACLMessage.INFORM){
				System.out.println(myAgent.getLocalName() + ": Obrigado CENTRAL, fico à espera do " + msg.getContent() +".");
			} 
			if(msg.getPerformative() == ACLMessage.REFUSE){
				System.out.println(myAgent.getLocalName() + ": OK. Dentro de minutos faço novo pedido.");
			}
							
			if(msg.getPerformative() == ACLMessage.FAILURE){
				System.out.println("FAILURE");
			}
			
		}
	}

	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			// Extracting the integer.
			this.nClientes = Integer.parseInt((String) args[0]);
			Random r = new Random();
			this.xi = Math.abs(r.nextInt()) % 6;
			this.yi = Math.abs(r.nextInt()) % 6;
			this.xf = Math.abs(r.nextInt()) % 6;
			this.yf = Math.abs(r.nextInt()) % 6;
		}
		
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