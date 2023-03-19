package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.File;

@Deprecated
public class Feedback {
    public static final String MODE_PROBLEM = "problem";
    public static final String MODE_SUGGESTION = "suggestion";

    private long id;
    private String subject;
    private String description;
    private String email;
    private String date;
    private File screenshot;
    @SerializedName("mode")
    private String mode = MODE_PROBLEM;
    @SerializedName("target_id")
    private int targetId;
    @SerializedName("target_ver")
    private int targetVersion;
    private String os;
    private String device;
    private String solution;
    private boolean solved;

    public File getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(File screenshot) {
        this.screenshot = screenshot;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(int targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @NonNull
    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", mode='" + mode + '\'' +
                ", targetId=" + targetId +
                ", targetVersion=" + targetVersion +
                ", os='" + os + '\'' +
                ", device='" + device + '\'' +
                ", solution='" + solution + '\'' +
                ", solved=" + solved +
                '}';
    }
}
