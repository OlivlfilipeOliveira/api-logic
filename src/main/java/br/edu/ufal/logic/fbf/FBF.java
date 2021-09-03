package br.edu.ufal.logic.fbf;

public class FBF {

	private Object left;
	private String operator;
	private Object right;

	public FBF() {
	}

	public FBF(Object left, String operator, Object right) {

		if (left.equals("") && operator.equals("")) {
			this.right = right;
		}
		if (left.equals("") && right.equals("") && operator.equals("~")) {
			this.right = right;
			this.operator = operator;
		} else {
			this.left = left;
			this.operator = operator;
			this.right = right;

		}
	}

	public Object getLeft() {
		return left;
	}

	public void setLeft(Object left) {
		this.left = left;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Object getRight() {
		return right;
	}

	public void setRight(Object right) {
		this.right = right;
	}

	@Override
	public String toString() {
		String returnn = "";

		if (this.left.equals("") && this.operator.equals("") && !this.right.equals("")) {
			returnn += (this.right instanceof FBF) ? this.right.toString() : this.right;
		} else if (this.left.equals("") && this.operator.equals("~")) {
			returnn += ((this.right instanceof FBF) ? "~" + this.right.toString() : "~" + this.right);
		} else {// if (!this.left.equals("") && !this.right.equals("") &&
				// !this.operator.equals("")) {
			returnn += ((this.left instanceof FBF) ? "(" + this.left.toString() : "(" + this.left);
			returnn += " " + this.operator + " ";
			returnn += ((this.right instanceof FBF) ? this.right.toString() + ")" : this.right + ")");
		} // else {
//			returnn = "Fórmula inválida";
//		}
		return returnn;
	}

}
