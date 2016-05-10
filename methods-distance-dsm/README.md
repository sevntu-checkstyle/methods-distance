[![Build Status](https://travis-ci.org/alex-zuy/method-call-graph.svg?branch=master)](https://travis-ci.org/alex-zuy/method-call-graph)

Running:
- clone sources
- enter directory methods-distance-dsm
- compile:
```
mvn package
```
- run
```
java -jar target/methods-distance-dsm-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/sources/InputFile.java
```
This will produce files 'InputFile.java.html' and 'InputFile.java.dot' in working directory

Output .html file will contain design structure matrix of dependencies between methods.
This matrix looks like [this](checkstyle/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java) java file:
![DSM example](http://alex-zuy.github.io/methods-distance-dsm/checker-dsm.png)

Output .dot file will contain graph of method dependencies in .dot format that can be visualized by many viewers.
Viewers:
* [Viz.js - online viewer](http://mdaines.github.io/viz.js/)
* [XDot](https://github.com/jrfonseca/xdot.py)

As an example, here is the output for [this](checkstyle/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java) java file:
```dot
digraph "dependencies" {
rankdir = "LR";
"setupChild(Configuration)" [ color="#ffff00" shape="trapezium" ];
"process(List)" [ color="#00ff00" shape="ellipse" ];
"Checker()" [ color="#00ff00" shape="ellipse" ];
"processFiles(List)" [ color="#000000" shape="ellipse" ];
subgraph clustersimple {
"fireAuditStarted()" [ color="#000000" shape="ellipse" ];
"addFilter(Filter)" [ color="#00ff00" shape="ellipse" ];
"addFileSetCheck(FileSetCheck)" [ color="#00ff00" shape="ellipse" ];
"fireFileFinished(String)" [ color="#00ff00" shape="trapezium" ];
"fireErrors(String,SortedSet)" [ color="#00ff00" shape="trapezium" ];
"processFile(File)" [ color="#000000" shape="ellipse" ];
"fireAuditFinished()" [ color="#000000" shape="ellipse" ];
"addListener(AuditListener)" [ color="#00ff00" shape="ellipse" ];
"fireFileStarted(String)" [ color="#00ff00" shape="trapezium" ];
}
"setupChild(Configuration)" -> "addFileSetCheck(FileSetCheck)" [ label="1/43" ];
"setupChild(Configuration)" -> "addFilter(Filter)" [ label="2/52" ];
"setupChild(Configuration)" -> "addListener(AuditListener)" [ label="3/60" ];
"process(List)" -> "fireAuditStarted()" [ label="1/26" ];
"process(List)" -> "processFiles(List)" [ label="3/47" ];
"process(List)" -> "fireAuditFinished()" [ label="2/34" ];
"Checker()" -> "addListener(AuditListener)" [ label="18/298" ];
"processFiles(List)" -> "fireFileStarted(String)" [ label="2/58" ];
"processFiles(List)" -> "processFile(File)" [ label="1/35" ];
"processFiles(List)" -> "fireErrors(String,SortedSet)" [ label="3/73" ];
"processFiles(List)" -> "fireFileFinished(String)" [ label="4/92" ];
/*
Legend
Node border color:
    a) GREEN - public
    b) YELLOW - protected
    c) BLACK - private
    d) BLUE - default
Node shape:
    if static - rectangle
    otherwise if override - trapezium
    otherwise if overloaded - triangle
    otherwise ellipse
*/
}

```
This graph looks like this:
![Graph example](http://alex-zuy.github.io/methods-distance-dsm/checker-dot.png)
