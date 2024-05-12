#code modified from tutorial at: https://www.geeksforgeeks.org/bar-plot-in-matplotlib/
import numpy as np 
import matplotlib.pyplot as plt 
 
# set width of bar 
barWidth = 0.2
fig, ax = plt.subplots(2, figsize =(12, 8)) 
 
# set height of bar 
matrixtwotwo = [1123214, 1433933, 3546823, 3857542]
matrixtwofour = [948133, 1258852, 3546823, 3857542]
matrixfourtwo = [1257990, 1568709, 3546823, 3857542]
matrixfourfour = [781972, 1092691, 3546823, 3857542]

# set height of bar 
selectiontwotwo = [24560248, 34095399, 58224623, 67759774]
selectiontwofour = [16799998, 26335149, 58224623, 67759774]
selectionfourtwo = [26167091, 35702242, 58224623, 67759774]
selectionfourfour = [17508789, 27043940, 58224623, 67759774]



# Set position of bar on X axis 
br1 = np.arange(len(matrixtwotwo))
br2 = [x + barWidth for x in br1] 
br3 = [x + barWidth for x in br2] 
br4 = [x + barWidth for x in br3]

# Make the plot
ax[0].bar(br1, matrixtwotwo, color ='r', width = barWidth, 
        edgecolor ='black', label ='Associativity: 2\nLine Length: 2') 
ax[0].bar(br2, matrixtwofour, color ='g', width = barWidth, 
        edgecolor ='black', label ='Associativity: 2\nLine Length: 4') 
ax[0].bar(br3, matrixfourtwo, color ='b', width = barWidth, 
        edgecolor ='black', label ='Associativity: 4\nLine Length: 2') 
ax[0].bar(br4, matrixfourfour, color ='y', width = barWidth, 
        edgecolor ='black', label ='Associativity: 4\nLine Length: 4')
ax[1].bar(br1, selectiontwotwo, color ='r', width = barWidth, 
        edgecolor ='black', label ='Associativity: 2\nLine Length: 2') 
ax[1].bar(br2, selectiontwofour, color ='g', width = barWidth, 
        edgecolor ='black', label ='Associativity: 2\nLine Length: 4') 
ax[1].bar(br3, selectionfourtwo, color ='b', width = barWidth, 
        edgecolor ='black', label ='Associativity: 4\nLine Length: 2') 
ax[1].bar(br4, selectionfourfour, color ='y', width = barWidth, 
        edgecolor ='black', label ='Associativity: 4\nLine Length: 4') 
ax[0].sharex(ax[1])

ax[0].set_title("25 X 25 Matrix Multiplication")
ax[1].set_title("1000 Integer Selection Sort")

plt.ylabel('Clock Cycles', fontsize = 15) 
# Adding Xticks
plt.xticks([r + 1.5*barWidth for r in range(len(matrixtwotwo))], 
        ['Cache and Pipe', 'Cache, No Pipe', 'No Cache, Piped', 'No Cache, No Pipe'])

plt.legend()
plt.show() 