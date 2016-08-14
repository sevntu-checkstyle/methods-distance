[![Build Status](https://travis-ci.org/sevntu-checkstyle/methods-distance.svg?branch=master)](https://travis-ci.org/sevntu-checkstyle/methods-distance)
[![Build status](https://ci.appveyor.com/api/projects/status/1pex335cc3ow5mxx/branch/master?svg=true)](https://ci.appveyor.com/project/Checkstyle/methods-distance/branch/master)

Running:
- clone sources
- compile:
```
mvn package
```
- run
```
java -jar methods-distance/target/methods-distance-dsm-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/sources/InputFile.java
```
This will produce files 'InputFile.java.html' and 'InputFile.java.dot' in working directory.

Alternatively you can try to use web service for generating these files.
Web service is hosted at [herooku](https://methods-distance.herokuapp.com).
To get .html file perform GET request at /dsm uri with parameter source_url pointing to
url of Java source file. For example:
```
https://methods-distance.herokuapp.com/dsm?source_url=https://raw.githubusercontent.com/checkstyle/checkstyle/b4e884c2ff3bef182b045692b59c1aceae3cb892/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java
```
To get .dot file perform GET request using /dot uri with parameter source_url:
```
https://methods-distance.herokuapp.com/dot?source_url=https://raw.githubusercontent.com/checkstyle/checkstyle/b4e884c2ff3bef182b045692b59c1aceae3cb892/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java
```

Output .html file will contain design structure matrix of dependencies between methods.
This matrix looks like [this](https://github.com/checkstyle/checkstyle/blob/b4e884c2ff3bef182b045692b59c1aceae3cb892/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java) java file:
![DSM example](http://alex-zuy.github.io/methods-distance-dsm/checker-dsm.png)

Output .dot file will contain graph of method dependencies in .dot format that can be visualized by many viewers.
Viewers:
* [Viz.js - online viewer](http://mdaines.github.io/viz.js/)
* [XDot](https://github.com/jrfonseca/xdot.py)

As an example, here is the output for [this](https://github.com/checkstyle/checkstyle/blob/b4e884c2ff3bef182b045692b59c1aceae3cb892/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java) java file:
```dot
digraph "dependencies" {
rankdir = "LR";
"process(List)" [ color="#00ff00" shape="ellipse" ];
"processFiles(List)" [ color="#000000" shape="ellipse" ];
"Checker()" [ color="#00ff00" shape="ellipse" ];
"setupChild(Configuration)" [ color="#ffff00" shape="trapezium" ];
subgraph clustersimple {
"processFile(File)" [ color="#000000" shape="ellipse" ];
"addFilter(Filter)" [ color="#00ff00" shape="ellipse" ];
"fireFileFinished(String)" [ color="#00ff00" shape="trapezium" ];
"fireFileStarted(String)" [ color="#00ff00" shape="trapezium" ];
"fireAuditStarted()" [ color="#000000" shape="ellipse" ];
"fireErrors(String,SortedSet)" [ color="#00ff00" shape="trapezium" ];
"getExternalResourceLocations()" [ color="#000000" shape="ellipse" ];
"addFileSetCheck(FileSetCheck)" [ color="#00ff00" shape="ellipse" ];
"fireAuditFinished()" [ color="#000000" shape="ellipse" ];
"addListener(AuditListener)" [ color="#00ff00" shape="ellipse" ];
}
"process(List)" -> "getExternalResourceLocations()" [ label="1/35" ];
"process(List)" -> "fireAuditStarted()" [ label="2/55" ];
"process(List)" -> "processFiles(List)" [ label="4/76" ];
"process(List)" -> "fireAuditFinished()" [ label="3/63" ];
"processFiles(List)" -> "fireFileStarted(String)" [ label="2/58" ];
"processFiles(List)" -> "processFile(File)" [ label="1/35" ];
"processFiles(List)" -> "fireErrors(String,SortedSet)" [ label="3/73" ];
"processFiles(List)" -> "fireFileFinished(String)" [ label="4/92" ];
"Checker()" -> "addListener(AuditListener)" [ label="19/327" ];
"setupChild(Configuration)" -> "addFileSetCheck(FileSetCheck)" [ label="1/43" ];
"setupChild(Configuration)" -> "addFilter(Filter)" [ label="2/52" ];
"setupChild(Configuration)" -> "addListener(AuditListener)" [ label="3/60" ];
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
