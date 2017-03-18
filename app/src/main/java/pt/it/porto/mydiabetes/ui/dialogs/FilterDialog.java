package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;
import pt.it.porto.mydiabetes.utils.ListViewUtils;

public class FilterDialog extends DialogFragment {
	private static final String ARG_DATA = "param3";

	private ChartData data;
	private boolean toggleFilter[];
	private boolean toggleExtras[];


	public static FilterDialog newInstance(ChartData chartData) {
		FilterDialog fragment = new FilterDialog();
		Bundle args = new Bundle();
		args.putParcelable(ARG_DATA, chartData);
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			data = args.getParcelable(ARG_DATA);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getContext().getString(R.string.filterdialog_title));
		View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_range_pick, null, false);
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
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

}
