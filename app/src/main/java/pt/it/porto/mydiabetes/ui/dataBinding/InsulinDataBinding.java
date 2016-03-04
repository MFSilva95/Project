package pt.it.porto.mydiabetes.ui.dataBinding;


public class InsulinDataBinding {

	private int id;
	private String name;
	private String type;
	private String action;
	private double duration;

	public InsulinDataBinding() {
	}

	public InsulinDataBinding(InsulinDataBinding oldInsulin) {
		if (oldInsulin == null) {
			return;
		}
		id = oldInsulin.getId();
		name = oldInsulin.getName();
		type = oldInsulin.getType();
		action = oldInsulin.getAction();
		duration = oldInsulin.getDuration();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}
}
