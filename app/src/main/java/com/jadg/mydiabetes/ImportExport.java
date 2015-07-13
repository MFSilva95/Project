package com.jadg.mydiabetes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.view.Menu;
import android.view.MenuItem;

import com.jadg.mydiabetes.database.BloodPressureDataBinding;
import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.CholesterolDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DiseaseRegDataBinding;
import com.jadg.mydiabetes.database.ExerciseRegDataBinding;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.HbA1cDataBinding;
import com.jadg.mydiabetes.database.InsulinRegDataBinding;
import com.jadg.mydiabetes.database.WeightDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.fragments.DB_BackupRestore;
import com.jadg.mydiabetes.fragments.PdfExport;
import com.jadg.mydiabetes.fragments.Sync;
import com.jadg.mydiabetes.sync.crypt.KeyGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;




public class ImportExport extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_export);
		// Show the Up button in the action bar.
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tab = getActionBar().newTab();
		Fragment syncFragment = new Sync();
		tab.setTabListener(new MyTabsListener(syncFragment));
		tab.setText("Sincronização");
		getActionBar().addTab(tab);
		
		
		tab = getActionBar().newTab();
		Fragment bacuprestoreFragment = new DB_BackupRestore();
		tab.setTabListener(new MyTabsListener(bacuprestoreFragment));
		tab.setText("Cópia de Segurança");
		getActionBar().addTab(tab);

		 tab = getActionBar().newTab();
		Fragment impexpFragment = new PdfExport();
		tab.setTabListener(new MyTabsListener(impexpFragment));
		tab.setText("Relatório");
		getActionBar().addTab(tab);
	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.import_export, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void createPDF(View v) throws Exception {
		// Toast.makeText(this, "cenas", Toast.LENGTH_LONG).show();

		Document document = new Document();
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/MyDiabetes/Export");
		dir.mkdirs();
		final Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateString = formatter.format(d);
		File file = new File(dir, dateString + ".pdf");
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();

		addMetaData(document);
		addTitlePage(document);
		addUserInfo(document);

		CheckBox cb = (CheckBox) findViewById(R.id.cb_pdfexport_glycemia);
		if (cb.isChecked()) {
			addGlycemia(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_insulin);
		if (cb.isChecked()) {
			addInsulin(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_carbs);
		if (cb.isChecked()) {
			addCarbs(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_exercise);
		if (cb.isChecked()) {
			addExercise(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_bloodpressure);
		if (cb.isChecked()) {
			addBPressure(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_diseases);
		if (cb.isChecked()) {
			addDisease(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_cholesterol);
		if (cb.isChecked()) {
			addCholesterol(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_weight);
		if (cb.isChecked()) {
			addWeight(document);
		}
		cb = (CheckBox) findViewById(R.id.cb_pdfexport_hba1c);
		if (cb.isChecked()) {
			addHbA1c(document);
		}

		document.close();

		cb = (CheckBox) findViewById(R.id.cb_pdfexport_email);
		if (cb.isChecked()) {
			EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
			EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
			Intent emailIntent = new Intent("android.intent.action.SEND");
			String sub = "Relatório diabetes - "
					+ datefrom.getText().toString() + " a "
					+ dateto.getText().toString() + ".";
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
			emailIntent.setType("application/pdf");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			startActivity(Intent.createChooser(emailIntent, "Enviar usando:"));
		}

		ShowMsgExport("Dados exportados com sucesso para: " + file.getPath());
	}

	private static void addMetaData(Document document) {
		document.addTitle("A minha Diabetes - Exportação");
		document.addAuthor("A minha Diabetes");
		document.addCreator("A minha Diabetes");
	}

	private void addTitlePage(Document document) throws DocumentException {
		Paragraph title = new Paragraph();
		Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
		title.add(new Paragraph("Relatório - A Minha Diabetes", titleFont));
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		title.add(new Paragraph("De " + datefrom.getText().toString() + " a "
				+ dateto.getText().toString()));
		addEmptyLine(title, 1);
		document.add(title);
	}

	private void addUserInfo(Document document) throws DocumentException,
			IOException {

		Paragraph header = new Paragraph();

		Font headerFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
		// Get user
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		String name = obj[1].toString();
		rdb.close();
		Log.d("nome", name);
		header.add(new Paragraph("Nome: " + obj[1].toString(), headerFont));
		header.add(new Paragraph("Diabetes: " + obj[2].toString(), headerFont));
		header.add(new Paragraph("Rácio Insulina: " + obj[3].toString(),
				headerFont));
		header.add(new Paragraph("Rácio H. Carbono: " + obj[4].toString(),
				headerFont));
		LineSeparator ls = new LineSeparator();
		header.add(ls);
		addEmptyLine(header, 1);
		document.add(header);
	}

	private void addGlycemia(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de glicemia");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);

		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);
		Paragraph glycemia = new Paragraph();

		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<GlycemiaDataBinding> reads = rdb.Glycemia_GetByDate(datefrom
				.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(5);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Valor (mg/dl)", tf));
		t.addCell(new Phrase("Fase do dia", tf));
		t.addCell(new Phrase("Notas", tf));

		for (GlycemiaDataBinding g : reads) {
			t.addCell(new Phrase(g.getDate(), cf));
			t.addCell(new Phrase(g.getTime(), cf));
			t.addCell(new Phrase(String.valueOf(g.getValue()), cf));
			t.addCell(new Phrase(rdb.Tag_GetById(g.getIdTag()).getName(), cf));
			t.addCell(new Phrase((g.getIdNote() > 0) ? rdb.Note_GetById(
					g.getIdNote()).getNote() : "", cf));
		}

		rdb.close();
		addEmptyLine(glycemia, 1);
		glycemia.add(t);
		addEmptyLine(glycemia, 1);
		document.add(glycemia);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addInsulin(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de insulina");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph insulin = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<InsulinRegDataBinding> reads = rdb.InsulinReg_GetByDate(
				datefrom.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(8);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Glicemia (mg/dl)", tf));
		t.addCell(new Phrase("Objectivo (mg/dl)", tf));
		t.addCell(new Phrase("Nome Insulina", tf));
		t.addCell(new Phrase("Insulina (unidades)", tf));
		t.addCell(new Phrase("Fase do dia", tf));
		t.addCell(new Phrase("Notas", tf));

		for (InsulinRegDataBinding i : reads) {
			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(
					String.valueOf((i.getIdBloodGlucose() > 0) ? rdb
							.Glycemia_GetById(i.getIdBloodGlucose()).getValue()
							: ""), cf));
			t.addCell(new Phrase(String.valueOf(i.getTargetGlycemia()), cf));
			t.addCell(new Phrase(rdb.Insulin_GetById(i.getIdInsulin())
					.getName(), cf));
			t.addCell(new Phrase(String.valueOf(i.getInsulinUnits()), cf));
			t.addCell(new Phrase(rdb.Tag_GetById(i.getIdTag()).getName(), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(insulin, 1);
		insulin.add(t);
		addEmptyLine(insulin, 1);
		document.add(insulin);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addExercise(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de exercicio");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph exercise = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<ExerciseRegDataBinding> reads = rdb.ExerciseReg_GetByDate(
				datefrom.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(6);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Exercicio", tf));
		t.addCell(new Phrase("Duração (min)", tf));
		t.addCell(new Phrase("Esforço", tf));
		t.addCell(new Phrase("Notas", tf));

		for (ExerciseRegDataBinding i : reads) {

			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(i.getExercise(), cf));
			t.addCell(new Phrase(String.valueOf(i.getDuration()), cf));
			t.addCell(new Phrase(i.getEffort(), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(exercise, 1);
		exercise.add(t);
		addEmptyLine(exercise, 1);
		document.add(exercise);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addCarbs(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de Hidratos de Carbono");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph carb = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<CarbsDataBinding> reads = rdb.CarboHydrate_GetBtDate(datefrom
				.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(6);

		// Headers
		t.addCell(new Phrase("Foto", tf));
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("H. Carbono (mg)", tf));
		t.addCell(new Phrase("Fase do dia", tf));
		t.addCell(new Phrase("Notas", tf));

		for (CarbsDataBinding i : reads) {
			t.addCell(new Phrase(i.getPhotoPath(), cf));
			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(i.getCarbsValue().toString(), cf));
			t.addCell(new Phrase(rdb.Tag_GetById(i.getId_Tag()).getName(), cf));
			t.addCell((i.getId_Note() > 0) ? new Phrase(rdb.Note_GetById(
					i.getId_Note()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(carb, 1);
		carb.add(t);
		addEmptyLine(carb, 1);
		document.add(carb);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addBPressure(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de Pressão Arterial");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph bpressure = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<BloodPressureDataBinding> reads = rdb
				.BloodPressure_GetBtDate(datefrom.getText().toString(), dateto
						.getText().toString());
		PdfPTable t = new PdfPTable(6);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Sistólica", tf));
		t.addCell(new Phrase("Diastólica", tf));
		t.addCell(new Phrase("Fase do dia", tf));
		t.addCell(new Phrase("Notas", tf));

		for (BloodPressureDataBinding i : reads) {
			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(String.valueOf(i.getSystolic()), cf));
			t.addCell(new Phrase(String.valueOf(i.getDiastolic()), cf));
			t.addCell(new Phrase(rdb.Tag_GetById(i.getIdTag()).getName(), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(bpressure, 1);
		bpressure.add(t);
		addEmptyLine(bpressure, 1);
		document.add(bpressure);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addDisease(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de Doenças");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph disease = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<DiseaseRegDataBinding> reads = rdb.DiseaseReg_GetByDate(
				datefrom.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(4);

		// Headers
		t.addCell(new Phrase("Inicio", tf));
		t.addCell(new Phrase("Fim", tf));
		t.addCell(new Phrase("Doença", tf));
		t.addCell(new Phrase("Notas", tf));

		for (DiseaseRegDataBinding i : reads) {

			t.addCell(new Phrase(i.getStartDate(), cf));
			t.addCell(new Phrase(i.getEndDate(), cf));
			t.addCell(new Phrase(i.getDisease(), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(disease, 1);
		disease.add(t);
		addEmptyLine(disease, 1);
		document.add(disease);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addCholesterol(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de Colesterol");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph cholesterol = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<CholesterolDataBinding> reads = rdb.Cholesterol_GetBtDate(
				datefrom.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(4);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Valor (mg/dl)", tf));
		t.addCell(new Phrase("Notas", tf));

		for (CholesterolDataBinding i : reads) {

			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(String.valueOf(i.getValue()), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(cholesterol, 1);
		cholesterol.add(t);
		addEmptyLine(cholesterol, 1);
		document.add(cholesterol);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addWeight(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de Peso");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph weight = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<WeightDataBinding> reads = rdb.Weight_GetBtDate(datefrom
				.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(4);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Valor (Kg)", tf));
		t.addCell(new Phrase("Notas", tf));

		for (WeightDataBinding i : reads) {

			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(String.valueOf(i.getValue()), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(weight, 1);
		weight.add(t);
		addEmptyLine(weight, 1);
		document.add(weight);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private void addHbA1c(Document document) throws DocumentException,
			IOException {
		Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
		Paragraph title = new Paragraph();
		addEmptyLine(title, 1);
		title.setFont(hFont);
		title.add("Leituras de HbA1c");
		document.add(title);

		Font cf = new Font(Font.HELVETICA, 8, Font.NORMAL);
		Font tf = new Font(Font.HELVETICA, 10, Font.NORMAL);

		Paragraph hba1c = new Paragraph();
		EditText datefrom = (EditText) findViewById(R.id.et_pdfexport_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_pdfexport_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<HbA1cDataBinding> reads = rdb.HbA1c_GetBtDate(datefrom
				.getText().toString(), dateto.getText().toString());
		PdfPTable t = new PdfPTable(4);

		// Headers
		t.addCell(new Phrase("Data", tf));
		t.addCell(new Phrase("Hora", tf));
		t.addCell(new Phrase("Valor (%)", tf));
		t.addCell(new Phrase("Notas", tf));

		for (HbA1cDataBinding i : reads) {

			t.addCell(new Phrase(i.getDate(), cf));
			t.addCell(new Phrase(i.getTime(), cf));
			t.addCell(new Phrase(String.valueOf(i.getValue()), cf));
			t.addCell((i.getIdNote() > 0) ? new Phrase(rdb.Note_GetById(
					i.getIdNote()).getNote(), cf) : new Phrase("", cf));
		}

		rdb.close();
		addEmptyLine(hba1c, 1);
		hba1c.add(t);
		addEmptyLine(hba1c, 1);
		document.add(hba1c);
		LineSeparator ls = new LineSeparator();
		document.add(ls);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	public void ShowMsgExport(String msg) {
		// final Context c = this;
		new AlertDialog.Builder(this).setTitle("Informação").setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						// Rever porque não elimina o registo de glicemia

					}
				}).show();
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox", R.id.et_pdfexport_DataFrom);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox", R.id.et_pdfexport_DataTo);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void backup(View v) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getDataDirectory() + "/data/"
					+ this.getPackageName() + "/databases/DB_Diabetes");

			File outputDir = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					ShowDialogMsg("Cópia de segurançaa efectuada com sucesso!");
				} catch (IOException ioException) {
					ShowDialogMsg("Ocurreu um erro durante a cópia de segurançaa, verifique se a memória externa está disponivel!");
				} catch (Exception exception) {
					ShowDialogMsg("Ocurreu um erro durante a cópia de segurançaa, verifique se a memória externa está disponivel!");
				}
			}
		}
	}

	public void restore(View v) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup/DB_Diabetes");

			File outputDir = new File(Environment.getDataDirectory() + "/data/"
					+ this.getPackageName() + "/databases");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					ShowDialogMsg("Restauro efectuado com sucesso!");
				} catch (IOException ioException) {
					ShowDialogMsg("Ocurreu um erro durante o restauro, verifique se a memória externa está disponivel!");
				} catch (Exception exception) {
					ShowDialogMsg("Ocurreu um erro durante o restauro, verifique se a memória externa está disponivel!");
				}
			}
		}
	}

	private boolean isSDWriteable() {
		boolean rc = false;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			rc = true;
		}

		return rc;
	}

	@SuppressWarnings("resource")
	private static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public void ShowDialogMsg(String msg) {
		// final Context c = this;
		new AlertDialog.Builder(this).setTitle("Informação").setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						// Rever porque não elimina o registo de glicemia
						fillBackup();
					}
				}).show();
	}

	@SuppressLint("SimpleDateFormat")
	public boolean fillBackup() {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(inputFile.lastModified());
				Date newDate = cal.getTime();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String dateString = formatter.format(newDate);

				TextView lastbackup = (TextView) findViewById(R.id.tv_lastBackup);
				lastbackup.setText(dateString);
				Button restore = (Button) findViewById(R.id.bt_Restore);
				restore.setEnabled(true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void syncPC(View view) {
		Intent intent = new Intent(this, com.jadg.mydiabetes.ScanActivity.class);
		startActivity(intent);
	}

	public void syncCloud(View view) {
		Intent intent = new Intent(this, com.jadg.mydiabetes.TransferActivity.class);
		intent.putExtra("host", "192.168.1.44");
        byte[] key = new KeyGenerator().generateKey();
        byte[] iv = new KeyGenerator().generateKey();
        intent.putExtra("key", key);
        intent.putExtra("iv",iv);
        intent.putExtra("onPC", false);
		startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

				@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.replace(R.id.importexport_FragmentContainer, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

	}
}