package efruchter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Scanner;

import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;
import efruchter.examples.ContinuousPeaksExpirement;
import efruchter.examples.CountOnesExpirement;
import efruchter.examples.TSMExpirement;
import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic
 * algorithm to find optimal weights to a neural network that is classifying
 * irises as one a given type or not.
 * 
 * @author Eric Fruchter
 */
public class Expirements {

	// IRIS
	private final static int NUM_ATTRIBUTES = 4;
	private final static int NUM_INSTANCES = 150;
	private static String TARGET_CLASS = "Iris-versicolor";
	private final static String DATA_FILE = "iris.txt";

	public static int inputLayer = NUM_ATTRIBUTES, hiddenLayer = 10, outputLayer = 1, trainingIterations = 10000;
	private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

	private static ErrorMeasure measure = new SumOfSquaresError();

	private static Instance[] instances;
	private static DataSet set;

	private static BackPropagationNetwork networks;
	private static NeuralNetworkOptimizationProblem nnop;

	private static OptimizationAlgorithm oa;
	private static String results = "";

	private static DecimalFormat df = new DecimalFormat("0.000");

	public static double alpha = .95;
	public static int popSize = 200, toMate = 100, toMutate = 10, samples = 200, toKeep = 20;
	
	public static String algorithm = "";
	public static boolean optExpirement = false;
	public static String problem = "tsm";
	public static int onlyAlgo  = -1;
	
	public static void main(String[] args) {

		for (String s : args)
			System.out.print(s + " ");
		System.out.println();

		/*
		 * l: layers i: iterations t: target class algo: rhc, sa, ga alpha:
		 * cooling alpha
		 */

		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.equalsIgnoreCase("-l")) {
				hiddenLayer = Integer.parseInt(args[i + 1]);
				System.out.println("Hidden Layers: " + hiddenLayer);
			} else if (s.equalsIgnoreCase("-i")) {
				trainingIterations = Integer.parseInt(args[i + 1]);
				System.out.println("Iterations: " + trainingIterations);
			} else if (s.equalsIgnoreCase("-t")) {
				TARGET_CLASS = args[i + 1];
			} else if (s.equalsIgnoreCase("-algo")) {
				algorithm = args[i + 1];
			} else if (s.equalsIgnoreCase("-alpha")) {
				alpha = Double.parseDouble(args[i + 1]);
				System.out.println("Cooling alpha: " + alpha);
			} else if (s.equalsIgnoreCase("-popsize")) {
				popSize = Integer.parseInt(args[i + 1]);
				System.out.println("Population Size: " + popSize);
			} else if (s.equalsIgnoreCase("-tomate")) {
				toMate = Integer.parseInt(args[i + 1]);
				System.out.println("toMate: " + toMate);
			} else if (s.equalsIgnoreCase("-tomutate")) {
				toMutate = Integer.parseInt(args[i + 1]);
				System.out.println("toMutate: " + toMutate);
			} else if (s.equalsIgnoreCase("-opt")) {
				optExpirement = true;
				System.out.println("Optimization expirement");
			} else if (s.equalsIgnoreCase("-problem")) {
				problem = args[i + 1];
				System.out.println("Problem set to: " + problem);
			} else if (s.equalsIgnoreCase("-samples")) {
				samples = Integer.parseInt(args[i + 1]);
				System.out.println("MIMIC samples: " + samples);
			} else if (s.equalsIgnoreCase("-tokeep")) {
				toKeep = Integer.parseInt(args[i + 1]);
				System.out.println("MIMIC toKeep: " + toKeep);
			}
		}
		
		if (algorithm.equalsIgnoreCase("hc")) {
			onlyAlgo = 0;
		} else if (algorithm.equalsIgnoreCase("sa")) {
			onlyAlgo = 1;
		} else if (algorithm.equalsIgnoreCase("ga")) {
			onlyAlgo = 2;
		}  else if (algorithm.equalsIgnoreCase("mimic")) {
			onlyAlgo = 3;
		}

		if (optExpirement) {
			if (problem.equalsIgnoreCase("tsp")) {
				TSMExpirement.run();
			} else if(problem.equalsIgnoreCase("co")) {
				CountOnesExpirement.run();
			} else if(problem.equalsIgnoreCase("cp")) {
				ContinuousPeaksExpirement.run();
			} else {
				System.out.println("An incorrect opt has been chosen!");
				System.exit(1);
			}
		} else {
			instances = initializeInstances();
			set = new DataSet(instances);

			networks = factory.createClassificationNetwork(new int[] { inputLayer, hiddenLayer, outputLayer });
			nnop = new NeuralNetworkOptimizationProblem(set, networks, measure);

			if (algorithm.equals("rhc")) {
				oa = new RandomizedHillClimbing(nnop);
			} else if (algorithm.equals("sa")) {
				oa = new SimulatedAnnealing(1E11, alpha, nnop);
			} else if (algorithm.equals("ga")) {
				oa = new StandardGeneticAlgorithm((int) popSize, (int) toMate, (int) toMutate, nnop);
			} else {
				System.out.println("Valid algorithm not specified!");
				System.exit(1);
			}

			// for(int i = 0; i < oa.length; i++) {
			int i = 0;
			double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
			train(oa, networks, algorithm.toUpperCase()); // trainer.train();
			end = System.nanoTime();
			trainingTime = end - start;
			trainingTime /= Math.pow(10, 9);

			Instance optimalInstance = oa.getOptimal();
			networks.setWeights(optimalInstance.getData());

			double predicted, actual;
			start = System.nanoTime();
			for (int j = 0; j < instances.length; j++) {
				networks.setInputValues(instances[j].getData());
				networks.run();

				predicted = Double.parseDouble(instances[j].getLabel().toString());
				actual = Double.parseDouble(networks.getOutputValues().toString());

				double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
			}
			end = System.nanoTime();
			testingTime = end - start;
			testingTime /= Math.pow(10, 9);

			results += "\nResults for " + algorithm.toUpperCase() + "\nTarget: " + TARGET_CLASS + ": \nCorrectly classified " + correct
					+ " instances." + "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
					+ df.format(correct / (correct + incorrect) * 100) + "%\nTraining time: " + df.format(trainingTime)
					+ " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";

			System.out.println(results);
		}
	}

	private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
		System.out.println("\nError results for " + oaName + "\nTarget: " + TARGET_CLASS + "\n---------------------------");

		for (int i = 0; i < trainingIterations; i++) {
			oa.train();

			double error = 0;
			for (int j = 0; j < instances.length; j++) {
				network.setInputValues(instances[j].getData());
				network.run();

				Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
				example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
				error += measure.value(output, example);
			}

			System.out.println(df.format(error));
		}
	}

	private static Instance[] initializeInstances() {

		double[][][] attributes = new double[NUM_INSTANCES][][];

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(DATA_FILE)));

			for (int i = 0; i < attributes.length; i++) {
				Scanner scan = new Scanner(br.readLine());
				scan.useDelimiter(",");

				attributes[i] = new double[2][];
				attributes[i][0] = new double[NUM_ATTRIBUTES];
				attributes[i][1] = new double[1];

				for (int j = 0; j < NUM_ATTRIBUTES; j++) {
					attributes[i][0][j] = Double.parseDouble(scan.next());
				}

				attributes[i][1][0] = scan.next().trim().equalsIgnoreCase(TARGET_CLASS) ? 1 : 0;
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Instance[] instances = new Instance[attributes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = new Instance(attributes[i][0]);
			instances[i].setLabel(new Instance(Math.floor(attributes[i][1][0])));
		}

		return instances;
	}
}
