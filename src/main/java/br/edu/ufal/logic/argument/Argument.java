package br.edu.ufal.logic.argument;

import java.util.ArrayList;

import br.edu.ufal.logic.fbf.FBF;


public class Argument {

	private ArrayList<FBF> premisses = new ArrayList<FBF>();

	private FBF conclusion;

	public Argument() {
	}

	public FBF getConclusion() {
		return conclusion;
	}

	public ArrayList<FBF> getPremisses() {
		return premisses;
	}

	public void setConclusion(FBF conclusion) {
		this.conclusion = conclusion;
	}

	public void setPremisses(ArrayList<FBF> premisses) {
		this.premisses = premisses;
	}

	public boolean addPremisse(FBF premisse) {
		return this.premisses.add(premisse);
	}

	public boolean removePremisse(FBF premisse) {
		return this.premisses.remove(premisse);
	}

	@Override
	public String toString() {
		String argument = "{";

		int cont = 0;
		for (FBF premisse : premisses) {
			argument += premisse.toString();
			cont += 1;
			if (cont < premisses.size()) {
				argument += ", ";
			}

		}

		argument += "}";
		argument += " |- ";
		argument += conclusion.toString();

		return argument;
	}
}
