package efruchter.examples;

import java.util.*;
import java.text.*;

import dist.*;
import efruchter.Expirements;
import opt.*;
import opt.example.*;
import opt.ga.*;
import opt.prob.*;
import shared.*;
import util.ABAGAILArrays;

/**
 * TS
 */
public class TSMExpirement {

	private static final int N = 50, maxNumIterations = 1000;

	private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[4];
	private static String[] oaNames = { "RHC", "SA", "GA", "MIMIC" };
	private static String results = "";

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void run() {
		
		Random random = new Random();
		double[][] points = new double[N][2];
		for (int i = 0; i < points.length; i++) {
			points[i][0] = random.nextDouble();
			points[i][1] = random.nextDouble();
		}
		// for rhc, sa, and ga we use a permutation based encoding
		TravelingSalesmanEvaluationFunction[] ef = { new TSMEvaluationFunction(points), new TSMEvaluationFunctionMIMIC(points) };

		Distribution odd = new DiscretePermutationDistribution(N);
		NeighborFunction nf = new SwapNeighbor();
		MutationFunction mf = new SwapMutation();
		CrossoverFunction cf = new TravelingSalesmanCrossOver(ef[0]);
		HillClimbingProblem hcp = new GenericHillClimbingProblem(ef[0], odd, nf);
		GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef[0], odd, mf, cf);

		oa[0] = new RandomizedHillClimbing(hcp);
		oa[1] = new SimulatedAnnealing(1E12, Expirements.alpha, hcp);
		oa[2] = new StandardGeneticAlgorithm(Expirements.popSize, Expirements.toMate, Expirements.toMutate, gap);

		int[] ranges = new int[N];
		Arrays.fill(ranges, N);
		odd = new DiscreteUniformDistribution(ranges);
		Distribution df = new DiscreteDependencyTree(.1, ranges);
		ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef[1], odd, df);

		oa[3] = new MIMIC(Expirements.samples, Expirements.toKeep, pop);

		if (Expirements.onlyAlgo == -1) {
			for (int i = 1; i < oa.length; i++)
				train(oa[i], ef[i / 3], oaNames[i]);
		} else {
			train(oa[Expirements.onlyAlgo], ef[Expirements.onlyAlgo / 3], oaNames[Expirements.onlyAlgo]);
		}

		System.out.println(results);

	}

	private static void train(OptimizationAlgorithm oa, TravelingSalesmanEvaluationFunction ef, String oaName) {
		System.out.println("\nTraining " + oaName);

		double optimal = 0.0, start = System.nanoTime(), stamp = 0, end, trainingTime, temp;
		for (int i = 0; i < maxNumIterations; i++) {
			oa.train();

			temp = ef.value(oa.getOptimal());
			System.out.println(temp);

			if (optimal < temp) {
				optimal = temp;
				stamp = System.nanoTime();
			}

			
			 if(optimal < temp) { stamp = System.nanoTime(); optimal = temp;
			 System.out.println("Iteration " + i + ": " + optimal); }
			 
		}
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);
		stamp -= start;
		stamp /= Math.pow(10, 9);

		results += "\n\nResults for " + oaName + ":\nTraining time: " + df.format(trainingTime) + " seconds."
				+ "\nOptimal instance found after " + stamp + " seconds.\nFinal optimal solution found: " + optimal;
	}

	private static class TSMEvaluationFunction extends TravelingSalesmanEvaluationFunction implements EvaluationFunction {
		double[][] distances;

		public TSMEvaluationFunction(double[][] points) {
			super(points);
			distances = new double[points.length][];
			for (int i = 0; i < points.length; i++) {
				distances[i] = new double[i];
				for (int j = 0; j < i; j++) {
					double[] a = points[i];
					double[] b = points[j];
					distances[i][j] = Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
				}
			}
		}

		public double getDistance(int i, int j) {
			if (i == j) {
				return 0;
			} else {
				int a = Math.max(i, j);
				int b = Math.min(i, j);
				return distances[a][b];
			}
		}

		public double value(Instance d) {
			double distance = 0;
			for (int i = 0; i < d.size() - 1; i++) {
				distance += getDistance(d.getDiscrete(i), d.getDiscrete(i + 1));
			}
			distance += getDistance(d.getDiscrete(d.size() - 1), d.getDiscrete(0));
			return 1 / distance;
		}

	}

	private static class TSMEvaluationFunctionMIMIC extends TravelingSalesmanEvaluationFunction {
		public TSMEvaluationFunctionMIMIC(double[][] points) {
			super(points);
		}

		public double value(Instance d) {
			double[] data = new double[d.size()];
			for (int i = 0; i < data.length; i++) {
				data[i] = d.getContinuous(i);
			}
			int[] order = ABAGAILArrays.indices(d.size());
			ABAGAILArrays.quicksort(data, order);
			double distance = 0;
			for (int i = 0; i < order.length - 1; i++) {
				distance += getDistance(order[i], order[i + 1]);
			}
			distance += getDistance(order[order.length - 1], order[0]);
			return 1 / distance;
		}
	}
}
