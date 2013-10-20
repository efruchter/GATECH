from osm2networkx import *
import networkx
import networkx as nx
import random
import math
import sys
import time
from networkx.algorithms.shortest_paths.generic import has_path

"""
Searching a street network using Breadth First Search

REQUIREMENTS:

  networkx: http://networkx.github.io/

REFERENCES:

  [1] Russel, Norvig: "Artificial Intelligene A Modern Approach", 3rd ed, Prentice Hall, 2010

ASSIGNMENT:

  Extend this program to Tridirectional Search.
  Find a path between three starting points.

author: Daniel Kohlsdorf and Thad Starner
"""

"""
The state space in our problem hold:

   1) A node in the street graph
   2) A parent node

"""
class State:

    def __init__(self, node, parent, cost = 0):
        self.node   = node
        self.parent = parent
        self.cost = cost

    def __eq__(self, other):
        if isinstance(other, State):
            return self.node['data'].id == other.node['data'].id
        return NotImplemented

    def __cmp__(self, other):
        return cmp(self.cost, other.cost)

"""
Implements BFS on our GPS data

see [1] Figure 3.11
"""
def bfs_default(graph, start, goal):

    if start == goal:
        return {'cost' : 0, 'expanded' : 0}

    frontier = [start]
    explored = []
    num_explored = 0

    while len(frontier) > 0:

        node = frontier.pop(0)

        #print node.cost

        explored.append(node)
        for edge in networkx.edges(graph, node.node['data'].id):
            child = State(graph.node[edge[1]], node, node.cost + nodeDist(node.node, graph.node[edge[1]])) 
            if child not in explored and child not in frontier:
                if child == goal:
                    return {'cost' : child.cost, 'expanded' : num_explored}
                else:
                    frontier.append(child)
                num_explored = num_explored + 1

    return None

def bfs_ucs(graph, start, goal):
    
    if start == goal:
        return {'cost' : 0, 'expanded' : 0}

    frontier = [start]
    explored = []
    num_explored = 0

    while len(frontier) > 0:

        node = popClosest(frontier)

        #print node.cost

        explored.append(node)
        if node == goal:
            return {'cost' : node.cost, 'expanded' : num_explored}
        for edge in networkx.edges(graph, node.node['data'].id):
            child = State(graph.node[edge[1]], node, node.cost + nodeDist(node.node, graph.node[edge[1]])) 
            if child not in explored:
                if child not in frontier:
                    frontier.append(child)
                    num_explored += 1
                else:
                    swapIfBetter(frontier, child)

    return None

def bfs_bd_ucs(graph, start, goal):

    if start == goal:
        return {'cost' : 0, 'expanded' : 0}

    frontier0 = [start]
    frontier1 = [goal]
    explored0 = []
    explored1 = []
    num_explored = 0

    commonNodes = []

    while len(frontier0) + len(frontier1) > 0:

        linked01 = len(commonNodes) > 0

        ## 0 front
        if frontier0:
            node = popClosest(frontier0)
            explored0.append(node)
            if node in explored0 and node in explored1:
                commonNodes += [node]
            for edge in networkx.edges(graph, node.node['data'].id):
                child = State(graph.node[edge[1]], node, node.cost + nodeDist(node.node, graph.node[edge[1]])) 
                if child not in explored0:
                    if child not in frontier0:
                        if not linked01:
                            frontier0.append(child)
                            num_explored = num_explored + 1
                    else:
                        swapIfBetter(frontier0, child)
                        
        ## 0 front
        if frontier1:
            node = popClosest(frontier1)
            explored1.append(node)
            if node in explored0 and node in explored1:
                commonNodes += [node]
            for edge in networkx.edges(graph, node.node['data'].id):
                child = State(graph.node[edge[1]], node, node.cost + nodeDist(node.node, graph.node[edge[1]])) 
                if child not in explored1:
                    if child not in frontier1:
                        if not linked01:
                            frontier1.append(child)
                            num_explored = num_explored + 1
                    else:
                        swapIfBetter(frontier1, child)

    if commonNodes:
        bestNode = commonNodes[0]
        for node in commonNodes:
            if commonCost(node, explored0, explored1) < commonCost(bestNode, explored0, explored1):
                bestNode = node
        return {'cost' : commonCost(bestNode, explored0, explored1), 'expanded' : num_explored}

    print "No path found, explored: ", num_explored

    return None

def bfs_tri_ucs(graph, s1, s2, s3):

    frontier0 = [s1]
    frontier1 = [s2]
    frontier2 = [s3]


    explored0 = []
    explored1 = []
    explored2 = []

    num_explored = 0

    commonNodes01 = []
    commonNodes12 = []
    commonNodes20 = []

    while len(frontier0) + len(frontier1) + len(frontier2) > 0:

        for i in range(0,3):
            frontier = None
            explored = None
            e1 = None
            e2 = None
            c1 = None
            c2 = None
            l1 = False
            l2 = False

            if i == 0:
                frontier = frontier0
                explored = explored0
                e1 = explored1
                e2 = explored2
                c1 = commonNodes01
                c2 = commonNodes20
                l1 = len(commonNodes01) > 0
                l2 = len(commonNodes20) > 0
            elif i == 1:
                frontier = frontier1
                explored = explored1
                e1 = explored0
                e2 = explored2
                c1 = commonNodes01
                c2 = commonNodes12
                l1 = len(commonNodes01) > 0
                l2 = len(commonNodes12) > 0
            elif i == 2:
                frontier = frontier2
                explored = explored2
                e1 = explored0
                e2 = explored1
                c1 = commonNodes20
                c2 = commonNodes12
                l1 = len(commonNodes20) > 0
                l2 = len(commonNodes12) > 0

            ## 0 front
            if frontier:

                node = popClosest(frontier)
                explored.append(node)
                
                if node in explored and node in e1:
                    c1 += [node]
                if node in explored and node in e2:
                    c2 += [node]

                for edge in networkx.edges(graph, node.node['data'].id):
                    child = State(graph.node[edge[1]], node, node.cost + nodeDist(node.node, graph.node[edge[1]])) 
                    
                    if child not in explored:
                        if child not in frontier:
                            if not l1 or not l2:
                                frontier.append(child)
                                num_explored += 1
                        else:
                            swapIfBetter(frontier, child)


    if commonNodes01 and commonNodes12 and commonNodes20:
        commons = [commonNodes01, commonNodes12, commonNodes20]
        bestNode = [commonNodes01[0], commonNodes12[0], commonNodes20[0]]
        cost = [{}, {}, {}]
        for i in range(0,3):
            e1 = None
            e2 = None
            if i == 0:
                e1 = explored0
                e2 = explored1
            elif i == 1:
                e1 = explored1
                e2 = explored2
            elif i == 2:
                e1 = explored2
                e2 = explored0
            for node in commons[i]:
                if commonCost(node, e1, e2) < commonCost(bestNode[i], e1, e2):
                    bestNode[i] = node
                cost[i] = {'cost' : commonCost(bestNode[i], e1, e2), 'expanded' : num_explored}

        # Now we have the three paths. Drop the worst one
        cost = [cost[0]['cost'], cost[1]['cost'], cost[2]['cost']]
        cost.remove(max(cost))
        cost = sum(cost)
        return {'cost' : cost, 'expanded' : num_explored}

    print "No path found, explored: ", num_explored

    return None

def commonCost(link, explored0, explored1):
    return explored0[explored0.index(link)].cost + explored1[explored1.index(link)].cost

def popClosest(frontier):
    if len(frontier) == 0:
        return None
    else:
        bestNode = frontier[0]
        for node in frontier:
            if node.cost < bestNode.cost:
                bestNode = node
        frontier.remove(bestNode)
        return bestNode

def swapIfBetter(frontier, node):
    for anode in frontier:
        if anode == node and node.cost < anode.cost:
            anode.cost = node.cost
            anode.parent = node.parent
            break

"""
Backtrack and output your solution
"""
def backtrack(state, graph):
    if state.parent != None:
        print "Node: ", state.node['data'].id
        if len(state.node['data'].tags) > 0:
            for key in state.node['data'].tags.keys():
                print "       N: ", key, " ", state.node['data'].tags[key]
              
        for edge in networkx.edges(graph, state.node['data'].id):
            if len(graph.node[edge[1]]['data'].tags) > 0:
                for key in graph.node[edge[1]]['data'].tags:
                    print "       E: ", graph.node[edge[1]]['data'].tags[key]
        backtrack(state.parent, graph)

"""
Get the distance between to networkx nodes
"""
def nodeDist(node1, node2):
    lat1 = node1['data'].lat
    lon1 = node1['data'].lon
    lat2 = node2['data'].lat
    lon2 = node2['data'].lon
    return math.sqrt((lat1 - lat2)**2 + (lon1 - lon2)**2)

"""
The setup
"""

print "\n\n----- 6601 Grad AI: Seaching ATLANTA ------\n\n"
graph = read_osm('atlanta.osm', True)

num_trials = 5
trial = 0

while trial < num_trials:

    num1 = random.randint(0, len(graph.nodes()))
    num2 = random.randint(0, len(graph.nodes()))
    num3 = random.randint(0, len(graph.nodes()))
    
    if len(sys.argv) > 3:
        id1 = sys.argv[1]
        id2 = sys.argv[2]
        id3 = sys.argv[3]
        for node in graph.nodes():
            if graph.node[node]['data'].id == id1:
                num1 = graph.nodes().index(node)
            if graph.node[node]['data'].id == id2:
                num2 = graph.nodes().index(node)
            if graph.node[node]['data'].id == id3:
                num3 = graph.nodes().index(node)
    
    n1 = graph.node[graph.nodes()[num1]]
    n2  = graph.node[graph.nodes()[num2]]
    n3  = graph.node[graph.nodes()[num3]]

    """
    print "NUMBER OF NODES: ", len(graph.nodes())
    print "NUMBER OF EDGES: ", len(graph.edges())
    print "1:               ", n1['data'].id
    print "2:               ", n2['data'].id
    print "3:               ", n3['data'].id
    """

    if not (has_path(graph, n1['data'].id, n2['data'].id) and has_path(graph, n2['data'].id, n3['data'].id)):
        continue

    print "\n\n"
    print "UNIFORM_COST:"
    results1 = bfs_ucs(graph, State(n1, None), State(n2, None))
    results2 = bfs_ucs(graph, State(n2, None), State(n3, None))
    results3 = bfs_ucs(graph, State(n3, None), State(n1, None))

    cost = [results1['cost'], results2['cost'], results3['cost']]
    cost.remove(max(cost))
    cost = sum(cost)
    expanded = sum([results1['expanded'], results2['expanded'], results3['expanded']])
    print "Total Path Length: " + str(cost) + ", Expanded: " + str(expanded)
    
    with open('output_uc.txt', 'a') as the_file:
        the_file.write(str(expanded) + "\n")

    ##BD

    print "\n\n"
    print "BI_UNIFORM_COST:"
    results1 = bfs_bd_ucs(graph, State(n1, None), State(n2, None))
    results2 = bfs_bd_ucs(graph, State(n2, None), State(n3, None))
    results3 = bfs_bd_ucs(graph, State(n3, None), State(n1, None))
    
    cost = [results1['cost'], results2['cost'], results3['cost']]
    cost.remove(max(cost))
    cost = sum(cost)
    expanded = sum([results1['expanded'], results2['expanded'], results3['expanded']])
    print "Total Path Length: " + str(cost) + ", Expanded: " + str(expanded)
    
    with open('output_bi.txt', 'a') as the_file:
        the_file.write(str(expanded) + "\n")
    
    ##TD

    print "\n\n"
    print "TRI_UNIFORM_COST:"
    results = bfs_tri_ucs(graph, State(n1, None), State(n2, None), State(n3, None))
    
    cost = results['cost']
    expanded = results['expanded']
    print "Total Path Length: " + str(cost) + ", Expanded: " + str(expanded)

    with open('output_tri.txt', 'a') as the_file:
        the_file.write(str(expanded) + "\n")

    print "\n\n"
    print "Complete: " + str(round(trial * 100.0 / num_trials)) + "%" + "\n\n"

    trial += 1
