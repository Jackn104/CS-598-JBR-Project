from argparse import ArgumentParser
from pathlib import Path
import os
import subprocess
from subprocess import run

parser = ArgumentParser()
requiredArgs = parser.add_argument_group('required named arguments')
requiredArgs.add_argument("-d", "--dir", dest="directory",
                    help="the path to the directory to analyze", required=True)
requiredArgs.add_argument("-g", "--graph", dest="graph",
                    help="set type of graph", required=True)
requiredArgs.add_argument("-f", "--file", dest="file",
                    help="set file name", required=False)

args = vars(parser.parse_args())
dir = args['directory']
graph = args['graph']
file = args['file']

try: 
    #Attempt to access file location and ensure that file exists there: 
    if not (Path(dir).is_dir()):
        raise Exception("Path doesn't exist")

    if graph not in ["AST", "CG", "ICFG"]:
        raise Exception("Graph type is not in list of possible options")

except Exception as error: 
    print("Error:\t" + str(error))

# At this point it is assumed that the basic checks have passed and that nothing is wrong or missing
def compile_java(directory):
    subprocess.check_call(['javac', directory])

def run_analysis(directory, graph):
    if graph in ["CG", "ICFG"]:
        jar_path = f"SootScripts/out/artifacts/{graph}_jar/SootScripts.jar"
        cmd = ['java', '-jar', jar_path, directory]
        run(cmd)
    else:
        os.system("py SootScripts/out/artifacts/AST_Stuff/pipe.py")
        # jar_path = "SootScripts/out/artifacts/AST_Stuff/checkstyle-9.3-all.jar"
        # cmd = "java -jar " +jar_path + " -t " + directory +"/ExampleCode.java"+">"+directory+"/AST.txt"
        # os.system(cmd)
        # print("done1")
        # os.system("py SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py>SootScripts/out/artifacts/AST_Stuff/AST.txt")
    # print("done")
    # proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    # stdout,stderr = proc.communicate(stdin) #wanting to pass in info to java file that is using Scanner
    # print ('This was "' + stdout.decode("utf-8") + '"')

def make_graph(directory, graph):
    graph_path = f'Graphviz/DotFiles/{graph}.txt'
    dir_name = directory.split('/')[0]
    output_file = f'Graphviz/GraphFilesPNG/{dir_name}-{graph}.png'
    print("running")
    cmd = ['dot', '-Tpng', graph_path, '-o', output_file]
    run(cmd)
    print("done")

# compile_java(args['filename']) #may not need to compile if we're passing in .class files
run_analysis(dir, graph)
make_graph(dir, graph)