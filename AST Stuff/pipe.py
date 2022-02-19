import os
import subprocess
from subprocess import run

os.system("java -jar checkstyle-9.3-all.jar -t ExampleCode.java>ExampleCodeAST.txt")

os.system("py check_to_graphViz.py>ExampleCodeDot.txt")

run('dot -Tpng ExampleCodeDot.txt -o ExampleCodeAST.png')