package pt.it.porto.mydiabetes.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class Preferences {

	public static final String PREFERENCES = "MyPreferences";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String KEYSTORE_PK_ALIAS = "keystore_pk_keys";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}

	public static boolean showFeatureForFirstTime(Context context, String featureName) {
		SharedPreferences prefs = getPreferences(context);
		if (prefs.contains(featureName)) {
			return false;
		} else {
			prefs.edit().putBoolean(featureName, false).apply();// set it as seen
			return true;
		}
	}

	public static boolean saveCloudSyncCredentials(Context context, String username, String password) {
		boolean saved = false;
		if (Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT) {
			try {
				saved = saveCloudSyncCredentialsJB(context, username, password);
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			}
		}
		if (saved) {
			return true;
		}
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(USERNAME, username).putString(PASSWORD, password).apply();
		return true;
	}

	public static String getUsername(Context context) {
		return getPreferences(context).getString(USERNAME, null);
	}

	public static String getPassword(Context context) {
		if (Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT) {
			try {
				return getPasswordJB(context);
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			}
		}
		return getPreferences(context).getString(PASSWORD, null);
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static boolean saveCloudSyncCredentialsJB(Context context, String username, String password) throws GeneralSecurityException, IOException {
		KeyStore keyStore = initKeyStore(context);

		RSAPublicKey publicKey = getPublicKey(context, keyStore);

		Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
		input.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] pass = input.doFinal(password.getBytes("UTF-8"));
		getPreferences(context).edit().putString(USERNAME, username).putString(PASSWORD, Base64.encodeToString(pass, Base64.DEFAULT)).apply();
		return true;
	}

	private static String getPasswordJB(Context context) throws GeneralSecurityException, IOException {
		KeyStore keyStore = initKeyStore(context);

		RSAPrivateKey privateKey = getPrivateKey(context, keyStore);
		Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
		output.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] password = output.doFinal(Base64.decode(getPreferences(context).getString(PASSWORD, ""), Base64.DEFAULT));

		return new String(password, "UTF-8");
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static RSAPrivateKey getPrivateKey(Context context, KeyStore keyStore) throws GeneralSecurityException, IOException {
		if (keyStore == null) {
			keyStore = initKeyStore(context);
		}
		KeyStore.PrivateKeyEntry keys = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_PK_ALIAS, null);
		return (RSAPrivateKey) keys.getPrivateKey();
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static RSAPublicKey getPublicKey(Context context, KeyStore keyStore) throws GeneralSecurityException, IOException {
		if (keyStore == null) {
			keyStore = initKeyStore(context);
		}
		KeyStore.PrivateKeyEntry keys = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_PK_ALIAS, null);
		return (RSAPublicKey) keys.getCertificate().getPublicKey();
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static KeyStore initKeyStore(Context context) throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
		keyStore.load(null);
		if (!keyStore.containsAlias(KEYSTORE_PK_ALIAS)) {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", keyStore.getProvider());
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			end.add(Calendar.YEAR, 2);

			KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
					.setAlias(KEYSTORE_PK_ALIAS)
					.setSubject(new X500Principal("CN=MyDiabetes, O=MyDiabetes"))
					.setSerialNumber(BigInteger.ONE)
					.setStartDate(start.getTime())
					.setEndDate(end.getTime())
					.build();
			generator.initialize(spec);

			generator.generateKeyPair();
		}
		return keyStore;

	}
}
