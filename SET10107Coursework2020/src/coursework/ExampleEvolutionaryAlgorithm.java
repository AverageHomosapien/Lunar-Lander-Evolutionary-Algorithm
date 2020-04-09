package coursework;

import java.util.ArrayList;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = select(); 
			Individual parent2 = select();

			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = reproduce(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection -- Tournament Selection
	 * Parameters: Tournament Size
	 */
	private Individual select() {
		ArrayList<Individual> selection = new ArrayList<>();

		// Randomly selecting population members
		for (int i = 0; i < Parameters.selectionSize; i++){
			selection.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		Individual parent = selection.get(0);

		// Finds best fitness in population
		for (int i = 1; i < selection.size(); i++){
			if (selection.get(i).fitness > parent.fitness){
				parent = selection.get(i);
			}
		}
		return parent.copy();
	}
	                                                                                  
	/**
	 * Crossover / Reproduction
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();

		// If parent 1 and parent 2 are the same, selects a new parent from the population
		while (parent1 == parent2){
			parent2 = select();
		}
		children.add(parent1.copy());
		children.add(parent2.copy());

		double temp;
		int rand = Parameters.random.nextInt(parent1.chromosome.length + 1);
		System.out.println(rand);

		// 1pt crossover
		for (int i = 0; i < rand; i++){
			temp = children.get(0).chromosome[i];
			children.get(0).chromosome[i] = children.get(1).chromosome[i];
			children.get(1).chromosome[i] = temp;
		}

		return children;
	} 
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {
		for(Individual individual : individuals){
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate){
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	/*public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}*/
	public double activationFunction(double x){
		if (x < 0){
			return 0;
		}else{
			return x;
		}
	}
}
