package fr.outerleys.giovanni.engine.items;

public class Quais {
    int nbQuais; // nombre de quais du port
    int quaisOcc; // nombre de quais occupé dans le port

    public Quais(){
        this.nbQuais = 3;
        this.quaisOcc = 0;
    }

    public Quais(int nbQuais){
        if(nbQuais<=0){
            this.nbQuais = 3;
            this.quaisOcc = 0;
        }else{
            // nbQuais > à 0
            this.nbQuais = nbQuais;
            this.quaisOcc = 0;
        }
    }

    public boolean ajouterBateau(){
        if(quaisOcc<nbQuais){
            quaisOcc++;
            return true;
        }
        return false;
    }

    public void retraitBateau(){
        quaisOcc--;
    }

    public int getNbQuais() {
        return nbQuais;
    }

    public int getQuaisOcc() {
        return quaisOcc;
    }
}
