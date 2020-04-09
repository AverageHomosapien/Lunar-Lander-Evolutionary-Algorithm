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
			// Select 2 Individuals from the current population
			Individual parent1 = select(true);
			Individual parent2 = select(false);
			Individual parent3 = select(false); // Started using elite + elite and non-elite + non-elite

			// Generate a child by crossover. Not Implemented
			ArrayList<Individual> children = reproduce(parent1, parent2, parent3);

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
	private Individual select(boolean elite) {
		ArrayList<Individual> selection = new ArrayList<>();

		int temp;
		// Randomly selecting population members
		if (elite){
			temp = Parameters.eliteSelectionSize;
		}else{
			temp = Parameters.selectionSize;
		}
		for (int i = 0; i < temp; i++){
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
	 * Parent 1 and 2 (elite/non-elite) have 2-point crossover
	 * Parent 1 and 3 (elite/non-elite) have arithmetic crossover
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2, Individual parent3) {

		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent1.copy());

		// Two Point Crossover
		int rand = Parameters.random.nextInt(parent1.chromosome.length);
		int rand2;
		do{ // Ensuring rand2 is bigger than rand
			rand2 = Parameters.random.nextInt(parent1.chromosome.length + 1);
		}
		while (rand <= rand2);

		for (int i = rand; i < rand2; i++){ // Crossover
			children.get(0).chromosome[i] = parent2.chromosome[i];
		}

		// Arithmetic Crossover
		for (int i = 0; i< parent1.chromosome.length; i++){
			children.get(1).chromosome[i] += parent3.chromosome[i];
			children.get(1).chromosome[i] = children.get(1).chromosome[i] / 2;
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
				//if (Parameters.random.nextDouble() < Parameters.mutateRate){ 				// If mutate
					if (individual.chromosome[i] > 0){ 										// If chromosone[i] > 0
						if (Parameters.random.nextDouble() < 0.55){							// Tends to push higher if > 0
							individual.chromosome[i] -= (Parameters.mutateChange * Parameters.random.nextDouble());
						}else{																// 0.4 chance of decreasing
							individual.chromosome[i] += (Parameters.mutateChange * Parameters.random.nextDouble());
						}
					}else if (individual.chromosome[i] < 0){ 								// If chromosone[i] < 0
						if (Parameters.random.nextDouble() < 0.55){							// Tends to push lower if < 0
							individual.chromosome[i] += (Parameters.mutateChange * Parameters.random.nextDouble());
						}else{
							individual.chromosome[i] -= (Parameters.mutateChange * Parameters.random.nextDouble());
						}
					}else{ // If mutation is 0
						if (Parameters.random.nextBoolean()) {
							individual.chromosome[i] += (Parameters.mutateChange * Parameters.random.nextDouble());
						} else {
							individual.chromosome[i] -= (Parameters.mutateChange * Parameters.random.nextDouble());
						}
					}
				//}
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
