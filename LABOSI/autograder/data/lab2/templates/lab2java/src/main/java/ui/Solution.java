package ui;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.util.*;



public class Solution {
    public static boolean nasliNil = false;  //korisitm
    public static LinkedHashSet<Set<String>> setOfSupport = new LinkedHashSet<>();  //koristim
	public static LinkedHashSet<Set<String>> setOfSupportCopy = new LinkedHashSet<>();  //koristim
	public static LinkedHashSet<Set<String>> allClauses = new LinkedHashSet<>();    //koristim
	public static LinkedHashSet<Set<String>> allClausesCopy = new LinkedHashSet<>();    //koristim
	//public static LinkedHashMap<String, String> nastanakRezolventi = new LinkedHashMap<>(); 
	//public static TreeMap<Integer, String> rijecnik = new TreeMap<>();   //sifra, klauzula 
	public static LinkedHashMap<String, Set<String>> roditelji = new LinkedHashMap<>();  //korisitm
	public static LinkedHashMap<String, Set<String>> roditeljiDupli = new LinkedHashMap<>();  //za cooking
	public static Integer sifra = 1;
	public static boolean nasliAps = false;

	
	
	public static void main(String ... args) {
		int ind = 0;
		boolean res=false;
		boolean cooking = false;
		String putanja="";   //putanja di su klauzule
		String putanjaInput ="";    //

		//ucitaj iz retka
		for(String a : args) {
			if (a.equals("resolution")) {
				res= true;
			} if (a.equals("cooking")) {
                cooking = true;
			}  
			if (ind==1) {
				putanja = a;
			}
			if (ind == 2) {
                putanjaInput = a;
			}
			
			ind = ind + 1;

		}

		//ucitaavanje klauzula
		Set<Set<String>> pocetne = new LinkedHashSet<>();
		
		LinkedHashSet<String> konacno = new LinkedHashSet<>();
		LinkedHashSet<String> pocetnaZaP = new LinkedHashSet<>();   //upisujemo string pocetnih funckija za prinntati jer mi se ne da iterirati kasnije
		String stringKlauzula="";
		try {
			File myObj = new File(putanja);
			Scanner myReader = new Scanner(myObj, "utf-8");
			ind = 0;
			boolean zadnja = false;
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				data =  data.toLowerCase();
				stringKlauzula = data;
				
				String[] pomoc = data.split(" v ");

				TreeSet<String> klauzula = new TreeSet<>();
             
				if (data.contains("#")) {
					continue;
				} if (!myReader.hasNextLine() && cooking == false) {
					zadnja = true;
					String nova="";
					for (String s: pomoc) {
						TreeSet<String> klauz = new TreeSet<>();
						if (s.contains("~")) {
                            nova = s.replaceAll("~", "");
						} else {
							nova = "~" + s;
						}
						
						klauz.add(nova);
						setOfSupport.add(klauz);
						pocetnaZaP.add(nova);
						konacno.addAll(klauz);
					    //rijecnik.put(sifra, nova);
						sifra = sifra + 1;
					}
					break;
				}
			
				for (String s : pomoc) {
					klauzula.add(s);
				}
				pocetnaZaP.add(stringZaRez(klauzula));
				allClauses.add(klauzula);
			    pocetne.add(klauzula);
				//rijecnik.put(sifra, stringKlauzula);
				sifra = sifra + 1;

				

			}
			myReader.close();

		} catch (FileNotFoundException e) {
			System.out.println("KOJI F U CHATU.");
			e.printStackTrace();
		  } 


		
        //resolution
		if (res==true) {
			 resolution();
			 printanje(pocetnaZaP, stringKlauzula, false);
		}

		//cooking
		if (cooking == true) {
		    allClausesCopy.addAll(allClauses);
			setOfSupportCopy.addAll(setOfSupport);
			
			//treba procitati iz putanje 2
			try {
			    File myObj = new File(putanjaInput);
			    Scanner myReader = new Scanner(myObj, "utf-8");
				while (myReader.hasNextLine()) {
					boolean upitnik = false;
				    boolean plus = false;
				    boolean minus = false;
                    String ulaz = myReader.nextLine();
					ulaz =  ulaz.toLowerCase();
					System.out.println("User's command: " + ulaz);
					String ivan="";  //ponestaju mi imena
					if (ulaz.contains("?")) {
                        upitnik = true;
						ivan = ulaz.replaceAll("\\?", "");
					} if (ulaz.contains("-")) {
						minus = true;
                        ivan = ulaz.replaceAll("\\-", "");
					} if (ulaz.contains("+")) {
                        plus = true;
						ivan = ulaz.replaceAll("\\+", "");
					}
					ivan=ivan.substring(0, ivan.length() - 1);  //micem onaj zadnji razmak 
					String[] pomoc = ivan.split(" v ");
					TreeSet<String> klauz = new TreeSet<>();
					String zaMaknuti="";
					for (String s :  pomoc) {
						s = s.replaceAll("\\s", "");
						if (upitnik == true) {
						    if (s.contains("~") ) {
							    s= s.replaceAll("~" , "");
						     } else {
							    s= "~" +  s;
						    } 
					    }
						zaMaknuti = s;
						klauz.add(s);
						nasliNil = false;
						roditelji.clear();

                        
						if (upitnik == true) {
							setOfSupport.add(klauz);
							pocetnaZaP.add(s);
						    konacno.addAll(klauz);
					        resolution();
							printanje(pocetnaZaP, ivan, cooking);
							
							allClauses.clear();
							setOfSupport.clear();
							pocetnaZaP.remove(s);
							konacno.removeAll(klauz);
						    allClauses.addAll(allClausesCopy);
							setOfSupport.addAll(setOfSupportCopy);
							//printajSetSet(setOfSupport);
						}
					} if (minus == true) {
						    setOfSupport.remove(klauz);
							allClauses.remove(klauz);
							setOfSupportCopy.remove(klauz);
							allClausesCopy.remove(klauz);
							konacno.removeAll(klauz);
							pocetnaZaP.remove(zaMaknuti);
							System.out.println("removed " + ivan);
							
					} if (plus == true) {
							setOfSupport.add(klauz);
                            allClauses.add(klauz);
							setOfSupportCopy.add(klauz);
							allClausesCopy.add(klauz);
							System.out.println("Added " + ivan);
							
					}
					
					
				}				

			    myReader.close();


		    } catch (FileNotFoundException e) {
			    System.out.println("KOJI F U CHATU.");
			    e.printStackTrace();
		    }
		}


	}

	public static void printajSetSet(Set<Set<String>> setset) {
		for (Set<String> set : setOfSupport) {
			String b="";
            for (String s: set) {
                b = b + s + " ";
			}
			System.out.println(b);
		}
	}

    public static void reallyJan() {
		Set<String> novaK = new TreeSet<>();
		Set<Set<String>> newKlauzule = new LinkedHashSet<>();
		String negacija="";
		
		Set<Set<String>> zaApsorbirati = new LinkedHashSet<>();
		for (Set<String> set1 : allClauses) {
			
			for (Set<String> set2 : allClauses) {
				
				for (String s: set1) {
					if (s.contains("~")) {
						//s je negiran
						negacija = s.replace("~", "");
					} else {
						//s nije negiran
						negacija = "~" + s;
					}	
                     
					novaK= nadiKlauzule(s, negacija, set1, set2);
					if (novaK.isEmpty()) {
						if (nasliNil == true) {
							return;
						}
						continue;
					}

					if (nasliAps == true) {
						nasliAps = false;
						zaApsorbirati.add(novaK);
						continue;
					}
					newKlauzule.add(novaK);
				}
			}
		}
        if (!zaApsorbirati.isEmpty()) {
			allClauses.removeAll(zaApsorbirati);
			setOfSupport.removeAll(zaApsorbirati);
		}

		if (!newKlauzule.isEmpty()) {
            setOfSupport.addAll(newKlauzule);
		}
		
        allClauses.addAll(setOfSupport);
		
	}


	public static void resolution() {
        Set<String> novaK = new TreeSet<>();
		Set<Set<String>> newKlauzule = new LinkedHashSet<>();   //nova
        reallyJan();
		if (nasliNil == true) {
			return;
		}


		pocetak:
		while(true) {
			Set<Set<String>> zaApsorbirati = new LinkedHashSet<>();
			
			String negacija="";
			for (Set<String> set1 : setOfSupport) {    //neka prodje kroz pocetne prvo
               
				for (Set<String> set2 : allClauses) {
					
					for (String s: set1) {
						if (s.contains("~")) {
							//s je negiran
							negacija = s.replace("~", "");
						} else {
							//s nije negiran
							negacija = "~" + s;
						}
						
						novaK= nadiKlauzule(s, negacija, set1, set2);
						if (novaK.isEmpty()) {
							if (nasliNil == true) {
								break pocetak;
							}
							continue;
						}
						if (nasliAps == true) {
							nasliAps = false;
							zaApsorbirati.add(novaK);
							continue;
						}
						newKlauzule.add(novaK);
					}

                    
				}
				
                

            } 
			if (allClauses.containsAll(newKlauzule)) {
				
				break;
			}

			if (!zaApsorbirati.isEmpty()) {
				allClauses.removeAll(zaApsorbirati);
				setOfSupport.removeAll(zaApsorbirati);
			}
 			allClauses.addAll(newKlauzule);
			setOfSupport.addAll(newKlauzule);
			
		} 

	}

	
	public static boolean provjeriTautologiju(Set<String> set) {
		for (String s : set) {
			String negacija = "~" + s;
			if (s.contains("~")) {
				negacija= s.replace("~", "");
			}
			if (set.contains(negacija)) {
                //pronasli smo tautologiju
				return true;
			}
		}
		return false;
	}


    public static String stringZaRez(Set<String> set) {
		String rez="";
		int i = 0;
		for (String var: set) {
			rez = rez + var;
			if (i <= set.size() - 2) {
				rez = rez + " v ";
			}
			i = i + 1;
			
		}
		return rez;

	}


	public static Set<String> nadiKlauzule(String s, String negacija, Set<String> set1, Set<String> set2) {
        Set<String> novaK = new TreeSet<>();
		if (set2.contains(s)) {
			if (set1.size() == 1 || set2.size() ==1 ) {
				nasliAps =  true;
				if ( set2.size() > 1) {
					novaK.addAll(set2);
				} else if (set1.size() > 1) {
					novaK.addAll(set1);
				}
			} 
			return novaK;			
		} 
		if (set2.contains(negacija)) {
			String rezolventa = "";
			String roditelj1 = stringZaRez(set1);
			String roditelj2 = stringZaRez(set2);
			if (set1.size() == 1 && set2.size() == 1) {
                nasliNil = true;
				rezolventa = "NIL";
			} else {
                if (provjeriTautologiju(set1)==true || provjeriTautologiju(set2) == true) {
					nasliAps = true;
					if (provjeriTautologiju(set1)==true) {
						novaK.addAll(set1);
					} else {
						novaK.addAll(set2);
					}
					
					return novaK;
				} 

				novaK.addAll(set1);
				novaK.addAll(set2);
				novaK.remove(s);
				novaK.remove(negacija);
				
				if (provjeriTautologiju(novaK)) {
                    novaK.clear();
					return novaK;
				}
				rezolventa = stringZaRez(novaK);
			}
			
			if (!roditelji.containsKey(rezolventa)) {
				HashSet<String> poljeRod = new HashSet<>();
				poljeRod.add(roditelj1);
                poljeRod.add(roditelj2);
				roditelji.put(rezolventa, poljeRod);
			}
		}
		return novaK;
	}

	

	public static void printanje(Set<String> pocetna, String konacniString, boolean cooking) {
		String con = "[CONCLUSION]: " + konacniString;
		int index = 1;
		for (String s : pocetna) {
			System.out.println(String.valueOf(index) + ". " + s);
		    index = index + 1;
		}
		System.out.println("===============");


		//for (String klauz :roditelji.keySet()) { 
		//	System.out.println(klauz);
		//}
		if (nasliNil == false) {
            System.out.println(con + " is unknown");
			return;
		}
		String klauzula = "NIL";
		LinkedHashSet<String> polje = new LinkedHashSet<>();
		polje.addAll(roditelji.get(klauzula)); 
		
        HashSet<String> set = new HashSet<>();
		set.add(klauzula);
		
		
		while(true) {
			LinkedHashSet<String> poljepomoc = new LinkedHashSet<>();
			for (String s : pocetna) {
				polje.remove(s);
			}
			
			if (polje.isEmpty()) {
				break;
			}
			
		
		    for (String s : polje) {
			    klauzula = s;
                set.add(klauzula);
				if (roditelji.containsKey(s)) {
					poljepomoc.addAll(roditelji.get(s));
				}
			    
         
		    } 
			if (poljepomoc.isEmpty()) {
				break;
			}
			else {
				polje.addAll(poljepomoc);
			}
		    polje.removeAll(set);
		    
		
	    
	    }
		
		for (String klauz :roditelji.keySet()) {
            if (set.contains(klauz)) {
				Set<String> rod = roditelji.get(klauz);
				String rod_str = "";
				int a = 0;
				for (String k : rod) {
                   if (a == 0) {
					rod_str ="( " + k + " ) + ( ";
					a++;
				   } else {
					rod_str = rod_str + k + " )"; }

				}
				System.out.println(String.valueOf(index) + ". " + klauz  + " <= " + rod_str);
				index ++;
			}

		}
		System.out.println("===============");
		System.out.println(con + " is true");
		
   

	}

	

}