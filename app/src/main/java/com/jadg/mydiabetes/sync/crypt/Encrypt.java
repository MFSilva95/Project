package com.jadg.mydiabetes.sync.crypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	public static byte[] encrypt(byte[] data, byte[] key, byte[] iv)
	{
		byte[] encryptedData=null;
		Cipher cipher;
		SecretKeySpec k;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			k = new SecretKeySpec(key,"AES");
			cipher.init(Cipher.ENCRYPT_MODE, k,new IvParameterSpec(iv));
			encryptedData= cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedData;
	}
}