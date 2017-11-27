package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class Taxi {

	private int passengers = 0;

	class TaxiBehaviour extends SimpleBehaviour {
		private int n = 0;
		private int passengers = 0;

		// construtor do behaviour
		public TaxiBehaviour(Agent a) {
			super(a);
		}

		// método action
		public void action() {

			//ler a caixa de correio
			//ACLMessage msg = blockingReceive();

			//se receber uma mensagem do tipo inform(de outro agente)
			//if(msg.getPerformative() == ACLMessage.INFORM) {

			//}
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

	}
}
