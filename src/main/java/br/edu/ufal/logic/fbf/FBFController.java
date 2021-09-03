package br.edu.ufal.logic.fbf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufal.logic.util.InstanciaRetorno;
import br.edu.ufal.logic.util.Util;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

@RestController
@RequestMapping("/fbfs")
public class FBFController {

	@Autowired
	private FBFService service;

	private static String modelFormulaTeste = "abstract sig Formula{}\n" + "\n"
			+ "abstract sig Unary extends Formula{ child: Formula }\n"
			+ "abstract sig Binary extends Formula{ left, right: Formula }\n" + "\n" + "sig Atom extends Formula {}\n"
			+ "sig Not extends Unary{}\n" + "sig And, Or, Imply, BiImply extends Binary {  }\n" + "\n"
			+ "one sig FBF{ mainOperator: one Formula }\n" + "\n"
			+ "fact NoCycle{ no n, n': Formula | n in n'.^(child + left + right) and n' in n.^(child + left + right) }\n"
			+ "fact EveryNodeAtAFBF{ all n: Formula | one t: FBF | n in t.mainOperator.*(child + left + right) }\n";

	@GetMapping
	private ArrayList<FBFDTO> findAll() throws IOException {

		ArrayList<FBFDTO> fbfs = new ArrayList<>();

		return fbfs;
	}

	@GetMapping("/{atomosMin}/{atomosMax}/{atomosQuantidade}/{quantidadeFbfs}/{exatoPelomenos}/{operadoresLista}/{listasExercicios}")
	private ArrayList<FBFDTO> findFbfs(@PathVariable String atomosMin, @PathVariable String atomosMax,
			@PathVariable String atomosQuantidade, @PathVariable String quantidadeFbfs,
			@PathVariable String exatoPelomenos, @PathVariable String operadoresLista, 
			@PathVariable String listasExercicios) throws IOException {
		
		String quantAtomos = "";

		if (atomosQuantidade.equals("0")) {
			quantAtomos = "#Atom >= " + atomosMin + " && #Atom <= " + atomosMax;
		} else {
			quantAtomos = "#Atom = " + atomosQuantidade;
		}

		String config = "";

		String and = "";
		String or = "";
		String imply = "";
		String biImply = "";
		String not = "";

		String[] operadores = operadoresLista.split(",");

		ArrayList<String> oprs = new ArrayList<>();
		for (String s : operadores) {
			oprs.add(s);
		}
		System.out.println("Exatamen pelo menot " + exatoPelomenos);
		if(exatoPelomenos.equals("2")) {
			if (oprs.contains("And")) {
				and = "#And > 0";
			}
			if (oprs.contains("Or")) {
				or = "#Or > 0";
			}
			if (oprs.contains("BiImply")) {
				biImply = "#BiImply > 0";
			}
			if (oprs.contains("Imply")) {
				imply = "#Imply > 0";
			}
			if (oprs.contains("Not")) {
				not = "#Not > 0";
			}
		}else if(exatoPelomenos.equals("1")) {
			and = "#And = 0";
			or = "#Or = 0";
			imply = "#Imply = 0";
			biImply = "#BiImply = 0";
			not = "#Not = 0";
			
			if (oprs.contains("And")) {
				and = "#And > 0";
			}
			if (oprs.contains("Or")) {
				or = "#Or > 0";
			}
			if (oprs.contains("BiImply")) {
				biImply = "#BiImply > 0";
			}
			if (oprs.contains("Imply")) {
				imply = "#Imply > 0";
			}
			if (oprs.contains("Not")) {
				not = "#Not > 0";
			}
		}
		

		int cont = 0;
		int valueRun = 4;

		ArrayList<FBFDTO> fbfs = new ArrayList<>();
		ArrayList<String> fbfsTeste = new ArrayList<>();

		Util util = new Util();

		while (cont < (Integer.parseInt(quantidadeFbfs) * Integer.parseInt(listasExercicios))) {
			valueRun += 1;

			config = "pred ConfigFormula(){ \n" + and + "\n" + not + "\n" + imply + "\n" + biImply + "\n" + or + "\n"
					+ "	" + quantAtomos + "\n" + "	\n" + "}\n" + "run ConfigFormula for " + valueRun;

			System.out.println(config);

			A4Reporter rep = new A4Reporter();
			String[] alfabeto = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "I", "M", "N", "O", "P", "Q",
					"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

			String model = modelFormulaTeste + config;

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			File tmpAls = File.createTempFile("alloyFormula" + timestamp.getTime(), ".als");
			tmpAls.deleteOnExit();
			flushModelToFile(tmpAls, model);
			{
				CompModule world = CompUtil.parseEverything_fromFile(rep, null, tmpAls.getAbsolutePath());
				A4Options opt = new A4Options();
				opt.originalFilename = tmpAls.getAbsolutePath();
				opt.solver = A4Options.SatSolver.SAT4J;
				Command cmd = world.getAllCommands().get(0);

				A4Solution sol = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), cmd,
						opt);

				while (sol.satisfiable()) {
					InstanciaRetorno ir = util.montarInstancia(sol.toString());
					FBF fbf = util.montaFBF(ir.getMainOperator(), ir.getOperators(), ir.getNotRelacao(),
							ir.getLeftRelacao(), ir.getRightRelacao());

					fbf = util.fillWithAtoms(fbf, alfabeto);

					if (!fbfsTeste.contains(fbf.toString())) {
						fbfs.add(service.FBFToFBFDTO(fbf, cont));
						fbfsTeste.add(fbf.toString());
						cont += 1;
					} else {
						System.out.println(fbf);
					}
					sol = sol.next();
					if (cont == (Integer.parseInt(quantidadeFbfs) * Integer.parseInt(listasExercicios))) {
						break;
					}
				}
			}
			tmpAls.delete();
		}

		return fbfs;
	}

	private static void flushModelToFile(File tmpAls, String model) throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(tmpAls));
			bos.write(model.getBytes());
			bos.flush();
		} finally {
			if (bos != null)
				bos.close();
		}
	}
}
