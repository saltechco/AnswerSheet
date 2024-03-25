package ir.saltech.answersheet.`object`.data

import java.io.File

class Backup : Thing {
    var id: Int = 0
    var name: String? = null
    var examNames: ExamNames? = null
    var bookmarks: Bookmarks? = null
    var exams: Exams? = null
    var folderName: String? = null
    var indexOfFolder: Int = 0
    var fileSize: Double = 0.0
    var isRestoring: Boolean = false
    var isCreating: Boolean = false
    var backupFile: File? = null

    constructor()

    constructor(name: String?) {
        this.name = name
    }

    override fun toString(): String {
        return "Backup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", restored=" + isRestoring +
                '}'
    }
}
