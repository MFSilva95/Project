/**
 * 
 */
package pt.it.porto.mydiabetes.sync.transfer;

import java.io.Serializable;

/**
 * @author artur
 *
 */
public class AESKeyObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] key;
	private byte[] iv;
	/**
	 * @return the key
	 */
	public byte[] getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(byte[] key) {
		this.key = key;
	}
	/**
	 * @return the iv
	 */
	public byte[] getIv() {
		return iv;
	}
	/**
	 * @param iv the iv to set
	 */
	public void setIv(byte[] iv) {
		this.iv = iv;
	}

}
