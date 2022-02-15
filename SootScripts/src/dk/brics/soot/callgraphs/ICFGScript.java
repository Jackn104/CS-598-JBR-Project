package dk.brics.soot.callgraphs;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.scalar.Pair;
import soot.options.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        System.out.println("Starting solver");

        solver.solve();
        System.out.println(icfg.toString());
        System.out.println("Done");
    }

    public static void main(String args[]) {
        // Soot classpath
        String path = System.getProperty("user.dir")+"/../jackson-core/src/test/java";

        // Setting the classpath programatically
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(path);

        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_output_dir(System.getProperty("user.dir")+"/output");

//        // Set Soot's internal classpath
//        Options.v().set_soot_classpath(System.getProperty("user.dir")+"/src");

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
        Options.v().set_main_class("perf.ManualIntRead");

        // Load the main class
        SootClass c = Scene.v().loadClass("perf.ManualIntRead", SootClass.BODIES);
        c.setApplicationClass();

        // Load the "main" method of the main class and set it as a Soot entry point
        SootMethod entryPoint = c.getMethodByName("main");
        List<SootMethod> entryPoints = new ArrayList<SootMethod>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new ICFGScript()));

        soot.Main.main(new String[]{"-w", "perf.ManualIntRead", "-pp"});
    }
}
