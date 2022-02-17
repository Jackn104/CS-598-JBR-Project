package sootscripts;

import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.SceneTransformer;
import soot.SootMethod;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CallGraphScript
{	
	public static void main(String[] args) {
		// Soot classpath
		String path = System.getProperty("user.dir")+"/../auto/common/src";

		// Setting the classpath programatically
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(path);

		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
		Options.v().set_app(true);

		args = new String[]{"-w", "-process-dir", path};

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map options) {
				CHATransformer.v().transform();

				// This line generates the call graph of the project you're going to analyze
				CallGraph cg = Scene.v().getCallGraph();
				// Now that you have the call graph, you can start doing whatever type of
				// analysis needed for your problem. Below, we are going to traverse the
				// generated call graph and print caller/callee relationships (BFS)

				SootMethod src = Scene.v().getMainClass().getMethodByName("main");
				List<SootMethod> nodes = new ArrayList<>();
				nodes.add(src);

				File file = new File("../GraphViz/DotFiles/CG.txt");
				try {
					file.delete();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				FileWriter writer = null;
				try {
					writer = new FileWriter("../GraphViz/DotFiles/CG.txt");
					writer.write("digraph {\n");
					while(!nodes.isEmpty()){
						SootMethod parent = nodes.get(0);
						Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(parent));
						while (targets.hasNext()) {
							SootMethod child = (SootMethod) targets.next();
							if(!child.getSignature().contains("init"))
								nodes.add(child);
							writer.write("\t\""+parent.getDeclaringClass()+"."+parent.getName()+
									"\" -> \"" + child.getDeclaringClass()+"."+child.getName()+"\"\n");
						}
						nodes.remove(0);
					}
					writer.write("}");
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}));

		Main.main(args);
	}
}
