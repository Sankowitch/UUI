import java.util.*;

public class Cvor implements Comparable<Cvor> {
    private String ime;
    private int dubina;
    private double udaljenost;
    private String roditelj;  
    private double funkcija;

    public Cvor(String ime, int dubina, double udaljenost, String roditelj, double funkcija) {
        this.ime = ime;
        this.dubina = dubina;
        this.udaljenost = udaljenost;
        this.roditelj = roditelj;
        this.funkcija = funkcija;
    }


    public String getIme() {
        return ime;
    }
    public int getDubina() {
        return dubina;
    }
    public double  getUdaljenost() {
        return udaljenost;
    }
    public String getRoditelj() {
        return roditelj;
    }
    public Double getFunkcija() {
        return funkcija;
    }

    public void setUdaljenost(double udaljenost) {
        this.udaljenost = udaljenost;
    }

    public void setFunkcija(double funkcija) {
        this.funkcija = funkcija;
    }

    @Override
    public String toString(){
        return String.format("(%s, %d, %.2f)", ime, dubina, udaljenost);
        }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cvor)) {
            return false;
        }
        Cvor c = (Cvor) o;
       
        return ime.equals(c.ime) && dubina == c.dubina && udaljenost == c.udaljenost;  
     }
    
     @Override
     public int hashCode() {
         return Objects.hash(ime, dubina, udaljenost);
     }

     @Override
    public int compareTo(Cvor other) {
         
        int a = Double.compare(getUdaljenost(), other.getUdaljenost());
        if (a!=0) {
            return a;
        } 
        a = getIme().compareTo(other.getIme());
        if (a!=0) {
            return a;
        }
        
        return Integer.compare(getDubina(), other.getDubina());
    }

}
