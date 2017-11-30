package agents;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

//class central
@SuppressWarnings("serial")
public class Central extends Agent { // taxis

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

		// m�todo action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo request(do cliente)
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				System.out.println("Central recebe pedido request do cliente");

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription taxi = new ServiceDescription();
				taxi.setType("Taxi");
				template.addServices(taxi);
				try {
					// procra todos os taxis
					// result sao todos os taxis
					DFAgentDescription[] result = DFService.search(myAgent, template);

					// envia uma mensagem do tipo cfp para todos os taxis
					ACLMessage pedido = new ACLMessage(ACLMessage.CFP);
					nTaxis = result.length;
					for (int i = 0; i < result.length; ++i)
						pedido.addReceiver(result[i].getName());
					String agentName = getAID().getLocalName();
					System.out.println("Central envia mensagem cfp para todos os taxis");
					pedido.setContent(agentName + " -O Cliente quer um taxi.");
					send(pedido);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}

			// se receber uma mensagem do tipo propose(do taxi)
			if (msg.getPerformative() == ACLMessage.PROPOSE) {
				
				// incrementa o contador do numero de taxis
				countTaxis++;
				
				int x = Integer.parseInt(msg.getContent());
				// se o tempo recebido pelo taxi for menor que o minimo tempo
				// atual atualiza o melhor taxi para o servi�o
				if (x < min) {
					min = x;
					taxiWinner = msg.getSender();
				}
				// se ja tiver percorrido todos os taxis
				if (countTaxis == nTaxis) {
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription taxi = new ServiceDescription();
					taxi.setType("Client");
					template.addServices(taxi);
					try {
						// procra todos os taxis
						// result sao todos os taxis
						DFAgentDescription[] result = DFService.search(myAgent, template);

						// envia para os taxis com os piores tempos uma mensagem do tipo reject proposal
						ACLMessage resposta = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
					
						for (int i = 0; i < result.length; ++i) {
							// envia para o taxi com o melhor tempo uma mensagem do tipo accept proposal
							if (result[i].getName() == taxiWinner) {
								ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								respostaW.addReceiver(result[i].getName());
								String agentName = getAID().getLocalName();
								System.out.println("Central envia resposta para o taxi que vai efectuar o servi�o.");
								respostaW.setContent(agentName + "Efectue o servi�o.");
								send(respostaW);
							} else {
								resposta.addReceiver(result[i].getName());
							}
						}
						// efetua o pedido do cliente
						String agentName = getAID().getLocalName();
						System.out.println("Central envia resposta para os taxis que nao vao efectuar o servi�o.");
						resposta.setContent(agentName + "O cliente ja esta atendido, nao efectue o servi�o.");
						send(resposta);

					} catch (FIPAException e) {
						e.printStackTrace();
					}
				}

				// recebe a resposta de cada um dos taxis
				// guarda o id do taxi com a melhor proposta
				// responde aos taxis
			}
			System.out.println("RECEBE MENSAGEM PROPOSE dO TAXI");
		}
		

		// m�todo done
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