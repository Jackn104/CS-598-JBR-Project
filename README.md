# CS-598-JBR-Project

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


### Call Graph
For Call Graphs, we use the soot analysis tool in the following directory:
'''
SootScripts/src/sootscripts/CallGraphScript.java
'''

Step 1) Code -> Analysis

Step 2) Anaylsis -> Graph

# TODO
<details>
  <summary>CallGraphScript.java</summary>
  
'''
	
package sootscripts;

import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import java.util.Iterator;
import soot.SootMethod;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CallGraphScript
{	
	public static void main(String[] args) {
		// Soot classpath
		String path = System.getProperty("user.dir") + "/" + args[0];

		// Setting the classpath programatically
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(path);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_app(true);
		args = new String[] {"-w", "-process-dir", path};

		System.out.println("Starting analysis");
		Main.main(args);
		System.out.println("Finished analysis");

		CallGraph cg = Scene.v().getCallGraph();
		System.out.println("Got Callgraph");

		File file = new File("GraphViz/DotFiles/CG.txt");
		try {
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileWriter writer;
		try {
			writer = new FileWriter("GraphViz/DotFiles/CG.txt");
			writer.write("digraph {\n");
			Iterator<Edge> edgeIterator = cg.iterator();
			while(edgeIterator.hasNext()){
				Edge edge = edgeIterator.next();
				if (edge.src().isJavaLibraryMethod() || edge.src().getDeclaringClass().getName().startsWith("jdk"))
					continue;

				SootMethod src = edge.src();
				SootMethod tgt = edge.tgt();
				writer.write("\t\""+src.getDeclaringClass()+"."+src.getName()+
							"\" -> \"" + tgt.getDeclaringClass()+"."+tgt.getName()+"\"\n");
				}
			writer.write("}");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	
'''
  
</details>



ExampleCode
ExampleCode.dot
ExampleCode.png
