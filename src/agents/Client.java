package agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Client extends Agent {

	int clientN;

	public Client() {
	}

	// waiting for taxi
	class ClientBehaviour extends OneShotBehaviour {
		private int n = 0;

		// construtor do behaviour
		public ClientBehaviour(Agent a) {
			// send request central
			super(a);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd1 = new ServiceDescription();
			sd1.setType("Central");
			template.addServices(sd1);
			try {
				DFAgentDescription[] result = DFService.search(a, template);

				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < result.length; ++i)
					msg.addReceiver(result[i].getName());

				String agentName = getAID().getLocalName();
				msg.setContent(agentName + " quer um Taxi.");
				System.out.println(agentName + " : Quero um Taxi.");
				send(msg);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

		// método action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo inform(de outro agente)
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println("Ok, obrigado.");
			}
			//ACLMessage.REFUSE
			else{
				System.out.println("Fica para a próxima.");
			}
		}
	}

	protected void setup() {
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

		addBehaviour(new ClientBehaviour(this));
	}
}