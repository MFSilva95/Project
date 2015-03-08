package com.jadg.mydiabetes.sync.transfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.os.Environment;


/**
 * @author artur
 *
 * Stores information about the file being sync
 * 
 * Based on
 * JFSFileInfo.java,v 1.24 2009/10/02 08:21:19 heidrich
 * From Project
 * JFileSync, Version 2.3 Alpha
 * Copyright (C) 2002-2013, Jens Heidrich
 * 
 * 
 * 
 */
public class FileInfo implements Serializable {

	private static final long serialVersionUID = 3L;
	
	private String name = "";
	
	private String relativePath = "";
	
	private String rootPath ="";
	
	private boolean isDir = false;
	
	/** Determines whether we can read the file. */
	private boolean canRead = true;

	/** Determines whether we can write to the file. */
	private boolean canWrite = true;
	
	private boolean exists = false;
	
	private long length = 0;
	
	private long lastModified = 0;
	
	private FileInfo[] fileList = null;
	
	public FileInfo(String relativePath) {
		this.relativePath = relativePath;
	}
	
	public FileInfo(String rootPath, String relativePath) {
		this.setRelativePath(relativePath);
		this.rootPath = rootPath;
//		update();
	}
	
	/**
	 * Completes the information of the JFS file information object. This method
	 * is called on the server side in order to set information about the path
	 * on the server.
	 * 
	 * @return The file object used to extract the path information.
	 */
	public final File complete() {
		//TODO Screw Windows, Linux rules!!! (Dir/File Separator)
		File file = new File(getAbsolutePath());

		name = file.getName();
		exists = file.exists();

		if (exists) {
			canRead = file.canRead();
			canWrite = file.canWrite();
		}

		return file;
	}
	
	/**
	 * Updates the object on the basis of the current file system. This method
	 * is called on the server side in order to fill the object with real
	 * information about the file. The whole structure for the server's file
	 * systen is read in.
	 */
	public final void update() {
		File file = complete();

		if (exists) {
			isDir = file.isDirectory();
			if (!isDir) {
				length = file.length();
				lastModified = file.lastModified();
			}

//			if (this.fileList == null) {
				String[] fileList = file.list();

				if (isDir && (fileList != null)) {
					this.fileList = new FileInfo[fileList.length];

					for (int i = 0; i < fileList.length; i++) {
						this.fileList[i] = new FileInfo(/*rootPath, */relativePath
								+ File.separator + fileList[i]);
						this.fileList[i].update();
					}
				}
//			}
		}
	}

	/**
	 * Updates the current file on the basis of this object.
	 * 
	 * @return True if the update could be performed successfully.
	 */
	public final boolean updateFileSystem() {
		boolean success = true;

		File file = new File(rootPath + relativePath);

		if (!isDir) {
			success = success && file.setLastModified(lastModified);
		}

		if (!canWrite) {
			success = success && file.setReadOnly();
		}


		if (isDir && fileList != null) {
			for (FileInfo fi : fileList) {
				success = success && fi.updateFileSystem();
			}
		}

		return success;
	}
	
	/**
	 * Prints information about the specified file to the JFS standard out.
	 */
	public void print() {
		//TODO
		System.out.println("Name: " + getRelativePath());

		if (isDir()) {
			System.out.println("  List: ");
			for(FileInfo fi: getFileList())
				fi.print();
		} else {
			System.out.println("  Length: " + getLength());
			System.out.println("  Last Modified: " + getLastModified());
		}
		System.out.println();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the relativePath
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * @param relativePath the relativePath to set
	 */
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	/**
	 * @return the rootPath
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * @param rootPath the rootPath to set
	 */
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * 
	 */
	public String getAbsolutePath() {
		//TODO File Separator
		String absolutePath;
		String rootPath;
		if (this.rootPath == null || this.rootPath.equals("")) 
			rootPath = Environment.getExternalStorageDirectory() + "/MyDiabetes";
		else 
			rootPath = this.rootPath;
		if (relativePath.equals("")) {
			absolutePath = rootPath;
		} else
			absolutePath = rootPath + relativePath;
		return absolutePath; 
	}
	
	/**
	 * @return the isDir
	 */
	public boolean isDir() {
		return isDir;
	}

	/**
	 * @param isDir the isDir to set
	 */
	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	/**
	 * @return the exists
	 */
	public boolean exists() {
		return exists;
	}

	public boolean checkFileExists() {
		if (new File(Environment.getExternalStorageDirectory()
				+ "/MyDiabetes" + this.getRelativePath()).exists())
			return true;
		return false;
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
	 * @param exists the exists to set
	 */
	public void setExists(boolean exists) {
		this.exists = exists;
	}

	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the fileList
	 */
	public FileInfo[] getFileList() {
		return fileList;
	}

	/**
	 * @param fileList the fileList to set
	 */
	public void setFileList(FileInfo[] fileList) {
		this.fileList = fileList;
	}
	
	/**
	 * Returns whether we can read the file.
	 * 
	 * @return True if and only if we can read the file.
	 */
	public final boolean canRead() {
		return canRead;
	}

	/**
	 * Returns whether we can write to the file.
	 * 
	 * @return True if and only if we can write to the file.
	 */
	public final boolean canWrite() {
		return canWrite;
	}
}
