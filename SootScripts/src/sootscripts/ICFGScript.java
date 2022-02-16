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
        while(!nodes.isEmpty()){
            Unit parent = nodes.get(0);
            List<Unit> targets = icfg.getSuccsOf(parent);
            while (!targets.isEmpty()) {
                Unit child = targets.get(0);
                nodes.add(child);
                System.out.println(parent.toString() + " -> " + child.toString());
                targets.remove(0);
            }
            nodes.remove(0);
        }
    }

    public static void main(String args[]) {
        // Soot classpath
        String path = System.getProperty("user.dir")+"/src";

        // Setting the classpath programatically
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(path);

//        Options.v().set_output_format(Options.output_format_jimple);
//        Options.v().set_output_dir(System.getProperty("user.dir")+"/output");

        // Enable whole-program mode
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        // Call-graph options
        Options.v().setPhaseOption("cg", "safe-newinstance:true");
        Options.v().setPhaseOption("cg.cha","enabled:false");

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark","enabled:true");
        Options.v().setPhaseOption("cg.spark","verbose:true");
        Options.v().setPhaseOption("cg.spark","on-fly-cg:true");

        Options.v().set_allow_phantom_refs(true);

        // Set the main class of the application to be analysed
        Options.v().set_main_class("testers.ExampleCode");

        // Load the main class
        SootClass c = Scene.v().loadClass("testers.ExampleCode", SootClass.BODIES);
        c.setApplicationClass();

        // Load the "main" method of the main class and set it as a Soot entry point
        SootMethod entryPoint = c.getMethodByName("main");
        List<SootMethod> entryPoints = new ArrayList<SootMethod>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new ICFGScript()));

        soot.Main.main(new String[]{"-w", "testers.ExampleCode", "-pp"});
    }
}
