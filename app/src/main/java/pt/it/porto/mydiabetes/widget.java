package pt.it.porto.mydiabetes;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.w3c.dom.Text;

import java.util.LinkedList;

import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.LogbookChartList;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.fragments.home.homeMiddleFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.database.DB_Read;

/**
 * Implementation of App Widget functionality.
 */
public class widget extends AppWidgetProvider {

    private static final String FROM_WIDGET = "FROM_WIDGET";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Bundle bundle = new Bundle();
        bundle.putString(FROM_WIDGET,FROM_WIDGET);

        // call new registry activity after + click
        Intent intent = new Intent(context, Home.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setOnClickPendingIntent(R.id.addReg, pendingIntent);

        // call logbook activity after widget click
        Intent intent2 = new Intent(context, LogbookChartList.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.lastRecord_background, pendingIntent2);


        // get values from database
        DB_Read db = new DB_Read(context);
        LinkedList<String> recordValues;
        int userId = db.getUserId();
        recordValues = db.getLastRecord(userId);

        // call method to set values
        if (recordValues != null) {
            setText(remoteViews, R.id.widget_day, recordValues.get(0).substring(0, 10));
            setText(remoteViews, R.id.widget_hour, recordValues.get(0).substring(11, 16));
            setText(remoteViews, R.id.widget_c_val, recordValues.get(1));
            setText(remoteViews, R.id.widget_i_val, recordValues.get(2));
            setText(remoteViews, R.id.widget_g_val, recordValues.get(3));
        }
        db.close();


        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.widget, null, true);
        LineChart chart = (LineChart) rowView.findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        LinkedList<Entry> entries = new LinkedList<>();

        entries.add(new BarEntry(1, 100));
        entries.add(new BarEntry(2, 120));
        entries.add(new BarEntry(3, 110));
        entries.add(new BarEntry(4, 90));

        LineDataSet set1 = new LineDataSet(entries, "");
        set1.setColor(Color.GREEN);
        set1.setLineWidth(3f);
        set1.setValueTextSize(15f);
        set1.setCubicIntensity(0.2f);
        set1.setDrawCircles(true);
        set1.setCircleRadius(5f);
        set1.setCircleColor(Color.GREEN);
        set1.setHighLightColor(Color.rgb(244, 117, 117));

        LineData lineData = new LineData(set1);
        chart.setData(lineData);
        chart.invalidate();


        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    // write values
    public static void setText(RemoteViews remoteViews, int field, String replaceValue) {

        if (replaceValue == null) remoteViews.setTextViewText(field, "---");
        else if (replaceValue.equals("-1") || replaceValue.equals("-1.0")) {
            remoteViews.setTextViewText(field, "---");
        }
        else remoteViews.setTextViewText(field, replaceValue);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}



