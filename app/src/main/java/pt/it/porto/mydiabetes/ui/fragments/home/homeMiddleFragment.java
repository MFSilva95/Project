package pt.it.porto.mydiabetes.ui.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

/**
 * Created by parra on 21/02/2017.
 */

public class homeMiddleFragment extends Fragment {
	private YapDroid yapDroid;
	final int WAIT_REGISTER = 123;
    //Number of last days shown
    final int NUMBER_OF_DAYS = 7;
    final String TAG = "homeFrag";
	//private ItemTouchHelper helper = null;
	private FloatingActionButton fab;
	ArrayList<Task> taskListFromYap = new ArrayList<>();
	ArrayList<Advice> receiverAdviceList = new ArrayList<>();



	private RecyclerView homeRecyclerView;
	private List<HomeElement> logBookList;
	public static homeMiddleFragment newInstance() {
		homeMiddleFragment fragment = new homeMiddleFragment();
		return fragment;
	}
	public homeMiddleFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == WAIT_REGISTER && resultCode == Home.CHANGES_OCCURRED) {
			updateHomeList();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_home_middle, container, false);
		//yapDroid = YapDroid.newInstance(this);
		homeRecyclerView = (RecyclerView) layout.findViewById(R.id.HomeListDisplay);
		fab = (FloatingActionButton) layout.findViewById(R.id.fab);
		setFabClickListeners();
		fillHomeList();
		return layout;
	}
	private void updateHomeList(){
		logBookList = new LinkedList<>();
		//fillTaskList();
		//fillAdviceList();
		fillDays();

		logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
		logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
		((HomeAdapter) homeRecyclerView.getAdapter()).updateList(logBookList);
		homeRecyclerView.getAdapter().notifyDataSetChanged();
	}
	private void fillHomeList() {
		logBookList = new LinkedList<>();
		//fillTaskList();
		//fillAdviceList();
		fillDays();

		logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
		logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));

		HomeAdapter homeAdapter = new HomeAdapter(logBookList);
		/*if(helper==null){
			ItemTouchHelper.Callback callback = new HomeTouchHelper(homeAdapter);
			helper= new ItemTouchHelper(callback);
			helper.attachToRecyclerView(homeList);
		}*/
		homeRecyclerView.setAdapter(homeAdapter);
		homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
	}
	private void dbRead(Calendar calendar) {
		DB_Read db = new DB_Read(getContext());
		String date = DateUtils.getFormattedDate(calendar);
		long currentTime= System.currentTimeMillis();
		if (db.getLogBookByDate(date).size() > 0) {
			Log.e("getLogBookByDate.size", db.getLogBookByDate(date).size() + "");
			CharSequence dateText = android.text.format.DateUtils.getRelativeTimeSpanString(getDateInMillis(date), currentTime, android.text.format.DateUtils.DAY_IN_MILLIS);
			this.logBookList.add(new HomeElement(HomeElement.Type.HEADER, dateText.toString()));
			this.logBookList.addAll(db.getLogBookByDate(date));
		}
		db.close();
	}

	public void fillDays() {
		Calendar calendar = Calendar.getInstance(); // this would default to now
		int index = 0;
		while (index != NUMBER_OF_DAYS) {
			dbRead(calendar);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			index++;
		}
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

		if(taskListFromYap.size()>0 && BuildConfig.TASKS_AVAILABLE){
			logBookList.add(new HomeElement(HomeElement.Type.HEADER, getContext().getString(R.string.tasks)));
			logBookList.addAll(taskListFromYap);
		}

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

		if(receiverAdviceList.size()>0 && BuildConfig.ADVICES_AVAILABLE){
			logBookList.add(new HomeElement(HomeElement.Type.HEADER, getContext().getString(R.string.advices)));
			logBookList.addAll(receiverAdviceList);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		updateHomeList();
	}

	public static long getDateInMillis(String srcDate) {
		SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd");

		long dateInMillis = 0;
		try {
			Date date = desiredFormat.parse(srcDate);
			dateInMillis = date.getTime();
			return dateInMillis;
		} catch (ParseException e) {
			Log.d("Exception date.",e.getMessage());
			e.printStackTrace();
		}

		return 0;
	}
}
