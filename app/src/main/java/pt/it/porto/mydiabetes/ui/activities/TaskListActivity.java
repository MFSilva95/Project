package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.ui.dialogs.NewTaskDialog;
import pt.it.porto.mydiabetes.ui.listAdapters.TaskListAdapter;
import pt.it.porto.mydiabetes.ui.usability.TaskTouchHelper;

public class TaskListActivity extends BaseActivity {

	ArrayList<Task> receiverTaskList = new ArrayList<>();
	private RecyclerView taskList;
	private FloatingActionButton fab;
	//private Boolean fabOpen = false;
	//private String ROTATION = "rotation";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasklist);
		fillTaskList();
		//setFab();
	}

	/*private void setFab() {
		fab = (FloatingActionButton) findViewById(R.id.taskFab);
		fab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(fabOpen){
					animateFab(45);
					fabOpen = false;
				}else{
					NewTaskDialog dialog = NewTaskDialog.newInstance();
					FragmentManager myManager = getFragmentManager();
					dialog.show(myManager,"cenas");
					animateFab(-45);
					fabOpen = true;
				}
			}
		});
	}*/
	/*private void animateFab(int i) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(createRotationAnimator(i));
		animatorSet.start();
	}*/

	/*private Animator createRotationAnimator(float ang) {
		float rotation = fab.getRotation();
		return ObjectAnimator.ofFloat(fab, ROTATION, rotation, rotation + ang)
				.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
	}*/


	private void fillTaskList() {

		String[] temp2 = {"AVISO IMPORTANTE","Meal","10:s"};

		Task myTask1 = new Task("5 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 9);
		Task myTask2 = new Task("1 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 6);
		//setupAlarm(myTask2);
		Task myTask3 = new Task("2 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 2);
		Task myTask4 = new Task("4 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 4);
		Task myTask5 = new Task("3 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 3);

		receiverTaskList.add(myTask1);
		receiverTaskList.add(myTask2);
		receiverTaskList.add(myTask3);
		receiverTaskList.add(myTask4);
		receiverTaskList.add(myTask5);

		Collections.sort(receiverTaskList);

		taskList = (RecyclerView) findViewById(R.id.taskListDisplay);
		TaskListAdapter taskAdapter = new TaskListAdapter(receiverTaskList,this);

		ItemTouchHelper.Callback callback = new TaskTouchHelper(taskAdapter);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(taskList);
		taskList.setAdapter(taskAdapter);
		taskList.setLayoutManager(new LinearLayoutManager(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item!=null){
			if(item.getTitle().equals("Adicionar Tarefa")){
				NewTaskDialog dialog = NewTaskDialog.newInstance();
				dialog.show(getSupportFragmentManager(), null);
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
