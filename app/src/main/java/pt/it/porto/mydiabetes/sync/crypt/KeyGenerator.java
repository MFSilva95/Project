/**
 * 
 */
package pt.it.porto.mydiabetes.sync.crypt;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author artur
 * 
 */
public class KeyGenerator {
	public byte[] generateKey() {
		byte key[];
		Random ranGen = new SecureRandom();
		key = new byte[16];
		ranGen.nextBytes(key);
		return key;
	}
}
