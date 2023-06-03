import java.io.File;  
import java.io.FileNotFoundException;  
import java.util.*;
import java.util.Map.Entry;

//PRAVI SOLUTION
public class program {
    public static Set<String> visited = new LinkedHashSet<>();
	public static LinkedList<String> putDo = new LinkedList<>();
	public static Double duljinaPuta;
	public static Integer brojPosjecenihStanja;
	public static Map<String, Double> mapaUdaljenosti = new TreeMap<>();

	public static void main(String ... args)   {
		int ind=0;
		boolean jeli = false;     //jeli nasao rijesenje
		String putanja = "3x3_puzzle.txt";        //putanja do datoteke gdje su upute za stanja
		String algoritam ="";       //ime algoritma koji se koristi
		String h ="istra_heuristic.txt";               //putanja do heursitike koja se koristi istra_heuristic.txt
		boolean check_o = false;   //zastavica za cekiranje jeli h optimisticna
		boolean check_c = false;     //zastavica za cekiranje jeli h k
		duljinaPuta = 0.0;
		Boolean imaH = false;
		
		for(String a : args) {
			
			
			//iz polja args izvlacimo putanju do opisnika prostora stanja
			//i spremamo je u var putanja
			if (a.equals("--ss")) {
                putanja = args[ind + 1];
			}
            //iz polja args izvlacimo algoritsm zs pretrazivanje
			if (a.equals("--alg")) {
                algoritam = args[ind + 1];
			}
			//iz polja args izvlacimo heuristik (ako je ima)
			if (a.equals("--h")) {
               h = args[ind + 1];
			}
			//iz polja provjeravamo zastavice 
			if (a.equals("--check-optimistic")) {
                check_o = true;
			}
			if (a.equals("--check-consistent")) {
                check_c = true;
			}
			ind = ind + 1;
		}

		//System.out.printf("putanja do opisnika stanja: %s%n", putanja);
		//System.out.printf("algoritam: %s%n", algoritam);
		//System.out.printf("heuristika: %s%n", h);
		//System.out.println(check_o);
		//System.out.println(check_c);

		ind = 0;
		String pocetno="";
		
		Set<String> konacnaStanja= new HashSet<>();
		Map<String, Map<String, Double>> funkcija=new HashMap<>();


		//ucitavanje stanja
		try {
			
			File myObj = new File(putanja);
			Scanner myReader = new Scanner(myObj, "utf-8");
			while (myReader.hasNextLine()) {
			  String data = myReader.nextLine();
			  if (ind == 0 && data.contains("#")) {
				  continue;
			  }
			  if (ind == 0) {
				  
                  pocetno = data;
			  }
			  else if (ind == 1) {
			      String[] polje = data.split(" ");
				  for (String s: polje) {
				      konacnaStanja.add(s);
				   }
				
                  
			  } else {
				  String[] pomoc = data.split("\\: ");
				  if (pomoc.length < 2) {
                      continue;
				  }
				  String[] pomocDva = pomoc[1].split(" ");
				  Map<String, Double> fun = new TreeMap<>();
                  for (String s: pomocDva) {
					  String[] pomocTri = s.split("\\,");
					  
					  fun.put(pomocTri[0], Double.valueOf(pomocTri[1]));
				   }
				   funkcija.put(pomoc[0], fun);
			  }
			  ind = ind + 1;

			  //System.out.println(data);
			}
			myReader.close();
		  } catch (FileNotFoundException e) {
			System.out.println("KOJI F U CHATU.");
			e.printStackTrace();
		  } 

		Map<String, Double> heuristika = new TreeMap<>();
        //ucitavanje heurisitke
         try {
			 if (h.length()>1) {
				File myObj2 = new File(h);
				Scanner myReader2 = new Scanner(myObj2, "utf-8");
				while (myReader2.hasNextLine()) {
					String data = myReader2.nextLine();
					String[] pomoc = data.split("\\: ");
					heuristika.put(pomoc[0], Double.valueOf(pomoc[1]));
				}
				//for (String s: heuristika.keySet()) {
				//	System.out.println(s + " " + heuristika.get(s) );
				//}
				
				myReader2.close();
			 }

		 } catch (FileNotFoundException e) {
			System.out.println("nema heurisike");
			e.printStackTrace();
		  } 


          
		  if (algoritam.equals("bfs")) {
              jeli = algoritamBFS(pocetno, konacnaStanja, funkcija);
			  algoritam= "BFS";
		  } else if (algoritam.equals("ucs")) {
			  algoritam = "UCS";
			  jeli = UCS(pocetno, konacnaStanja, funkcija);
		  } else if (algoritam.equals("a-star")) {
              algoritam = "A-STAR";
              imaH = true;
			  jeli = aStar(pocetno, konacnaStanja, funkcija, heuristika);
			  
			  

          }

		  //ucitavanje heurisike

		  ispis(algoritam, jeli, imaH, h);
		  if (check_o==true) {
			isOptimistic(pocetno, konacnaStanja,  funkcija, heuristika, h);
			
		  }
		  if (check_c == true) {
			isConsistent(pocetno, konacnaStanja,  funkcija, heuristika, h);
		  }
		  
          
		  


	}




	//Algoritam pretrazivanja u sirinu (BFS)
	//u slucaju da nismo uspjeli naci vraca false
	public static boolean algoritamBFS(String pocetno, Set<String> konacno, Map<String, Map<String, Double>> fun) {
        putDo.clear();
		visited.clear();
		Map<String, Double> rijecnik = new TreeMap();  
		Map<String, String> puteljak = new HashMap<>();      
		ArrayList<String> open =  new ArrayList<>();     
		String roditelj = pocetno;
		rijecnik.putAll(fun.get(pocetno));
		
		//for (String s: rijecnik.keySet()) {
		//	open.add(s);
		//	puteljak.put(s, roditelj);    //ima stanja i roditelje
		//}

		open.add(pocetno);
		puteljak.put(pocetno, null);
		
        //visited.add(pocetno);
        while (!open.isEmpty()) {
			String stanje = open.get(0);
			open.remove(stanje);
			visited.add(stanje);
			
			if (konacno.contains(stanje)) {
				String trenutnoStanje = stanje;
				brojPosjecenihStanja = visited.size();
				while (true) {
					rijecnik.clear();
					putDo.addFirst(trenutnoStanje);

					if (trenutnoStanje.equals(pocetno)) {
						break;
					}
					String djete = trenutnoStanje;
					trenutnoStanje = puteljak.get(djete);
					rijecnik.putAll(fun.get(trenutnoStanje));
				    duljinaPuta = duljinaPuta + rijecnik.get(djete);
					
				}
				puteljak.clear();
				return true;
			}

			
			rijecnik.clear();
			rijecnik.putAll(fun.get(stanje));
		
			for (String s: rijecnik.keySet()) {
				open.add(s);  //dodan tu
				if (!visited.contains(s)) {
                    //open.add(s);
					if (!puteljak.containsKey(s)) {
						puteljak.put(s, stanje);
					}
					
				}
				
			}


		}
		return false;
	}

    
    public static boolean UCS(String pocetno, Set<String> konacno, Map<String, Map<String, Double>> fun)  {
		putDo.clear();
		visited.clear();
		Map<String, String> puteljak = new HashMap<>(); 
        visited.add(pocetno);
		TreeSet<Cvor> open = new TreeSet();
		 int dubina = 0;
		Map<String, Double> rijecnik = new HashMap(); 
		//rijecnik.putAll(fun.get(pocetno));  
		//for (String s : rijecnik.keySet()) {
        //    open.add(new Cvor(s, dubina, rijecnik.get(s), pocetno , 0.0));
		//}
        
        open.add(new Cvor(pocetno, dubina, 0.0 , null , 0.0));


        while (!open.isEmpty()) {
            Cvor prvi = open.first();
            open.remove(prvi);
            String stanje = prvi.getIme();

            if (!visited.contains(stanje)) {
                puteljak.put(stanje, prvi.getRoditelj());
            }
            visited.add(stanje);
            
            if (konacno.contains(stanje)) {
				brojPosjecenihStanja = visited.size();
                duljinaPuta = prvi.getUdaljenost();
				String trenutnoStanje = stanje;
				
				while (true) {
					open.clear();
					
					putDo.addFirst(trenutnoStanje);

					if (trenutnoStanje.equals(pocetno)) {
						break;
					}
					String djete = trenutnoStanje;
					trenutnoStanje = puteljak.get(djete);
				}
				puteljak.clear();
				
				return true;
			}
            
            
            
            rijecnik.clear();
            rijecnik.putAll(fun.get(stanje));
            dubina = dubina + 1;
            for (String s : rijecnik.keySet()) {
                if (!visited.contains(s)) {
                    open.add(new Cvor(s, dubina, rijecnik.get(s) + prvi.getUdaljenost(), stanje , 0.0));
                    
                }
                
            }
           

        }
        
		return false;
	}

    
    public static boolean aStar(String pocetno, Set<String> konacno, Map<String, Map<String, Double>> fun, Map<String, Double> heur) {
        Comparator<Cvor> poFunkciji = new Comparator<Cvor>() {
			@Override
			public int compare(Cvor c1, Cvor c2) {
				int a = Double.compare(c1.getFunkcija(), c2.getFunkcija());
				if (a!=0) {
					return a;
				}
				a = c1.getIme().compareTo(c2.getIme());
				return a;
			}
		};
		
        putDo.clear();
		visited.clear();

		HashMap<String, Double> posjetili = new HashMap<>(); //closed
		Map<String, String> puteljak = new HashMap<>(); 
        
		TreeSet<Cvor> open = new TreeSet(poFunkciji);
		int dubina = 0;
		Map<String, Double> rijecnik = new HashMap(); 
		//rijecnik.putAll(fun.get(pocetno));  
		//for (String s : rijecnik.keySet()) {
			//f = h(s) + put do tada prijedjen
		//	double udaljenost = rijecnik.get(s);
        //    open.add(new Cvor(s, dubina, udaljenost, pocetno ,udaljenost + heur.get(s) ));
		//}
		open.add(new Cvor(pocetno, dubina, 0.0, null ,0.0 ));
		//posjetili.put(pocetno, 0.0);
		
		
		while (!open.isEmpty()) {
            Cvor prvi = open.first();
            open.remove(prvi);
			String stanje = prvi.getIme();
            

			if (!posjetili.containsKey(stanje)) {
                puteljak.put(stanje, prvi.getRoditelj());
			} 
			posjetili.put(stanje, prvi.getFunkcija());
            if (konacno.contains(stanje)) {
				brojPosjecenihStanja = posjetili.size();
                duljinaPuta = prvi.getUdaljenost();
				String trenutnoStanje = stanje;
				
				while (true) {
					open.clear();
					
					putDo.addFirst(trenutnoStanje);

					if (trenutnoStanje.equals(pocetno)) {
						break;
					}
					String djete = trenutnoStanje;
					trenutnoStanje = puteljak.get(djete);
				}
				puteljak.clear();
				
				return true;
			}
			
			
			rijecnik.clear();
            rijecnik.putAll(fun.get(stanje));
            dubina = dubina + 1;
			for (String s : rijecnik.keySet()) {
				double ut = rijecnik.get(s) + prvi.getUdaljenost();
				double funkcija = ut + heur.get(s);
                if (posjetili.containsKey(s)) {
                    if (posjetili.get(s) > funkcija) {
                        posjetili.remove(s);
					} else {
						continue;
					}
                    
                }
				open.add(new Cvor(s, dubina, ut, stanje , funkcija));
                
            }
		}
		
		return false;
    }

	public static void isOptimistic(String pocetno, Set<String> konacno, Map<String, Map<String, Double>> fun, Map<String, Double> heur, String putanja) {
        boolean optimistic = true;
		//punjenjeMapeUdaljenosti(heur);
		String concusion = "[CONCLUSION]: Heuristic is optimistic.";
		System.out.println("# HEURISTIC-OPTIMISTIC " + putanja);

		for (String s: heur.keySet()) {
			String linija= "[CONDITION]: ";
			boolean jeli = UCS(s, konacno, fun);
			//System.out.println("GRAD: " + s +" UDALJENOST " +  duljinaPuta);
			double stvarniPut = duljinaPuta;
			//double stvarniPut = mapaUdaljenosti.get(s);
			//System.out.println("GRAD: " + s +" UDALJENOST " +   stvarniPut);
			double procjenaPut = heur.get(s);
			//ispis("UCS", jeli, false, s);
			if (stvarniPut >= procjenaPut) {
				//heur je opt
				linija = linija + "[OK] ";
			} else {
				//heuur nie opt
				linija = linija + "[ERR] ";
				optimistic = false;
			}
			linija = linija + String.format("h(%s) <= h*: %.1f <= %.1f", s, procjenaPut, stvarniPut);
			System.out.println(linija);

		}
		if (optimistic == false) {
			concusion = "[CONCLUSION]: Heuristic is not optimistic"; 
		}
		System.out.println(concusion);
	}
	
    public static void isConsistent(String pocetno, Set<String> konacno, Map<String, Map<String, Double>> fun, Map<String, Double> heur, String putanja) {
		boolean con = true;
		String conclusion = "[CONCLUSION]: Heuristic is consistent.";
		Map<String, Double> rijecnik = new TreeMap(); 
		System.out.println("# HEURISTIC-CONSISTENT " + putanja);
	    
		for (String s: heur.keySet()) {
			if (!fun.containsKey(s)) {
				continue;
			} 
            rijecnik.putAll(fun.get(s));
            //System.out.println(s);
			
			Double h = heur.get(s);
			for (String t : rijecnik.keySet()) {
				String linija= "[CONDITION]: ";
				String jeliOk = "[OK] "; 
				Double delta = rijecnik.get(t);  //koliko treba od s do t
				Double put = heur.get(t);   //t do cilja
				if (h <= delta + put) {
                    //sve je dobro
				} else {
                    jeliOk = "[ERR] ";
					con = false;
				}
                linija = linija + jeliOk + String.format("h(%s) <= h(%s) + c: %.1f <= %.1f + %.1f", s, t, h, put, delta );
				System.out.println(linija);

			}
			rijecnik.clear();
		}
		if (con == false) {
			conclusion = "[CONCLUSION]: Heuristic is not consistent."; 
		}
		System.out.println(conclusion);
		

	}

	public static void punjenjeMapeUdaljenosti(Set<String> konacno, Map<String, Map<String, Double>> fun, Map<String, Double> heur) {
         for (String s: heur.keySet()) {
            boolean jeli = UCS(s, konacno, fun);
			mapaUdaljenosti.put(s, duljinaPuta);
		}

	}
	
	
	public static void ispis(String algoritam, boolean nasli, boolean imaH, String heurisitka) {
        if (imaH) {
            algoritam = algoritam + " " + heurisitka;
        }
        System.out.println("# " + algoritam);
		if (nasli==true) {
			System.out.println("[FOUND_SOLUTION]: yes");
		} else {
            System.out.println("[FOUND_SOLUTION]: no");
		}
		System.out.println("[STATES_VISITED]: " + brojPosjecenihStanja);
		String path = "";
		int i = 0;
		for (String s : putDo) {
			path = path + s;
			i = i + 1;
			if (i != putDo.size()) {
				path = path + " => ";
			}
		}
		System.out.println("[PATH_LENGTH]: " + putDo.size());
		System.out.println("[TOTAL_COST]: " + duljinaPuta);
		System.out.println("[PATH]: " + path);
		
	}
	
	
	public static void iteriranjeKrozPrjelaze(Map<String, Map<String, Double>> fun) {
		//System.out.println("pocetno: " + pocetno);
		//System.out.println("koncacno: " );
		for (String s: fun.keySet()) {
			System.out.println("KLJUC: " + s);
			Map<String, Double> druga = fun.get(s);
			for (String k: druga.keySet()) {
				System.out.println("GRAD: " + k);
				Double val = druga.get(k);
				System.out.println(val);
			}
			
	    }  
	}

}
