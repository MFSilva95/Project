package pt.it.porto.mydiabetes.ui.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.fragments.ChartFragment;

public class GraphsActivity extends BaseActivity implements ChartFragment.OnFragmentInteractionListener {


	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
	private static final int MAX_VALUES_IN_GRAPH = 100;

	private int numberOfElementsInGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		getSupportActionBar();

		setupContent();
	}


	private void setupContent() {
		ChartFragment fragment = (ChartFragment) getSupportFragmentManager().findFragmentById(R.id.chart_fragment);
		fragment.setListAdapter(new WeightAdapter(MyDiabetesStorage.getInstance(this).getAllWeights(null)));
		fragment.setChartData(convertToGraphPoints(MyDiabetesStorage.getInstance(this)
				.getAllWeights(new MyDiabetesStorage.QueryOptions().setSortOrder(MyDiabetesStorage.QueryOptions.ORDER_DESC))));
		fragment.setName("Peso");
		fragment.endSetup();
	}


	private LineData convertToGraphPoints(Cursor cursor) {
		cursor.moveToFirst();
		numberOfElementsInGraph = cursor.getCount() > MAX_VALUES_IN_GRAPH ? MAX_VALUES_IN_GRAPH : cursor.getCount();

		String[] xs = new String[numberOfElementsInGraph];
		Entry[] set = new Entry[numberOfElementsInGraph];


		String date;
		double value;
		int i = numberOfElementsInGraph - 1;
		while (i >= 0) {
			date = cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME));
			value = cursor.getDouble(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE));
			xs[i] = date;
			set[i] = new Entry((float) value, i);
			i--;
			cursor.moveToNext();
		}

		LineData result = new LineData(xs);
		LineDataSet lineSet = new LineDataSet(Arrays.asList(set), "");
		setupStyleLineSet(lineSet);

		result.addDataSet(lineSet);
		result.setHighlightEnabled(true);
		return result;
	}

	public void setupStyleLineSet(LineDataSet lineSet) {
		lineSet.setLineWidth(0f);
		lineSet.setDrawCircles(true);
		lineSet.setCircleSize(5f);
		lineSet.setFillFormatter(new DefaultFillFormatter());
		lineSet.setDrawFilled(true);

		lineSet.setDrawCubic(true);
		lineSet.setCubicIntensity(0.2f);

		lineSet.setValueTextSize(Utils.convertDpToPixel(6));

		lineSet.setFillColor(Color.RED);
		lineSet.setCircleColor(Color.RED);
	}

	@Override
	public void onItemSelected(int position) {

	}


	class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

		private Cursor cursor;

		public WeightAdapter(Cursor cursor) {
			this.cursor = cursor;
//			setHasStableIds(true);
		}

		@Override
		public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_graph_weights, parent, false);
			return new WeightViewHolder(view);
		}

		@Override
		public void onBindViewHolder(WeightViewHolder holder, int position) {
			cursor.moveToPosition(position);
			Date date = null;
			try {
				date = iso8601Format.parse(cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.date.setText(DateFormat.getDateInstance().format(date));
//			holder.date.setText(dateFormat.format(date));
			holder.value.setText(String.format("%.1f", (Double) cursor.getDouble(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE))));
		}

		@Override
		public void onAttachedToRecyclerView(RecyclerView recyclerView) {
			super.onAttachedToRecyclerView(recyclerView);
		}

		@Override
		public int getItemCount() {
			return cursor.getCount();
		}

		class WeightViewHolder extends RecyclerView.ViewHolder {
			TextView date;
			TextView value;

			public WeightViewHolder(View itemView) {
				super(itemView);
				date = (TextView) itemView.findViewById(R.id.txt_date);
				value = (TextView) itemView.findViewById(R.id.txt_weight);
			}
		}
	}
}
