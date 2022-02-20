import os
import subprocess
from subprocess import run

#Everytime there is ExampleCode it can be replaced with the parameter to fit the file being analyzed

os.system("java -jar SootScripts/out/artifacts/AST_Stuff/checkstyle-9.3-all.jar -t SootScripts/out/artifacts/AST_Stuff/ExampleCode.java>SootScripts/out/artifacts/AST_Stuff/AST.txt")


os.system("py SootScripts/out/artifacts/AST_Stuff/check_to_graphViz.py>SootScripts/out/artifacts/AST_Stuff/ExampleCodeDot.txt")

run('dot -Tpng SootScripts/out/artifacts/AST_Stuff/ExampleCodeDot.txt -o GraphViz/GraphFilesPNG/ExampleCodeAST.png')