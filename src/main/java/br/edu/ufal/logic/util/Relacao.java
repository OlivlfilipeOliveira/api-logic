package br.edu.ufal.logic.util;

public class Relacao {

	private String left;
	private String right;

	public Relacao() {
	}

	public Relacao(String left, String right) {
		this.left = left;
		this.right = right;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

}
