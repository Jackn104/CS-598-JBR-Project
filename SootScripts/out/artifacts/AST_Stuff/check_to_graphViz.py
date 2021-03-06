from argparse import ArgumentParser
from pathlib import Path
import os
import subprocess
from subprocess import run

parser = ArgumentParser()
requiredArgs = parser.add_argument_group('required named arguments')
requiredArgs.add_argument("-d", "--dir", dest="directory",
                    help="the path to the directory to analyze", required=True)
requiredArgs.add_argument("-f", "--file", dest="file",
                    help="set file name", required=False)
args = vars(parser.parse_args())

f = open(args["directory"] + args["file"])
l = f.readlines()

def clean_lines(txt):     
    return txt.replace(" -> ",":")
        
all_text = ['digraph{ \n']

for k in range(6):
    i = k*4
    top = ''
    ind = []
    for line in l:
        try:
            if line[i] not in "`| ":
                if i-1 >0:
                    k=1
                else:
                    k=0
                new_line = clean_lines(line[i-k:(len(line)-1)])
                top = '"'+new_line+'" -> '
                ind.append(l.index(line))
            if line[i+2] in "-":
                new_line = clean_lines(line[3+i:(len(line)-1)]).strip()
                if "SEMI" not in top+new_line and "CURLY" not in top+new_line and "PAREN" not in top+new_line:
                    all_text.append(top +'"'+new_line + '"\n')
        except:
            all_text.append('\n', end ='')
    offset = 0
    for index in ind:
        l.pop(index-offset)
        offset+=1
    
all_text.append('\n}')

for line in all_text:
    print(line, end ='')

