package com.ghc.app.math;

import java.util.ArrayList;

/**
 * Unique perturbation
 * Compute unique perturbations of integer values whose sum are the user specified SUM_VALUE. Relative position of the integer values is important.
 * Implemented up to integer value 9 (for higher number of values, the String representation of the result becomes ambiguous)
 * Call computePerturbation to retrieve String list of integer values representing this perturbation
 * 
 * Example for numValues = 4:
 *  Perturbation #0: 4
 *  Perturbation #1: 31
 *  Perturbation #2: 22
 *  Perturbation #3: 211
 *  Perturbation #4: 13
 *  Perturbation #5: 121
 *  Perturbation #6: 112
 *  Perturbation #7: 1111
 */
public class UniquePerturbation {
	private ArrayList<String> _pertubations;

	/**
	 * 
	 * @param sumValue Summation result of perturbated integer values
	 * @throws Exception
	 */
	public UniquePerturbation(int sumValue) {
		_pertubations = computePerturbation(sumValue);
	}

	/**
	 * 
	 * @return Number of perturbations
	 */
	public int numPerturbations() {
		return _pertubations.size();
	}

	/**
	 * Retrieve perturbation
	 * 
	 * @param index Perturbation index
	 * @return List of integer values representating this perturbation
	 */
	public java.util.List<Integer> perturbation(int index) {
		if (index < 0 || index >= numPerturbations()) {
			return null;
		}
		String pertText = _pertubations.get(index);
		int numItems = pertText.length();
		ArrayList<Integer> list = new ArrayList<Integer>(numItems);
		for (int i = 0; i < numItems; i++) {
			list.add(new Integer(pertText.charAt(i) - 48));
		}
		return list;
	}

	/**
	 * Perturbation function Create list with all unique perturbations of integer values
	 * whose sum is 'num'
	 * 
	 * @param num Sum of integer numbers that shall be perturbed. NOTE: num <= 9!
	 * @return List that contains all perturbations as text Strings, such as "3", "21",
	 *         "12", "111" (for num=3)
	 */
	private ArrayList<String> computePerturbation(int sumValue) {
		ArrayList<String> listCurrent = new ArrayList<String>();
		for (int i = sumValue; i > 0; i--) {
			int diff = sumValue - i;
			if (diff == 0) {
				listCurrent.add("" + i);
			} else {
				ArrayList<String> listNew = computePerturbation(diff);
				for (int ii = 0; ii < listNew.size(); ii++) {
					listCurrent.add("" + i + listNew.get(ii));
				}
			}
		}
		return listCurrent;
	}

	public static void main(String[] args) {
		UniquePerturbation pert = new UniquePerturbation(4);

		for (int i = 0; i < pert.numPerturbations(); i++) {
			java.util.List<Integer> perturbation = pert.perturbation(i);
			System.out.print("Perturbation #" + i + ": ");
			for (int index = 0; index < perturbation.size(); index++) {
				System.out.print("" + perturbation.get(index).intValue());
			}
			System.out.print("\n");
		}
	}

}
