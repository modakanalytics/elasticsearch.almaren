package com.github.music.of.the.ainur.almaren.elasticsearch

import org.apache.spark.sql.{DataFrame, SaveMode}
import com.github.music.of.the.ainur.almaren.Tree
import com.github.music.of.the.ainur.almaren.builder.Core
import com.github.music.of.the.ainur.almaren.state.core.{Target, Source}
import org.elasticsearch.spark._
import org.elasticsearch.spark.sql._

private[almaren] case class SourceElasticSearch(nodes: String,
                                                port: String,
                                                query: Option[String],
                                                resource: String,
                                                user: Option[String],
                                                password: Option[String],
                                                options: Map[String, String]) extends Source {

  def source(df: DataFrame): DataFrame = {
    logger.info(s"nodes:{$nodes}, port:{$port}, resource:{$resource}, query:{$query}, user:{$user}, options:{$options}")
    var optionsMap = (user, password) match {
      case (Some(u), Some(p)) =>
        logger.info(s"user info : {$u}")
        Map("es.net.http.auth.user" -> s"$u", "es.net.http.auth.pass" -> s"$p") ++ options
      case (_, _) => options
    }
    optionsMap = query match {
      case Some(query) => optionsMap ++ Map("es.query" -> query)
      case _ => optionsMap
    }
    df.sparkSession.sqlContext
      .read
      .format("org.elasticsearch.spark.sql")
      .options(optionsMap ++ Map("es.nodes" -> s"$nodes", "es.port" -> s"$port"))
      .load(resource)
  }
}

private[almaren] case class TargetElasticSearch(nodes: String,
                                                port: String,
                                                resource: String,
                                                user: Option[String],
                                                password: Option[String],
                                                options: Map[String, String],
                                                saveMode: SaveMode) extends Target {

  def target(df: DataFrame): DataFrame = {
    logger.info(s"nodes:{$nodes}, port:{$port}, resource:{$resource}, user:{$user}, options:{$options}")
    df.write
      .format("org.elasticsearch.spark.sql")
      .options(
        ((user, password) match {
          case (Some(u), Some(p)) =>
            logger.info(s"user info : {$u}")
            Map("es.net.http.auth.user" -> s"$u", "es.net.http.auth.pass" -> s"$p") ++ options
          case (_, _) => options
        }) ++ Map("es.nodes" -> s"$nodes", "es.port" -> s"$port"))
      .mode(saveMode)
      .save(resource)
    df
  }

}

private[almaren] trait ElasticSearchConnector extends Core {
  def targetElasticSearch(nodes: String, port: String, resource: String, user: Option[String] = None, password: Option[String] = None, options: Map[String, String] = Map(), saveMode: SaveMode = SaveMode.ErrorIfExists): Option[Tree] =
    TargetElasticSearch(nodes, port, resource, user, password, options, saveMode)

  def sourceElasticSearch(nodes: String, port: String, query: Option[String] = None, resource: String, user: Option[String] = None, password: Option[String] = None, options: Map[String, String] = Map()): Option[Tree] =
    SourceElasticSearch(nodes, port, query, resource, user, password, options)
}

object ElasticSearch {
  implicit class ElasticSearchImplicit(val container: Option[Tree]) extends ElasticSearchConnector
}
