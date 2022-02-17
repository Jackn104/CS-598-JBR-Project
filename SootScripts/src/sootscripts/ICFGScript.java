package sootscripts;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.scalar.Pair;
import soot.options.Options;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Subclass of SceneTransformer to run Heros IFDS solver in Soot's "wjtp" pack
public class ICFGScript extends SceneTransformer {
    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
        IFDSTabulationProblem<Unit, Pair<Value,
                Set<DefinitionStmt>>, SootMethod,
                InterproceduralCFG<Unit, SootMethod>> problem = new IFDSReachingDefinitions(icfg);

        IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>,
                SootMethod, InterproceduralCFG<Unit, SootMethod>> solver =
                new IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>, SootMethod,
                        InterproceduralCFG<Unit, SootMethod>>(problem);

        solver.solve();

        SootMethod src = Scene.v().getMainClass().getMethodByName("main");
        List<Unit> nodes = (List) icfg.getStartPointsOf(src);

        File file = new File("../GraphViz/DotFiles/ICFG.txt");
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter("../GraphViz/DotFiles/ICFG.txt");
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

    public static void main(String args[]) {
        // Soot classpath
        String path = System.getProperty("user.dir")+"/../auto/common/src";

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

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark","enabled:true");
        Options.v().setPhaseOption("cg.spark","verbose:true");
        Options.v().setPhaseOption("cg.spark","on-fly-cg:true");

        // Set the main class of the application to be analysed
//        Options.v().set_main_class("testers.ExampleCode");

        // Load the main class
//        SootClass c = Scene.v().loadClass("testers.ExampleCode", SootClass.BODIES);
//        c.setApplicationClass();

        // Load the "main" method of the main class and set it as a Soot entry point
//        SootMethod entryPoint = c.getMethodByName("main");
//        List<SootMethod> entryPoints = new ArrayList<SootMethod>();
//        entryPoints.add(entryPoint);
//        Scene.v().setEntryPoints(entryPoints);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new ICFGScript()));

        soot.Main.main(new String[]{"-w", "-process-dir", path});
    }
}
