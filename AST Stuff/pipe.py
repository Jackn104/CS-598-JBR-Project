import os
import subprocess
from subprocess import run

#Everytime there is ExampleCode it can be replaced with the parameter to fit the file being analyzed

os.system("java -jar checkstyle-9.3-all.jar -t ExampleCode.java>ExampleCodeAST.txt")

os.system("py check_to_graphViz.py>ExampleCodeDot.txt")

run('dot -Tpng ExampleCodeDot.txt -o ExampleCodeAST.png')