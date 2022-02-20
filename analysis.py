from argparse import ArgumentParser
from subprocess import run
from pathlib import Path
import os

parser = ArgumentParser()
requiredArgs = parser.add_argument_group('required named arguments')
requiredArgs.add_argument("-d", "--dir", dest="directory",
                    help="the path to the directory to analyze", required=True)
requiredArgs.add_argument("-g", "--graph", dest="graph",
                    help="set type of graph", required=True)
requiredArgs.add_argument("-f", "--file", dest="file",
                    help="set file name", required=False)
requiredArgs.add_argument("-c", "--class", dest="class",
                    help="set main class", required=False)

args = vars(parser.parse_args())
dir = args['directory']
graph = args['graph']
file = args['file']
cls = args['class'] if args['class'] else "N/A"

try: 
    if not (Path(dir).is_dir()):
        raise Exception("Path doesn't exist")

    if graph not in ["AST", "CG", "ICFG"]:
        raise Exception("Graph type is not in list of possible options")

except Exception as error: 
    print("Error:\t" + str(error))

def run_analysis(directory, graph, cls):
    if graph in ["CG", "ICFG"]:
        jar_path = f"SootScripts/out/artifacts/{graph}_jar/SootScripts.jar"
        cmd = ['java', '-jar', jar_path, directory, cls]
        run(cmd)
    elif graph == "AST":
        os.system("py SootScripts/out/artifacts/AST_Stuff/pipe.py")
        # jar_path = "SootScripts/out/artifacts/AST_Stuff/checkstyle-9.3-all.jar"
        # cmd = "java -jar " +jar_path + " -t " + directory +"/ExampleCode.java"+">"+directory+"/AST.txt"
        # os.system(cmd)
        # print("done1")
        # os.system("py SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py>SootScripts/out/artifacts/AST_Stuff/AST.txt")

def make_graph(directory, graph):
    graph_path = f'Graphviz/DotFiles/{graph}.txt'
    dir_name = directory.split('/')[0]
    output_file = f'Graphviz/GraphFilesPNG/{dir_name}-{graph}.png'
    cmd = ['dot', '-Tpng', graph_path, '-o', output_file]
    run(cmd)

run_analysis(dir, graph, cls)
make_graph(dir, graph)