package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.io.File;

public class Backup extends Thing {
    private int id;
    private String name;
    private ExamNames examNames;
    private Bookmarks bookmarks;
    private Exams exams;
    private String folderName;
    private int indexOfFolder;
    private double fileSize;
    private boolean restoring;
    private boolean creating;
    private File backupFile;

    public Backup() {
    }

    public Backup(String name) {
        this.name = name;
    }

    public File getBackupFile() {
        return backupFile;
    }

    public void setBackupFile(File backupFile) {
        this.backupFile = backupFile;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getIndexOfFolder() {
        return indexOfFolder;
    }

    public void setIndexOfFolder(int indexOfFolder) {
        this.indexOfFolder = indexOfFolder;
    }

    public boolean isCreating() {
        return creating;
    }

    public void setCreating(boolean creating) {
        this.creating = creating;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public Bookmarks getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Bookmarks bookmarks) {
        this.bookmarks = bookmarks;
    }

    public ExamNames getExamNames() {
        return examNames;
    }

    public void setExamNames(ExamNames examNames) {
        this.examNames = examNames;
    }

    public Exams getExams() {
        return exams;
    }

    public void setExams(Exams exams) {
        this.exams = exams;
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

    public boolean isRestoring() {
        return restoring;
    }

    public void setRestoring(boolean restoring) {
        this.restoring = restoring;
    }

    @NonNull
    @Override
    public String toString() {
        return "Backup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", restored=" + restoring +
                '}';
    }
}
