package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by parra on 04/02/2017.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}