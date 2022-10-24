package com.ainsg.lwfx.util

import java.sql.CallableStatement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types

import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.DataFrame

import com.ainsg.lwfx.common.Spark

/**
 *
 *
 * @define
 *
 */
object JdbcOracleHelper {

  private var conn: Connection = null
  //  private var preparedStatement: PreparedStatement = null
  private var callableStatement: CallableStatement = null

  /**
   * 建立数据库连接
   *
   * @return
   * @throws SQLException
   */
  @throws[SQLException]
  private def getConnection: Connection = {
    conn = DBConnectionPool.getConn()
    conn
  }

  /**
   * 释放连接
   *
   * @param conn
   */
  private def freeConnection(conn: Connection) = {
    try
      DBConnectionPool.releaseCon(conn)
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  /**
   * 释放statement
   *
   * @param statement
   */
  private def freeStatement(statement: Statement) = {
    try
      statement.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  /**
   * 释放resultset
   *
   * @param rs
   */
  private def freeResultSet(rs: ResultSet) = {
    try
      rs.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  /**
   * 释放PreparedStatement
   *
   * @param conn
   */
  private def freePreparedStatement(pst: PreparedStatement) = {
    try
      pst.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  /**
   * 释放资源
   *
   * @param conn
   * @param statement
   * @param rs
   */
  def free(conn: Connection, statement: Statement, rs: ResultSet, pst: PreparedStatement): Unit = {
    if (rs != null) freeResultSet(rs)
    if (statement != null) freeStatement(statement)
    if (conn != null) freeConnection(conn)
    if (pst != null) freePreparedStatement(pst)
  }

  /////////////////////////////////////////////////////////

  /**
   * 获取PreparedStatement
   *
   * @param sql
   * @throws SQLException
   */
  @throws[SQLException]
  private def getPreparedStatement(sql: String): PreparedStatement = {
    conn = getConnection
    var preparedStatement: PreparedStatement = null
    preparedStatement = conn.prepareStatement(sql)
    preparedStatement
  }

  /**
   * 用于查询，返回结果集
   *
   * @param sql
   * sql语句
   * @return 结果集
   * @throws SQLException
   */
  @throws[SQLException]
  def query(sql: String): List[Map[String, Object]] = {
    var rs: ResultSet = null
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      rs = preparedStatement.executeQuery
      ResultToListMap(rs)
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, preparedStatement)
  }

  /**
   * 用于带参数的查询，返回结果集
   *
   * @param sql
   * sql语句
   * @param paramters
   * 参数集合
   * @return 结果集
   * @throws SQLException
   */
  @throws[SQLException]
  def query(sql: String, paramters: Array[String]): List[Map[String, Object]] = {
    var rs: ResultSet = null
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      var i = 0
      while ({
        i < paramters.length
      }) {
        preparedStatement.setObject(i + 1, paramters(i))

        {
          i += 1;
          i - 1
        }
      }
      rs = preparedStatement.executeQuery
      ResultToListMap(rs)
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, preparedStatement)
  }

  /**
   * 返回单个结果的值，如count\min\max等等
   *
   * @param sql
   * sql语句
   * @return 结果集
   * @throws SQLException
   */
  @throws[SQLException]
  def getSingle(sql: String): Any = {
    var result: Any = null
    var rs: ResultSet = null
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      rs = preparedStatement.executeQuery
      if (rs.next) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, preparedStatement)
  }

  /**
   * 返回单个结果值，如count\min\max等
   *
   * @param sql
   * sql语句
   * @param paramters
   * 参数列表
   * @return 结果
   * @throws SQLException
   */
  @throws[SQLException]
  def getSingle(sql: String, paramters: Array[String]): Any = {
    var result: Any = null
    var rs: ResultSet = null
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      var i = 0
      while ({
        i < paramters.length
      }) {
        preparedStatement.setObject(i + 1, paramters(i))

        {
          i += 1;
          i - 1
        }
      }
      rs = preparedStatement.executeQuery
      if (rs.next) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, preparedStatement)
  }

  /**
   * 用于增删改
   *
   * @param sql
   * sql语句
   * @return 影响行数
   * @throws SQLException
   */
  @throws[SQLException]
  def update(sql: String): Int = {
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      preparedStatement.executeUpdate
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, null, preparedStatement)
  }

  /**
   * 用于增删改（带参数）
   *
   * @param sql
   * sql语句
   * @param paramters
   * sql语句
   * @return 影响行数
   * @throws SQLException
   */
  @throws[SQLException]
  def update(sql: String, paramters: Array[String]): Int = {
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = getPreparedStatement(sql)
      var i = 0
      while ({
        i < paramters.length
      }) {
        preparedStatement.setObject(i + 1, paramters(i))

        {
          i += 1;
          i - 1
        }
      }
      preparedStatement.executeUpdate
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, null, preparedStatement)
  }

  /**
   * 插入值后返回主键值
   *
   * @param sql
   * 插入sql语句
   * @return 返回结果
   * @throws Exception
   */
  @throws[SQLException]
  def insertWithReturnPrimeKey(sql: String): Any = {
    var rs: ResultSet = null
    var result: Object = null
    var preparedStatement: PreparedStatement = null
    try {
      conn = getConnection
      preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
      preparedStatement.execute
      rs = preparedStatement.getGeneratedKeys
      if (rs.next) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, null, preparedStatement)
  }

  /**
   * 插入值后返回主键值
   *
   * @param sql
   * 插入sql语句
   * @param paramters
   * 参数列表
   * @return 返回结果
   * @throws SQLException
   */
  @throws[SQLException]
  def insertWithReturnPrimeKey(sql: String, paramters: Array[String]): Any = {
    var rs: ResultSet = null
    var result: Object = null
    var preparedStatement: PreparedStatement = null
    try {
      conn = getConnection
      preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
      var i = 0
      while ({
        i < paramters.length
      }) {
        preparedStatement.setObject(i + 1, paramters(i))

        {
          i += 1;
          i - 1
        }
      }
      preparedStatement.execute
      rs = preparedStatement.getGeneratedKeys
      if (rs.next) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, null, preparedStatement)
  }

  /**
   * 用RDD插入值后返回主键值
   *
   * @param sql
   * 插入sql语句
   * @param paramters
   * 参数列表
   * @return 返回结果
   * @throws SQLException
   */
  def insertWithRDD(featuresData: DataFrame, sql: String): Unit = {
    featuresData.foreachPartition(partitionOfRecords => {
      var con: Connection = null
      var ps: PreparedStatement = null
      try {
        var ct = 0
        for (row <- partitionOfRecords) {
          if (null == con) {
            con = getConnection
            con.setAutoCommit(false)
            ps = con.prepareStatement(sql)
          }
          var i = 0
          while (i < row.length) {
            ps.setObject(i + 1, row.getAs[String](i))
            i = i + 1
          }
          ps.addBatch()
          ct = ct + 1
          if (ct == 2000) {
            ps.executeBatch()
            con.commit()
            ps.clearBatch()
            ct = 0
          }
        }
        if (ct > 0) {
          ps.executeBatch()
          con.commit()
        }
      } catch {
        case e: Exception => {
          e.printStackTrace()
        }
      } finally { free(con, null, null, ps) }
    })
  }

  /**
   * 用于增删改（带参数）
   *
   * @param sql
   * sql语句
   * @param paramters
   * sql语句
   * @return 影响行数
   * @throws SQLException
   */
  def updateWithRDD(featuresData: DataFrame, sql: String, Columns: Array[String]): Unit = {
    featuresData.foreachPartition(partitionOfRecords => {
      var con: Connection = null
      var ps: PreparedStatement = null
      try {
        var ct = 0
        for (row <- partitionOfRecords) {
          if (null == con) {
            con = getConnection
            con.setAutoCommit(false)
            ps = con.prepareStatement(sql)
          }
          var i = 0
          while (i < row.length) {
            ps.setObject(i + 1, row.getAs[String](i))
            i = i + 1
          }
          ps.addBatch()
          ct = ct + 1
          if (ct == 2000) {
            ps.executeBatch()
            con.commit()
            ps.clearBatch()
            ct = 0
          }
        }
        if (ct > 0) {
          ps.executeBatch()
          con.commit()
        }
      } catch {
        case e: Exception => {
          e.printStackTrace()
        }
      } finally { free(con, null, null, ps) }
    })
  }

  //////////////////////////////////////////////////////////////////

  /**
   * 获取CallableStatement
   *
   * @param procedureSql
   * @throws SQLException
   */
  @throws[SQLException]
  private def getCallableStatement(procedureSql: String) = {
    conn = getConnection
    conn.prepareCall(procedureSql)
  }

  /**
   * 存储过程查询
   * 注意outNames和outOracleTypes的顺序要对应 顺序按存储过程的参数顺序排列
   *
   * @param procedureSql
   * @param ins            输入参数数组
   * @param outNames       输出参数名称
   * @param outOracleTypes 输出参数类型
   * @return
   *
   */
  @throws[SQLException]
  def callableQuery(procedureSql: String, ins: Array[Object], outNames: Array[String], outOracleTypes: Array[Int]): Map[String, Object] = {

    val listBuffer = new ListBuffer[Object]

    try {
      callableStatement = getCallableStatement(procedureSql)

      var count = 0

      for (i <- 0 until ins.length) {
        count = count + 1
        callableStatement.setObject(count, ins(i))
      }

      for (j <- 0 until outOracleTypes.length) {
        count = count + 1
        callableStatement.registerOutParameter(count, outOracleTypes(j))
      }

      callableStatement.execute()

      count = count - outOracleTypes.length
      for (i <- 0 until outOracleTypes.length) {
        count = count + 1
        listBuffer.append(callableStatement.getObject(count))
      }
      outNames.zip(listBuffer.toList).toMap
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, null, null)
  }

  /**
   * 调用存储过程，查询单个值
   *
   * @param procedureSql
   * @return
   * @throws SQLException
   */
  @throws[SQLException]
  def callableGetSingle(procedureSql: String): Any = {
    var result: Any = null
    var rs: ResultSet = null
    try {
      rs = getCallableStatement(procedureSql).executeQuery
      while ({
        rs.next
      }) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, null)
  }

  /**
   * 调用存储过程(带参数)，查询单个值
   *
   * @param procedureSql
   * @return
   * @throws SQLException
   */
  @throws[SQLException]
  def callableGetSingle(procedureSql: String, paramters: Array[String]): Any = {
    var result: Any = null
    var rs: ResultSet = null
    try {
      callableStatement = getCallableStatement(procedureSql)
      var i = 0
      while ({
        i < paramters.length
      }) {
        callableStatement.setObject(i + 1, paramters(i))

        {
          i += 1;
          i - 1
        }
      }
      rs = callableStatement.executeQuery
      while ({
        rs.next
      }) result = rs.getObject(1)
      result
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, callableStatement, rs, null)
  }

  @throws[SQLException]
  def callableWithParamters(procedureSql: String): Any = try {
    callableStatement = getCallableStatement(procedureSql)
    callableStatement.registerOutParameter(0, Types.OTHER)
    callableStatement.execute
    callableStatement.getObject(0)
  } catch {
    case e: SQLException =>
      throw new SQLException(e)
  } finally free(conn, callableStatement, null, null)

  /**
   * 调用存储过程，执行增删改
   *
   * @param procedureSql
   * 存储过程
   * @return 影响行数
   * @throws SQLException
   */
  @throws[SQLException]
  def callableUpdate(procedureSql: String): Int = try {
    callableStatement = getCallableStatement(procedureSql)
    callableStatement.executeUpdate
  } catch {
    case e: SQLException =>
      throw new SQLException(e)
  } finally free(conn, callableStatement, null, null)

  /**
   * 调用存储过程（带参数），执行增删改
   *
   * @param procedureSql
   * 存储过程
   * @param parameters
   * @return 影响行数
   * @throws SQLException
   */
  @throws[SQLException]
  def callableUpdate(procedureSql: String, parameters: Array[String]): Int = try {
    callableStatement = getCallableStatement(procedureSql)
    var i = 0
    while ({
      i < parameters.length
    }) {
      callableStatement.setObject(i + 1, parameters(i))

      {
        i += 1;
        i - 1
      }
    }
    callableStatement.executeUpdate
  } catch {
    case e: SQLException =>
      throw new SQLException(e)
  } finally free(conn, callableStatement, null, null)

  @throws[SQLException]
  private def ResultToListMap(rs: ResultSet) = {

    val list = new ListBuffer[Map[String, Object]]

    while (rs.next) {
      val map = new scala.collection.mutable.HashMap[String, Object]
      val md = rs.getMetaData
      for (i <- 1 until md.getColumnCount) {
        map.put(md.getColumnLabel(i), rs.getObject(i))
      }
      list.append(map.toMap)
    }
    list.toList
  }

  /**
   * 全量清除表数据
   */
  @throws[SQLException]
  def truncateTable(tablename: String): Unit = {
    var preparedStatement: PreparedStatement = null
    try {
      conn = getConnection
      if (conn.isClosed()) {
        println("conn.isClosed()")
      }
      println(conn.isReadOnly())
      var sql = "truncate table " + tablename
      preparedStatement = conn.prepareStatement(sql)
      preparedStatement.execute()
    } catch {
      case e: SQLException =>
        throw new SQLException(e)
    } finally free(conn, null, null, preparedStatement)
  }

  def main(args: Array[String]): Unit = {
    //        val spark = Spark.spark
    //        val featuresData = spark.read.format("csv")
    //          .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
    //          .option("inferSchema", true) //自动推断属性列的数据类型。
    //          .load("F:/kdzlfx/statistic/userCharacter/201909")
    //        val sql = "INSERT INTO CES_USER_CHARACTER_12 (USER_NAME,DIAL_SUM_MONTH,DIAL_MEAN_MONTH,DIAL_MEDIAN_MONTH,DIAL_SD2_MONTH,DIAL_ENTROPY_MONTH,TIMELEN_SUM_MONTH,TIMELEN_MEAN_MONTH,TIMELEN_MEDIAN_MONTH,TIMELEN_SD2_MONTH,TIMELEN_ENTROPY_MONTH,OUTOCTETS_SUM_MONTH,OUTOCTETS_MEAN_MONTH,OUTOCTETS_MEDIAN_MONTH,OUTOCTETS_SD2_MONTH,OUTOCTETS_ENTROPY_MONTH,INOCTETS_SUM_MONTH,INOCTETS_MEAN_MONTH,INOCTETS_MEDIAN_MONTH,INOCTETS_SD2_MONTH,INOCTETS_ENTROPY_MONTH,OUTPACKETS_SUM_MONTH,OUTPACKETS_MEAN_MONTH,OUTPACKETS_MEDIAN_MONTH,OUTPACKETS_SD2_MONTH,OUTPACKETS_ENTROPY_MONTH,INPACKETS_SUM_MONTH,INPACKETS_MEAN_MONTH,INPACKETS_MEDIAN_MONTH,INPACKETS_SD2_MONTH,INPACKETS_ENTROPY_MONTH,UNUSE_TIME,CITY_CODE,STATUS,STATUS_M) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    //        insertWithRDD(featuresData, sql)
    //    val sql2 = "UPDATE CES_USER_CHARACTER_01 SET STATUS = ?,STATUS_M = ? WHERE USER_NAME = ? AND CITY_CODE = ?"
    //    val paramters = Array("STATUS", "STATUS_M", "USERNAME", "AREANO")
    //    updateWithRDD(featuresData, sql2, paramters)

    //    val list = query("select * from M_ADMIN")
    //    System.out.println(list);
  }

} 
