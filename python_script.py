from argparse import ArgumentParser
from pathlib import Path
import subprocess
from subprocess import run

parser = ArgumentParser()
requiredArgs = parser.add_argument_group('required named arguments')
requiredArgs.add_argument("-d", "--dir", dest="directory",
                    help="the path to the directory to analyze", required=True)
requiredArgs.add_argument("-g", "--graph", dest="graph",
                    help="set type of graph", required=True)

args = vars(parser.parse_args())
dir = args['directory']
graph = args['graph']

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
    jar_path = f"SootScripts/out/artifacts/{graph}_jar/SootScripts.jar"
    cmd = ['java', '-jar', jar_path, directory]
    run(cmd)
    # proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    # stdout,stderr = proc.communicate(stdin) #wanting to pass in info to java file that is using Scanner
    # print ('This was "' + stdout.decode("utf-8") + '"')

def make_graph(directory, graph):
    graph_path = f'Graphviz/DotFiles/{graph}.txt'
    dir_name = dir.split('/')[0]
    output_file = f'Graphviz/GraphFilesPNG/{dir_name}-{graph}.png'
    cmd = ['dot', '-Tpng', graph_path, '-o', output_file]
    run(cmd)

# compile_java(args['filename']) #may not need to compile if we're passing in .class files
run_analysis(dir, graph)
make_graph(dir, graph)