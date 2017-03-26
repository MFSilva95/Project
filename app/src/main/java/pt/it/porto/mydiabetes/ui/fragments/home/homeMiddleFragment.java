package pt.it.porto.mydiabetes.ui.fragments.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.esafirm.imagepicker.features.ImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.Day;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.LogBookEntry;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.CholesterolDetail;
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
import pt.it.porto.mydiabetes.ui.activities.MealActivity;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.activities.WeightDetail;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.ui.usability.HomeTouchHelper;
import pt.it.porto.mydiabetes.utils.DateUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeMiddleFragment extends Fragment {
	private YapDroid yapDroid;
	final int WAIT_REGISTER = 123;
    //Number of last days shown
    final int NUMBER_OF_DAYS = 7;
    //Number of records shown in (exercice, cholesterol, ..). if changed need ui modifications
    final int NUMBER_OF_RECORDS = 3;
	final String TAG = "homeFrag";
	//private ItemTouchHelper helper = null;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == WAIT_REGISTER && resultCode == Home.CHANGES_OCCURRED) {
			updateHomeList();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private FloatingActionButton fab;


	ArrayList<Task> taskListFromYap = new ArrayList<>();
	//ArrayList<Task> receiverTaskList = new ArrayList<>();
	ArrayList<Advice> receiverAdviceList = new ArrayList<>();

    LinkedList<Day> daysList = new LinkedList<>();

    private LinkedList<WeightRec> weightList;
    private LinkedList<ExerciseRec> exerciseList;
    private LinkedList<DiseaseRec> diseaseList;
    private LinkedList<BloodPressureRec> bloodPressureList;
    private LinkedList<HbA1cRec> hbA1cList;
    private LinkedList<CholesterolRec> cholesterolList;
    private LinkedList<LogBookEntry> logBookEntries;


	private ListView logbookList;
	private RecyclerView homeList;

	public static homeMiddleFragment newInstance() {
		homeMiddleFragment fragment = new homeMiddleFragment();
		return fragment;
	}

	public homeMiddleFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_home_middle, container, false);

		//yapDroid = YapDroid.newInstance(this);

		homeList = (RecyclerView) layout.findViewById(R.id.HomeListDisplay);
		fab = (FloatingActionButton) layout.findViewById(R.id.fab);

		setFabClickListeners();
		fillHomeList();

		return layout;
	}

	private void updateHomeList(){
		fillTaskList();
		fillAdviceList();
		fillDays();
		((HomeAdapter)homeList.getAdapter()).updateList(receiverAdviceList, taskListFromYap, daysList);
		homeList.getAdapter().notifyDataSetChanged();
	}

	private void fillHomeList() {

		fillTaskList();
		fillAdviceList();
        fillDays();

		HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, taskListFromYap, daysList);

		/*if(helper==null){
			ItemTouchHelper.Callback callback = new HomeTouchHelper(homeAdapter);
			helper= new ItemTouchHelper(callback);
			helper.attachToRecyclerView(homeList);
		}*/
		homeList.setAdapter(homeAdapter);
		homeList.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	private void setFabClickListeners() {
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(fab.getContext(), NewHomeRegistry.class);
				//startActivity(intent);
				startActivityForResult(intent, WAIT_REGISTER);
			}
		});

	}

	private void fillTaskList() {
		//taskListFromYap = yapDroid.getYapMultipleTasks();
		Task task1 = new Task();
		task1.setSummaryText("Fazer exercicio hoje!");
		task1.setExpText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
		task1.setUrg(5);

		Task task2 = new Task();
		task2.setSummaryText("Actualizar dados!");
		task2.setExpText("Fazer a sincronização da bomba com a aplicação!");
		task2.setUrg(3);

		taskListFromYap = new ArrayList<>();
		taskListFromYap.add(task1);
		taskListFromYap.add(task2);
	}

	public void fillAdviceList() {
		//receiverAdviceList.addAll(yapDroid.getAllEndAdvices(getApplicationContext()));
		Advice task1 = new Advice();
		task1.setSummaryText("Fazer exercicio hoje!");
		task1.setExpandedText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
		task1.setUrgency(5);

		Advice task2 = new Advice();
		task2.setSummaryText("Actualizar dados!");
		task2.setExpandedText("Fazer a sincronização da bomba com a aplicação!");
		task2.setUrgency(3);

		ArrayList<Advice> adviceList = new ArrayList<>();
		adviceList.add(task1);
		adviceList.add(task2);

		receiverAdviceList = new ArrayList<>();
		receiverAdviceList.addAll(adviceList);
		Collections.sort(receiverAdviceList);

	}

	public void fillDays(){
		Calendar calendar = Calendar.getInstance(); // this would default to now
		int index = 0;
		while(index!=NUMBER_OF_DAYS){
			dbRead(calendar);
			Day day = new Day(DateUtils.getFormattedDate(calendar),logBookEntries,exerciseList,weightList,diseaseList,bloodPressureList,hbA1cList,cholesterolList);
			if(!day.isEmpty()){
				daysList.add(day);
			}

			calendar.add(Calendar.DAY_OF_MONTH, -1);
			index++;
		}
    }

    private void dbRead(Calendar calendar) {
        DB_Read db = new DB_Read(getContext());
        String date = DateUtils.getFormattedDate(calendar);
        logBookEntries = db.getLogBookByDate(date);
        exerciseList = db.getExerciceByDate(date,NUMBER_OF_RECORDS);
		weightList = db.getWeightByDate(date,NUMBER_OF_RECORDS);
		diseaseList = db.getDiseaseByDate(date,NUMBER_OF_RECORDS);
		bloodPressureList = db.getBloodPressureByDate(date,NUMBER_OF_RECORDS);
		hbA1cList = db.getHbA1cByDate(date,NUMBER_OF_RECORDS);
		cholesterolList = db.getCholesterolByDate(date,NUMBER_OF_RECORDS);
		db.close();
    }


	@Override
	public void onResume() {
		super.onResume();
		daysList = new LinkedList<Day>();
		updateHomeList();
	}
}
