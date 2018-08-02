package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;

public class NewTaskDialog extends DialogFragment {

	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_TIME_START = "param1";
	private static final String ARG_TIME_END = "param2";
	private static final String ARG_DATA = "param3";

	private Calendar timeStart;
	private Calendar timeEnd;
	private ChartData data;
	private boolean toggleFilter[];
	private boolean toggleExtras[];
	private ArrayList<String> daysOfWeek;

	private EditText timeStartEditText;
	private EditText timeEndEditText;

	public static NewTaskDialog newInstance() {
		NewTaskDialog fragment = new NewTaskDialog();
		//Bundle args = new Bundle();
//		args.putSerializable(ARG_TIME_START, timeStart);
//		args.putSerializable(ARG_TIME_END, timeEnd);
//		args.putParcelable(ARG_DATA, chartData);
		//fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Bundle args = getArguments();
		/*if (args != null) {
			timeStart = (Calendar) args.getSerializable(ARG_TIME_START);
			timeEnd = (Calendar) args.getSerializable(ARG_TIME_END);
			data = args.getParcelable(ARG_DATA);
		}*/
		daysOfWeek = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("CRIAR NOVA TAREFA");
		View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_task, null, false);
		setSpinners(layout);
		setButtonListeners(layout);
		builder.setView(layout);
		setDialogMainButtons(builder);

		timeStart = Calendar.getInstance();
		//timeStart.roll(Calendar.WEEK_OF_YEAR, false);
		timeEnd = Calendar.getInstance();


		/*View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_range_pick, null, false);
		timeStartEditText = (EditText) layout.findViewById(R.id.time_start);
		timeStartEditText.setText(DateUtils.getFormattedDate(timeStart));
		timeStartEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setStart();
			}
		});
		timeEndEditText = (EditText) layout.findViewById(R.id.time_end);
		timeEndEditText.setText(DateUtils.getFormattedDate(timeEnd));
		timeEndEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setEnd();
			}
		});
		if (data != null && data.hasFilters()) {
			ListView list = (ListView) layout.findViewById(R.id.list);
			data.setupFilter(getContext(), list);
			toggleFilter = new boolean[list.getAdapter().getCount()];
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					((CheckedTextView) view).setChecked(!((CheckedTextView) view).isChecked());
					toggleFilter[position] = !toggleFilter[position];
				}
			});
			ListViewUtils.setListViewHeightBasedOnChildren(list);
		} else {
			layout.findViewById(R.id.txt_header_filters).setVisibility(View.GONE);
		}
		if (data != null && data.hasExtras()) {
			layout.findViewById(R.id.extras).setVisibility(View.VISIBLE);
			ListView list = (ListView) layout.findViewById(R.id.list_extras);
			data.setupExtras(getContext(), list);
			toggleExtras = new boolean[list.getAdapter().getCount()];
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					((CheckedTextView) view).setChecked(!((CheckedTextView) view).isChecked());
					toggleExtras[position] = !toggleExtras[position];
				}
			});
			ListViewUtils.setListViewHeightBasedOnChildren(list);
		}
		builder.setView(layout);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (toggleFilter != null) {
					for (int i = 0; i < toggleFilter.length; i++) {
						if (toggleFilter[i]) {
							data.toggleFilter(i);
						}
					}
				}
				if (toggleExtras != null) {
					for (int i = 0; i < toggleExtras.length; i++) {
						if (toggleExtras[i]) {
							data.toggleExtra(i);
						}
					}
				}
				((TimeUpdate) getActivity()).setTimes(timeStart, timeEnd); // TODO validate if start date before end date
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);*/
		return builder.create();
	}

	private void setDialogMainButtons(AlertDialog.Builder builder) {
		builder.setPositiveButton("Criar Tarefa", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
	}
	private void setWeekDay(Button clickedButton, String dayOfWeek){

		if(daysOfWeek.contains(dayOfWeek)){
			clickedButton.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
			daysOfWeek.remove(dayOfWeek);
		}else{
			daysOfWeek.add(dayOfWeek);
			clickedButton.setBackgroundResource(android.R.drawable.button_onoff_indicator_on);
		}
	}
/*
	private void setEnd() {
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (timeEnd.get(Calendar.YEAR) != year || timeEnd.get(Calendar.MONTH) != monthOfYear || timeEnd.get(Calendar.DAY_OF_MONTH) != dayOfMonth) {
					timeEnd.set(year, monthOfYear, dayOfMonth);
					timeEndEditText.setText(DateUtils.getFormattedDate(timeEnd));
				}
			}
		}, timeEnd.get(Calendar.YEAR), timeEnd.get(Calendar.MONTH), timeEnd.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void setStart() {
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (timeStart.get(Calendar.YEAR) != year || timeStart.get(Calendar.MONTH) != monthOfYear || timeStart.get(Calendar.DAY_OF_MONTH) != dayOfMonth) {
					timeStart.set(year, monthOfYear, dayOfMonth);
					timeStartEditText.setText(DateUtils.getFormattedDate(timeStart));
				}
			}
		}, timeStart.get(Calendar.YEAR), timeStart.get(Calendar.MONTH), timeStart.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public interface TimeUpdate {
		void setTimes(Calendar start, Calendar end);
	}
*/
	private void setButtonListeners(View layout){
		Button segButton = (Button) layout.findViewById(R.id.seg_button);

		segButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "seg");
			}
		});
		Button terButton = (Button) layout.findViewById(R.id.ter_button);

		terButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "ter");
			}
		});
		Button quaButton = (Button) layout.findViewById(R.id.qua_button);

		quaButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "qua");
			}
		});
		Button quiButton = (Button) layout.findViewById(R.id.qui_button);

		quiButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "qui");
			}
		});
		Button sexButton = (Button) layout.findViewById(R.id.sex_button);

		sexButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "sex");
			}
		});
		Button sabButton = (Button) layout.findViewById(R.id.sab_button);

		sabButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "sab");
			}
		});
		Button domButton = (Button) layout.findViewById(R.id.dom_button);

		domButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWeekDay((Button) v, "dom");
			}
		});
	}
	private void setSpinners(View layout){
		Spinner frequency = (Spinner) layout.findViewById(R.id.definePeriodList);
		final GridLayout frequencyLayout = (GridLayout) layout.findViewById(R.id.periodVars);

		frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(position == 1){
					frequencyLayout.setVisibility(View.VISIBLE);
				}
				else{
					if(frequencyLayout.getVisibility()!= View.GONE){
						frequencyLayout.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
}
