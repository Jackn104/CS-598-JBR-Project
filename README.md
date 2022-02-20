### CS-598-JBR-Project

Our project relies on these, so make sure to have updated versions of each.
- Java
- Python
- Soot
- CheckStyle
- GraphViz 


## Overview
Our project focuses on static analysis of Java Code.

1) Soot and CheckStyle creates an analysis of code in a given representation.
2) The analysis is passed in a format that is read by GraphViz
3) GraphViz creates a visual of the analysis in a png file

We use python to create a pipeline that conviniently runs all of our code together:

```
Python python_script.py -d <the path to the directory to analyze (Required)> -g <set type of graph (Required)> -f <set file name (Optional)> -c <set main class (Optional)>
```
Note: All Visual Representations are found in the 
'''GraphViz/GraphFilesPNG''' folder named appropriately based on the analyzed file and graph representation.

## Details
We create three different representations of Call Graph, Interprocedural Control Flow Graph, and Abstract Syntax Trees.


# Call Graph
For Call Graphs, we use the soot analysis tool in the following directory:
'''
SootScripts/src/sootscripts/CallGraphScript.java
'''

Step 1) Code -> Analysis

Step 2) Anaylsis -> Graph

##### TODO
ExampleCode
ExampleCode.dot
ExampleCode.png
