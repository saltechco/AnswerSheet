package ir.saltech.answersheet.`object`.data

import android.os.Environment
import android.util.Log
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import java.io.File
import java.util.Objects

class Backups : Things() {
    val things: List<Thing?>?
        get() = super.convertToThings(backupList)

    companion object {
        const val ZIP_PASSWORD_CONSTANT: Int = 14001021
        const val BACKUPS_FOLDER: String = "/SalTech/Answer Sheet/Backups"
        const val TEMPORARY_FOLDER: String = ".~temp"

        val backupList: List<Backup>
            get() {
                val backupList: MutableList<Backup> = ArrayList()
                val directory = backupsFolder
                if (directory != null) {
                    val files = directory.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray().size == 2) {
                                if (file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()[1] == "zip") {
                                    val backup = Backup()
                                    backup.name = getBackupName(file.name)
                                    backup.id = file.name.split("\\.".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()[0].split("_".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()[2].substring(4).toInt()
                                    backup.isCreating = false
                                    backup.backupFile = file
                                    backup.fileSize = file.length().toDouble()
                                    backupList.add(backup)
                                }
                            }
                        }
                        for (j in backupList.indices) {
                            for (k in backupList.indices) {
                                if (backupList[j].id == backupList[k].id && j != k) {
                                    if (backupList[k].backupFile!!.delete()) {
                                        if (backupList[k].folderName != null) Log.i(
                                            "TAG",
                                            "File " + backupList[k].backupFile?.name + " has been deleted because it was duplicated!"
                                        )
                                        backupList.removeAt(k)
                                    }
                                    break
                                }
                            }
                        }
                        return backupList
                    } else {
                        return ArrayList()
                    }
                } else {
                    Log.e("TAG", "ERROR Backups Directory IS EMPTY!")
                    return ArrayList()
                }
            }

        val backupsFolder: File?
            get() {
                val dir: File =
                    File(Environment.getExternalStorageDirectory().path + BACKUPS_FOLDER)
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

        private fun getBackupName(fileName: String): String {
            val fileNameParts = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].split("_".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val date = fileNameParts[1]
            val time = fileNameParts[2].substring(0, 4)
            return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(
                6,
                8
            ) + " " + time.substring(0, 2) + ":" + time.substring(2, 4)
        }

        fun extractZipFile(zipFile: File) {
            try {
                val destPath = backupsFolder!!.path + "/" + TEMPORARY_FOLDER
                val dest = File(destPath)
                if (dest.exists()) {
                    if (dest.listFiles() != null) {
                        for (f in Objects.requireNonNull(dest.listFiles())) {
                            if (f.delete()) {
                                Log.i("TAG", "File " + f.name + " in temporary has been removed!")
                            }
                        }
                    }
                    if (dest.delete()) {
                        val file = ZipFile(
                            zipFile,
                            (zipFile.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()[0].split("_".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[2].substring(4)
                                .toInt() * ZIP_PASSWORD_CONSTANT).toString().toCharArray())
                        file.extractAll(destPath)
                    }
                } else {
                    val file = ZipFile(
                        zipFile,
                        (zipFile.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0].split("_".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[2].substring(4)
                            .toInt() * ZIP_PASSWORD_CONSTANT).toString().toCharArray())
                    file.extractAll(destPath)
                }
            } catch (e: ZipException) {
                e.printStackTrace()
            }
        }
    }
}
