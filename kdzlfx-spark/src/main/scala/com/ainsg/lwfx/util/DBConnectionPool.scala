package com.ainsg.lwfx.util

import java.sql.Connection
import java.sql.DriverManager
import java.util.LinkedList

/**
 * 数据库连接池工具类
 */
object DBConnectionPool {
  //读取db.properties
  val file: LoadFiles = new LoadFiles("db.properties")
  // 数据库驱动类
  private val driver: String = file.getProp("driver")
  // 数据库连接地址
  private val url: String = file.getProp("url")
  // 数据库连接用户名
  private val username: String = file.getProp("username")
  // 数据库连接密码
  private val password: String = file.getProp("password")
  // 连接池大小
  val max_connection: Int = file.getProp("max_connection").toInt
  private val connection_num = file.getProp("connection_num").toInt //产生连接数
  private var current_num = 0 //当前连接池已产生的连接数
  private val pools = new LinkedList[Connection]() //连接池

  /**
   * 加载驱动
   */
  private def before() {
    if (current_num > max_connection.toInt && pools.isEmpty()) {
      println("busyness")
      Thread.sleep(2000)
      before()
    } else {
      Class.forName(driver)
    }
  }
  /**
   * 获得连接
   */
  private def initConn(): Connection = {
    val conn = DriverManager.getConnection(url, username, password)
    //    logError(url)
    conn
  }

  /**
   * 初始化连接池
   */
  private def initConnectionPool(): LinkedList[Connection] = {
    AnyRef.synchronized({
      if (pools.isEmpty()) {
        before()
        for (i <- 1 to connection_num.toInt) {
          pools.push(initConn())
          current_num += 1
        }
      }
      pools
    })
  }
  /**
   * 获得连接
   */
  def getConn(): Connection = {
    initConnectionPool()
    pools.poll()
  }
  /**
   * 释放连接
   */
  def releaseCon(con: Connection) {
    if(!con.getAutoCommit) {
      con.setAutoCommit(true)
    }
    pools.push(con)
  }

}
