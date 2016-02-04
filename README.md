# method-call-graph

Running:
- clone sources
- compile:
```
mvn package
```
- run
```
java -jar target/method-call-graph-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/sources/InputFile.java
```
This will produce file 'InputFile.java.dot' in working directory

Output .dot file will contain graph of method dependencies in .dot format that can be visualized by many viewers.
Viewers:
* [Viz.js - online viewer](http://mdaines.github.io/viz.js/)
* [XDot](https://github.com/jrfonseca/xdot.py)

As an example, here is the output for [this](https://github.com/sevntu-checkstyle/sevntu.checkstyle/blob/master/sevntu-checks/src/main/java/com/github/sevntu/checkstyle/checks/coding/NameConventionForJunit4TestClassesCheck.java) java file:
```dot
digraph "dependencies" {
	graph [bgcolor="transparent"]
	Node_0 [label="setExpectedClassNameRegex(String)"];
	Node_1 [label="getDefaultTokens()"];
	Node_2 [label="getIdentifierName(DetailAST)"];
	Node_3 [label="hasAnnotation(DetailAST,Pattern)"];
	Node_4 [label="isMatchesRegex(Pattern,String)"];
	Node_5 [label="setMethodAnnotationNameRegex(String)"];
	Node_6 [label="isClassDefinitionAnnotated(DetailAST)"];
	Node_7 [label="logUnexpectedClassName(DetailAST)"];
	Node_8 [label="visitToken(DetailAST)"];
	Node_9 [label="setClassAnnotationNameRegex(String)"];
	Node_10 [label="isAtleastOneMethodAnnotated(DetailAST)"];
	Node_11 [label="hasUnexpectedName(DetailAST)"];
	Node_3 -> Node_2 [label="1"];
	Node_3 -> Node_4 [label="2"];
	Node_6 -> Node_3 [label="2"];
	Node_8 -> Node_6 [label="0"];
	Node_8 -> Node_10 [label="1"];
	Node_8 -> Node_11 [label="2"];
	Node_8 -> Node_7 [label="4"];
	Node_10 -> Node_3 [label="1"];
	Node_11 -> Node_2 [label="2"];
	Node_11 -> Node_4 [label="3"];
}
```
 This graph looks like this:
 ![Graph example](http://pirat9600q.github.io/graph.png)
 
