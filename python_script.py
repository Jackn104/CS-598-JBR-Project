# #!/usr/bin/python

# import sys, getopt

# def main(argv):
#    filepath = ''
#    classfile = ''
#    try:
#       opts, args = getopt.getopt(argv,"hi:o:",["filep=","classf="])
#    except getopt.GetoptError:
#       print('Error:\npython_script.py -p <filepath> -f <classfile>')
#       sys.exit(2)
#    for opt, arg in opts:
#       if opt == '-h': #help and prints parameters 
#          print('python_script.py -p <filepath> -f <classfile>')
#          sys.exit()
#       elif opt in ("-p", "--filep"):
#          filepath = arg
#       elif opt in ("-f", "--classf"):
#          classfile = arg
#    print('path is "', filepath)
#    print('class file is "', classfile)

# if __name__ == "__main__":
#    main(sys.argv[1:])

import sys
import os
from argparse import ArgumentParser
from pathlib import Path
import subprocess
from subprocess import STDOUT, PIPE

parser = ArgumentParser()
requiredArgs = parser.add_argument_group('required named arguments')
requiredArgs.add_argument("-f", "--file", dest="filename",
                    help="choose file to run script on", required=True)
requiredArgs.add_argument("-p", "--path", dest="path",
                    help="set path to file to run on", required=True)
requiredArgs.add_argument("-g", "--graph", dest="graph",
                    help="set type of graph", required=True)

# args = parser.parse_args()
args = vars(parser.parse_args())
# print(args)
# print(args['filename'])

full_path = args['path'] + '/' + args['filename']
print("Full File Path: " + full_path)    

# path = args['path']
# file_name = args['filename']

try: 
    #Attempt to access file location and ensure that file exists there: 
    if not (Path(args['path']).is_dir()):
        raise Exception("path doesn't exist")

    if not (Path(full_path).is_file()):
        raise Exception("file doesn't exist")

    # if (args["filename"][-6:] != '.class'):
    #     raise Exception("file is not of java class")

    if args['graph'] not in ["AST", "CG", "CFG"]:
         raise Exception("graph type is not in list of possible options")

except Exception as error: 
    print("Error:\t" + str(error))

#at this point it is assumed that the basic checks have passed and that nothing is wrong or missing
#replace the body scratch work with running soot script
def compile_java(java_file):
    subprocess.check_call(['javac', java_file])

def execute_java(java_file, stdin):
# def execute_java(java_file):
    java_class, ext = os.path.splitext(java_file)
    cmd = ['java', java_class]
    proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    stdout,stderr = proc.communicate(stdin) #wanting to pass in info to java file that is using Scanner
    print ('This was "' + stdout.decode("utf-8") + '"')

compile_java(args['filename']) #may not need to compile if we're passing in .class files
execute_java(args['filename'], "")

#call graph viz here and generate graph

#may not write the information to the right place 
os.system('cmd /c "dot -Tpng DotFiles/'+ args['filename'] +' -o GraphFilesPNG/'+ args['filename'] +'.png"') #the filename that was specified in the original filed by jackson may not refer to the same filename as listed here
print("dot -Tpng DotFiles/"+ args['filename'] +" -o GraphFilesPNG/"+ args['filename'] +".png")