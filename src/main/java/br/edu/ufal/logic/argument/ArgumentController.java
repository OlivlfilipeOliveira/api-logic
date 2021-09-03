package br.edu.ufal.logic.argument;

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

import br.edu.ufal.logic.fbf.FBF;
import br.edu.ufal.logic.util.InstanciaRetorno;
import br.edu.ufal.logic.util.Relacao;
import br.edu.ufal.logic.util.Util;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

@RestController
@RequestMapping("/arguments")
public class ArgumentController {

	@Autowired
	private ArgumentService service;
//	private boolean executando = false;

	private static String modelArgument = "abstract sig Formula{}\n"
			+ "abstract sig Unary extends Formula{child: Formula}	\n"
			+ "abstract sig Binary extends Formula{ left, right: Formula }\n" + "sig Atom extends Formula{} \n"
			+ "sig Not extends Unary { } \n" + "sig And, Or, Imply, BiImply extends Binary { }\n" + "\n"
			+ "fact NoCycle{ no n, n': Formula | n in n'.^(child + Binary<:left + Binary<:right) and n' in n.^(child + Binary<:left + Binary<:right) }\n"
			+ "\n" + "//formas das regras\n" + "abstract sig Rule { }\n" + "sig NE extends Rule {p1: Not, r: Formula}\n"
			+ "sig NI extends Rule {p1: Formula, r: Not}\n" + "sig CI extends Rule {p1: Formula, p2: Formula, r: And}\n"
			+ "sig CE extends Rule {p1: And, r: Formula}\n" + "sig DI extends Rule {p1: Formula, r: Or}\n"
			+ "sig DE extends Rule {p1: Imply, p2: Imply, p3: Or, r: Formula}\n"
			+ "sig BI extends Rule {p1: Imply, p2: Imply, r: BiImply}\n"
			+ "sig BE extends Rule {p1: BiImply, r: Imply}\n"
			+ "sig MP extends Rule {p1: Formula, p2: Imply, r: Formula}\n"
			+ "sig MT extends Rule {p1: Formula, p2: Imply, r: Formula}\n"
			+ "sig SD extends Rule {p1: Formula, p2: Or, r: Formula}\n" + "\n" + "//regras\n" + "fact rules{\n"
			+ "	all ne:NE | ne.p1.child.child = ne.r//negation exclusion\n"
			+ "	all ni:NI | ni.p1 = ni.r.child.child//negation inclusion\n"
			+ "	all ci:CI | (ci.r.left = ci.p1 and ci.r.right = ci.p2) or (ci.r.left = ci.p2 and ci.r.right = ci.p1)//conjunction inclusion\n"
			+ "	all ce:CE | ce.r = ce.p1.left or ce.r = ce.p1.right//conjunction exclusion\n"
			+ "	all di:DI | di.p1 in di.r.(right+left)//disjunction inclusion\n"
			+ "	all de:DE | //disjunction exclusion\n"
			+ "		((de.p1.left=de.p3.left and de.p2.left=de.p3.right) or (de.p1.left=de.p3.right and de.p2.left=de.p3.left)) \n"
			+ "		and de.p1.right=de.p2.right and de.r=de.p2.right\n" + "	all bi:BI |//biimply inclusion\n"
			+ "		bi.p1.right=bi.p2.left and bi.p2.right=bi.p1.left \n"
			+ "		and ((bi.r.right=bi.p2.right and bi.r.left=bi.p2.left) or (bi.r.right=bi.p2.left and bi.r.left=bi.p2.right))\n"
			+ "	all be:BE | (be.r.left=be.p1.left and be.r.right=be.p1.right) or (be.r.left=be.p1.right and be.r.right=be.p1.left)//biimply exclusion\n"
			+ "	all mp:MP | mp.p1 = mp.p2.left and mp.r = mp.p2.right//MP\n"
			+ "	all mt:MT | (mt.p1.child = mt.p2.right and mt.r.child = mt.p2.left) or (mt.p1 = mt.p2.right.child and mt.r = mt.p2.left.child)//MT\n"
			+ "	all sd:SD | (sd.p1.child = sd.p2.left and sd.r = sd.p2.right) or (sd.p1.child = sd.p2.right and sd.r = sd.p2.left)//SD\n"
			+ "}\n" + "\n" + "//unicidades\n"
			+ "pred isEqualTo[a:Formula,a':Formula]{ ((a.right = a'.right and a.left = a'.left) or (a.right = a'.left and a.left = a'.right)) implies a = a' }\n"
			+ "pred avoidA_A[a:Formula]{ a.right != a.left }\n"
			+ "pred avoidA_noA[a:Formula]{ (a.right.child != a.left) and (a.right != a.left.child) }\n"
			+ "fact { //to avoid\n" + "	all a,a':Not | a.child = a'.child implies a = a'\n"
			+ "	all a,a':And | a.isEqualTo[a']\n" + "	all a,a':Or | a.isEqualTo[a']\n"
			+ "	all a,a':BiImply | a.isEqualTo[a']\n"
			+ "	all a,a':Imply | (a.right = a'.right and a.left = a'.left) implies a = a'\n"
			+ "	all x:And | x.avoidA_A//avoid A^A\n" + "	all x:Or | x.avoidA_A//avoid AvA\n"
			+ "	all x:Imply | x.avoidA_A//avoid A->A\n" + "	all x:BiImply | x.avoidA_A//avoid A<->A\n"
			+ "	all x:And | x.avoidA_noA//avoid  A^~A\n" + "	all x:Or | x.avoidA_noA//avoid  Av~A\n"
			+ "	all x:Imply | x.avoidA_noA//avoid  ~A->A / A->~A\n"
			+ "	all x:BiImply | x.avoidA_noA//avoid  ~A<->A / A<->~A\n" + "}\n" + "\n"
			+ "fact { //unique applications\n" + "	all a,a':NE | (a.r=a'.r) implies a=a' \n"
			+ "	all a,a':NI | (a.r=a'.r) implies a=a' \n" + "	all a,a':CE | (a.p1=a'.p1 and a.r=a'.r) implies a=a' \n"
			+ "	all a,a':CI | (a.r=a'.r) implies a=a' \n" + "	all a,a':DI | (a.p1=a'.p1 and a.r=a'.r) implies a=a' \n"
			+ "	all a,a':DE | \n"
			+ "		((a.p1.isEqualTo[a'.p1] and a.p2.isEqualTo[a'.p2]) or (a.p1.isEqualTo[a'.p2] and a.p2.isEqualTo[a'.p1]))\n"
			+ "		and a.p3.isEqualTo[a'.p3] implies a=a'\n"
			+ "	all a,a':BE | (a.p1=a'.p1 and a.r=a'.r) implies a=a' \n"
			+ "	all a,a':SD | (a.p1=a'.p1 and a.p2=a'.p2) implies a=a' \n"
			+ "	all a,a':MP | (a.p1=a'.p1 and a.p2=a'.p2) implies a=a' \n"
			+ "	all a,a':MT | (a.p1=a'.p1 and a.p2=a'.p2) implies a=a' \n" + "}\n" + "\n"
			+ "let P1 = NE<:p1+NI<:p1+CI<:p1+CE<:p1+DI<:p1+DE<:p1+BI<:p1+BE<:p1+MP<:p1+MT<:p1+SD<:p1\n"
			+ "let P2 = CI<:p2+DE<:p2+BI<:p2+MP<:p2+MT<:p2+SD<:p2\n"
			+ "let R = NE<:r+NI<:r+CI<:r+CE<:r+DI<:r+DE<:r+BI<:r+BE<:r+MP<:r+MT<:r+SD<:r\n" + "fact OneOrigin{ \n"
			+ "	one rule: Rule | all f: Formula | \n"
			+ "		f in rule.(P1+P2+p3+R).*(child +Binary<:left + Binary<:right) or f=rule.P1 or f=rule.P2 or f=rule.p3 or f = rule.R\n"
			+ "}\n" + "\n"
			+ "one sig Argument{ premisse: set Formula, conclusion: one Formula }{ #premisse=3 not (conclusion in premisse) }\n";

	@GetMapping
	public ArrayList<ArgumentDTO> findAll() throws IOException {
		ArrayList<ArgumentDTO> argumentos = new ArrayList<>();
		
		return argumentos;

	}

	@GetMapping("/{regras}/{atomos}/{quantidade}/{listas}")
	public ArrayList<ArgumentDTO> findArguments(@PathVariable String regras, @PathVariable String atomos,
			@PathVariable String quantidade, @PathVariable String listas) throws IOException {
		
		System.out.println(regras);
		System.out.println(atomos);
		System.out.println(quantidade);
		System.out.println(listas);
		 
		String ne = "";
		String ni = "";
		String ci = "";
		String ce = "";
		String di = "";
		String de = "";
		String be = "";
		String bi = "";
		String mp = "";
		String mt = "";
		String sd = "";

		String[] regrasSplit = regras.split(",");
	//	System.out.println(regrasSplit[0]);
		ArrayList<String> regs = new ArrayList<>();
		for(String s: regrasSplit) {
			regs.add(s);
		}

		
		if(regs.contains("NE")) {
			ne = "#NE > 0";
		}
		if(regs.contains("NI")) {
			ni = "#NI > 0";
		}
		if(regs.contains("CI")) {
			ci = "#CI > 0";
		}
		if(regs.contains("CE")) {
			ce = "#CE > 0";
		}
		if(regs.contains("DI")) {
			di = "#DI > 0";
		}
		if(regs.contains("DE")) {
			de = "#DE > 0";
		}
		if(regs.contains("BE")) {
			be = "#BE > E";
		}
		if(regs.contains("BI")) {
			bi = "#BI > 0";
		}
		if(regs.contains("MP")) {
			mp = "#MP > 0";
		}
		if(regs.contains("MT")) {
			mt = "#MT > 0";
		}
		if(regs.contains("SD")) {
			sd = "#SD > 0";
		}
		
		Util util = new Util();
		ArrayList<ArgumentDTO> argumentos = new ArrayList<>();
		ArrayList<String> argumentTeste = new ArrayList<>();
		
		A4Reporter rep = new A4Reporter();
		String[] alfabeto = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "I", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };

		int cont = 0;
		int valueRun = 4;
		
		while(cont < (Integer.parseInt(quantidade) * Integer.parseInt(listas))) {
			
			valueRun += 1;
			String config = "pred ConfigArgument(){ \n" 
					+ " #Atom="+atomos+"\n" + "	#MT=0 <=> #MP!=0\n" 
					+ ne+"\n"
					+ ni+"\n"
					+ ci+"\n"
					+ ce+"\n"
					+ di+"\n"
					+ de+"\n"
					+ be+"\n"
					+ bi+"\n"
					+ mp+"\n"
					+ mt+"\n"
					+ sd+"\n"
					+ "	one ru,ru':Rule | ru.R in ru'.(P1+P2+p3)\n"
					+ "	one arg:Argument | all ru:Rule | ru.(P1+P2+p3) in arg.premisse\n"
					+ "	one arg:Argument | all ru:Rule | no fo:Formula | fo in ru.(P1+P2+p3) and fo in ru.R and fo in arg.conclusion\n"
					+ "	one arg:Argument | some ru:Rule | arg.conclusion=ru.R\n" + "}\n" + "\n" + "run ConfigArgument for "+valueRun+"\n"
					+ "";
			System.out.println(config);
			
			String model = modelArgument + config;
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.println(model);
			System.out.println(timestamp.getTime());
			File tmpAls = File.createTempFile("alloyArgument"+timestamp.getTime(), ".als");
			tmpAls.deleteOnExit();
			flushModelToFile(tmpAls, model);
			{
				CompModule world = CompUtil.parseEverything_fromFile(rep, null, tmpAls.getAbsolutePath());
				A4Options opt = new A4Options();
				opt.originalFilename = tmpAls.getAbsolutePath();
				opt.solver = A4Options.SatSolver.SAT4J;
				Command cmd = world.getAllCommands().get(0);

				A4Solution sol = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), cmd, opt);

				while (sol.satisfiable()) {
					Argument arg = new Argument();
						
					InstanciaRetorno ir = util.montarInstancia(sol.toString());

					ArrayList<String> mainOperatorPremisses = new ArrayList<String>();

					ArrayList<Relacao> argumentRelacao = ir.getArgumentRelacao();

					for (Relacao relacao : argumentRelacao) {
						mainOperatorPremisses.add(relacao.getRight());
					}

					for (String mainOperator : mainOperatorPremisses) {
						FBF premisse = util.montaFBF(mainOperator, ir.getOperators(), ir.getNotRelacao(),
								ir.getLeftRelacao(), ir.getRightRelacao());
						arg.addPremisse(util.fillWithAtoms(premisse, alfabeto));
					}

					FBF conclusion = util.montaFBF(ir.getConclusion().getRight(), ir.getOperators(), ir.getNotRelacao(),
							ir.getLeftRelacao(), ir.getRightRelacao());
					arg.setConclusion(util.fillWithAtoms(conclusion, alfabeto));
					
					if(!argumentTeste.contains(arg.toString())){
						argumentos.add(service.argumentToArgumentDTO(cont, arg));
						System.out.println(argumentos.size());
						argumentTeste.add(arg.toString());
						System.out.println(argumentos.size());
						cont += 1;
					}else {
						System.out.println("quant: "+arg);
					}
					sol = sol.next();
					
					if (cont == (Integer.parseInt(quantidade) * Integer.parseInt(listas))) {
						break;
					}

				}
 
			}
			tmpAls.delete();
		}
		return argumentos;

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
