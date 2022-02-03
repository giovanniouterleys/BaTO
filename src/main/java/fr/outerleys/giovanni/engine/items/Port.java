package fr.outerleys.giovanni.engine.items;

public class Port {

    private float x;
    private float y;
    private float z;
    private Quais quais;

    public Port(float x1, float y1, float z1){
        this.x = x1;
        this.y = y1;
        this.z = z1;
        this.quais = new Quais();
    }

    public Port(float x1, float y1, float z1,int nbQuais){
        this.x = x1;
        this.y = y1;
        this.z = z1;
        this.quais = new Quais(nbQuais);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public boolean ajouterBateau(){
        return this.quais.ajouterBateau();
    }

    public void retirerBateau(){
        this.quais.retraitBateau();
    }

    public Quais getQuais(){
        return quais;
    }

}
