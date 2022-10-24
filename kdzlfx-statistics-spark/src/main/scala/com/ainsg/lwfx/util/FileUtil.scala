package com.ainsg.lwfx.util
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.common.Spark.spark
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import java.io.File
/**
 * 工具类
 */
object FileUtil {
  val LOG: Logger = LoggerFactory.getLogger(FileUtil.getClass)
  
  /**
   * 根据路径取出目标路径下所有的子目录名
   *
   * @param dirPath 路径
   * @return
   */
  def getDirNameFromPath(dirPath: String): ArrayBuffer[String] = {
    var result = new ArrayBuffer[String]()
    var dirFile = new File(dirPath)
    if(dirFile.exists()) {
      var listFiles:Array[File]=dirFile.listFiles()
      var isDirectoryList=listFiles.filter(x=>x.isDirectory)
      isDirectoryList.foreach(file => {
      var fileName = file.getName()
      result.append(fileName)
    })
    }
    result
  }
  
  /**
   * 删除目录下所有文件、目录
   */
  def deleteDirChildFile(path: String): Unit = {
    var file = new File(path)
    if(file.exists() && file.isDirectory()) {
      val files: Array[File] = file.listFiles()
      files.foreach(f => {
        deleteDir(f)
      })
    }
  }
  
   /**
   * 删除目录及文件
   */
  def deleteDir(path: String): Unit = {
    var file = new File(path)
    deleteDir(file)
  }
  
 
  def deleteDir(path: File): Unit = {
    if (!path.exists())  
      return  
    else if (path.isFile()) {  
      path.delete()  
      LOG.info(path + ":  文件被删除")  
      return  
    }  
  
    val file: Array[File] = path.listFiles()  
    for (d <- file) {
      deleteDir(d)  
    }  
    path.delete()  
    LOG.info(path + ":  目录被删除")  
  }
  
  /**
   * 判断目录、文件是否存在
   */
  def isExist(path: String): Boolean = {
    var file = new File(path)
    file.exists()
  }
  
  /**
   * 移动 source 目录中 文件名后缀符合 fileSuffix 的文件到 target 目录
   * @param source 源目录
   * @param target 目标目录
   * @param fileSuffix 文件名后缀
   */
  def moveFile(source: String, target: String, fileSuffix: String): Unit = {
    LOG.info("move " + source + " to " + target)
    var sourceDirFile = new File(source)
    if(! sourceDirFile.exists()) {
      LOG.info("dir path " + source + " is not exist.")
      return
    }
    var targetDirFile = new File(target)
    if(!targetDirFile.exists()) {
      targetDirFile.mkdirs()
    }
    var isFileList = sourceDirFile.listFiles().filter(file => file.isFile())
    isFileList.foreach(file => {
      val fileName = file.getName()
      if(fileName.endsWith(fileSuffix)) {
        var renameFile = new File(target+"/"+file.getName())
        LOG.info("move " + source + "/" + fileName + " to " + target + "/" +fileName)
        file.renameTo(renameFile)
      }
    })
  }
  
}