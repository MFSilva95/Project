package pt.it.porto.mydiabetes.ui.usability;

public class Event {

	private final String category;
	private final String action;

	public Event(Builder builder) {
		this.category = builder.category;
		this.action = builder.action;
	}

	public String getCategory() {
		return category;
	}

	public String getAction() {
		return action;
	}

	class Builder {
		private String category;

		public Builder setCategory(String category) {
			this.category = category;
			return this;
		}

		private String action;

		public Builder setAction(String action) {
			this.action = action;
			return this;
		}

		public Event build() {
			return new Event(this);
		}

	}
}
