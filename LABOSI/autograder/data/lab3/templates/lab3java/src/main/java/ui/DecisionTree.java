package ui;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class DecisionTree {
    public static Double velikientropy = 0.;
    public static String odluka = "";
    public static ArrayList<String> grane = new ArrayList<>();
    public static ArrayList<String> predikcije = new ArrayList<>();
    public static TreeMap<String, TreeMap<String, Integer>> matrix = new TreeMap<>();
    public static TreeSet<String> konacneVrijednosti = new TreeSet<>();
    TreeSet<String> konacneVrijednostiUzStablo = null;
    public static HashMap<String, String> maxKonacnih = new HashMap<>();
    Node pocetakStabla = null;

    public DecisionTree() {
        this.pocetakStabla = null;
        this.konacneVrijednostiUzStablo = new TreeSet<>();

    }


    public  Node getPocetakStabla() {
        return this.pocetakStabla;
    }

    //funkcija stabla
    //train
    public void train(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> dataset) {
        odluka = znacajke.get(znacajke.size() - 1);
        Upper up = ID3(znacajke, dataset, dataset, "", 0);
        if (up instanceof Node) {
            Node upNode = (Node) up;
            pocetakStabla = upNode;
            
        }
        printBranches();
        this.konacneVrijednostiUzStablo.addAll(konacneVrijednosti);
    }

    public void trainSogranicenjem(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> dataset, int dubina) {
        odluka = znacajke.get(znacajke.size() - 1);
        Upper up = ID3bounds(znacajke, dataset, dataset, "", 0, dubina);
        if (up instanceof Node) {
            Node upNode = (Node) up;
            pocetakStabla = upNode;
            //System.out.println(pocetakStabla);
            
        }
        printBranches();
        this.konacneVrijednostiUzStablo.addAll(konacneVrijednosti);
        //System.out.println(konacneVrijednosti);
       
    }

    public void predict(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> testset) {
        odluka = znacajke.get(znacajke.size() - 1);
        int brojPrimjeraka = testset.size();
        int brojTocnihPrimjeraka = 0;
        createMatrix();
        for (HashMap map : testset) {
            HashMap<String, String> primjerak = map;
            String value = findValue(primjerak, getPocetakStabla() );
            String valueKojiTrebaBiti = (String) map.get(odluka);
            predikcije.add(value);
            
            if (value.equals(valueKojiTrebaBiti)) {
                brojTocnihPrimjeraka = brojTocnihPrimjeraka + 1;
            }
            TreeMap<String, Integer> mapa = new TreeMap<>();
            mapa = matrix.get(valueKojiTrebaBiti);
            int povecaj = mapa.get(value);
            mapa.put(value, povecaj+1);
            matrix.put(valueKojiTrebaBiti, mapa);
        }
        printPredictions();
        float accuracy = (float)brojTocnihPrimjeraka/brojPrimjeraka;
        System.out.printf("[ACCURACY]: %.5f\n", accuracy);
        printMatrix();

    }




    public static String findValue(HashMap<String, String> primjerak, Node pocetni) {
        String val = "";
        Node pocetak = pocetni;
        boolean nasli = false;
        while (true) {
            
            String znacajka = pocetak.getZnacajka();
            String valueZnacajkePrimjerak = primjerak.get(znacajka);
            ArrayList<Pair> parovi = pocetak.getPodstabla();
            Upper nekiNode = new Upper();
            nasli = false;
            
            petlja:
            for (Pair pair : parovi) {
                String first = pair.getValue();
               
                if (first.equals(valueZnacajkePrimjerak)) {
                    nasli = true;
                    nekiNode = pair.getUpper();
                    if (nekiNode instanceof Leaf) {
                        Leaf leaf = (Leaf) nekiNode;
                        
                        val = leaf.toString();
                        return val;
                        //break;
                    } else if (nekiNode instanceof Node) {
                        pocetak = (Node) nekiNode;
                        
                    }
                    break petlja;
                }
            } if (nasli == false ) {
                return maxKonacnih.get(znacajka);
            }
            
            
        }
        
        
    }

    public void  createMatrix() {
       
        for (String s : this.konacneVrijednostiUzStablo) {
            String key = s;
            TreeMap<String, Integer> val = new TreeMap<>();
            for (String k : this.konacneVrijednostiUzStablo) {
                String keyDva = k;
                val.put(k, 0);
            }
            matrix.put(key, val);
            
        }
        
    }


    public static void printBranches() {
        ArrayList<String> stringovi = new ArrayList<>();
        System.out.println("[BRANCHES]:");
        for (String s : grane) {
            System.out.println(s);
        }
        

    }


    public static void printPredictions() {
        String printaj =  "[PREDICTIONS]:";
        for (String s : predikcije) {
            printaj = printaj + " " + s;
        }
        System.out.println(printaj);
    }

    public static void printMatrix() {
        System.out.println("[CONFUSION_MATRIX]:");
        for (String s : matrix.keySet()) {
            String redak = "";
            TreeMap<String, Integer> mapa =  matrix.get(s);
            int size = mapa.size();
            int i = 0;
            for (String k :mapa.keySet() ) {
                 int val = mapa.get(k);
                 redak =redak + Integer.toString(val);
                 if (i < size) {
                     redak = redak + " ";
                 }
            }
            System.out.println(redak);
        }
    }

    public static Upper ID3bounds(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> D, ArrayList<HashMap<String, String>> Dparent, String str, int ind, int dubina) {
        if (ind == dubina) {
            String z = argmax(znacajke, D);
            konacneVrijednosti.add(z);
            grane.add(str + z);
            return new Leaf(z);
        }
        
        
        if (D.isEmpty()) {
            String v = argmax(znacajke, Dparent);
            konacneVrijednosti.add(v);
            grane.add(str + v);
           
            return new Leaf(v);
        }
        String v = argmax(znacajke, D);
        if (znacajke.isEmpty()) {//|| D.containsAll(Dparent)) {
            konacneVrijednosti.add(v);
            grane.add(str + v);
          
            
            return new Leaf(v);
        }
        String x = getMaxIg(znacajke, D);

        if (x.equals("")) { 
            x = v;
        }
        if (!znacajke.contains(x)) {  //znaci returnao se y jer je E ispao nula ili 1
            //System.out.println("x" + x);
            konacneVrijednosti.add(x);
            grane.add(str + x);
            return new Leaf(x);
        }
        ind = ind + 1;
        
        
        String salji =  str + Integer.toString(ind) + ":" + x + "=";
       
        //tu su subestovi
        
        
        HashMap<String, ArrayList<HashMap<String, String>>> subtrees = filter(x, D);
        ArrayList<Pair> podstabla = new ArrayList<>();
        for (String s : subtrees.keySet()) {
            ArrayList<HashMap<String, String>> newData = subtrees.get(s);
            znacajke.remove(x);
            
            
           
            
            podstabla.add(new Pair(s, ID3bounds(znacajke, newData, D, salji + s + " ", ind, dubina)));
            
            
            
            
        }
        return new Node(x, podstabla);
       
    }
       
    
    
    public static Upper ID3(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> D, ArrayList<HashMap<String, String>> Dparent, String str, int ind) {
        if (D.isEmpty()) {
            String v = argmax(znacajke, Dparent);
            konacneVrijednosti.add(v);
            grane.add(str + v);
            return new Leaf(v);
        }
        String v = argmax(znacajke, D);
        if (znacajke.isEmpty()) {
            konacneVrijednosti.add(v);
            grane.add(str + v);
           
            
            return new Leaf(v);
        }
        String x = getMaxIg(znacajke, D);

        if (x.equals("")) { 
            x = v;
        }
        if (!znacajke.contains(x)) {  //znaci returnao se y jer je E ispao nula ili 1
            //System.out.println("x" + x);
            konacneVrijednosti.add(x);
            grane.add(str + x);
            return new Leaf(x);
        }
        ind = ind + 1;
       // System.out.println(ind);
        
        String salji =  str + Integer.toString(ind) + ":" + x + "=";

        //tu su subestovi
        
        
        HashMap<String, ArrayList<HashMap<String, String>>> subtrees = filter(x, D);
        ArrayList<Pair> podstabla = new ArrayList<>();
        for (String s : subtrees.keySet()) {
            ArrayList<HashMap<String, String>> newData = subtrees.get(s);
            znacajke.remove(x);
            
            
           
            
            podstabla.add(new Pair(s, ID3(znacajke, newData, D, salji + s + " ", ind)));
            
            
            
            
        }
        return new Node(x, podstabla);
       
    }


   

    public static HashMap<String, ArrayList<HashMap<String, String>>> filter(String znacajka,ArrayList<HashMap<String, String>> dataset ) {
         HashMap<String, ArrayList<HashMap<String, String>>> rijecnik = new HashMap<>();
         
         for (HashMap map : dataset) {
             String key = (String) map.get(znacajka);
             map.remove(znacajka);
             ArrayList<HashMap<String, String>> lista = new ArrayList<>(); 
             if (rijecnik.containsKey(key)) {
                 lista = rijecnik.get(key);
                 
          
             } 
             lista.add(map);
             rijecnik.put(key, lista);
         }
          //System.out.println(rijecnik);
         return rijecnik;
    }

    

    public static String argmax(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> dataset) {
        
        TreeMap<String, Integer> max = new TreeMap<>();
    

        for (HashMap map : dataset) {
            HashMap<String, String> ubijme =  (HashMap<String, String>) map;
            String vrijednostY = ubijme.get(odluka);
            if (max.containsKey(vrijednostY)) {
                max.put(vrijednostY,max.get(vrijednostY) + 1 );
            } else {
                max.put(vrijednostY, 1);
            }
            konacneVrijednosti.add(vrijednostY);

        }

        String maximum = "";
        int maxNum = 0;
        for (String key : max.keySet()) {
            if (max.get(key) > maxNum) {
                maxNum = max.get(key);
                maximum = key;
            }
        }
        
        return maximum;

    }


    public static String getMaxIg(ArrayList<String> znacajke, ArrayList<HashMap<String, String>> dataset) {
        LinkedHashMap<String, HashMap<String, HashMap<String, Integer>> >  counter = new LinkedHashMap<>();
        
        HashMap<String, Integer> brojOdluka = new HashMap<>();   //yes: n no: m..
        String akoJeEnula = "";

        //System.out.println("ODLUKA " + odluka);

        if (!dataset.isEmpty()) {
            HashMap<String, String> datasetEl = new HashMap<>();
            datasetEl = dataset.get(0);
            if (datasetEl.size() > znacajke.size()) {
                for (String key : datasetEl.keySet()) {
                    if (!znacajke.contains(key)) {
                        znacajke.add(key);
                    }
                }

            } 

        }
        
        //System.out.println("znacajke "+ znacajke);

        for (String znacajka : znacajke) {
            
            if (znacajka.equals(odluka)) {
                for (Map mapa : dataset) {
                    HashMap<String, String> ubijme =  (HashMap<String, String>) mapa;
                    if (ubijme.containsKey(znacajka)) {
                        String key = ubijme.get(znacajka);
                        akoJeEnula = key;
                        if (!brojOdluka.containsKey(key)) {
                            brojOdluka.put(key, 1);
                        } else {
                            int value = brojOdluka.get(key);
                            brojOdluka.put(key, value+1);
                        }
                    }
                }
                
                continue;
            }
            //vrijeme: suncano : n , rainy : m ..
            HashMap<String, HashMap<String, Integer>> vrijednosti = new HashMap<>();
            for (HashMap mapa : dataset) {
                HashMap<String, String> ubijme =  mapa;
                if (ubijme.containsKey(znacajka)) {
                    String key = ubijme.get(znacajka);
                    String keyDva = ubijme.get(odluka);
                    HashMap<String, Integer> broj = new HashMap<>();
                    if (!vrijednosti.containsKey(key)) {
                        broj.put(keyDva, 1);
                        
                    } else {
                        broj = vrijednosti.get(key);
                        if (!broj.containsKey(keyDva)) {
                            broj.put(keyDva, 1);
                        } else {
                            int value = broj.get(keyDva);
                            broj.put(keyDva, value + 1);
                        }

                    }
                    vrijednosti.put(key, broj);
                }
            }
            counter.put(znacajka, vrijednosti);
        }

        

        
        ArrayList<Double> lista = new ArrayList<>();
        double num = 0.;
        for (String s : brojOdluka.keySet()) {
            lista.add(Double.valueOf(brojOdluka.get(s)));
            num = num + brojOdluka.get(s);
        }
        
        double entropy = E(lista, num);
        //System.out.println("ENTROPIJA " + entropy);
        if (entropy == 0.0) {
            velikientropy = 0.;
            return argmax(znacajke, dataset);

        }
        if (entropy == 1) {
            velikientropy = 1.;
            //return argmax(znacajke, dataset);
        }
        

        
        

        HashMap<String,Double> IGs = new HashMap<>();

   
        for (String key : counter.keySet()) {
           
            HashMap<String, HashMap<String, Integer>> rijecnik = counter.get(key);
            ArrayList<Double> entropies = new ArrayList<>();
            ArrayList<Double> entropiesK = new ArrayList<>();
            for (String keyDva : rijecnik.keySet()) {
                HashMap<String, Integer> rijecnikDva = rijecnik.get(keyDva);
                ArrayList<Double> list = new ArrayList<>();
                double brojVar = 0.;
                for (int keyTri : rijecnikDva.values()) {
                    list.add(Double.valueOf(keyTri));
                    brojVar = brojVar + keyTri;
                }
                int sizeOdluka = brojOdluka.size();
                if (sizeOdluka > list.size()) {
                    //treba dodati nule u list
                    for (int i = 0; i < sizeOdluka - list.size() ; i++) {
                        list.add(0.0);
                    }
                }
                double entropy_i = E(list, brojVar);
                entropies.add(entropy_i);
                entropiesK.add(brojVar/num);
                

            }
            double ig = IG(entropy, entropies, entropiesK);
            IGs.put(key, ig);

        }
       
        String max = "";
        Double maxValue = 0.;
        for (String key : IGs.keySet()) {
            if (IGs.get(key) > maxValue) {
                max = key;
                maxValue = IGs.get(key);
            }
        }
        
        int najveciBroj = 0;
        String najvecaOdluka = "x";
        //System.out.println(brojOdluka);
        for (String s : brojOdluka.keySet()) {
            if (brojOdluka.get(s) >= najveciBroj) {
                if (najvecaOdluka.compareTo(s) > 0) {
                    najvecaOdluka = s;
                } 
                najveciBroj = brojOdluka.get(s);
                

            }
        }

         //System.out.println(IGs);
        
        maxKonacnih.put(max, najvecaOdluka) ;
        
        return max;

    }


    //calculate E(d)
    public static double E(List<Double> list,  double c) {
        double d = 0;
        for (double elem : list) {
            if (elem == 0) {
                d = d - 0;
            } else {
                d = d - ((elem/c) * Math.log(elem/c))/ Math.log(2);
            }
        }
        
        return d;

    }

    //calculate ig
    public static double IG(double entropy, ArrayList<Double> entropies, ArrayList<Double> num) {
        int i = 0;
        double ig = entropy;
        for (double e: entropies) {
            ig = ig - e * num.get(i);
            i = i + 1;
        }
       
        
        return ig;

    }
}