import os

#Here we pass the file name from the original parameter
file_name = 'CG.txt'



#cmd /c invokes the command line
#dot -Tpng is the command for converting a file into a png graph
#directory as necessary
# -o is for the output 
#you might also need to adjust the path as well 
os.system('cmd /c "dot -Tpng DotFiles/'+ file_name +' -o GraphFilesPNG/'+ file_name +'.png"')
print("dot -Tpng DotFiles/"+ file_name +" -o GraphFilesPNG/"+ file_name +".png")


#We can do the same thing in the shell as well if needed 