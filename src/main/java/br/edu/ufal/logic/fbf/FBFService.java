package br.edu.ufal.logic.fbf;

import org.springframework.stereotype.Service;

@Service
public class FBFService {

	public FBFDTO FBFToFBFDTO(FBF fbf, int id) {
		FBFDTO fbfdto = new FBFDTO(id, fbf.toString());
		
		return fbfdto;
	}

	
}
