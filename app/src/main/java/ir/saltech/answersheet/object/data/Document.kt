package ir.saltech.answersheet.`object`.data

import java.io.File

class Document : Thing {
    var name: String? = null
    var folderName: String? = null
    var documentFile: File? = null
    var indexOfFolder: Int = 0
    var fileSize: Double = 0.0
    var isNightMode: Boolean = false

    constructor()

    constructor(name: String) {
        this.name = name
    }

    companion object {
        val SUPPORTED_DOC_FORMATS: Array<String> = arrayOf("pdf")
        fun checkFileHasValidFormat(fileName: String): Boolean {
            var hasValidFormat = false
            for (format in SUPPORTED_DOC_FORMATS) {
                if (fileName.endsWith(format)) {
                    hasValidFormat = true
                    break
                }
            }
            return hasValidFormat
        }
    }
}
