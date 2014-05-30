package com.jadg.mydiabetes.sync.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.jadg.mydiabetes.sync.crypt.Encrypt;
import com.jadg.mydiabetes.sync.crypt.EncryptionUtil;

public class Stream extends Service{
	public Stream() {
		super();
	}
	private int result = Activity.RESULT_CANCELED;
	private Socket socket = null;
	private byte[] bytes;
	private final IBinder mBinder = new StreamBinder();
	private FileInfo fiServer;
	private Bundle extras;

	public class StreamBinder extends Binder {
		public Stream getService() {
			return Stream.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		extras = intent.getExtras();
		byte cmd = extras.getByte("cmd");
		switch (cmd) {
		case Transmission.GET_INFO:
			GetInfo giTask = new GetInfo();
			giTask.execute(extras);
			break;
		case Transmission.PUT_CONTENTS:
			SendFile task = new SendFile();
			task.execute(extras);
			break;
		case 31:
			SyncServer ssTask = new SyncServer();
			ssTask.execute(extras);
			break;
		default:
			System.out.println("Stream.onBind not good");
		}
		return mBinder;
	}
	
	public class SendFile extends AsyncTask<Bundle, Void, Bundle> {
		@Override
		protected Bundle doInBackground(Bundle... extras) {

			if (extras[0].getByte("cmd") == Transmission.PUT_CONTENTS) {
				String host = "";
				int port = 0;
				byte[] key;
				ArrayList<FileInfo> fil;
				Transmission t;

				try {
					host = extras[0].getString("host");
					port = extras[0].getInt("port");
					key = extras[0].getByteArray("key").clone();
					byte[] iv = extras[0].getByteArray("iv").clone();
					fil = (ArrayList<FileInfo>) extras[0].get("files");
					socket = new Socket(host, port);
					OutputStream out = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(out);
					BufferedInputStream buf;

					t = new Transmission(Transmission.PUT_CONTENTS);
					t.setNumberFiles(fil.size());
					bytes = t.getBytes();
					bytes = Encrypt.encrypt(bytes, key, iv);
					oos.write(bytes);
					oos.flush();
					oos.write(getBytes("END_OF_OBJECT"));
					oos.flush();
					
					for (FileInfo fi : fil) {
						fi.update();
						bytes = Encrypt.encrypt(fi.getBytes(),key,iv);
						oos.write(bytes);
						oos.flush();
						oos.write(getBytes("END_OF_OBJECT"));
						oos.flush();
						
						buf = new BufferedInputStream(new FileInputStream(
								fi.getAbsolutePath()));
						bytes = new byte[(int) fi.getLength()];
						buf.read(bytes, 0, (int) fi.getLength());
						buf.close();
						bytes = Encrypt.encrypt(bytes, key, iv);
						oos.write(bytes);
						oos.flush();
						oos.write(getBytes("END_OF_OBJECT"));
						oos.flush();

					}

					oos.close();
					
					result = Activity.RESULT_OK;

				} catch (UnknownHostException e) {
					System.err.println("Unknown host: " + host);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				System.out.println("Stream.SendFile Wrong Command");
			}
			return extras[0];
		}

		@Override
		protected void onPostExecute(Bundle extras) {
			if (extras != null) {
				Messenger messenger = (Messenger) extras.get("MESSENGER");
				Message msg = Message.obtain();
				msg.arg1 = result;
				try {
					messenger.send(msg);
				} catch (android.os.RemoteException e1) {
					Log.w(getClass().getName(), "Exception sending message", e1);
				}
			}
		}
	}

	public class GetInfo extends AsyncTask<Bundle, Void, Bundle> {

		@Override
		protected Bundle doInBackground(Bundle... extras) {
			String host = "";
			int port = 0;
			byte[] key;

			String dirToUpdate = extras[0].getString("file");
			FileInfo fi = new FileInfo(dirToUpdate, "");

			Transmission t = new Transmission(Transmission.GET_INFO, fi);
			try {
				bytes = t.getBytes();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				host = extras[0].getString("host");
				port = extras[0].getInt("port");
				key = extras[0].getByteArray("key").clone();
				byte[] iv = extras[0].getByteArray("iv").clone();
				socket = new Socket(host, port);
				OutputStream out = socket.getOutputStream();

				ObjectOutputStream oos = new ObjectOutputStream(out);
				bytes = Encrypt.encrypt(bytes, key, iv);
				oos.write(bytes);
				oos.flush();
				oos.write(getBytes("END_OF_OBJECT"));
				oos.flush();

				InputStream in = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				FileInfo fi2 = (FileInfo) ois.readObject();

				fiServer = fi2;

				result = Activity.RESULT_OK;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return extras[0];
		}

		@Override
		protected void onPostExecute(Bundle extras) {
			if (extras != null) {
				Messenger messenger = (Messenger) extras.get("MESSENGER");
				Message msg = Message.obtain();
				msg.arg1 = result;
				try {
					messenger.send(msg);
				} catch (android.os.RemoteException e1) {
					Log.w(getClass().getName(), "Exception sending message", e1);
				}
			}
		}

	}
	
	public class SyncServer extends AsyncTask<Bundle, Void, Bundle> {

		@Override
		protected Bundle doInBackground(Bundle... extras) {
			String host = "";
			int port = 0;
			byte[] key;

			String dirToUpdate = extras[0].getString("file");
			FileInfo fi = new FileInfo(dirToUpdate, "");

			Transmission t = new Transmission(Transmission.GET_INFO, fi);
			try {
				bytes = t.getBytes();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				host = extras[0].getString("host");
				port = extras[0].getInt("port");
				key = extras[0].getByteArray("key").clone();
				byte[] iv = extras[0].getByteArray("iv").clone();
				socket = new Socket(host, port);
				
				// Receive Pub Key
				InputStream in2 = socket.getInputStream();
				ObjectInputStream ois2 = new ObjectInputStream(in2);
				PublicKey pubKey = (PublicKey) ois2.readObject();
				
				// Send AES Key
				AESKeyObject ako = new AESKeyObject();
				ako.setKey(key);
				ako.setIv(iv);
				byte[] bytes2 = EncryptionUtil.encrypt(getBytes(ako), pubKey);
				
				OutputStream out = socket.getOutputStream();

				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.write(bytes2);
				oos.flush();
				
				
				bytes = Encrypt.encrypt(bytes, key, iv);
				oos.write(bytes);
				oos.flush();
				oos.write(getBytes("END_OF_OBJECT"));
				oos.flush();

				InputStream in = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(in);
				FileInfo fi2 = (FileInfo) ois.readObject();

				fiServer = fi2;

				result = Activity.RESULT_OK;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return extras[0];
		}

		@Override
		protected void onPostExecute(Bundle extras) {
			if (extras != null) {
				Messenger messenger = (Messenger) extras.get("MESSENGER");
				Message msg = Message.obtain();
				msg.arg1 = result;
				try {
					messenger.send(msg);
				} catch (android.os.RemoteException e1) {
					Log.w(getClass().getName(), "Exception sending message", e1);
				}
			}
		}

	}
	

	public FileInfo getFIServer() {
		return this.fiServer;
	}

	public byte[] getBytes(Object obj) throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		byte[] bytes = bos.toByteArray();
		return bytes;
	}
}