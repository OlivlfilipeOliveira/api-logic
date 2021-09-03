package br.edu.ufal.logic.argument;

public class ArgumentDTO {

	private int id;
	
	private String argument;
	
	public ArgumentDTO(){
		
	}
	
	public ArgumentDTO(int id, String argument) {
		this.id = id;
		this.argument = argument;
	}
	
	public String getArgument() {
		return argument;
	}
	
	public void setArgument(String argument) {
		this.argument = argument;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}
