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
Python analysis.py -d <the path to the directory to analyze (Required)> -g <set type of graph (Required)> -f <set file name (Optional)> -c <set main class (Optional)>
```
Note: All Visual Representations are found in the 
```GraphViz/GraphFilesPNG``` folder named appropriately based on the analyzed file and graph representation.

## Details
We create three different representations of Call Graph, Interprocedural Control Flow Graph, and Abstract Syntax Trees.

<br>
<br>

### Call Graph
<br>
For Call Graphs, we use the soot analysis tool in the following directory:
```
SootScripts/src/sootscripts/CallGraphScript.java
```
<details>
  <summary>CallGraphScript.java</summary>
  
```
	
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
```
</details>


Step 1) Code -> Analysis

Step 2) Anaylsis -> Graph

Here is what the run in command line will look for:
```
Python analysis.py -d path -g CG -f ExampleCode.java
```
	
<details>
  <summary>ExampleCode.java</summary>

```
package testers;


public class ExampleCode
{
    public static void main(String[] args) {

        new Print().bar();
        int x = 20;
        int y;
        if (x > 10)
            y = 10;
        else
            y = 5;
        math(x,y);

    }

    public static void math(int x, int y) {
        int sum = x+y;
        int mul = x*y;
        int sub = x-y;
        new Print().foo();
    }

}

class Print
{
    public void foo() {
        bar();
    }

    public void bar() {
    }

}
```

</details>
	
	
<details>
  <summary>CG Dot File</summary>

```
digraph {
    "testers.ExampleCode.main" -> "testers.ExampleCode.math"
    "testers.ExampleCode.main" -> "testers.Print.bar"
    "testers.ExampleCode.main" -> "testers.Print.<init>"
    "testers.ExampleCode.math" -> "testers.Print.<init>"
    "testers.ExampleCode.math" -> "testers.Print.foo"
    "testers.Print.foo" -> testers.Print.bar
}
```
</details>
	

<details>
  <summary>ExampleCode.png</summary>


![This is an image](https://media.discordapp.net/attachments/942159728287572099/945031339252523088/testers-CG.png)


</details>
	
	
Note that this code will run on entire directories if the -f argument is omitted. Make sure the code has been compiled and class files are available to be run on, otherwise it will not run correctly. 

<br>
<br>

### Interprocedural Control Flow Graph

For Interprocedural Control Flow Graph, we use the soot analysis tool in the following directory:
```
SootScripts/src/sootscripts/ICFGScript.java
```
	
<details>
  <summary>ICFGScript.java</summary>
	
```
package sootscripts;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.exampleproblems.IFDSLocalInfoFlow;
import soot.jimple.toolkits.ide.exampleproblems.IFDSPossibleTypes;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.OnTheFlyJimpleBasedICFG;
import soot.toolkits.scalar.Pair;
import soot.options.Options;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ICFGScript {
    public static void main(String args[]) {
        // Soot classpath
        String path = System.getProperty("user.dir") + "/" + args[0];
        String cls = args[1];

        // Setting the classpath programatically
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(path);

        // Enable whole-program mode
        Options.v().set_whole_program(true);
        Options.v().set_app(true);
        Options.v().set_allow_phantom_refs(true);

        // Call-graph options
        Options.v().setPhaseOption("cg", "safe-newinstance:true");
        Options.v().setPhaseOption("cg.cha","enabled:false");
        Options.v().setPhaseOption("cg.spark","enabled:true");
        Options.v().setPhaseOption("cg.spark","verbose:true");
        Options.v().setPhaseOption("cg.spark","on-fly-cg:true");

        // Set the main class of the application to be analysed
        Options.v().set_main_class(cls);

        // Load the main class
        SootClass c = Scene.v().loadClass(cls, SootClass.BODIES);
        c.setApplicationClass();

        // Load the "main" method of the main class and set it as a Soot entry point
        SootMethod entryPoint = c.getMethodByName("main");
        List<SootMethod> entryPoints = new ArrayList<SootMethod>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);

        // Set the args
        args = new String[]{"-w", cls};

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {
                JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
                IFDSTabulationProblem<Unit, Pair<Value,
                        Set<DefinitionStmt>>, SootMethod,
                        InterproceduralCFG<Unit, SootMethod>> problem = new IFDSReachingDefinitions(icfg);

                IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>,
                        SootMethod, InterproceduralCFG<Unit, SootMethod>> solver =
                        new IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>, SootMethod,
                                InterproceduralCFG<Unit, SootMethod>>(problem);

                System.out.println("Starting Solver");
                solver.solve();
                System.out.println("Done");
                SootMethod src = Scene.v().getMainClass().getMethodByName("main");
                List<Unit> nodes = (List) icfg.getStartPointsOf(src);
                File file = new File("GraphViz/DotFiles/ICFG.txt");
                try {
                    file.delete();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileWriter writer = null;
                try {
                    writer = new FileWriter("GraphViz/DotFiles/ICFG.txt");
                    writer.write("digraph {\n");
                    while(!nodes.isEmpty()){
                        Unit parent = nodes.remove(0);
                        List<Unit> targets = icfg.getSuccsOf(parent);
                        while (!targets.isEmpty()) {
                            Unit child = targets.remove(0);
                            if (child.toString().equals("nop") || child.toString().equals("goto [?= nop]")) {
                                targets.addAll(icfg.getSuccsOf(child));
                            }
                            else {
                                nodes.add(child);
                                String parentString = parent.toString();
                                String childString = child.toString();
                                parentString = parentString.contains(" goto nop") ? parentString.substring(0, parentString.length() - 9) : parentString;
                                childString = childString.contains(" goto nop") ? childString.substring(0, childString.length() - 9) : childString;
                                writer.write("\t\"" + parentString + "\" -> \"" + childString + "\"\n");
                            }
                        }
                    }
                    writer.write("}");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        soot.Main.main(args);
    }
}
```
	
</details>
	
Step 1) Code -> Analysis

Step 2) Anaylsis -> Graph

Here is what the run in command line will look for:
# TODO NEED MORE INFO FOR MAIN CLASS 
```
Python analysis.py -d path -g ICFG -f ExampleCode.java
```
	
# TODO
- ExampleCode.java
- ExampleCode.dot
- ExampleCode.png

Note that this code will run on entire directories if the -f argument is omitted. Make sure the main class and method are located as well.
	
	
### Abstract Syntax Trees


