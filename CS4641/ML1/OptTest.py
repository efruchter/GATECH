from randomOptimization import *
import random

def EVAL(n):
	return -((1010- n.x)**2 + (20200 - n.y)**2)

class Node:
	def __init__(self, x=0, y=0):
		self.x = x
		self.y = y	

def getStartNode():
	return Node()

#GET THE NEIGHBORING NODES
def getNeighbors(c):
	n = []
	for x in range(-1, 2):
		for y in range(-1, 2):
			n +=  [Node(c.x + x, c.y + y)]
	random.shuffle(n)
	return n

result = hillClimbing(getStartNode, getNeighbors, EVAL)

print "Answer found in", result.generations, "generations:"
print result.node.x, result.node.y

result = simulatedAnnealing(getStartNode, getNeighbors, EVAL, maxIterStop(50000), kirkpatrick_cooling(1, .2))

print "Answer found in", result.generations, "generations:"
print result.node.x, result.node.y
