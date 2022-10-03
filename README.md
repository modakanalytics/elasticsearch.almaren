# Elastic Search Connector

[![Elasticsearch Almaren](https://github.com/modakanalytics/elasticsearch.almaren/actions/workflows/elasticsearch-alamren.yml/badge.svg)](https://github.com/modakanalytics/elasticsearch.almaren/actions/workflows/elasticsearch-alamren.yml)

Add to your build:
```
libraryDependencies += "com.github.music-of-the-ainur" %% "elasticsearch-almaren" % "0.0.2-3.1"
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-30" % "8.4.2"
```

Example in Spark Shell
```
spark-shell --master local[*] --packages "com.github.music-of-the-ainur:almaren-framework_2.12:0.9.5-$SPARK_VERSION,com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.2-$SPARK_VERSION,org.elasticsearch:elasticsearch-spark-30_2.12:8.4.2"
```

## Source and Target

Connector was implemented using: 

[https://github.com/elastic/elasticsearch-hadoop](https://github.com/elastic/elasticsearch-hadoop).
[https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html](https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html).

## Example

### Source

Parameters:

| Parameters | Description                                                                        | Eaxmple Value         |
|------------|------------------------------------------------------------------------------------|-----------------------|
| nodes      | elasticsearch nodes details                                                        | localhost             |
| port       | elasticsearch service port                                                         | 9200                  |
| resource   | collection name to query the results                                               | myindex               |
| query      | elasticsearch query value                                                          | ?q=*                  |  
| user       | username of the elasticsearch service if it is associated with basic authentication | username              |
| password   | password of the elasticsearch service if it is associated with basic authentication | password              |
| options    | es configuration options                                                         | "es.net.ssl" -> "false" |

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


| Parameters | Description                                                                         | Eaxmple Value          |
|------------|-------------------------------------------------------------------------------------|------------------------|
| nodes      | elasticsearch nodes details                                                         | localhost              |
| port       | elasticsearch service port                                                          | 9200                   |
| resource   | collection name to query the results                                                | myindex                |
| user       | username of the elasticsearch service if it is associated with basic authentication | username               |
| password   | password of the elasticsearch service if it is associated with basic authentication | password               |
| options    | es configuration options                                                            | "es.net.ssl" -> "false" |
| saveMode   | savemode in spark dataframe                                                         | SaveMode.Overwrite                        |

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
