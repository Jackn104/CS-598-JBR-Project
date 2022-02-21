from argparse import ArgumentParser
from pathlib import Path
import pathlib
import os
import subprocess
from subprocess import run
import sys

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
dir = args['directory'].strip("/")
graph = args['graph']
cls = args['class'] if args['class'] else "N/A"
formatted_directory = ("/"+dir).replace('\\', '/')+"/"

try: 
    if not (os.access(str(pathlib.Path().resolve()) + formatted_directory, os.F_OK)):
        raise Exception("Path doesn't exist")

    if args['file'] != None: 
        extension = args['file'].split(".")[-1] 
        if (extension not in ["class", "java"]):
            raise Exception("File Type is wrong")

    if graph not in ["AST", "CG", "ICFG"]:
        raise Exception("Graph type is not in list of possible options")

except Exception as error: 
    print("Error:\t" + str(error))
    sys.exit()
 
def make_graph(directory, graph):
    graph_path = f'Graphviz/DotFiles/{graph}.txt'
    dir_name = directory.split('/')[0]
    output_file = f'Graphviz/GraphFilesPNG/{dir_name}-{graph}.png'
    cmd = ['dot', '-Tpng', graph_path, '-o', output_file]
    run(cmd)

def one_file_AST(formatted_directory, file, flag):
    outputFile = file.replace(".", "_") + "_AST_GRAPH.txt"
    dotFile = file.replace(".", "_") + "_DOT.txt"
    pngFile = file.replace(".", "_") + "_AST.png"
    
    os.system("java -jar SootScripts/out/artifacts/AST_Stuff/checkstyle-9.3-all.jar -t {}>{}".format(formatted_directory + file, outputFile))
    os.system("mv " + outputFile + " " + formatted_directory + "Graphviz/GraphFilesPNG/" + flag)
       
    os.system("python3 SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py -d {} -f {} > {}".format(formatted_directory + "Graphviz/GraphFilesPNG/" + flag, outputFile, formatted_directory + "Graphviz/GraphFilesPNG/" + flag + dotFile))

    os.system('dot -Tpng {} -o {}'.format(formatted_directory + "Graphviz/GraphFilesPNG/" + flag + dotFile, formatted_directory + "Graphviz/GraphFilesPNG/" + flag + pngFile))

    os.system('rm {}'.format(formatted_directory + "Graphviz/GraphFilesPNG/" + flag + outputFile))
    os.system('rm {}'.format(formatted_directory + "Graphviz/GraphFilesPNG/" + flag + dotFile))

    os.system('cp {} {}'.format(formatted_directory + "Graphviz/GraphFilesPNG/" + flag + pngFile, 'Graphviz/DotFiles/' + flag + pngFile))

def run_analysis(directory, graph, cls):
    if graph in ["CG", "ICFG"]:
        jar_path = f"SootScripts/out/artifacts/{graph}_jar/SootScripts.jar"
        cmd = ['java', '-jar', jar_path, directory, cls]
        run(cmd)
        make_graph(directory, graph)
    else:
        if (os.path.isdir(formatted_directory[1:] + "Graphviz/GraphFilesPNG/")):
            os.system("rm -rf " + formatted_directory[1:] + "Graphviz/GraphFilesPNG/")
            os.system("mkdir -p " + formatted_directory[1:] + "Graphviz/GraphFilesPNG/")
        else: 
            os.system("mkdir -p " + formatted_directory[1:] + "Graphviz/GraphFilesPNG/")

        if (args['file'] == None):
            for file in os.listdir(formatted_directory[1:]):
                if file.split(".")[-1] == "java":
                    os.system("mkdir " + formatted_directory[1:] + "Graphviz/GraphFilesPNG/" + file[:-5] + "AST")
                    one_file_AST(formatted_directory[1:], file, file[:-5]+"AST/")

        else:
            file = args['file']
            one_file_AST(formatted_directory[1:], file, "")
            
run_analysis(dir, graph, cls)
