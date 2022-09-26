package com.github.music.of.the.ainur.almaren.elasticsearch

import org.apache.spark.sql.SaveMode
import org.scalatest._
import com.github.music.of.the.ainur.almaren.Almaren
import com.github.music.of.the.ainur.almaren.builder.Core.Implicit
import com.github.music.of.the.ainur.almaren.elasticsearch.ElasticSearch.ElasticSearchImplicit
import org.apache.spark.sql.{DataFrame, Column}
import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.functions._

class Test extends FunSuite with BeforeAndAfter {

  val almaren = Almaren("App Test")

  val spark = almaren.spark
    .master("local[*]")
    .config("spark.ui.enabled", "false")
    .config("spark.sql.shuffle.partitions", "1")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val testTable = "movies"
  createSampleData(testTable)

  //Write Data From elasticSearch
  val df1 = almaren.builder.sourceSql(s"SELECT * FROM $testTable")
    .targetElasticSearch("localhost", "9200", "test", None, None,
      Map("es.nodes.wan.only" -> "true", "es.net.ssl" -> "false"),
      SaveMode.Overwrite)
    .batch

  // Read Data From elasticSearch with QUery
  val df2 = almaren.builder
    .sourceElasticSearch("localhost", "9200", query = Some("?q=*"), "test", None, None,
      Map("es.nodes.wan.only" -> "true",
        "es.net.ssl" -> "false",
        "es.index.auto.create" -> "yes",
        "es.index.read.missing.as.empty" -> "yes"))
    .batch

  // Read Data From elasticSearch without QUery
  val df3 = almaren.builder
    .sourceElasticSearch("localhost", "9200", query = None, "test", None, None,
      Map("es.nodes.wan.only" -> "true",
        "es.net.ssl" -> "false",
        "es.index.auto.create" -> "yes",
        "es.index.read.missing.as.empty" -> "yes"))
    .batch

  test(df1,df2,"ElasticSearch")
  test(df1,df3,"ElasticSearch without Query")


  def test(df1: DataFrame, df2: DataFrame, name: String): Unit = {
    testCount(df1, df2, name)
    testCompare(df1, df2, name)
  }

  def testCount(df1: DataFrame, df2: DataFrame, name: String): Unit = {
    val count1 = df1.count()
    val count2 = df2.count()
    val count3 = spark.emptyDataFrame.count()
    test(s"Count Test:$name should match") {
      assert(count1 == count2)
    }
    test(s"Count Test:$name should not match") {
      assert(count1 != count3)
    }
  }

  // Doesn't support nested type and we don't need it :)
  def testCompare(df1: DataFrame, df2: DataFrame, name: String): Unit = {
    val diff = compare(df1, df2)
    test(s"Compare Test:$name should be zero") {
      assert(diff == 0)
    }
    test(s"Compare Test:$name, should not be able to join") {
      assertThrows[AnalysisException] {
        compare(df2, spark.emptyDataFrame)
      }
    }
  }

  private def compare(df1: DataFrame, df2: DataFrame): Long =
    df1.as("df1").join(df2.as("df2"), joinExpression(df1), "leftanti").count()

  private def joinExpression(df1: DataFrame): Column =
    df1.schema.fields
      .map(field => col(s"df1.${field.name}") <=> col(s"df2.${field.name}"))
      .reduce((col1, col2) => col1.and(col2))


  def createSampleData(tableName: String): Unit = {
    val res = spark.read.csv("src/test/resources/sample_data/data.csv")
    res.createTempView(tableName)
  }


}
