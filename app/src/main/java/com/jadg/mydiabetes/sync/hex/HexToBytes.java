package com.jadg.mydiabetes.sync.hex;

public class HexToBytes {
	public static byte[] decode(String s)
	{
		int len = s.length();
	    byte[] key = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        key[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return key;
	}
}
