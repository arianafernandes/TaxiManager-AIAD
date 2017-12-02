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
	
	public boolean available;
	public int capacity;
	
	public Taxi() {
		this.available = true;
		this.capacity = 2;
	}
	
	public boolean getAva(){
		return available;
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
					// cria numero random para tempo do taxi de resposta
					Random r = new Random();
					int randint = Math.abs(r.nextInt()) % 11;
					String x = Integer.toString(randint);
					proposta.setContent(x);
					send(proposta);
					
					System.out.println(agentName + ": estou a " + proposta.getContent() + " minutos.");
				} catch (FIPAException e) {
					e.printStackTrace();
				}

			}
			
			/* RECEBE MENSAGEM CENTRAL
			 * se receber uma mensagem do tipo proposal(da central)
			 * significa que é o taxi que vai efectuar o serviço */
			if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {			
				//System.out.println("CENTRAL envia resposta para o taxi que vai efectuar o serviço.");
				available = false;
				System.out.println(msg.getSender().getLocalName() + ": " + msg.getContent());
				System.out.println(myAgent.getLocalName() + ": Ok, obrigado. Já vou efectuar o serviço." + "\n");
			}
			// se receber uma mensagem do tipo reject(da central)
			//significa que é o taxi que nao vai efectuar o serviço
			if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
				System.out.println(msg.getSender().getLocalName() + ": " + msg.getContent());
				System.out.println(myAgent.getLocalName() + ": Ok Central, aguardo por novos clientes." + "\n");
			}
		}

		// método done
		public boolean done() {
			return n == 10;
		}
	}

	protected void setup() {
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