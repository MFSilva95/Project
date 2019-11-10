package pt.it.porto.mydiabetes.data;


public class Insulin {

	private int id;
	private String name;
	private String type;
	private String action;
	private double duration;

	public Insulin() {
	}

	public Insulin(int i,String n,String t,String a, double d) {
		this.id = i;
		this.name = n;
		this.type = t;
		this.action = a;
		this.duration = d;
	}

	public Insulin(Insulin oldInsulin) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Insulin)) return false;

		Insulin that = (Insulin) o;

		if (id != that.id) return false;
		if (Double.compare(that.duration, duration) != 0) return false;
		if (!name.equals(that.name)) return false;
		if (!type.equals(that.type)) return false;
		return action.equals(that.action);

	}

	@Override
	public String toString() {
		return "Insulin{" +
				"id=" + id +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", action='" + action + '\'' +
				", duration=" + duration +
				'}';
	}
}
