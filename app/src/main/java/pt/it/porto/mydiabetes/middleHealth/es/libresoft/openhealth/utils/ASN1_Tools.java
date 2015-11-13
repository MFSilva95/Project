/*
Copyright (C) 2008-2009  Santiago Carot Nemesio
email: scarot@libresoft.es

This program is a (FLOS) free libre and open source implementation
of a multiplatform manager device written in java according to the
ISO/IEEE 11073-20601. Manager application is designed to work in
DalvikVM over android platform.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.utils;

import pt.it.porto.mydiabetes.middleHealth.org.bn.CoderFactory;
import pt.it.porto.mydiabetes.middleHealth.org.bn.IDecoder;
import pt.it.porto.mydiabetes.middleHealth.org.bn.IEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class ASN1_Tools {
	/**
	 * Get the Object from an byte array
	 * @param <T> type of object class
	 * @param data array of bytes that representing the ASN.1 object
	 * @param objectClass Class of the object encapsulated in the byte array
	 * @param enc_rules Rules used to encode the object
	 * @return Object instance class decoded from the byte array
	 * @throws Exception
	 */
	public static final <T> T decodeData(byte[] data, Class<T> objectClass, String enc_rules) throws Exception{
		IDecoder decoder = CoderFactory.getInstance().newDecoder(enc_rules);
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		return decoder.decode(input, objectClass);
	}

	/**
	 * Encode an object in a byte array using specified encoding rules
	 * @param <T> Type of the object class to encode
	 * @param object Object that will be encoded
	 * @param enc_rules rules used to encode the object
	 * @return array of bytes which contains encoded object
	 * @throws Exception
	 */
	public static final <T> byte[] encodeData(T object, String enc_rules) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IEncoder<T> encoder = CoderFactory.getInstance().newEncoder(enc_rules);
		encoder.encode(object, os);
		return os.toByteArray();
	}

	/**
	 * Get hexadecimal string representation about raw data
	 * @param raw data
	 * @return string representation about data
	 * @throws UnsupportedEncodingException
	 */
	public static String getHexString(byte[] raw) throws UnsupportedEncodingException {
	    final byte[] hex = new byte[2 * raw.length];
	    int index = 0;

	    for (byte b : raw) {
	      int v = b & 0xFF;
	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	    }
	    return new String(hex, "ASCII");
	  }

	private static final byte[] HEX_CHAR_TABLE = {
	    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
	    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
	    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
	    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
	  };
}
