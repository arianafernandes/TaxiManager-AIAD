package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Client extends Agent {

	private int nClients;
	private double cost = 0;
	private String srcPoint;
	private String dstPoint;

	// Class constructor
	public Client(int nClients, String srcPoint, String dstPoint) {
		this.nClients = nClients;
		this.srcPoint = srcPoint;
		this.dstPoint = dstPoint;
	}

	public String getSrcPoint() {
		return srcPoint;
	}

	public String getDstPoint() {
		return dstPoint;
	}

	class CallCentral extends SimpleBehaviour {
		private int n = 0;

		// construtor do behaviour
		public CallCentral(Agent a) {
			super(a);
		}

		// m�todo action
		public void action() {

			// ler a caixa de correio
			// ACLMessage msg = blockingReceive();

			// se receber uma mensagem do tipo inform(de outro agente)
			// if (msg.getPerformative() == ACLMessage.INFORM) {

			// }
		}

		// m�todo done
		public boolean done() {
			return n == 10;
		}
	}

	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		// descreve servico
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Cliente");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("Central");
		template.addServices(sd1);

		try {
			DFAgentDescription[] result = DFService.search(this, template);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < result.length; ++i)
				msg.addReceiver(result[i].getName());

			String agentName = getAID().getLocalName();
			msg.setContent(agentName + " - need a Taxi.");
			System.out.println(agentName + " : Want a Taxi.");
			send(msg);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
