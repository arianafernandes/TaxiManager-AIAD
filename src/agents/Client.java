package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.ACLMessage;

public class Client extends Agent {

	private int nClients;
	private double cost = 0;
	private String srcPoint;
	private String dstPoint;

	// Class constructor
	/*public Client(int nClients, String srcPoint, String dstPoint) {
		this.nClients = nClients;
		this.srcPoint = srcPoint;
		this.dstPoint = dstPoint;
	}

	public String getSrcPoint() {
		return srcPoint;
	}

	public String getDstPoint() {
		return dstPoint;
	}*/

	class ClientBehaviour extends SimpleBehaviour {
		private int n = 0;

		// construtor do behaviour
		public ClientBehaviour(Agent a) {
			super();
		}

		// método action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo inform(de outro agente)
			// if (msg.getPerformative() == ACLMessage.INFORM) {

			// }
		}

		private ACLMessage blockingReceive() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public boolean done(int n) {
		return n == 10;
	}
	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		// dfd.setName(getAID());
		// descreve servico
		ServiceDescription sd = new ServiceDescription();
		// sd.setName(getName());
		sd.setType("Client");
		dfd.addServices(sd);
		ClientBehaviour b = new ClientBehaviour(this);
		addBehaviour(b);
	}
	
	private void addBehaviour(ClientBehaviour b) {
		// TODO Auto-generated method stub

	}
}
