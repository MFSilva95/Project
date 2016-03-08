package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PreferencesTest {


	Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
	}


	@Test
	public void testGetPreferences() throws Exception {
		SharedPreferences prefs = Preferences.getPreferences(mMockContext);
		Assert.assertNotNull(prefs);
	}

	@Test
	public void testShowFeatureForFirstTime() throws Exception {
		Assert.assertTrue(Preferences.showFeatureForFirstTime(mMockContext, "test"));
		Assert.assertFalse(Preferences.showFeatureForFirstTime(mMockContext, "test"));
	}

	@Test
	public void testSaveCloudSyncCredentials() throws Exception {
		Assert.assertTrue(Preferences.saveCloudSyncCredentials(mMockContext, "user", "pass"));
	}

	@Test
	public void testGetUsername() throws Exception {
		Assert.assertTrue(Preferences.saveCloudSyncCredentials(mMockContext, "user", "pass"));

		Assert.assertEquals("user", Preferences.getUsername(mMockContext));
	}

	@Test
	public void testGetPassword() throws Exception {
		Assert.assertTrue(Preferences.saveCloudSyncCredentials(mMockContext, "user", "pass"));

		Assert.assertEquals("pass", Preferences.getPassword(mMockContext));

	}
}