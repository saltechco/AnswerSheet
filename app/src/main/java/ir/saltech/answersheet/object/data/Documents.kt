package ir.saltech.answersheet.object.data;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Documents extends Things {
	public static final String DOCUMENTS_FOLDER = "/SalTech/Answer Sheet/Documents";

	public List<Thing> getThings() {
		return super.convertToThings(getDocumentList());
	}

	@NonNull
	public static List<Document> getDocumentList() {
		List<Document> documentList = new ArrayList<>();
		File directory = getDocumentsFolder();
		if (directory != null) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (Document.checkFileHasValidFormat(file.getName())) {
						Document document = new Document();
						document.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
						document.setDocumentFile(file);
						document.setFileSize(file.length());
						documentList.add(document);
					}
				}
				for (int j = 0; j < documentList.size(); j++) {
					for (int k = 0; k < documentList.size(); k++) {
						if (documentList.get(j).getName().equals(documentList.get(k).getName()) && j != k) {
							if (documentList.get(k).getDocumentFile().delete()) {
								if (documentList.get(k).getFolderName() != null)
									Log.i("TAG", "File " + documentList.get(k).getDocumentFile().getName() + " has been deleted because it was duplicated!");
								documentList.remove(k);
							}
							break;
						}
					}
				}
				return documentList;
			} else {
				return new ArrayList<>();
			}
		} else {
			Log.e("TAG", "ERROR Documents Directory IS EMPTY!");
			return new ArrayList<>();
		}
	}

	@Nullable
	public static File getDocumentsFolder() {
		File dir = new File(Environment.getExternalStorageDirectory().getPath() + DOCUMENTS_FOLDER);
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
}
