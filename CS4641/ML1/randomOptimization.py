from random import random
from math import *

class Result:
	def __init__(self, node=0, generations=0):
		self.node = node
		self.generations = generations

def hillClimbing(getStartNode, getNeighbors, eval, verbose=False):

	#LOOP PREP
	generation = 0
	currentNode = getStartNode()

	while True:
		generation += 1

		#PRINT DIAGNOSTIC INFO
		if verbose:
			print "Generation:", generation, "Value:", eval(currentNode)

		#COLLECT NEIGHBORS
		L = getNeighbors(currentNode)
		nextEval = float("-infinity")
		nextNode = None

		#FIND BEST NEIGHBOR
		for n in L:
			nVal = eval(n)
			if nVal > nextEval:
				nextNode = n
				nextEval = nVal

		#CONTINUE IF NECESSARY
		if nextEval <= eval(currentNode):
			return Result(currentNode, generation)
		else:
			currentNode = nextNode

def kirkpatrick_cooling(start_temp, alpha):
	T=start_temp
	while True:
		yield T
		T=alpha*T

def maxIterStop(maxIters):
	def annealStopFunction(temp, iterations, bestValue):
		if iterations >= maxIters:
			return True
		else:
			return False
	return annealStopFunction

def simulatedAnnealing(getStartNode, getNeighbors, eval, stopFunction, cooling):

	#LOOP PREP
	currentNode = getStartNode()
	bestSolution = {"node" : None, "score" : float("-infinity")}
	iterations = 0

	for temp in cooling:
		iterations += 1

		#Stop if satisfied
		if stopFunction(temp, iterations, bestSolution["score"]):
			return Result(bestSolution["node"], iterations)

		#COLLECT NEIGHBORS
		L = getNeighbors(currentNode)
		nextVal = eval(currentNode)
		
		#STORE BEST
		if nextVal > bestSolution["score"]:
			bestSolution["node"] = currentNode
			bestSolution["score"] = nextVal
		
		#FIND BEST NEIGHBOR
		for n in L:
			nVal = eval(n)
			
			#Pick something for sure if it is better
			#or pick worse neighbor if we have enough energy
			if nVal > nextVal or (temp != 0 and (exp(-abs(nVal-nextVal)/temp) < random())):
				currentNode = n
				nextVal = nVal
				break
