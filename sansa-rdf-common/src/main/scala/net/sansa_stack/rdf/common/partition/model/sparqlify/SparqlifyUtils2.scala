package net.sansa_stack.rdf.common.partition.model.sparqlify

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashMap

import scala.reflect.runtime.universe._

import org.aksw.jena_sparql_api.utils.Vars
import org.aksw.jena_sparql_api.views.E_RdfTerm
import org.aksw.obda.domain.impl.LogicalTableTableName
import org.aksw.obda.jena.domain.impl.ViewDefinition
import org.apache.jena.graph.NodeFactory
import org.apache.jena.sparql.core.Quad
import org.apache.jena.sparql.core.Var
import org.apache.jena.sparql.expr.Expr
import org.apache.jena.sparql.expr.ExprVar
import org.apache.jena.sparql.expr.NodeValue

import net.sansa_stack.rdf.common.partition.core.RdfPartitionDefault

/**
 * Converter from RdfPartitionDefault to OBDA ViewDefinition 
 * These ViewDefinitions can then be serialized as e.g. R2RML or SML
 *
 * TODO Make tableName generation strategy configurable
 */
object SparqlifyUtils2 {
  def newExprVar(varName: String): ExprVar = new ExprVar(Var.alloc(varName))
  def newExprVar(varId: Int): ExprVar = newExprVar("_" + varId)

  def newExprVar(i: Int, attrNames: List[String]) : ExprVar = {
    val attrName = attrNames(i)
    val result = newExprVar(attrName)
    result
  }

  def createViewDefinition(p: RdfPartitionDefault): ViewDefinition = {
    //val basicTableInfo = basicTableInfoProvider.getBasicTableInfo(sqlQueryStr)
    //println("Result schema: " + basicTableInfoProvider.getBasicTableInfo(sqlQueryStr))

    //items.foreach(x => println("Item: " + x))

    val t = p.layout.schema
    val attrNames = t.members.sorted.collect({ case m: MethodSymbol if m.isCaseAccessor => m.name.toString }).toList


    //println("Counting the dataset: " + ds.count())
    val pred = p.predicate
    
    // For now let's just use the full predicate as the uri
    //val predPart = pred.substring(pred.lastIndexOf("/") + 1)
    val predPart = pred;
    val pn = NodeFactory.createURI(p.predicate)


    val dt = p.datatype
    val dtPart = if(dt != null && !dt.isEmpty) "_" + dt.substring(dt.lastIndexOf("/") + 1) else ""
    val langPart = if(p.langTagPresent) "_lang" else ""

    val tableName = (predPart + dtPart + langPart) //.replace("#", "__").replace("-", "_")

    val quad = new Quad(Quad.defaultGraphIRI, Vars.s, pn, Vars.o)
    val quadPattern = new ArrayList(Collections.singleton(quad))

    val sTerm = createExprForNode(0, attrNames, p.subjectType, "", p.langTagPresent)
    val oTerm = createExprForNode(1, attrNames, p.objectType, p.datatype, p.langTagPresent)

    /*
    val se = new E_Equals(new ExprVar(Vars.s), sTerm)
    val oe = new E_Equals(new ExprVar(Vars.o), oTerm)
    val varDefs = new ArrayList[Expr] //new ExprList()
    varDefs.add(se)
    varDefs.add(oe)
		*/
    val varDefs = new LinkedHashMap[Var, Expr]()
    varDefs.put(Vars.s, sTerm)
    varDefs.put(Vars.o, oTerm)

    //val typeMap = basicTableInfo.getRawTypeMap.asScala.map({ case (k, v) => (k, TypeToken.alloc(v)) }).asJava


    //val schema = new SchemaImpl(new ArrayList[String](basicTableInfo.getRawTypeMap.keySet()), typeMap)

    //println("Schema: " + schema)

    val logicalTable = new LogicalTableTableName(tableName)
    val vd = new ViewDefinition(tableName, quadPattern, varDefs, new LinkedHashMap(), logicalTable)

    vd
  }

  def createExprForNode(offset: Int, attrNames: List[String], termType: Byte, datatype: String, langTagPresent: Boolean): E_RdfTerm = {
    //val o = offset + 1
    val o = offset

    val on = newExprVar(o, attrNames)

    termType match {
      case 0 => E_RdfTerm.createBlankNode(on)
      case 1 => E_RdfTerm.createUri(on)
      case 2 => if(langTagPresent) E_RdfTerm.createPlainLiteral(on, newExprVar(o + 1, attrNames)) else E_RdfTerm.createTypedLiteral(on, NodeValue.makeString(datatype))
      //case 2 if(!Option(datatype).getOrElse("").isEmpty) => E_RdfTerm.createTypedLiteral(o, o + 1)
      case _ => throw new RuntimeException("Unhandled case")
    }
  }

}
