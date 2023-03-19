package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Product {
	private int id;
	private String name;
	@SerializedName("version_code")
	private long versionCode;
	@SerializedName("version_name")
	private String versionName;
	@SerializedName("package_name")
	private String packageName;
	private String platform;
	private String description;
	private String changes;
	@SerializedName("file_size")
	private long fileSize;
	@SerializedName("release_date")
	private String releaseDate;
	private boolean released;
	private boolean active;
	private boolean beta;

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

	public long getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(long versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@NonNull
	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(@NonNull String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public boolean isReleased() {
		return released;
	}

	public void setReleased(boolean released) {
		this.released = released;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isBeta() {
		return beta;
	}

	public void setBeta(boolean beta) {
		this.beta = beta;
	}

	public boolean isUpdateAvailable(int versionCode) {
		return this.versionCode > versionCode && !beta;
	}

	public boolean isBetaUpdateAvailable(int versionCode) {
		return this.versionCode > versionCode && beta && String.valueOf(versionCode).length() == 10;
	}

	@NonNull
	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", versionCode=" + versionCode +
				", versionName='" + versionName + '\'' +
				", packageName='" + packageName + '\'' +
				", platform='" + platform + '\'' +
				", description='" + description + '\'' +
				", changes='" + changes + '\'' +
				", fileSize=" + fileSize +
				", releaseDate='" + releaseDate + '\'' +
				", released=" + released +
				", active=" + active +
				", beta=" + beta +
				'}';
	}
}
