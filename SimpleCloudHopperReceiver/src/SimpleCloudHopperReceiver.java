import java.io.IOException;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;

public class SimpleCloudHopperReceiver extends DefaultSmppSessionHandler {

	/**
	 * @param args
	 */
	private SmppSession session = null;
	private String ipAddress = "192.168.43.28";
	private String systemId = "smppclient1";
	private String password = "password";
	private int smppPort = 2775;

	public static void main(String[] args) {
		System.out.println("Program starts...");
		SimpleCloudHopperReceiver objSimpleCloudHopperReceiver = new SimpleCloudHopperReceiver();
		objSimpleCloudHopperReceiver.bindToSMSC();
		objSimpleCloudHopperReceiver.waitForExitSignal();
	}

	private void bindToSMSC() {
		DefaultSmppClient smppClient = new DefaultSmppClient();

		SmppSessionConfiguration config0 = new SmppSessionConfiguration();
		config0.setHost(ipAddress);
		config0.setPort(smppPort);
		config0.setSystemId(systemId);
		config0.setPassword(password);
		config0.setType(SmppBindType.RECEIVER);

		try {
			this.session = smppClient.bind(config0, this);
			System.out.println("Connected to SMSC...");
			System.out.println("Ready to receive PDU...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PduResponse firePduRequestReceived(PduRequest pduRequest) {
		PduResponse response = pduRequest.createResponse();
		DeliverSm sms = (DeliverSm) pduRequest;
		
		if ((int)sms.getDataCoding() == 0 ) {
			//message content is English
			System.out.println("***** New English Message Received *****");
			System.out.println("From: " + sms.getSourceAddress().getAddress());
			System.out.println("To: " + sms.getDestAddress().getAddress());
			System.out.println("Content: " + new String(sms.getShortMessage()));
		}
		return response;
	}

	private void waitForExitSignal() {

		System.out.println("Press any key to exit");
		try {
			System.in.read();
			this.session.unbind(0);
			System.out.println("System terminated");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
