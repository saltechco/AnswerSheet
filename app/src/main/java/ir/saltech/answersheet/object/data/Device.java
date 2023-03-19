package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

public class Device {
	private int id;
	private String model;
	private String manufacturer;
	private boolean evoked;
	private boolean rejected;
	private boolean accepted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@NonNull
	public String getModel() {
		return model;
	}

	public void setModel(@NonNull String model) {
		this.model = model;
	}

	@NonNull
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(@NonNull String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public boolean isEvoked() {
		return evoked;
	}

	public void setEvoked(boolean evoked) {
		this.evoked = evoked;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@NonNull
	@Override
	public String toString() {
		return "Device{" +
				"id=" + id +
				", model='" + model + '\'' +
				", manufacturer='" + manufacturer + '\'' +
				", evoked=" + evoked +
				", rejected=" + rejected +
				", accepted=" + accepted +
				'}';
	}
}
