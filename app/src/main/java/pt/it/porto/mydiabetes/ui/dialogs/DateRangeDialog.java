package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.AbstractChartActivity;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class DateRangeDialog extends DialogFragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_TIME_START = "param1";
	private static final String ARG_TIME_END = "param2";
	private static final String ARG_DATA = "param3";

	private Calendar timeStart;
	private Calendar timeEnd;
	private ChartData data;
	private boolean toggleFilter[];

	private EditText timeStartEditText;
	private EditText timeEndEditText;

	public static DateRangeDialog newInstance(Calendar timeStart, Calendar timeEnd, ChartData chartData) {
		DateRangeDialog fragment = new DateRangeDialog();
		Bundle args = new Bundle();
		args.putSerializable(ARG_TIME_START, timeStart);
		args.putSerializable(ARG_TIME_END, timeEnd);
		args.putParcelable(ARG_DATA, chartData);
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			timeStart = (Calendar) args.getSerializable(ARG_TIME_START);
			timeEnd = (Calendar) args.getSerializable(ARG_TIME_END);
			data = args.getParcelable(ARG_DATA);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Filtro");
		View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_range_pick, null, false);
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
				((TimeUpdate) getActivity()).setTimes(timeStart, timeEnd); // TODO validate if start date before end date
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

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
}
