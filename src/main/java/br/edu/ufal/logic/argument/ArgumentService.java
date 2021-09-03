package br.edu.ufal.logic.argument;

import org.springframework.stereotype.Service;

@Service
public class ArgumentService {

	public ArgumentDTO argumentToArgumentDTO(int id, Argument argument) {
		ArgumentDTO argumentdto = new ArgumentDTO(id, argument.toString());

		return argumentdto;
	}

}
