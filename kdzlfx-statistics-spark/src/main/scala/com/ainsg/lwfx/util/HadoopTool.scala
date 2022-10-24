package com.ainsg.lwfx.util

import java.net.URI

import scala.collection.mutable.ArrayBuffer

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by ShiGZ on 2016/8/10. */
object HadoopTool {
  val LOG: Logger = LoggerFactory.getLogger(HadoopTool.getClass)

  def initHadoopConfig(): Configuration = {
    val result = new Configuration()
    result.set(
      "dfs.client.failover.proxy.provider." + "ns",
      "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider")
    //防止 file System io closed
    result.setBoolean("fs.hdfs.impl.disable.cache", true)
    //在配置文件conf中指定所用的文件系统---HDFS
    result
  }

  /**
   * 根据路径取出目标路径下所有的file status
   *
   * @param dirPath 目录路径
   * @return
   */
  def getFileStatusesFromPath(dirPath: String): Array[FileStatus] = {
    val fileSystem = FileSystem.newInstance(URI.create(dirPath), initHadoopConfig())
    try {
      val filePath = new Path(dirPath)
      if (fileSystem.exists(filePath)) {
        fileSystem.listStatus(filePath)
      } else {
        new Array[FileStatus](0)
      }
    } finally {
      fileSystem.close()
    }

  }

  /**
   * 根据路径取出第一个文件的名称
   *
   * @param dirPath 目录路径
   * @return
   */
  def getFirstFileNameOptionFromPath(dirPath: String): Option[String] = {
    val radiusFileNameLB = getFileNameABFromPath(dirPath)
    if (0 < radiusFileNameLB.size) {
      Some(radiusFileNameLB.head)
    } else {
      None
    }
  }

  /**
   * 根据路径取出目标路径下所有的文件名
   *
   * @param dirPath 路径
   * @return
   */
  def getFileNameABFromPath(dirPath: String): ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    for (radiusLogStatus <- getFileStatusesFromPath(dirPath)) {
      val fileName = radiusLogStatus.getPath.getName
      if (!fileName.endsWith(".TMP") && fileName.length() > 0) {
        result.append(fileName)
      }
    }
    result
  }

  /**
   * 根据路径取出目标路径下符合后缀的文件路径
   *
   * @param dirPath 路径
   * @param fileSuffix 文件名后缀
   * @return
   */
  def getFilePathFromPathByFileSuffix(dirPath: String, fileSuffix: String): ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    for (radiusLogStatus <- getFileStatusesFromPath(dirPath)) {
      val fileName = radiusLogStatus.getPath.getName
      if (fileName.endsWith(fileSuffix) && fileName.length() > 0) {
        result.append(radiusLogStatus.getPath.toString())
      }
    }
    result
  }

  /**
   * 在指定目录下查找目标文件,并删除
   *
   * @param dirPath  目录路径
   * @param fileName 文件名
   * @return
   */
  def deleteFile(dirPath: String, fileName: String): Unit = {
    val fileSystem = FileSystem.newInstance(URI.create(dirPath), initHadoopConfig())
    try {
      val filePath = new Path(StringTool.joinValues(dirPath, "/", fileName))
      if (fileSystem.exists(filePath)) {
        fileSystem.delete(filePath, true)
      }
    } finally {
      fileSystem.close()
    }
  }

  /**
   * 在指定目录下寻找指定文件,并将它改名
   *
   * @param dirPath     目录路径
   * @param fileName    旧文件名
   * @param newFilePath 新文件路径
   * @return
   */
  def renameFile(dirPath: String, fileName: String, newFilePath: String): Unit = {
    val fileSystem = FileSystem.newInstance(URI.create(dirPath), initHadoopConfig())
    try {
      val filePath = new Path(StringTool.joinValues(dirPath, "/", fileName))
      val destPath = new Path(newFilePath)
      if (fileSystem.exists(filePath)) {
        fileSystem.rename(filePath, destPath)
      }
    } finally {
      fileSystem.close()
    }

  }

  def mkDir(parentDirPath: String, dirPath: String): Unit = {
    val fileSystem = FileSystem.newInstance(URI.create(parentDirPath), initHadoopConfig())
    try {
      val filePath = new Path(dirPath)
      if (!fileSystem.exists(filePath)) {
        fileSystem.mkdirs(filePath)
      }
    } finally {
      fileSystem.close()
    }
  }

  /**
   * 移动 source 目录中 文件名后缀符合 fileSuffix 的文件到 target 目录
   * @param source 源目录
   * @param target 目标目录
   * @param fileSuffix 文件名后缀
   */
  def moveFile(source: String, target: String, fileSuffix: String): Unit = {
    LOG.info("move " + source + " to " + target)
    val fileSystem = FileSystem.newInstance(URI.create(source), initHadoopConfig())
    try {
      val targetPath = new Path(target)
      if (!fileSystem.exists(targetPath)) {
        fileSystem.mkdirs(targetPath)
      }
      for (radiusLogStatus <- getFileStatusesFromPath(source)) {
        val fileName = radiusLogStatus.getPath.getName
        if (fileName.endsWith(fileSuffix)) {
          //符合条件，则移动文件到targetPath 目录下
          fileSystem.rename(radiusLogStatus.getPath, targetPath)
          LOG.info("move " + radiusLogStatus.getPath.toString() + " to " + target)
        }
      }
    } finally {
      fileSystem.close()
    }
  }

  /**
   * 删除目录下文件
   */
  def deleteFile(dirPath: String): Unit = {
    val fileSystem = FileSystem.newInstance(URI.create(dirPath), initHadoopConfig())
    try {
      val path = new Path(dirPath)
      if (!fileSystem.exists(path)) {
        LOG.info("目录 " + dirPath + " 不存在")
      } else {
        for (radiusLogStatus <- getFileStatusesFromPath(dirPath)) {
          val filePath = radiusLogStatus.getPath.toString()
          fileSystem.delete(radiusLogStatus.getPath, true)
          LOG.info("删除 " + filePath + " 成功")
        }
        fileSystem.delete(path, true)
        LOG.info("删除目录 " + dirPath + " 成功")
      }
    } finally {
      fileSystem.close()
    }
  }

  /**
   * 根据路径取出目标路径下所有的子目录名
   *
   * @param dirPath 路径
   * @return
   */
  def getDirNameABFromPath(dirPath: String): ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    for (radiusLogStatus <- getFileStatusesFromPath(dirPath)) {
      val dirName = radiusLogStatus.getPath.getName
      result.append(dirName)
    }
    result
  }

  /**
   * 移动文件file到target目录
   * @param filepath 文件目录
   * @param target 文目录路径
   */
  def moveFile(filepath: String, target: String): Unit = {
    val fileSystem = FileSystem.newInstance(URI.create(filepath), initHadoopConfig())
    try {
      val targetPath = new Path(target)
      if (!fileSystem.exists(targetPath)) {
        fileSystem.mkdirs(targetPath)
      }
      val filePath = new Path(filepath)
      fileSystem.rename(filePath, targetPath)
      LOG.info("move " + filepath + " to " + target)
    } finally {
      fileSystem.close()
    }
  }

  /**
   * 判断路径是否存在
   */
  def isExitDir(pathDir: String): Boolean = {
    val fileSystem = FileSystem.newInstance(URI.create(pathDir), initHadoopConfig())
    val path = new Path(pathDir)
    fileSystem.exists(path)
  }

}
