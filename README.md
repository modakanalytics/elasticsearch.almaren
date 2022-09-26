# Elastic Search Connector

[![Build Status](https://travis-ci.com/music-of-the-ainur/elasticsearch.almaren.svg?branch=master)](https://travis-ci.com/music-of-the-ainur/elasticsearch.almaren)

Add to your build:
```
libraryDependencies += "com.github.music-of-the-ainur" %% "elasticsearch-almaren" % "0.0.1-2-4"
```

Example in Spark Shell
```
spark-shell --master local[*] --packages "com.github.music-of-the-ainur:almaren-framework_2.12:0.9.5-$SPARK_VERSION,com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.1-$SPARK_VERSION"
```

## Source and Target

Connector was implemented using: 

[https://github.com/elastic/elasticsearch-hadoop](https://github.com/elastic/elasticsearch-hadoop).
[https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html](https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html).

## Example

### Source

Parameters:

| Parameters | Description              |
|------------|--------------------------|
| nodes      | localhost                |
| port       | 9200                     |
| resource   | collection               |
| query      | Elastic Search Query     |  
| user       | username                 |
| password   | password                 |
| options    | es configuration options |

#### Source Example

```scala
import com.github.music.of.the.ainur.almaren.Almaren
import com.github.music.of.the.ainur.almaren.builder.Core.Implicit
import com.github.music.of.the.ainur.almaren.elasticsearch.ElasticSearch.ElasticSearchImplicit

almaren.builder.sourceElasticSearch("localhost", "9200", query = Some("?q=*"), "test", None, None,
  Map("es.nodes.wan.only" -> "true",
    "es.net.ssl" -> "false",
    "es.index.auto.create" -> "yes",
    "es.index.read.missing.as.empty" -> "yes"))
  .batch
```

### Target

Parameters:

| Parameters | Description              |
|------------|--------------------------|
| nodes      | localhost                |
| port       | 9200                     |
| resource   | collection               |
| user       | username                 |
| password   | password                 |
| options    | es configuration options |
| saveMode   | SaveMode.Overwrite      |

#### Target Example

```scala
import org.apache.spark.sql.SaveMode
import com.github.music.of.the.ainur.almaren.Almaren
import com.github.music.of.the.ainur.almaren.builder.Core.Implicit
import com.github.music.of.the.ainur.almaren.elasticsearch.ElasticSearch.ElasticSearchImplicit

almaren.builder.sourceSql(s"SELECT * FROM $testTable")
    .targetElasticSearch("localhost", "9200", "test", None, None,
      Map("es.nodes.wan.only" -> "true", "es.net.ssl" -> "false"),
      SaveMode.Overwrite)
    .batch
```
