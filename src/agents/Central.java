package agents;

import java.util.ArrayList;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

@SuppressWarnings("serial")
public class Central extends Agent {
	private ArrayList<Taxi> companyTaxis = new ArrayList<Taxi>(); // companny
																	// taxis
	int clientsNumber = 1;

	class CentralBehaviour extends SimpleBehaviour {
		int nTaxis = 0;
		int min = 50;
		AID taxiWinner;
		int countTaxis = 0;
		Agent myAgent;

		// construtor do behaviour
		public CentralBehaviour(Agent a) {
			super(a);
			myAgent = a;
		}

		// método action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo inform(de outro agente)
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				System.out.println("Central: " + msg.getContent());

				// cfp para os taxis
				// procura todos os taxis e envia um pedido para cada um
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription taxi = new ServiceDescription();
				taxi.setType("Taxi");
				template.addServices(taxi);
				try {
					// procra todos os taxis
					// result sao todos os taxis
					DFAgentDescription[] result = DFService.search(myAgent, template);

					// envia o pedido request
					ACLMessage pedido = new ACLMessage(ACLMessage.CFP);
					nTaxis = result.length;
					for (int i = 0; i < result.length; ++i)
						pedido.addReceiver(result[i].getName());

					String agentName = getAID().getLocalName();
					pedido.setContent(agentName + "[Central] Quer um taxi.");
					send(pedido);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}

			if (msg.getPerformative() == ACLMessage.PROPOSE) {
				countTaxis++;
				System.out.println("Central: " + msg.getContent());
				int x = Integer.parseInt(msg.getContent());
				if (x < min) {
					min = x;
					taxiWinner = msg.getSender();
				}
				if (countTaxis == nTaxis) {
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription taxi = new ServiceDescription();
					taxi.setType("Taxi");
					template.addServices(taxi);
					try {
						// procra todos os taxis
						// result sao todos os taxis
						DFAgentDescription[] result = DFService.search(myAgent, template);

						// envia o pedido request

						ACLMessage resposta = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						for (int i = 0; i < result.length; ++i) {
							if (result[i].getName() == taxiWinner) {
								ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								respostaW.addReceiver(result[i].getName());
								String agentName = getAID().getLocalName();
								respostaW.setContent(agentName + "[Central] Quer um taxi.");
								send(respostaW);
							} else {
								resposta.addReceiver(result[i].getName());
							}
						}
						String agentName = getAID().getLocalName();
						resposta.setContent(agentName + "[Central] Quer um taxi.");
						send(resposta);

					} catch (FIPAException e) {
						e.printStackTrace();
					}
				}

				// recebe a resposta de cada um dos taxis
				// guarda o id do taxi com a melhor proposta
				// responde aos taxis
			}
		}

		// método done
		public boolean done() {
			return false;
		}
	}

	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		// descreve servico
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Central");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		CentralBehaviour b = new CentralBehaviour(this);
		addBehaviour(b);
	}
}