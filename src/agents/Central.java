package agents;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Central extends Agent { // taxis

	public int nTotalTaxis;
	public Agent[] allAgents;

	public Central() {
	}

	class CentralBehaviour extends SimpleBehaviour {
		int nTaxis = 0;
		int min = 50;
		AID taxiWinner;
		Agent taxiW;
		Agent myAgent;
		AID clientInform;
		int NClients;
		String nPessoas;
		int countTaxis = 0;

		// construtor do behaviour
		public CentralBehaviour(Agent a) {
			super(a);
			myAgent = a;
		}

		// m�todo action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// PEDIDO DO CLIENTE
			// se receber uma mensagem do tipo request(do cliente)
			if (msg.getPerformative() == ACLMessage.REQUEST) {

				if (nTotalTaxis != 0) {
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription taxi = new ServiceDescription();
					taxi.setType("Taxi");
					template.addServices(taxi);

					try {
						// procra todos os taxis
						// result sao todos os taxis
						DFAgentDescription[] result = DFService.search(myAgent, template);

						// ENVIA MENSAGENS AOS TAXIS
						// envia uma mensagem do tipo cfp para todos os taxis
						ACLMessage pedido = new ACLMessage(ACLMessage.CFP);
						nTaxis = result.length;
						for (int i = 0; i < result.length; ++i) {
							pedido.addReceiver(result[i].getName());
						}
						nPessoas = msg.getContent();
						System.out.println(myAgent.getLocalName() + ": O " + msg.getSender().getLocalName()
								+ " quer um Taxi para " + msg.getContent() + " pessoa(s). Taxis qual o vosso tempo? ");
						System.out.println("A aguardar resposta dos taxis...");
						clientInform = msg.getSender();
						pedido.setContent(msg.getSender().getLocalName());
						send(pedido);

					} catch (FIPAException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println(myAgent.getLocalName() + ": Desculpe, atualmente n�o h� Taxis.");
				}

			}

			// RESPOSTA DO TAXI
			// se receber uma mensagem do tipo propose(do taxi)
			if (msg.getPerformative() == ACLMessage.PROPOSE) {
				this.countTaxis++;
				// System.out.println(msg.getContent());
				String[] parts = msg.getContent().split(",");
				String time = parts[0];
				String avaliable = parts[1];
				String cap = parts[2];//

				// System.out.println(time + avaliable + cap);

				int nPessoasPedido = Integer.parseInt(nPessoas);
				int capTaxi = Integer.parseInt(cap);
				// if p ver se esta avaliable

				if (avaliable.equals("1")) {

					if (capTaxi >= nPessoasPedido) {

						int x = Integer.parseInt(time);
						// se o tempo recebido pelo taxi for menor que o minimo
						// tempo
						// atual atualiza o melhor taxi para o servi�o

						if (x < min) {
							min = x;
							taxiWinner = msg.getSender();
						}

						if (countTaxis == nTaxis) {

							DFAgentDescription template = new DFAgentDescription();
							ServiceDescription taxi = new ServiceDescription();
							taxi.setType("Taxi");
							template.addServices(taxi);

							// alterando taxi e client uma das partes nao corre
							try {
								// procra todos os taxis
								// result sao todos os taxis
								DFAgentDescription[] result = DFService.search(myAgent, template);

								// envia para os taxis com os piores tempos uma
								// mensagem
								// do tipo reject proposal

								for (int i = 0; i < result.length; ++i) {
									// envia para o taxi com o melhor tempo uma
									// mensagem
									// do tipo accept proposal

									// SE O TAXI FOR O MELHOR TAXI PARA O
									// SERVI�O - MENOR TEMPO
									if (result[i].getName().equals(taxiWinner)) {
										ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

										// System.out.println("taxi winner " +
										// taxiWinner);
										respostaW.addReceiver(result[i].getName());
										System.out.println(result[i].getName().getLocalName() + " efectue o servi�o.");
										respostaW.setContent(nPessoas);

										send(respostaW);

										// Avisa o cliente que o taxi esta a
										// caminho
										ServiceDescription tellCliente = new ServiceDescription();
										taxi.setType("Client");
										template.addServices(tellCliente);

										ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
										String taxiResponsavel = result[i].getName().getLocalName();
										inform.setContent(taxiResponsavel);
										inform.addReceiver(clientInform);
										// System.out.println(inform);
										send(inform);

									}
									// SE O TAXI NAO FOR O MELHOR TAXI PARA O
									// SERVI�O - MAIORES TEMPOS
									else {
										ACLMessage respostaL = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
										respostaL.addReceiver(result[i].getName());
										System.out.println(result[i].getName().getLocalName()
												+ " N�o precisa de se deslocar. O cliente est� atendido.");
										respostaL.setContent(result[i].getName().getLocalName()
												+ " N�o precisa de se deslocar. O cliente est� atendido.");
										send(respostaL);
									}
								}

							} catch (FIPAException e) {
								e.printStackTrace();
							}
						}

					}

				}

			}

			if (msg.getPerformative() == ACLMessage.REFUSE) {
				//System.out.println("[Central]  ESPERAR");
				ACLMessage msg2 = new ACLMessage(ACLMessage.FAILURE);
				msg2.addReceiver(clientInform);
				System.out.println("[Central]  VAI ENVIAR PARA -> " + clientInform.getLocalName());
				//msg2.setContent("[Central]  CANCELA");
				send(msg2);
				//System.out.println("[Central] ENVIOUUUU");
			}
			/*
			 * else{ ACLMessage inform = new ACLMessage(ACLMessage.REFUSE);
			 * inform.setContent(myAgent.getLocalName() +
			 * ": Atualmente n�o h� taxis. Efetue novo pedido dentro de minutos.\n"
			 * ); System.out.println(inform.getContent());
			 * inform.addReceiver(clientInform); send(inform); }
			 */
		}

		// m�todo done
		public boolean done() {
			return false;
		}
	}

	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			// Extracting the integer.
			this.nTotalTaxis = Integer.parseInt((String) args[0]);
		}

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