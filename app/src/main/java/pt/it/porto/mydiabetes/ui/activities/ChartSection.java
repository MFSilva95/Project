package pt.it.porto.mydiabetes.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.widget;

import static android.graphics.Color.rgb;

public class ChartSection extends AppCompatActivity {

    private HorizontalBarChart barChart;
    private RadarChart radarChart;
    private LineChart lineChart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chart_section);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        horizontalBarChart();
        radarChart();
        //lineChart();

    }

    public void radarChart() {

        String[] labels = {getString(R.string.Carbs),
                getString(R.string.Insulin),
                getString(R.string.Glycemia),
                getString(R.string.Glycemia),
                getString(R.string.Exercise),
                getString(R.string.weight)};

        radarChart = findViewById(R.id.generalRadarChart);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(100);
        radarChart.getLegend().setEnabled(false);
        radarChart.getDescription().setEnabled(false);
        radarChart.getYAxis().setAxisMinimum(0);
        radarChart.animateY(1000);

        // get number of records per parameter
        DB_Read rdb = new DB_Read(this);
        Integer[] nRecords = rdb.getLastDayNumberOfRecords();
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        dataVals.add(new RadarEntry(nRecords[0]));
        dataVals.add(new RadarEntry(nRecords[1]));
        dataVals.add(new RadarEntry(nRecords[2]));
        dataVals.add(new RadarEntry(nRecords[3]));
        dataVals.add(new RadarEntry(nRecords[5]));
        rdb.close();

        RadarDataSet dataSet = new RadarDataSet(dataVals, "");
        dataSet.setColor(rgb(76, 175, 80));
        dataSet.setFillColor(rgb(76, 175, 80));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10);
        dataSet.setValueFormatter(new MyValueFormatter());


        RadarData data = new RadarData();
        data.addDataSet(dataSet);


        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        radarChart.getYAxis().setEnabled(false);
        radarChart.setData(data);
        radarChart.invalidate();
    }

    public void horizontalBarChart() {
        barChart = (HorizontalBarChart) findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.animateY(1000); // add a nice and smooth animation

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setAxisMaximum(25f);
        barChart.getAxisRight().setAxisMinimum(-25f);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawZeroLine(true);
        barChart.getAxisRight().setLabelCount(7, false);
        barChart.getAxisRight().setTextSize(9f);

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.getLegend().setEnabled(false);

        LinkedList<BarEntry> barEntries = new LinkedList<>();
        barEntries.add(new BarEntry(0, new float[]{ -20, 15 }));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setEnabled(false);
        //barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setColors(new int[] { R.color.primary_dark, R.color.primary_light, R.color.primary_light, R.color.primary_light }, this);

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.getData().setDrawValues(true);
        barChart.invalidate();
    }

    /*
    public void lineChart() {
        lineChart = (LineChart) findViewById(R.id.charthba1c);
        lineChart.getDescription().setEnabled(false);

        LimitLine ideal = new LimitLine(7, "Optimum value");
        ideal.setLineWidth(4f);
        ideal.enableDashedLine(10f, 10f, 0f);
        ideal.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ideal.setTextSize(10f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.addLimitLine(ideal);
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(1f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        LinkedList<Entry> entries = new LinkedList<>();
        entries.add(new BarEntry(1, 5));
        entries.add(new BarEntry(2, 6));
        entries.add(new BarEntry(3, 7));
        entries.add(new BarEntry(4, 9));

        LineDataSet set1 = new LineDataSet(entries, "");
        set1.setColor(rgb(76, 175, 80));
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setCubicIntensity(0.2f);
        set1.setDrawCircles(true);
        set1.setCircleRadius(5f);
        set1.setCircleColor(rgb(76, 175, 80));
        set1.setHighLightColor(Color.rgb(244, 117, 117));

        LineData lineData = new LineData(set1);
        lineData.setValueFormatter(new widget.MyValueFormatter());
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyValueFormatter extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "" + ((int) value);
        }
    }
}


