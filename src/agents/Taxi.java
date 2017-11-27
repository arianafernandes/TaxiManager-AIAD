package agents;

import agents.Central.CentralBehaviour;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Taxi extends Agent{

	private int passengers = 0;

	class TaxiBehaviour extends SimpleBehaviour {
		private int n = 0;
		private int passengers = 0;

		// construtor do behaviour
		public TaxiBehaviour(Agent a) {
			super();
		}

		// método action
		public void action() {

			//ler a caixa de correio
			ACLMessage msg = blockingReceive();

			//se receber uma mensagem do tipo inform(de outro agente)
			//if(msg.getPerformative() == ACLMessage.INFORM) {

			//}
		}

		private ACLMessage blockingReceive() {
			// TODO Auto-generated method stub
			return null;
		}

		// método done
		public boolean done() {
			return n==10;
		}
	} 

	class SetPrice extends SimpleBehaviour {
		private int n = 0;

		// construtor do behaviour
		public SetPrice(Agent a) {
			super(a);
		}

		// método action
		public void action() {

		}

		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	protected void setup() {
		// regista agente no DF
				DFAgentDescription dfd = new DFAgentDescription();
				// dfd.setName(getAID());
				// descreve servico
				ServiceDescription sd = new ServiceDescription();
				// sd.setName(getName());
				sd.setType("Taxi");
				dfd.addServices(sd);
				TaxiBehaviour b = new TaxiBehaviour(this);
				addBehaviour(b);
	}
	private void addBehaviour(TaxiBehaviour b) {
		// TODO Auto-generated method stub

	}
}
