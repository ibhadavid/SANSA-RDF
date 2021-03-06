package net.sansa_stack.rdf.spark.model.hdt

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.jena.graph
import org.apache.jena.riot.Lang
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite

class HDTTripleOpsTests extends FunSuite with DataFrameSuiteBase {

  import net.sansa_stack.rdf.spark.io._
  import net.sansa_stack.rdf.spark.model._

  val path = getClass.getResource("/loader/compression-data.nt").getPath

  test("converting RDD of triples into DataFrame of hdt should pass") {

    val triples = spark.rdf(Lang.NTRIPLES)(path)

    val hdt = triples.asHDT()

    val size = hdt.size()

    assert(size == 7)
  }

  test("getting subjects from hdt subject table should match") {

    val triples = spark.rdf(Lang.NTRIPLES)(path)

    val hdt = triples.getHDT()

    val subjects = spark.sql(s"select * from subjects_hdt").count()

    assert(subjects == 3)
  }

  test("getting predicates from hdt predicate table should match") {

    val triples = spark.rdf(Lang.NTRIPLES)(path)

    val hdt = triples.getHDT()

    val predicates = spark.sql(s"select * from predicates_hdt").count()

    assert(predicates == 5)
  }

  test("getting objects from hdt object table should match") {

    val triples = spark.rdf(Lang.NTRIPLES)(path)

    val hdt = triples.getHDT()

    val objects = spark.sql(s"select * from objects_hdt").count()

    assert(objects == 6)
  }

}
