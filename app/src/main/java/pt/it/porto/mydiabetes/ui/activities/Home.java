package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import java.util.HashMap;

import pt.it.porto.mydiabetes.R;

import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;



public class Home extends BaseActivity {

	public static final int CHANGES_OCCURRED = 1;

	private DrawerLayout drawerLayout;

	private static final int DB_OLD_PERMISSION_CONSTANT = 102;
    private static final int INIT_PERMISSION_REQUEST = 103;

    private static int idUser;
    private FloatingActionButton fab;


	@Override
	protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        DB_Read db = new DB_Read(this);
        HashMap<Integer, String[]> in = db.Insulin_GetAll();
        db.close();

		DB_Write w = new DB_Write(this);
		w.MyData_Save(new UserInfo(1,"teste",1,40,30,70,200,"11-04-1789",1,190.0,"11-12-1987",110));
		if(in == null || in.size()== 0) {
            w.Insulin_Add(new Insulin(1,"teste_pump","1","long", 10.0));
            w.Insulin_Add(new Insulin(1,"teste_pen","0","long", 10.0));
        }
		w.close();

		fab = findViewById(R.id.fabHOME);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fab.getContext(), NewHomeRegistry.class);
                startActivityForResult(intent, 2);
            }
        });


        idUser = 1;

        setMainView(savedInstanceState);

	}

	public void setMainView(Bundle savedInstanceState){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(mDrawerToggle);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getUserId();
                // id will be your layout's id: for example R.id.left_drawer
                if(idUser!=-1){
                    DB_Write dbwrite = new DB_Write(drawerView.getContext());//getBaseContext());
                    dbwrite.Log_Save(idUser,"Drawer_opened");
                    dbwrite.close();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
                if(idUser!=-1){
                    DB_Write dbwrite = new DB_Write(drawerView.getContext());//getBaseContext());
                    dbwrite.Log_Save(idUser,"Drawer_closed");
                    dbwrite.close();
                }
                //Log.i("test", "onDrawerClosed: ");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

    }


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode){
			case DB_OLD_PERMISSION_CONSTANT:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//backup_old_db(Home.this);
                    setMainView(null);
				} else {
					Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
				}
				break;
            case INIT_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMainView(null);
                } else {
                    Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
                    setMainView(null);
                }
                break;
		}
	}

}


