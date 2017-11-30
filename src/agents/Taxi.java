package agents;

import java.util.Random;

import agents.Client.ClientBehaviour;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Taxi extends Agent {

	public Taxi() {

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
				System.out.println("Taxi a receber mensagem do tipo cfp da central");
				System.out.println(msg.getContent());

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
				} catch (FIPAException e) {
					e.printStackTrace();
				}

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