package pt.it.porto.mydiabetes.ui.listAdapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;

public class StringSpinnerAdapter extends BaseAdapter {

	private Context context;
	private String[] mItems;

	public StringSpinnerAdapter(Context context, String[] mItems) {
		this.context = context;
		this.mItems = mItems;

	}
	public int getItemPosition(String elem){
		for(int index=0;index<mItems.length;index++){
			if(mItems[index].equals(elem)){
				return index;
			}
		}
		return -1;
	}

	@Override
	public int getCount() {
		return mItems.length;
	}

	@Override
	public String getItem(int position) {
		return mItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getDropDownView(int position, View view, ViewGroup parent) {
		if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
			view = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
			view.setTag("DROPDOWN");
		}

		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(getItem(position));

		return view;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
			view = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
			view.setTag("NON_DROPDOWN");
		}
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(getItem(position));
		return view;
	}

}
