package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.io.File;

public class Document extends Thing {
    public static final String[] SUPPORTED_DOC_FORMATS = {"pdf"};
    private String name;
    private String folderName;
    private File documentFile;
    private int indexOfFolder;
    private double fileSize;
    private boolean nightMode;

    public Document() {
    }

    public Document(@NonNull String name) {
        this.name = name;
    }

    public static boolean checkFileHasValidFormat(@NonNull String fileName) {
        boolean hasValidFormat = false;
        for (String format :
                SUPPORTED_DOC_FORMATS) {
            if (fileName.endsWith(format)) {
                hasValidFormat = true;
                break;
            }
        }
        return hasValidFormat;
    }

    public boolean isNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(File documentFile) {
        this.documentFile = documentFile;
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

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }
}
