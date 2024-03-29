# Elastic Search Connector

[![Elasticsearch Almaren](https://github.com/modakanalytics/elasticsearch.almaren/actions/workflows/elasticsearch-alamren.yml/badge.svg)](https://github.com/modakanalytics/elasticsearch.almaren/actions/workflows/elasticsearch-alamren.yml)

Add to your build:
```
libraryDependencies += "com.github.music-of-the-ainur" %% "elasticsearch-almaren" % "0.0.3-3.4"
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-30" % "8.10.2"
```

To run in spark-shell:
For scala-version(2.12):
```
spark-shell --master local[*] --packages "com.github.music-of-the-ainur:almaren-framework_2.12:0.9.10-3.4,com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-3.4,org.elasticsearch:elasticsearch-spark-30_2.12:8.10.2"
```
For scala-version(2.13):
```
spark-shell --master local[*] --packages "com.github.music-of-the-ainur:almaren-framework_2.13:0.9.10-3.4,com.github.music-of-the-ainur:elasticsearch-almaren_2.13:0.0.3-3.4,org.elasticsearch:elasticsearch-spark-30_2.13:8.10.2"
```

#### Maven / Ivy Package Usage
The connector is also available from the
[Maven Central](https://mvnrepository.com/artifact/com.github.music-of-the-ainur)
repository. It can be used using the `--packages` option or the
`spark.jars.packages` configuration property. Use the following value

| version                    | Connector Artifact                                                   |
|----------------------------|----------------------------------------------------------------------|
| Spark 3.4.x and scala 2.13 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.13:0.0.3-3.4` |
| Spark 3.4.x and scala 2.12 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-3.4` |
| Spark 3.3.x and scala 2.13 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.13:0.0.3-3.3` |
| Spark 3.3.x and scala 2.12 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-3.3` |
| Spark 3.2.x and scala 2.12 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-3.2` |
| Spark 3.1.x and scala 2.12 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-3.1` |
| Spark 2.4.x and scala 2.12 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.12:0.0.3-2.4` |
| Spark 2.4.x and scala 2.11 | `com.github.music-of-the-ainur:elasticsearch-almaren_2.11:0.0.3-2.4` |

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
