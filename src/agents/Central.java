package agents;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


@SuppressWarnings("serial")
public class Central extends Agent { //taxis
	
	public int nTotalTaxis;
	
	public Central(){
		this.nTotalTaxis = 1;
	}
	
	class CentralBehaviour extends SimpleBehaviour {
		int nTaxis = 0;
		int min = 50;
		AID taxiWinner;
		Agent taxiW;
		int countTaxis = 0;
		Agent myAgent;
		AID clientInform;
		int NClients;
		
		// construtor do behaviour
		public CentralBehaviour(Agent a) {
			super(a);
			myAgent = a;
		}

		// método action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			//PEDIDO DO CLIENTE
			// se receber uma mensagem do tipo request(do cliente)
			if (msg.getPerformative() == ACLMessage.REQUEST) {			
				
				if(nTotalTaxis != 0){
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription taxi = new ServiceDescription();
					taxi.setType("Taxi");
					template.addServices(taxi);
					
					try {
						// procra todos os taxis
						// result sao todos os taxis
						DFAgentDescription[] result = DFService.search(myAgent, template);
						
						//ENVIA MENSAGENS AOS TAXIS
						// envia uma mensagem do tipo cfp para todos os taxis
						ACLMessage pedido = new ACLMessage(ACLMessage.CFP);
						nTaxis = result.length;
						for (int i = 0; i < result.length; ++i){
							pedido.addReceiver(result[i].getName());
						}
						System.out.println(myAgent.getLocalName() + ": O " + msg.getSender().getLocalName() + " quer um Taxi. Taxis qual o vosso tempo? \n");
						System.out.println("A aguardar resposta dos taxis...");
						clientInform = msg.getSender();
						send(pedido);
						
					} catch (FIPAException e) {
						e.printStackTrace();
					}
				} else{
					System.out.println( myAgent.getLocalName() + ": Desculpe, atualmente não há Taxis.");
				}
				
			}

			//RESPOSTA DO TAXI
			// se receber uma mensagem do tipo propose(do taxi)
			if (msg.getPerformative() == ACLMessage.PROPOSE) {
				
				//((Taxi) myAgent).move((Location) ((Taxi) myAgent).getPath().get(0).getTarget());
				
				// incrementa o contador do numero de taxis
				countTaxis++;
				int x = Integer.parseInt(msg.getContent());
				// se o tempo recebido pelo taxi for menor que o minimo tempo
				// atual atualiza o melhor taxi para o serviço
				
				if (x < min ) {
					min = x;
					taxiWinner = msg.getSender();
				}
				
				// se ja tiver percorrido todos os taxis
				if (countTaxis == nTaxis) {
					// o problema é aqui
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription taxi = new ServiceDescription();
					taxi.setType("Taxi");
					template.addServices(taxi);
					// alterando taxi e client uma das partes nao corre
					try {
						// procra todos os taxis
						// result sao todos os taxis
						DFAgentDescription[] result = DFService.search(myAgent, template);

						// envia para os taxis com os piores tempos uma mensagem
						// do tipo reject proposal

						for (int i = 0; i < result.length; ++i) {
							// envia para o taxi com o melhor tempo uma mensagem
							// do tipo accept proposal

							//SE O TAXI FOR O MELHOR TAXI PARA O SERVIÇO - MENOR TEMPO
							if (result[i].getName().equals(taxiWinner)) {
								ACLMessage respostaW = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								// System.out.println("taxi winner " +
								// taxiWinner);
								respostaW.addReceiver(result[i].getName());
								respostaW.setContent(result[i].getName().getLocalName() + " efectue o serviço.");
								
								send(respostaW);
								
								//Avisa o cliente que o taxi esta a caminho
								ServiceDescription tellCliente = new ServiceDescription();
								taxi.setType("Client");
								template.addServices(tellCliente);
								
								ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
								String taxiResponsavel = result[i].getName().getLocalName();
								inform.setContent(taxiResponsavel);
								inform.addReceiver(clientInform);
								//System.out.println(inform);
								send(inform);
								
							}
							//SE O TAXI NAO FOR O MELHOR TAXI PARA O SERVIÇO - MAIORES TEMPOS
							else {
								ACLMessage respostaL = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
								respostaL.addReceiver(result[i].getName());
								respostaL.setContent(result[i].getName().getLocalName() + " Não precisa de se deslocar. O cliente está atendido.");
								send(respostaL);
							}
						}

					} catch (FIPAException e) {
						e.printStackTrace();
					}
				}
			}
			/*else{
				ACLMessage inform = new ACLMessage(ACLMessage.REFUSE);
				inform.setContent(myAgent.getLocalName() + ": Atualmente não há taxis. Efetue novo pedido dentro de minutos.\n");
				System.out.println(inform.getContent());
				inform.addReceiver(clientInform);
				send(inform);
			}*/
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