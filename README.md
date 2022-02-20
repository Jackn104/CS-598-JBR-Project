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

### Call Graph
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

Here is what the run in command line will look for:
```
Python analysis.py -d path -g CG -f ExampleCode.java
```

<strong> Step 1) Code -> Analysis</strong>

<strong>Step 2) Anaylsis -> Graph</strong>


	
<details>
 <summary>ExampleCode.java (Code)</summary>

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
  <summary>CG Dot File (Analysis Formatted)</summary>

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
  <summary>ExampleCode.png (Graph)</summary>


![This is an image](https://media.discordapp.net/attachments/942159728287572099/945031339252523088/testers-CG.png)


</details>
	
	
Note that this code will run on entire directories if the -f argument is omitted. Make sure the code has been compiled and class files are available to be run on, otherwise it will not run correctly. 

<br>

### Interprocedural Control Flow Graph

For Interprocedural Control Flow Graph, we use the soot analysis tool in the following directory:
```
SootScripts/src/sootscripts/ICFGScript.java
```

	
Here is what the run in command line will look like:
# TODO NEED MORE INFO FOR MAIN CLASS 
```
Python analysis.py -d path -g ICFG -f ExampleCode.java -m
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
	
<strong>Step 1) Code -> Analysis</strong>

<strong>Step 2) Anaylsis -> Graph</strong>

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

	
```
</details>

<details>
  <summary> ICFG Dot File</summary>
	
```
digraph {

    "args := @parameter0: java.lang.String[]" -> "temp$0 = new testers.Print"
    "temp$0 = new testers.Print" -> "specialinvoke temp$0.<testers.Print: void <init>()>()"
    "specialinvoke temp$0.<testers.Print: void <init>()>()" -> "virtualinvoke temp$0.<testers.Print: void bar()>()"
    "virtualinvoke temp$0.<testers.Print: void bar()>()" -> "x = 20"
    "x = 20" -> "if x > 10"
    "if x > 10" -> "temp$1 = 10"
    "if x > 10" -> "temp$2 = 5"
    "temp$1 = 10" -> "y = temp$1"
    "temp$2 = 5" -> "y = temp$2"
    "y = temp$1" -> "staticinvoke <testers.ExampleCode: void math(int,int)>(x, y)"
    "y = temp$2" -> "staticinvoke <testers.ExampleCode: void math(int,int)>(x, y)"
    "staticinvoke <testers.ExampleCode: void math(int,int)>(x, y)" -> "return"
}
```
</details>

	
<details>
  <summary>ExampleCode.png</summary>
	
![This is an image](https://media.discordapp.net/attachments/942159728287572099/945031339445452840/testers-ICFG.png?width=412&height=676)
</details>


Note that this code will run on entire directories if the -f argument is omitted. Make sure the main class and method are located as well.
	
	
### Abstract Syntax Trees
For Abstract Syntax Trees, we use the soot analysis tool in the following directory:
```
SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py

SootScripts/out/artifacts/AST_Stuff/pipe.py
```
	
Here is what the run in command line will look like:

```
Python analysis.py -d path -g AST -f ExampleCode.java
```

<details>
  <summary>check_to_graphViz.py</summary>
	
``` 
f = open("SootScripts/out/artifacts/AST_Stuff/AST.txt")
l = f.readlines()

def clean_lines(txt):     
    return txt.replace(" -> ",":")
        
all_text = ['digraph{ \n']

for k in range(6):
    i = k*4
    top = ''
    ind = []
    for line in l:
        try:
            if line[i] not in "`| ":
                if i-1 >0:
                    k=1
                else:
                    k=0
                new_line = clean_lines(line[i-k:(len(line)-1)])
                top = '"'+new_line+'" -> '
                ind.append(l.index(line))
            if line[i+2] in "-":
                new_line = clean_lines(line[3+i:(len(line)-1)]).strip()
                if "SEMI" not in top+new_line and "CURLY" not in top+new_line and "PAREN" not in top+new_line:
                    all_text.append(top +'"'+new_line + '"\n')
        except:
            all_text.append('\n', end ='')
    offset = 0
    for index in ind:
        l.pop(index-offset)
        offset+=1

        
    
all_text.append('\n}')

for line in all_text:
    print(line, end ='')

```
	
</details>

	
<details>
  <summary>pipe.py</summary>
	
```
import os
import subprocess
from subprocess import run
#Everytime there is ExampleCode it can be replaced with the parameter to fit the file being analyzed
os.system("java -jar SootScripts/out/artifacts/AST_Stuff/checkstyle-9.3-all.jar -t  SootScripts/out/artifacts/AST_Stuff/ExampleCode.java>SootScripts/out/artifacts/AST_Stuff/AST.txt")
os.system("py SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py>SootScripts/out/artifacts/AST_Stuff/ExampleCodeDot.txt")
run('dot -Tpng SootScripts/out/artifacts/AST_Stuff/ExampleCodeDot.txt -o GraphViz/GraphFilesPNG/ExampleCodeAST.png')
```
	
</details>
	
<strong> Step 1) Code -> CheckStyle Analysis</strong>

<strong>Step 2) CheckStyle Analysis -> Python Parser</strong>
	
<strong>Step 3) Python Parser -> Graph </strong>
	
<details>
	
  <summary>ExampleCode.java</summary>
	
```
package testers;

public class ExampleCode
{
	public static void main(String[] args) {

		new Print().bar();
		int x = 20;
		int y = 10;
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
  <summary>AST.txt</summary>
```
COMPILATION_UNIT -> COMPILATION_UNIT [1:0]
|--PACKAGE_DEF -> package [1:0]
|   |--ANNOTATIONS -> ANNOTATIONS [1:8]
|   |--IDENT -> testers [1:8]
|   `--SEMI -> ; [1:15]
|--CLASS_DEF -> CLASS_DEF [3:0]
|   |--MODIFIERS -> MODIFIERS [3:0]
|   |   `--LITERAL_PUBLIC -> public [3:0]
|   |--LITERAL_CLASS -> class [3:7]
|   |--IDENT -> ExampleCode [3:13]
|   `--OBJBLOCK -> OBJBLOCK [4:0]
|       |--LCURLY -> { [4:0]
|       |--METHOD_DEF -> METHOD_DEF [5:1]
|       |   |--MODIFIERS -> MODIFIERS [5:1]
|       |   |   |--LITERAL_PUBLIC -> public [5:1]
|       |   |   `--LITERAL_STATIC -> static [5:8]
|       |   |--TYPE -> TYPE [5:15]
|       |   |   `--LITERAL_VOID -> void [5:15]
|       |   |--IDENT -> main [5:20]
|       |   |--LPAREN -> ( [5:24]
|       |   |--PARAMETERS -> PARAMETERS [5:25]
|       |   |   `--PARAMETER_DEF -> PARAMETER_DEF [5:25]
|       |   |       |--MODIFIERS -> MODIFIERS [5:25]
|       |   |       |--TYPE -> TYPE [5:25]
|       |   |       |   |--IDENT -> String [5:25]
|       |   |       |   `--ARRAY_DECLARATOR -> [ [5:31]
|       |   |       |       `--RBRACK -> ] [5:32]
|       |   |       `--IDENT -> args [5:34]
|       |   |--RPAREN -> ) [5:38]
|       |   `--SLIST -> { [5:40]
|       |       |--EXPR -> EXPR [7:17]
|       |       |   `--METHOD_CALL -> ( [7:17]
|       |       |       |--DOT -> . [7:13]
|       |       |       |   |--LITERAL_NEW -> new [7:2]
|       |       |       |   |   |--IDENT -> Print [7:6]
|       |       |       |   |   |--LPAREN -> ( [7:11]
|       |       |       |   |   |--ELIST -> ELIST [7:12]
|       |       |       |   |   `--RPAREN -> ) [7:12]
|       |       |       |   `--IDENT -> bar [7:14]
|       |       |       |--ELIST -> ELIST [7:18]
|       |       |       `--RPAREN -> ) [7:18]
|       |       |--SEMI -> ; [7:19]
|       |       |--VARIABLE_DEF -> VARIABLE_DEF [8:2]
|       |       |   |--MODIFIERS -> MODIFIERS [8:2]
|       |       |   |--TYPE -> TYPE [8:2]
|       |       |   |   `--LITERAL_INT -> int [8:2]
|       |       |   |--IDENT -> x [8:6]
|       |       |   `--ASSIGN -> = [8:8]
|       |       |       `--EXPR -> EXPR [8:10]
|       |       |           `--NUM_INT -> 20 [8:10]
|       |       |--SEMI -> ; [8:12]
|       |       |--VARIABLE_DEF -> VARIABLE_DEF [9:2]
|       |       |   |--MODIFIERS -> MODIFIERS [9:2]
|       |       |   |--TYPE -> TYPE [9:2]
|       |       |   |   `--LITERAL_INT -> int [9:2]
|       |       |   |--IDENT -> y [9:6]
|       |       |   `--ASSIGN -> = [9:8]
|       |       |       `--EXPR -> EXPR [9:10]
|       |       |           `--NUM_INT -> 10 [9:10]
|       |       |--SEMI -> ; [9:12]
|       |       |--EXPR -> EXPR [10:6]
|       |       |   `--METHOD_CALL -> ( [10:6]
|       |       |       |--IDENT -> math [10:2]
|       |       |       |--ELIST -> ELIST [10:7]
|       |       |       |   |--EXPR -> EXPR [10:7]
|       |       |       |   |   `--IDENT -> x [10:7]
|       |       |       |   |--COMMA -> , [10:8]
|       |       |       |   `--EXPR -> EXPR [10:9]
|       |       |       |       `--IDENT -> y [10:9]
|       |       |       `--RPAREN -> ) [10:10]
|       |       |--SEMI -> ; [10:11]
|       |       `--RCURLY -> } [12:1]
|       |--METHOD_DEF -> METHOD_DEF [14:1]
|       |   |--MODIFIERS -> MODIFIERS [14:1]
|       |   |   |--LITERAL_PUBLIC -> public [14:1]
|       |   |   `--LITERAL_STATIC -> static [14:8]
|       |   |--TYPE -> TYPE [14:15]
|       |   |   `--LITERAL_VOID -> void [14:15]
|       |   |--IDENT -> math [14:20]
|       |   |--LPAREN -> ( [14:24]
|       |   |--PARAMETERS -> PARAMETERS [14:25]
|       |   |   |--PARAMETER_DEF -> PARAMETER_DEF [14:25]
|       |   |   |   |--MODIFIERS -> MODIFIERS [14:25]
|       |   |   |   |--TYPE -> TYPE [14:25]
|       |   |   |   |   `--LITERAL_INT -> int [14:25]
|       |   |   |   `--IDENT -> x [14:29]
|       |   |   |--COMMA -> , [14:30]
|       |   |   `--PARAMETER_DEF -> PARAMETER_DEF [14:32]
|       |   |       |--MODIFIERS -> MODIFIERS [14:32]
|       |   |       |--TYPE -> TYPE [14:32]
|       |   |       |   `--LITERAL_INT -> int [14:32]
|       |   |       `--IDENT -> y [14:36]
|       |   |--RPAREN -> ) [14:37]
|       |   `--SLIST -> { [14:39]
|       |       |--VARIABLE_DEF -> VARIABLE_DEF [15:2]
|       |       |   |--MODIFIERS -> MODIFIERS [15:2]
|       |       |   |--TYPE -> TYPE [15:2]
|       |       |   |   `--LITERAL_INT -> int [15:2]
|       |       |   |--IDENT -> sum [15:6]
|       |       |   `--ASSIGN -> = [15:10]
|       |       |       `--EXPR -> EXPR [15:13]
|       |       |           `--PLUS -> + [15:13]
|       |       |               |--IDENT -> x [15:12]
|       |       |               `--IDENT -> y [15:14]
|       |       |--SEMI -> ; [15:15]
|       |       |--VARIABLE_DEF -> VARIABLE_DEF [16:2]
|       |       |   |--MODIFIERS -> MODIFIERS [16:2]
|       |       |   |--TYPE -> TYPE [16:2]
|       |       |   |   `--LITERAL_INT -> int [16:2]
|       |       |   |--IDENT -> mul [16:6]
|       |       |   `--ASSIGN -> = [16:10]
|       |       |       `--EXPR -> EXPR [16:13]
|       |       |           `--STAR -> * [16:13]
|       |       |               |--IDENT -> x [16:12]
|       |       |               `--IDENT -> y [16:14]
|       |       |--SEMI -> ; [16:15]
|       |       |--VARIABLE_DEF -> VARIABLE_DEF [17:2]
|       |       |   |--MODIFIERS -> MODIFIERS [17:2]
|       |       |   |--TYPE -> TYPE [17:2]
|       |       |   |   `--LITERAL_INT -> int [17:2]
|       |       |   |--IDENT -> sub [17:6]
|       |       |   `--ASSIGN -> = [17:10]
|       |       |       `--EXPR -> EXPR [17:13]
|       |       |           `--MINUS -> - [17:13]
|       |       |               |--IDENT -> x [17:12]
|       |       |               `--IDENT -> y [17:14]
|       |       |--SEMI -> ; [17:15]
|       |       |--EXPR -> EXPR [18:17]
|       |       |   `--METHOD_CALL -> ( [18:17]
|       |       |       |--DOT -> . [18:13]
|       |       |       |   |--LITERAL_NEW -> new [18:2]
|       |       |       |   |   |--IDENT -> Print [18:6]
|       |       |       |   |   |--LPAREN -> ( [18:11]
|       |       |       |   |   |--ELIST -> ELIST [18:12]
|       |       |       |   |   `--RPAREN -> ) [18:12]
|       |       |       |   `--IDENT -> foo [18:14]
|       |       |       |--ELIST -> ELIST [18:18]
|       |       |       `--RPAREN -> ) [18:18]
|       |       |--SEMI -> ; [18:19]
|       |       `--RCURLY -> } [19:1]
|       `--RCURLY -> } [21:0]
`--CLASS_DEF -> CLASS_DEF [23:0]
    |--MODIFIERS -> MODIFIERS [23:0]
    |--LITERAL_CLASS -> class [23:0]
    |--IDENT -> Print [23:6]
    `--OBJBLOCK -> OBJBLOCK [24:0]
        |--LCURLY -> { [24:0]
        |--METHOD_DEF -> METHOD_DEF [25:1]
        |   |--MODIFIERS -> MODIFIERS [25:1]
        |   |   `--LITERAL_PUBLIC -> public [25:1]
        |   |--TYPE -> TYPE [25:8]
        |   |   `--LITERAL_VOID -> void [25:8]
        |   |--IDENT -> foo [25:13]
        |   |--LPAREN -> ( [25:16]
        |   |--PARAMETERS -> PARAMETERS [25:17]
        |   |--RPAREN -> ) [25:17]
        |   `--SLIST -> { [25:19]
        |       |--EXPR -> EXPR [26:5]
        |       |   `--METHOD_CALL -> ( [26:5]
        |       |       |--IDENT -> bar [26:2]
        |       |       |--ELIST -> ELIST [26:6]
        |       |       `--RPAREN -> ) [26:6]
        |       |--SEMI -> ; [26:7]
        |       `--RCURLY -> } [27:1]
        |--METHOD_DEF -> METHOD_DEF [29:1]
        |   |--MODIFIERS -> MODIFIERS [29:1]
        |   |   `--LITERAL_PUBLIC -> public [29:1]
        |   |--TYPE -> TYPE [29:8]
        |   |   `--LITERAL_VOID -> void [29:8]
        |   |--IDENT -> bar [29:13]
        |   |--LPAREN -> ( [29:16]
        |   |--PARAMETERS -> PARAMETERS [29:17]
        |   |--RPAREN -> ) [29:17]
        |   `--SLIST -> { [29:19]
        |       `--RCURLY -> } [30:1]
        `--RCURLY -> } [32:0]
```
</details>
	
<details>
  <summary>ExampleCodeDot.txt</summary>
```
digraph{ 
"COMPILATION_UNIT:COMPILATION_UNIT [1:0]" -> "PACKAGE_DEF:package [1:0]"
"COMPILATION_UNIT:COMPILATION_UNIT [1:0]" -> "CLASS_DEF:CLASS_DEF [3:0]"
"COMPILATION_UNIT:COMPILATION_UNIT [1:0]" -> "CLASS_DEF:CLASS_DEF [23:0]"
"PACKAGE_DEF:package [1:0]" -> "ANNOTATIONS:ANNOTATIONS [1:8]"
"PACKAGE_DEF:package [1:0]" -> "IDENT:testers [1:8]"
"CLASS_DEF:CLASS_DEF [3:0]" -> "MODIFIERS:MODIFIERS [3:0]"
"CLASS_DEF:CLASS_DEF [3:0]" -> "LITERAL_CLASS:class [3:7]"
"CLASS_DEF:CLASS_DEF [3:0]" -> "IDENT:ExampleCode [3:13]"
"CLASS_DEF:CLASS_DEF [3:0]" -> "OBJBLOCK:OBJBLOCK [4:0]"
"CLASS_DEF:CLASS_DEF [23:0]" -> "MODIFIERS:MODIFIERS [23:0]"
"CLASS_DEF:CLASS_DEF [23:0]" -> "LITERAL_CLASS:class [23:0]"
"CLASS_DEF:CLASS_DEF [23:0]" -> "IDENT:Print [23:6]"
"CLASS_DEF:CLASS_DEF [23:0]" -> "OBJBLOCK:OBJBLOCK [24:0]"
"MODIFIERS:MODIFIERS [3:0]" -> "LITERAL_PUBLIC:public [3:0]"
"OBJBLOCK:OBJBLOCK [4:0]" -> "METHOD_DEF:METHOD_DEF [5:1]"
"OBJBLOCK:OBJBLOCK [4:0]" -> "METHOD_DEF:METHOD_DEF [14:1]"
"OBJBLOCK:OBJBLOCK [24:0]" -> "METHOD_DEF:METHOD_DEF [25:1]"
"OBJBLOCK:OBJBLOCK [24:0]" -> "METHOD_DEF:METHOD_DEF [29:1]"
"METHOD_DEF:METHOD_DEF [5:1]" -> "MODIFIERS:MODIFIERS [5:1]"
"METHOD_DEF:METHOD_DEF [5:1]" -> "TYPE:TYPE [5:15]"
"METHOD_DEF:METHOD_DEF [5:1]" -> "IDENT:main [5:20]"
"METHOD_DEF:METHOD_DEF [5:1]" -> "PARAMETERS:PARAMETERS [5:25]"
"METHOD_DEF:METHOD_DEF [5:1]" -> "SLIST:{ [5:40]"
"METHOD_DEF:METHOD_DEF [14:1]" -> "MODIFIERS:MODIFIERS [14:1]"
"METHOD_DEF:METHOD_DEF [14:1]" -> "TYPE:TYPE [14:15]"
"METHOD_DEF:METHOD_DEF [14:1]" -> "IDENT:math [14:20]"
"METHOD_DEF:METHOD_DEF [14:1]" -> "PARAMETERS:PARAMETERS [14:25]"
"METHOD_DEF:METHOD_DEF [14:1]" -> "SLIST:{ [14:39]"
"METHOD_DEF:METHOD_DEF [25:1]" -> "MODIFIERS:MODIFIERS [25:1]"
"METHOD_DEF:METHOD_DEF [25:1]" -> "TYPE:TYPE [25:8]"
"METHOD_DEF:METHOD_DEF [25:1]" -> "IDENT:foo [25:13]"
"METHOD_DEF:METHOD_DEF [25:1]" -> "PARAMETERS:PARAMETERS [25:17]"
"METHOD_DEF:METHOD_DEF [25:1]" -> "SLIST:{ [25:19]"
"METHOD_DEF:METHOD_DEF [29:1]" -> "MODIFIERS:MODIFIERS [29:1]"
"METHOD_DEF:METHOD_DEF [29:1]" -> "TYPE:TYPE [29:8]"
"METHOD_DEF:METHOD_DEF [29:1]" -> "IDENT:bar [29:13]"
"METHOD_DEF:METHOD_DEF [29:1]" -> "PARAMETERS:PARAMETERS [29:17]"
"METHOD_DEF:METHOD_DEF [29:1]" -> "SLIST:{ [29:19]"
"MODIFIERS:MODIFIERS [5:1]" -> "LITERAL_PUBLIC:public [5:1]"
"MODIFIERS:MODIFIERS [5:1]" -> "LITERAL_STATIC:static [5:8]"
"TYPE:TYPE [5:15]" -> "LITERAL_VOID:void [5:15]"
"PARAMETERS:PARAMETERS [5:25]" -> "PARAMETER_DEF:PARAMETER_DEF [5:25]"
"SLIST:{ [5:40]" -> "EXPR:EXPR [7:17]"
"SLIST:{ [5:40]" -> "VARIABLE_DEF:VARIABLE_DEF [8:2]"
"SLIST:{ [5:40]" -> "VARIABLE_DEF:VARIABLE_DEF [9:2]"
"SLIST:{ [5:40]" -> "EXPR:EXPR [10:6]"
"MODIFIERS:MODIFIERS [14:1]" -> "LITERAL_PUBLIC:public [14:1]"
"MODIFIERS:MODIFIERS [14:1]" -> "LITERAL_STATIC:static [14:8]"
"TYPE:TYPE [14:15]" -> "LITERAL_VOID:void [14:15]"
"PARAMETERS:PARAMETERS [14:25]" -> "PARAMETER_DEF:PARAMETER_DEF [14:25]"
"PARAMETERS:PARAMETERS [14:25]" -> "COMMA:, [14:30]"
"PARAMETERS:PARAMETERS [14:25]" -> "PARAMETER_DEF:PARAMETER_DEF [14:32]"
"SLIST:{ [14:39]" -> "VARIABLE_DEF:VARIABLE_DEF [15:2]"
"SLIST:{ [14:39]" -> "VARIABLE_DEF:VARIABLE_DEF [16:2]"
"SLIST:{ [14:39]" -> "VARIABLE_DEF:VARIABLE_DEF [17:2]"
"SLIST:{ [14:39]" -> "EXPR:EXPR [18:17]"
"MODIFIERS:MODIFIERS [25:1]" -> "LITERAL_PUBLIC:public [25:1]"
"TYPE:TYPE [25:8]" -> "LITERAL_VOID:void [25:8]"
"SLIST:{ [25:19]" -> "EXPR:EXPR [26:5]"
"MODIFIERS:MODIFIERS [29:1]" -> "LITERAL_PUBLIC:public [29:1]"
"TYPE:TYPE [29:8]" -> "LITERAL_VOID:void [29:8]"
"PARAMETER_DEF:PARAMETER_DEF [5:25]" -> "MODIFIERS:MODIFIERS [5:25]"
"PARAMETER_DEF:PARAMETER_DEF [5:25]" -> "TYPE:TYPE [5:25]"
"PARAMETER_DEF:PARAMETER_DEF [5:25]" -> "IDENT:args [5:34]"
"EXPR:EXPR [7:17]" -> "METHOD_CALL:( [7:17]"
"VARIABLE_DEF:VARIABLE_DEF [8:2]" -> "MODIFIERS:MODIFIERS [8:2]"
"VARIABLE_DEF:VARIABLE_DEF [8:2]" -> "TYPE:TYPE [8:2]"
"VARIABLE_DEF:VARIABLE_DEF [8:2]" -> "IDENT:x [8:6]"
"VARIABLE_DEF:VARIABLE_DEF [8:2]" -> "ASSIGN:= [8:8]"
"VARIABLE_DEF:VARIABLE_DEF [9:2]" -> "MODIFIERS:MODIFIERS [9:2]"
"VARIABLE_DEF:VARIABLE_DEF [9:2]" -> "TYPE:TYPE [9:2]"
"VARIABLE_DEF:VARIABLE_DEF [9:2]" -> "IDENT:y [9:6]"
"VARIABLE_DEF:VARIABLE_DEF [9:2]" -> "ASSIGN:= [9:8]"
"EXPR:EXPR [10:6]" -> "METHOD_CALL:( [10:6]"
"PARAMETER_DEF:PARAMETER_DEF [14:25]" -> "MODIFIERS:MODIFIERS [14:25]"
"PARAMETER_DEF:PARAMETER_DEF [14:25]" -> "TYPE:TYPE [14:25]"
"PARAMETER_DEF:PARAMETER_DEF [14:25]" -> "IDENT:x [14:29]"
"PARAMETER_DEF:PARAMETER_DEF [14:32]" -> "MODIFIERS:MODIFIERS [14:32]"
"PARAMETER_DEF:PARAMETER_DEF [14:32]" -> "TYPE:TYPE [14:32]"
"PARAMETER_DEF:PARAMETER_DEF [14:32]" -> "IDENT:y [14:36]"
"VARIABLE_DEF:VARIABLE_DEF [15:2]" -> "MODIFIERS:MODIFIERS [15:2]"
"VARIABLE_DEF:VARIABLE_DEF [15:2]" -> "TYPE:TYPE [15:2]"
"VARIABLE_DEF:VARIABLE_DEF [15:2]" -> "IDENT:sum [15:6]"
"VARIABLE_DEF:VARIABLE_DEF [15:2]" -> "ASSIGN:= [15:10]"
"VARIABLE_DEF:VARIABLE_DEF [16:2]" -> "MODIFIERS:MODIFIERS [16:2]"
"VARIABLE_DEF:VARIABLE_DEF [16:2]" -> "TYPE:TYPE [16:2]"
"VARIABLE_DEF:VARIABLE_DEF [16:2]" -> "IDENT:mul [16:6]"
"VARIABLE_DEF:VARIABLE_DEF [16:2]" -> "ASSIGN:= [16:10]"
"VARIABLE_DEF:VARIABLE_DEF [17:2]" -> "MODIFIERS:MODIFIERS [17:2]"
"VARIABLE_DEF:VARIABLE_DEF [17:2]" -> "TYPE:TYPE [17:2]"
"VARIABLE_DEF:VARIABLE_DEF [17:2]" -> "IDENT:sub [17:6]"
"VARIABLE_DEF:VARIABLE_DEF [17:2]" -> "ASSIGN:= [17:10]"
"EXPR:EXPR [18:17]" -> "METHOD_CALL:( [18:17]"
"EXPR:EXPR [26:5]" -> "METHOD_CALL:( [26:5]"

}
```
</details>
	
<details>
  <summary>check_to_graphViz.py</summary>
![this is an image](https://raw.githubusercontent.com/Jackn104/CS-598-JBR-Project/main/SootScripts/out/artifacts/AST_Stuff/ExampleCodeAST.png)
</details>
