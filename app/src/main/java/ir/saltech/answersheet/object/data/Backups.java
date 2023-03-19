package ir.saltech.answersheet.object.data;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Backups extends Things {
	public static final int ZIP_PASSWORD_CONSTANT = 14001021;
	public static final String BACKUPS_FOLDER = "/SalTech/Answer Sheet/Backups";
	public static final String TEMPORARY_FOLDER = ".~temp";

	public List<Thing> getThings() {
		return super.convertToThings(getBackupList());
	}

	@NonNull
	public static List<Backup> getBackupList() {
		List<Backup> backupList = new ArrayList<>();
		File directory = getBackupsFolder();
		if (directory != null) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().split("\\.").length == 2) {
						if (file.getName().split("\\.")[1].equals("zip")) {
							Backup backup = new Backup();
							backup.setName(getBackupName(file.getName()));
							backup.setId(Integer.parseInt(file.getName().split("\\.")[0].split("_")[2].substring(4)));
							backup.setCreating(false);
							backup.setBackupFile(file);
							backup.setFileSize(file.length());
							backupList.add(backup);
						}
					}
				}
				for (int j = 0; j < backupList.size(); j++) {
					for (int k = 0; k < backupList.size(); k++) {
						if (backupList.get(j).getId() == backupList.get(k).getId() && j != k) {
							if (backupList.get(k).getBackupFile().delete()) {
								if (backupList.get(k).getFolderName() != null)
									Log.i("TAG", "File " + backupList.get(k).getBackupFile().getName() + " has been deleted because it was duplicated!");
								backupList.remove(k);
							}
							break;
						}
					}
				}
				return backupList;
			} else {
				return new ArrayList<>();
			}
		} else {
			Log.e("TAG", "ERROR Backups Directory IS EMPTY!");
			return new ArrayList<>();
		}
	}

	@Nullable
	public static File getBackupsFolder() {
		File dir = new File(Environment.getExternalStorageDirectory().getPath() + BACKUPS_FOLDER);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				return dir;
			} else {
				return null;
			}
		} else {
			return dir;
		}
	}

	private static String getBackupName(String fileName) {
		String[] fileNameParts = fileName.split("\\.")[0].split("_");
		String date = fileNameParts[1];
		String time = fileNameParts[2].substring(0, 4);
		return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8) + " " + time.substring(0, 2) + ":" + time.substring(2, 4);
	}

	public static void extractZipFile(@NonNull File zipFile) {
		try {
			String destPath = Objects.requireNonNull(getBackupsFolder()).getPath() + "/" + TEMPORARY_FOLDER;
			File dest = new File(destPath);
			if (dest.exists()) {
				if (dest.listFiles() != null) {
					for (File f :
							Objects.requireNonNull(dest.listFiles())) {
						if (f.delete()) {
							Log.i("TAG", "File " + f.getName() + " in temporary has been removed!");
						}
					}
				}
				if (dest.delete()) {
					ZipFile file = new ZipFile(zipFile, String.valueOf(Integer.parseInt(zipFile.getName().split("\\.")[0].split("_")[2].substring(4)) * ZIP_PASSWORD_CONSTANT).toCharArray());
					file.extractAll(destPath);
				}
			} else {
				ZipFile file = new ZipFile(zipFile, String.valueOf(Integer.parseInt(zipFile.getName().split("\\.")[0].split("_")[2].substring(4)) * ZIP_PASSWORD_CONSTANT).toCharArray());
				file.extractAll(destPath);
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}
}
