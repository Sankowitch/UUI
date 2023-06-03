package ui;

public class Pair {
    String value ="";
    Upper korijen = new Upper();

    public Pair(String value, Upper korijen) {
        this.value = value;
        this.korijen = korijen;
    }

    public String getValue() {
        return this.value;
    }

    public Upper getUpper() {
        return this.korijen;
    }



    @Override
    public String toString() {
        if ( this.korijen instanceof Leaf) {
            this.korijen = (Leaf) this.korijen;
        } 
        if (this.korijen instanceof Node) {
            this.korijen = (Node) this.korijen;
        }
        return "( " + this.value + ", " + this.korijen + ")";
    }

}
