package agents;

import java.util.*;
import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Central extends Agent { // taxis

	// total taxis from taxiManager
	public int nTotalTaxis;
	public Agent[] allAgents;
	public TreeMap<Double, AID> allTaxis = new TreeMap<Double, AID>();
	public int price;

	public Central() {
	}

	class CentralBehaviour extends SimpleBehaviour {
		int nTaxis = 0;
		int min = 50;
		int NClients;
		int countTaxis = 0;
		AID taxiWinner;
		AID clientInform;
		Agent taxiW;
		Agent myAgent;
		String nPessoas;

		public CentralBehaviour(Agent a) {
			super(a);
			myAgent = a;
		}

		public void action() {
			// LER CAIXA DO CORREIO
			ACLMessage msg = blockingReceive();

			// PEDIDO [REQUEST] DO CLIENTE
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				if (nTotalTaxis != 0) {
					callAllTaxis(msg);
				}
			}

			// RESPOSTA [PROPOSTA] DO TAXI
			if (msg.getPerformative() == ACLMessage.PROPOSE) {
				String[] parts = msg.getContent().split(",");
				String time = parts[0];
				String avaliable = parts[1];
				String cap = parts[2];//
				int nPessoasPedido = Integer.parseInt(nPessoas);
				int capTaxi = Integer.parseInt(cap);
				// variavel que garante que recebemos pedidos de TODOS os Taxis
				this.countTaxis++;

				if (avaliable.equals("1")) {
					if (capTaxi >= nPessoasPedido) {
						allTaxis.put(Double.parseDouble(time), msg.getSender());
					} else {
						ACLMessage respostaL = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						respostaL.addReceiver(msg.getSender());

						System.out.println(msg.getSender().getLocalName()
								+ " Não precisa de se deslocar. O seu taxi não tem espaço para o numero de passageiros.");
						System.out.println("-----------");
						send(respostaL);
					}
				} else {
					ACLMessage respostaL = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
					respostaL.addReceiver(msg.getSender());

					System.out.println(msg.getSender().getLocalName()
							+ " Não precisa de se deslocar. O seu taxi ja está ocupado.");
					System.out.println("-----------");
					send(respostaL);
				}

				if (countTaxis == nTaxis) {
					if (!allTaxis.isEmpty()) {
						// informa o melhor taxi para efectuar o serviço
						ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						respostaW.addReceiver(allTaxis.get(allTaxis.firstKey()));
						System.out.println(myAgent.getLocalName() + ": "
								+ allTaxis.get(allTaxis.firstKey()).getLocalName() + " efectue o serviço.");
						respostaW.setContent(nPessoas);
						send(respostaW);

						// Informa o client
						ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
						String taxiResponsavel = allTaxis.get(allTaxis.firstKey()).getLocalName();
						inform.setContent(taxiResponsavel);
						inform.addReceiver(clientInform);
						// System.out.println(inform);
						send(inform);
						this.countTaxis = 0;

						allTaxis.remove(allTaxis.firstKey());

						// Avisa todos os Taxis disponiveis a responder ao
						// CLiente que já não é necessario
						for (Double key : allTaxis.keySet()) {
							ACLMessage respostaL = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
							respostaL.addReceiver(allTaxis.get(key));

							System.out.println(allTaxis.get(key).getLocalName()
									+ " Não precisa de se deslocar. O cliente está atendido.");
							respostaL.setContent(allTaxis.get(key).getLocalName()
									+ " Não precisa de se deslocar. O cliente está atendido.");
							send(respostaL);

						}

						allTaxis.clear();
					}
				}
			}

			if (msg.getPerformative() == ACLMessage.REFUSE) {
				ACLMessage msg2 = new ACLMessage(ACLMessage.FAILURE);
				msg2.addReceiver(clientInform);
				// System.out.println("[Central] VAI ENVIAR PARA -> " +
				// clientInform.getLocalName());
				send(msg2);
			}
		}

		public void callAllTaxis(ACLMessage msg) {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription taxi = new ServiceDescription();
			taxi.setType("Taxi");
			template.addServices(taxi);

			try {
				// PROCURA TODOS OS TAXIS
				DFAgentDescription[] result = DFService.search(myAgent, template);

				// ENVIA MENSAGENS AOS TAXIS DO TIPO CFP
				ACLMessage pedido = new ACLMessage(ACLMessage.CFP);
				nTaxis = result.length;
				for (int i = 0; i < result.length; ++i) {
					pedido.addReceiver(result[i].getName());
				}
				String[] parts = msg.getContent().split(",");
				this.nPessoas = parts[0];
				String xi = parts[1];
				String yi = parts[2];//

				System.out.println(myAgent.getLocalName() + ": O " + msg.getSender().getLocalName()
						+ " quer um Taxi para " + nPessoas + " pessoa(s). Taxis qual o vosso tempo para o sitio " + xi
						+ "-" + yi + "?");
				System.out.println("A aguardar resposta dos taxis...");
				clientInform = msg.getSender();
				pedido.setContent(msg.getSender().getLocalName() + "," + xi + "," + yi);
				send(pedido);

			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

		// METODO DONE
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