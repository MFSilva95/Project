package pt.it.porto.mydiabetes.ui.dataBinding;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class ExerciseRegDataBinding extends DateTimeDataBinding {

	private int id;
	private String exercise;
	private int duration;
	private String effort;
	private int id_User;
	private int idNote;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExercise() {
		return exercise;
	}

	public void setExercise(String exercise) {
		this.exercise = exercise;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getEffort() {
		return effort;
	}

	public void setEffort(String effort) {
		this.effort = effort;
	}

	public int getId_User() {
		return id_User;
	}

	public void setId_User(int id_User) {
		this.id_User = id_User;
	}

	public int getIdNote() {
		return idNote;
	}

	public void setIdNote(int idNote) {
		this.idNote = idNote;
	}


}
