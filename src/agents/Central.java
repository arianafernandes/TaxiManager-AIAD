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
	
	//total taxis from taxiManager
	public int nTotalTaxis;
	public Agent[] allAgents;
	public TreeMap<Integer, AID> allTaxis = new TreeMap<Integer, AID>();

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

		// método action
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
					System.out.println(myAgent.getLocalName() + ": Desculpe, atualmente não há Taxis.");
				}

			}

			// RESPOSTA DO TAXI
			// se receber uma mensagem do tipo propose(do taxi)
			if (msg.getPerformative() == ACLMessage.PROPOSE) {
		
				this.countTaxis++;
				String[] parts = msg.getContent().split(",");
				String time = parts[0];
				String avaliable = parts[1];
				String cap = parts[2];//

				int nPessoasPedido = Integer.parseInt(nPessoas);
				int capTaxi = Integer.parseInt(cap);
				
				// if p ver se esta avaliable
				// adiciona a Taxi que enviou proposta na lista 
				// desta forma temos acesso a TODOS os taxis que enviaram proposta
				// sem array so era possivel aceder à ultima proposta [ERRADO]
				//quando todos os taxis responderem ao pedido da central podemos continuar
				if (avaliable.equals("1")) {					
					
					if (capTaxi >= nPessoasPedido) {
						
						allTaxis.put(Integer.parseInt(time), msg.getSender());
					}
				}
						
				if (countTaxis == nTaxis) {
						
					if (!allTaxis.isEmpty()) {
						ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						
						// System.out.println("taxi winner " +
						// taxiWinner);
						respostaW.addReceiver(allTaxis.get(allTaxis.firstKey()));
						System.out.println(myAgent.getLocalName() + ": " + allTaxis.get(allTaxis.firstKey()).getLocalName() + " efectue o serviço.");
						respostaW.setContent(nPessoas);

						send(respostaW);

						ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
						String taxiResponsavel = allTaxis.get(allTaxis.firstKey()).getLocalName();
						inform.setContent(taxiResponsavel);
						inform.addReceiver(clientInform);
						// System.out.println(inform);
						send(inform);
						this.countTaxis = 0;
						
						allTaxis.remove(allTaxis.firstKey());
						
						for(int key : allTaxis.keySet()) {
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
			 * ": Atualmente não há taxis. Efetue novo pedido dentro de minutos.\n"
			 * ); System.out.println(inform.getContent());
			 * inform.addReceiver(clientInform); send(inform); }
			 */
		}

		// método done
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