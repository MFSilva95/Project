/**
 * 
 */
package com.jadg.mydiabetes.sync.transfer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author artur
 * 
 *         Object to be transmitted from the client to server Based on
 *         JFSTransmission.java,v 1.10 2007/02/26 18:49:10 heidrich From Project
 *         JFileSync, Version 2.3 Alpha Copyright (C) 2002-2013, Jens Heidrich
 */
public class Transmission implements Serializable {

	private static final long serialVersionUID = 3L;

	public static final byte GET_INFO = 0;
	public static final byte PUT_FILE_INFO = 1;
	public static final byte GET_CONTENTS = 2;
	public static final byte PUT_CONTENTS = 3;

	private byte command = -1;
	
	private int numberFiles = 0;

	private String user = null;
	
	/** The transmited file information object. */
	private FileInfo info = null;

	/**
	 * Creates a new transmission.
	 * 
	 * @param command
	 *            The transmitted command.
	 */
	public Transmission(byte command) {
		this.command = command;
	}

	/**
	 * Creates a new transmission.
	 * 
	 * @param command
	 *            The transmitted command.
	 * @param info
	 *            The transmited file information object.
	 */
	public Transmission(byte command, FileInfo info) {
		this.command = command;
		this.info = info;
	}

	public byte[] getBytes() throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
		bos.close();
		byte[] bytes = bos.toByteArray();
		return bytes;
	}

	/**
	 * Returns the transmitted command.
	 * 
	 * @return The command identifier.
	 */
	public byte getCommand() {
		return command;
	}

	/**
	 * Returns the transmited file information object.
	 * 
	 * @return File info object.
	 */
	public FileInfo getInfo() {
		return info;
	}

	/**
	 * @return the numberFiles
	 */
	public int getNumberFiles() {
		return numberFiles;
	}

	/**
	 * @param numberFiles the numberFiles to set
	 */
	public void setNumberFiles(int numberFiles) {
		this.numberFiles = numberFiles;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}
