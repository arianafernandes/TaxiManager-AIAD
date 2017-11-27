package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.ACLMessage;

public class Central extends Agent {

	class CentralBehaviour extends SimpleBehaviour {
		private int n = 0;

		// construtor do behaviour
		public CentralBehaviour(Agent a) {
			super();
		}

		// método action
		public void action() {

			// ler a caixa de correio
			ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo inform(de outro agente)
			// if(msg.getPerformative() == ACLMessage.INFORM) {
			// System.out.println("Central: " + msg.getContent());
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

	// método done
	public boolean done(int n) {
		return n == 10;
	}
	// fim da classe PingPongBehaviour

	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		// dfd.setName(getAID());
		// descreve servico
		ServiceDescription sd = new ServiceDescription();
		// sd.setName(getName());
		sd.setType("Central");
		dfd.addServices(sd);
		CentralBehaviour b = new CentralBehaviour(this);
		addBehaviour(b);
	}

	private void addBehaviour(CentralBehaviour b) {
		// TODO Auto-generated method stub

	}
}
