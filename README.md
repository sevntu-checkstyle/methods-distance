# method-call-graph

Running:
- clone sources
- compile:
```
mvn package
```
- run. Application will look for checkstyle-config.xml in working directory, so it must be run from project root.
```
java -jar target/method-call-graph-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/sources/InputFile.java
```
This will produce file 'InputFile.java.dot' in working directory
