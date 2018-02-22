package net.sansa_stack.rdf.spark

import net.sansa_stack.rdf.spark.utils.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.Graph
import org.apache.jena.graph.{ Node, Triple }

/**
 * Wrap up implicit classes/methods o load RDF data into [[GraphX]].
 *
 * @author Gezim Sejdiu
 */

package object graph {

  /**
   * Adds methods, `asGraph` to [[RDD]] that allows to transform as a GraphX representation.
   */
  implicit class GraphLoader(triples: RDD[Triple]) extends Logging {

    /**
     * Constructs GraphX graph from RDD of triples
     * @return object of GraphX which contains the constructed  ''graph''.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.constructGraph]]
     */
    def asGraph() = GraphOps.constructGraph(triples)

    /**
     * Constructs Hashed GraphX graph from RDD of triples
     * @return object of GraphX which contains the constructed hashed ''graph''.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.constructHashedGraph]]
     */
    def asHashedGraph() = GraphOps.constructHashedGraph(triples)

    /**
     * Constructs String GraphX graph from RDD of triples
     * @return object of GraphX which contains the constructed string ''graph''.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.constructStringGraph]]
     */
    def asStringGraph() = GraphOps.constructStringGraph(triples)
  }

  /**
   * Adds methods, `astTriple`, `find`, `size` to [[Graph][Node, Node]] that allows to different operations to it.
   */
  implicit class GraphOperations(graph: Graph[Node, Node]) extends Logging {

    /**
     * Convert a graph into a RDD of Triple.
     * @return a RDD of triples.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.toRDD]]
     */
    def toRDD() = GraphOps.toRDD(graph)

    /**
     * Convert a graph into a DataFrame.
     * @return a DataFrame of triples.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.toDF]]
     */
    def toDF() = GraphOps.toDF(graph)

    /**
     * Convert a graph into a Dataset of Triple.
     * @return a Dataset of triples.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.toDS]]
     */
    def toDS() = GraphOps.toDS(graph)

    /**
     * Finds triplets  of a given graph.
     * @param subject
     * @param predicate
     * @param object
     * @return graph which contains subset of the reduced graph.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.find]]
     */
    def find(subject: Node, predicate: Node, `object`: Node) = GraphOps.find(graph, subject, predicate, `object`)

    /**
     * Compute the size of the graph
     * @return the number of edges in the graph.
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.size]]
     */
    def size() = GraphOps.size(graph)

    /**
     * Return the union of this graph and another one.
     *
     * @param other of the other graph
     * @return graph (union of all)
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.union]]
     */
    def union(other: Graph[Node, Node]) = GraphOps.union(graph, other)
    /**
     * Returns a new RDF graph that contains the intersection of the current RDF graph with the given RDF graph.
     *
     * @param other the other RDF graph
     * @return the intersection of both RDF graphs
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.difference]]
     */
    def difference(other: Graph[Node, Node]) = GraphOps.difference(graph, other)

    /**
     * Returns a new RDF graph that contains the difference between the current RDF graph and the given RDF graph.
     *
     * @param other the other RDF graph
     * @return the difference of both RDF graphs
     * @see [[net.sansa_stack.rdf.spark.graph.GraphOps.intersection]]
     */
    def intersection(other: Graph[Node, Node]) = GraphOps.intersection(graph, other)
  }
}