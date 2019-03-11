package com.wei.util

import android.content.ContentValues.TAG
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream

object FileUtil {

    val tag: String?
        get() = this::class.simpleName

    /**
     * 删除文件夹下所有内容包括文件夹本身
     *
     * @param path
     */
    fun delete(path: String): Boolean {
        return delete(File(path))
    }

    /**
     * 删除指定File，支持目录和文件
     *
     * @param file
     */
    fun delete(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return false
        }

        if (file.isFile) {
            return file.delete()
        } else {
            file.listFiles().forEach {
                // 递归删除每一个文件
                delete(it)
            }

            return file.delete()// 删除该文件夹
        }
    }

    /**
     * 保留空文件夹, 只删除文件夹下的内容
     *
     * @param folderPath
     */
//    fun delOnlyFolderContained(folderPath: String): Boolean {
//        try {
//            return delAllFiles(folderPath) // 删除完里面所有内容
//        } catch (e: Exception) {
//            L.e(TAG, e)
//        }
//
//        return false
//    }

    /**
     * 根据byte数组，生成文件
     */
    fun save(file: File, bytes: ByteArray): Boolean {
        // TODO: 需要验证
        return FileOutputStream(file).use {
            ensureFileExist(file.absolutePath)
            it.write(bytes)
            true
        }
    }

    fun save(file: File, text: String): Boolean = save(file, text.toByteArray())

    fun save(filePath: String, fileName: String, bytes: ByteArray): Boolean = save(File(filePath, fileName), bytes)

    fun ensureFileExist(path: String): Boolean = ensureFileExist(File(path))

    fun ensureFileExist(file: File): Boolean = if (!file.exists()) file.mkdirs() else true

    fun copy(pathFrom: String, pathTo: String): Boolean {
        return try {
            val streamFrom = FileInputStream(pathFrom)
            val streamTo = FileOutputStream(pathTo)
            streamFrom.copyTo(streamTo)
            streamFrom.close()
            streamTo.close()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun write(any: Any, f: File) {
        return ObjectOutputStream(FileOutputStream(f)).use { it.writeObject(any) }
    }

    /**
     * 读取文件内容
     *
     * @param path 完整的文件路径, 包括Sdcard路径
     * @return 文本txt
     * @throws Exception
     */
    @Throws(Exception::class)
    fun read(path: String): String = read(File(path))

    @Throws(Exception::class)
    fun read(file: File): String {
        val stream = FileInputStream(file)
        val data = ConvertUtil.toString(stream)
        stream.close()
        return data
    }

    /**
     * 获取文件夹大小
     *
     * @param file
     * @return 文件大小(字节)
     * @throws Exception
     */
    fun getSize(file: File): Long {
        var size: Long = 0
        file.listFiles().forEach {
            size += if (it.isDirectory) getSize(it) else it.length()
        }
        return size
    }
}