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
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class Preferences {

	public static final String PREFERENCES = "MyPreferences";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String KEYSTORE_PK_ALIAS = "keystore_pk_keys";
	private static final String LAST_RANK_UPDATE = "last_rank_update";
	private static final String POINTS = "points";
	private static final String STREAK = "streak";
	private static final String POINTS_G = "pointGlobal";
	private static final String STREAK_G = "streakGlobal";
	private static final String POINTS_W = "pointWeak";
	private static final String STREAK_W = "streakWeek";
	private static final String GLYCAEMIA_W = "glycaemiaWeek";
	private static final String HYPERHYPO_W = "hyperhypoWeek";


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

	public static void saveLastRankUpdate(Context context, String date) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(LAST_RANK_UPDATE, date).apply();
	}

	public static Date getLastRankUpdate(Context context) {
		String datestring = getPreferences(context).getString(LAST_RANK_UPDATE, null);
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		Date d = null;
		if(datestring!=null){
			try {
				d = dateFormat.parse(datestring);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return d;
	}

	public static void saveRankInfo(Context context, String points, String streak, String points_g, String streak_g, String points_w, String streak_w, String glycaemia_w, String hyperhypo_g) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(POINTS, points)
				.putString(STREAK, streak)
				.putString(POINTS_G, points_g)
				.putString(STREAK_G, streak_g)
				.putString(POINTS_W, points_w)
				.putString(STREAK_W, streak_w)
				.putString(GLYCAEMIA_W, glycaemia_w)
				.putString(HYPERHYPO_W, hyperhypo_g)
				.apply();
	}

	public static String[] getRankInfo(Context context) {
		String[] ranks = new String[8];
		ranks[0] = getPreferences(context).getString(POINTS, null);
		ranks[1] = getPreferences(context).getString(STREAK, null);
		ranks[2] = getPreferences(context).getString(POINTS_G, null);
		ranks[3] = getPreferences(context).getString(STREAK_G, null);
		ranks[4] = getPreferences(context).getString(POINTS_W, null);
		ranks[5] = getPreferences(context).getString(STREAK_W, null);
		ranks[6] = getPreferences(context).getString(GLYCAEMIA_W, null);
		ranks[7] = getPreferences(context).getString(HYPERHYPO_W, null);
		return ranks;
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static boolean saveCloudSyncCredentialsJB(Context context, String username, String password) throws GeneralSecurityException, IOException {
		KeyStore keyStore = initKeyStore(context);

		RSAPublicKey publicKey = getPublicKey(context, keyStore);

		Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
		input.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] pass = input.doFinal(password.getBytes("UTF-8"));
		getPreferences(context).edit()
				.putString(USERNAME, username)
				.putString(PASSWORD, Base64.encodeToString(pass, Base64.DEFAULT))
				.apply();
		return true;
	}

	private static String getPasswordJB(Context context) throws GeneralSecurityException, IOException {
		String cipherPassword = getPreferences(context).getString(PASSWORD, null);
		if (cipherPassword == null) {
			return null;
		}
		KeyStore keyStore = initKeyStore(context);

		PrivateKey privateKey = getPrivateKey(context, keyStore);
		Cipher output;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");

			output.init(Cipher.DECRYPT_MODE, privateKey);
		} else {
			// Marshmallow version :)
			output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			output.init(Cipher.DECRYPT_MODE, privateKey);
		}

		byte[] password = output.doFinal(Base64.decode(cipherPassword, Base64.DEFAULT));
		return new String(password, "UTF-8");
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static PrivateKey getPrivateKey(Context context, KeyStore keyStore) throws GeneralSecurityException, IOException {
		if (keyStore == null) {
			keyStore = initKeyStore(context);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			return  (PrivateKey) keyStore.getKey(KEYSTORE_PK_ALIAS, null);
		} else {
			KeyStore.PrivateKeyEntry keys = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_PK_ALIAS, null);
			return keys.getPrivateKey();
		}
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static RSAPublicKey getPublicKey(Context context, KeyStore keyStore) throws GeneralSecurityException, IOException {
		if (keyStore == null) {
			keyStore = initKeyStore(context);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			return (RSAPublicKey) keyStore.getCertificate(KEYSTORE_PK_ALIAS).getPublicKey();
		}else {

			KeyStore.PrivateKeyEntry keys = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_PK_ALIAS, null);
			return (RSAPublicKey) keys.getCertificate().getPublicKey();
		}
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

			KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias(KEYSTORE_PK_ALIAS)
					.setSubject(new X500Principal("CN=MyDiabetes, O=MyDiabetes"))
					.setSerialNumber(BigInteger.ONE)
					.setStartDate(start.getTime())
					.setEndDate(end.getTime())
					.build();
//			KeyGenParameterSpec  spec = new KeyGenParameterSpec.Builder(KEYSTORE_PK_ALIAS, KeyProperties.PURPOSE_DECRYPT)
//					.setCertificateSubject(new X500Principal("CN=MyDiabetes, O=MyDiabetes"))
//					.setCertificateSerialNumber(BigInteger.ONE)
//					.setKeyValidityStart(start.getTime())
//					.setKeyValidityEnd(end.getTime())
//					.build();


			generator.initialize(spec);

			generator.generateKeyPair();
		}
		return keyStore;

	}
}
