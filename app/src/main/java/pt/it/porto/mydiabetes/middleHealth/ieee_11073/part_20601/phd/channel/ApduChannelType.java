package pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.phd.channel;

import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElement;



public abstract class ApduChannelType implements IASN1PreparedElement {

	/* Channel for this APDU  */
	private int channel = 0;

	public void setChannel (int n) {
		this.channel = n;
	}

	public int getChannel () {
		return this.channel;
	}

}
