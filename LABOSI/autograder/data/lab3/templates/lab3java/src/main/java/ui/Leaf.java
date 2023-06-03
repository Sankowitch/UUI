package ui;

public class Leaf extends Upper{
    String odluka ="";
    public Leaf(String odluka) {
        super();
        this.odluka = odluka;
    }

    public String getOdluka() {
        return this.odluka;
    }

    @Override
  public String toString() {
      return this.odluka;
  }

}
