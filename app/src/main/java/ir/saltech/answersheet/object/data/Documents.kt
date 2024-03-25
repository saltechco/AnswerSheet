package ir.saltech.answersheet.`object`.data

import android.os.Environment
import android.util.Log
import java.io.File

class Documents : Things() {
    val things: List<Thing?>
        get() = super.convertToThings(documentList)

    companion object {
        const val DOCUMENTS_FOLDER: String = "/SalTech/Answer Sheet/Documents"

        val documentList: List<Document>
            get() {
                val documentList: MutableList<Document> = ArrayList()
                val directory = documentsFolder
                if (directory != null) {
                    val files = directory.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (Document.Companion.checkFileHasValidFormat(file.name)) {
                                val document = Document()
                                document.name = file.name.substring(0, file.name.lastIndexOf("."))
                                document.documentFile = file
                                document.fileSize = file.length().toDouble()
                                documentList.add(document)
                            }
                        }
                        for (j in documentList.indices) {
                            for (k in documentList.indices) {
                                if (documentList[j].name == documentList[k].name && j != k) {
                                    if (documentList[k].documentFile!!.delete()) {
                                        if (documentList[k].folderName != null) Log.i(
                                            "TAG",
                                            "File " + documentList[k].documentFile!!.name + " has been deleted because it was duplicated!"
                                        )
                                        documentList.removeAt(k)
                                    }
                                    break
                                }
                            }
                        }
                        return documentList
                    } else {
                        return ArrayList()
                    }
                } else {
                    Log.e("TAG", "ERROR Documents Directory IS EMPTY!")
                    return ArrayList()
                }
            }

        val documentsFolder: File?
            get() {
                val dir =
                    File(Environment.getExternalStorageDirectory().path + DOCUMENTS_FOLDER)
                return if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        dir
                    } else {
                        null
                    }
                } else {
                    dir
                }
            }
    }
}
