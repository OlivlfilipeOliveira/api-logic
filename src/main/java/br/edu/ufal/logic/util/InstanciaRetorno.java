package br.edu.ufal.logic.util;

import java.util.ArrayList;
import java.util.Map;

public class InstanciaRetorno {

	private String mainOperator;

	private Map<String, String> operators;

	private ArrayList<Relacao> leftRelacao = new ArrayList<Relacao>();

	private ArrayList<Relacao> rightRelacao = new ArrayList<Relacao>();

	private ArrayList<Relacao> notRelacao = new ArrayList<Relacao>();

	private ArrayList<Relacao> argumentRelacao = new ArrayList<Relacao>();

	private Relacao conclusion;

	public InstanciaRetorno() {
	}

	public InstanciaRetorno(String mainOperator, Map<String, String> operators, ArrayList<Relacao> leftRelacao,
			ArrayList<Relacao> rightRelacao, ArrayList<Relacao> notRelacao, ArrayList<Relacao> argumentRelacao,
			Relacao conclusion) {
		this.mainOperator = mainOperator;
		this.operators = operators;
		this.leftRelacao = leftRelacao;
		this.rightRelacao = rightRelacao;
		this.notRelacao = notRelacao;
		this.argumentRelacao = argumentRelacao;
		this.conclusion = conclusion;
	}

	public String getMainOperator() {
		return mainOperator;
	}

	public void setMainOperator(String mainOperator) {
		this.mainOperator = mainOperator;
	}

	public Map<String, String> getOperators() {
		return operators;
	}

	public void setOperators(Map<String, String> operators) {
		this.operators = operators;
	}

	public ArrayList<Relacao> getLeftRelacao() {
		return leftRelacao;
	}

	public void setLeftRelacao(ArrayList<Relacao> leftRelacao) {
		this.leftRelacao = leftRelacao;
	}

	public ArrayList<Relacao> getRightRelacao() {
		return rightRelacao;
	}

	public void setRightRelacao(ArrayList<Relacao> rightRelacao) {
		this.rightRelacao = rightRelacao;
	}

	public ArrayList<Relacao> getNotRelacao() {
		return notRelacao;
	}

	public void setNotRelacao(ArrayList<Relacao> notRelacao) {
		this.notRelacao = notRelacao;
	}

	public ArrayList<Relacao> getArgumentRelacao() {
		return argumentRelacao;
	}

	public void setArgumentRelacao(ArrayList<Relacao> argumentRelacao) {
		this.argumentRelacao = argumentRelacao;
	}

	public Relacao getConclusion() {
		return conclusion;
	}

	public void setConclusion(Relacao conclusion) {
		this.conclusion = conclusion;
	}

}
