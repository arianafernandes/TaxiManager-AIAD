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

	int nClientes;
	
	public Client() {
	}

	// client behaviour � one shot behaviour pois o agent so tem um
	// comportamento que � pedir o taxi
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
				msg.setContent(nC);
				
				System.out.println(a.getLocalName() + ": Quero um taxi para " + nClientes + " pessoa(s).");
				send(msg);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

		// m�todo action
		public void action() {
			
			ACLMessage msg = blockingReceive();
			
			// se receber mensagem do tipo cfp (da central)
			if (msg.getPerformative() == ACLMessage.INFORM){
				System.out.println(myAgent.getLocalName() + ": Obrigado CENTRAL, fico � espera do " + msg.getContent() +".");
			} else{
				System.out.println(myAgent.getLocalName() + ": OK. Dentro de minutos fa�o NOVO pedido.");
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