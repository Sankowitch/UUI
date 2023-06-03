package ui;

import java.util.ArrayList;


public class Node extends Upper{
    String znacajka = null;
    ArrayList<Pair>  podstabla= new ArrayList();
    
    public Node(String znacajka, ArrayList<Pair> podstabla) {
        super();
        this.znacajka = znacajka;
        this.podstabla = podstabla;
        
    }

    public String getZnacajka() {
        return this.znacajka;
    }

    public ArrayList<Pair> getPodstabla() {
        return this.podstabla;
    }

   // public String arrayStringify() {
        
    //}

    

  @Override
  public String toString() {
      return this.znacajka + " " + podstabla;
  }
    
}
