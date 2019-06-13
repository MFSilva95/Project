package pt.it.porto.mydiabetes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Calendar;

import android.os.Bundle;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;


import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.util.LinkedList;

import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.LogbookChartList;






import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;







/**
 * Implementation of App Widget functionality.
 */
public class widget extends AppWidgetProvider {

    private static final String FROM_WIDGET = "FROM_WIDGET";
    private static final int WIDGET_HEIGHT = 400;
    private static final int WIDTH_PADDING = 0;
    private static final int N_REG = 5;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Bundle bundle = new Bundle();
        bundle.putString(FROM_WIDGET,FROM_WIDGET);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        // call new registry activity after + click
        Intent intent = new Intent(context, Home.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.addReg, pendingIntent);

        // call logbook activity after widget click
        Intent intent2 = new Intent(context, LogbookChartList.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_info_text, pendingIntent2);

        // call logbook activity after widget click
        Intent intent3 = new Intent(context, LogbookChartList.class);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 0, intent3, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.graph_img, pendingIntent3);



        // get values from database
        DB_Read db = new DB_Read(context);
        LinkedList<String> recordValues;
        int userId = db.getUserId();
        recordValues = db.getLastRecord(userId);
        LinkedList<GlycemiaRec> lastXGlicaemias = db.getLastXGlycaemias(userId,N_REG);
        db.close();

        // call method to set values
        if (recordValues != null) {
            setText(remoteViews, R.id.widget_day, recordValues.get(0).substring(0, 10));
            setText(remoteViews, R.id.widget_hour, recordValues.get(0).substring(11, 16));
            setText(remoteViews, R.id.widget_c_val, recordValues.get(1));
            setText(remoteViews, R.id.widget_i_val, recordValues.get(2));
            setText(remoteViews, R.id.widget_g_val, recordValues.get(3));
        }


        //DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        AppWidgetProviderInfo widgetInfo = AppWidgetManager.getInstance (context).getAppWidgetInfo (appWidgetId);


        int width = widgetInfo.minWidth; //* getWidthColsNum (widgetInfo);
        int height = widgetInfo.minHeight; //* getHeightColsNum (widgetInfo);

        Bitmap b = drawToBitmap(context, width, height, lastXGlicaemias);


        //Bitmap b = drawToBitmap(context, metrics.widthPixels-WIDTH_PADDING, metrics.heightPixels-WIDTH_PADDING, lastXGlicaemias);//WIDGET_HEIGHT, lastXGlicaemias);
        if(b!=null){
            remoteViews.setImageViewBitmap(R.id.graph_img, b);
        }


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












public static class  MyXAxisValueFormatter extends IndexAxisValueFormatter {


    private long referenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    public MyXAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        this.mDate = new Date();
    }

    private String getHour(long timestamp){
        try{
            mDate.setTime(timestamp);
            String time_formated = mDataFormat.format(mDate);

            return time_formated;
        }
        catch(Exception ex){
            return "xx";
        }
    }


    @Override
    public String getFormattedValue(float value) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long convertedTimestamp = (long) value;

        // Retrieve original timestamp
        long originalTimestamp = referenceTimestamp + convertedTimestamp;

        // Convert timestamp to hour:minute
        return getHour(originalTimestamp);
    }
}




    public static class MyValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            // write your logic here
            return (int) value+"";
        }
    }










    public static void setChart(Context context, LineChart chart, LinkedList<GlycemiaRec> glicData){

        if(glicData==null){return;}
        if(glicData.size()<=0){return;}

        long firstTime = glicData.get(0).getDateTime().getTimeInMillis();

        if(glicData.size()<=0){return;}

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        MyXAxisValueFormatter xForm = new MyXAxisValueFormatter(firstTime);
        xAxis.setValueFormatter(xForm);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        LinkedList<Entry> entries = new LinkedList<>();

        for(GlycemiaRec rec: glicData){
            Calendar recC = rec.getDateTime();
            long timeStamp = recC.getTimeInMillis();
            long newTime = timeStamp-firstTime;
            entries.add(new BarEntry(newTime, rec.getValue()));

        }

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
        lineData.setValueFormatter(new MyValueFormatter());
        chart.setData(lineData);
        chart.invalidate();
    }

    public static Bitmap drawToBitmap(Context context, int width, int height, LinkedList<GlycemiaRec> glicData)
    {
        Bitmap drawing = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(drawing);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout;
        if(glicData!=null){
            layout = inflater.inflate(R.layout.chart_widget,null);
            LineChart chart = layout.findViewById(R.id.chart);
            setChart(context, chart, glicData);
        }else{
            layout = inflater.inflate(R.layout.chart_widget_error,null);
        }

        layout.setDrawingCacheEnabled(true);
        layout.measure(
                View.MeasureSpec.makeMeasureSpec(canvas.getWidth(),View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(canvas.getHeight(),View.MeasureSpec.EXACTLY));
        layout.layout(0,0,width,height);

        layout.draw(canvas);

        if(drawing != null){
            canvas.drawBitmap(drawing,0,0,new Paint());
        }
        return drawing;
    }
}



