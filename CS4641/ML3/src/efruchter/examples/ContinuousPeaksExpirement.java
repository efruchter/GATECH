package efruchter.examples;

import java.text.DecimalFormat;
import java.util.Arrays;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.Instance;
import util.linalg.Vector;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import efruchter.Expirements;

/**
 * Continuous Peaks
 */
public class ContinuousPeaksExpirement {

	private static final int N = 60;
	private static final int T = N / 10;

	private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[4];
	private static String[] oaNames = { "RHC", "SA", "GA", "MIMIC" };
	private static String results = "";

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static void run() {
		int[] ranges = new int[N];
		Arrays.fill(ranges, 2);
		EvaluationFunction ef = new PeaksEvaluationFunction(T);
		Distribution odd = new DiscreteUniformDistribution(ranges);
		NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
		MutationFunction mf = new DiscreteChangeOneMutation(ranges);
		CrossoverFunction cf = new SingleCrossOver();
		Distribution df = new DiscreteDependencyTree(.1, ranges);

		HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
		GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
		ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

		oa[0] = new RandomizedHillClimbing(hcp);
		oa[1] = new SimulatedAnnealing(1E12, Expirements.alpha, hcp);
		oa[2] = new StandardGeneticAlgorithm(Expirements.popSize, Expirements.toMate, Expirements.toMutate, gap);
		oa[3] = new MIMIC(Expirements.samples, Expirements.toKeep, pop);
		
		if (Expirements.onlyAlgo == -1) {
			for (int i = 1; i < oa.length; i++)
				train(oa[i], ef, oaNames[i]);
		} else {
			train(oa[Expirements.onlyAlgo], ef, oaNames[Expirements.onlyAlgo]);
		}

		System.out.println(results);
	}

	private static void train(OptimizationAlgorithm oa, EvaluationFunction ef, String oaName) {
		System.out.println("\nTraining " + oaName);

		double optimal = 0.0, start = System.nanoTime(), stamp = 0, end, trainingTime, temp;
		for (int i = 0; i < Expirements.trainingIterations; i++) {
			oa.train();
			temp = ef.value(oa.getOptimal());
			System.out.println(temp);

			if (optimal < temp) {
				optimal = temp;
				stamp = System.nanoTime();
			}
		}
		end = System.nanoTime();
		trainingTime = end - start;
		trainingTime /= Math.pow(10, 9);
		stamp -= start;
		stamp /= Math.pow(10, 9);

		results += "\n\nResults for " + oaName + ":\nTraining time: " + df.format(trainingTime) + " seconds."
				+ "\nOptimal instance found after " + stamp + " seconds.\nFinal optimal solution found: " + optimal;
	}

	private static class PeaksEvaluationFunction implements EvaluationFunction {
		private int t;

		public PeaksEvaluationFunction(int t) {
			this.t = t;
		}

		public double value(Instance d) {
			Vector data = d.getData();

			int max0 = 0, count = 0;
			for (int i = 0; i < data.size(); i++)
				if (data.get(i) == 0)
					count++;
				else if (count > max0) {
					max0 = count;
					count = 0;
				}

			count = 0;
			int max1 = 0;
			for (int i = 0; i < data.size(); i++)
				if (data.get(i) == 1)
					count++;
				else if (count > max1) {
					max1 = count;
					count = 0;
				}

			int r = 0;
			if (max1 > t && max0 > t)
				r = data.size();

			return Math.max(max1, max0) + r;
		}
	}
}
