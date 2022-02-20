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
		args = new String[] {"-w", "-process-dir", path};

		System.out.println("Starting analysis");
		Main.main(args);
		System.out.println("Finished analysis");

		CallGraph cg = Scene.v().getCallGraph();
		System.out.println("Got Callgraph");

		File file = new File("../GraphViz/DotFiles/CG.txt");
		try {
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileWriter writer;
		try {
			writer = new FileWriter("../GraphViz/DotFiles/CG.txt");
			writer.write("digraph {\n");
			Iterator<Edge> edgeIterator = cg.iterator();
			while(edgeIterator.hasNext()){
				Edge edge = edgeIterator.next();
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
