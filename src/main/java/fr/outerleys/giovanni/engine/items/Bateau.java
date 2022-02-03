package fr.outerleys.giovanni.engine.items;

public class Bateau {

    private Port departPort;
    private Port arriveePort;
    private Port actualHarbor;
    private Port portIndisponnible;

    boolean enMer;

    float x,y;

    public Bateau(){
        this.departPort = null;
        this.arriveePort = null;
        this.enMer = true;
    }

    public Bateau(Port port){
        if(port.ajouterBateau()){
            // We can go
            this.departPort = port;
            this.arriveePort = port;
            this.enMer = false;
            setPosition(port.getX(), port.getY());
        } else {
            this.departPort = null;
            this.arriveePort = null;
            this.enMer = true;
        }
    }

    public boolean accoster(Port a){
        if(a.ajouterBateau()){
            // OK
            System.out.println("A:"+a.getX());
            this.arriveePort = a;
            this.enMer = false;
            setPosition(a.getX(), a.getY());
            System.out.println("Accoster: " + arriveePort.getX());
            return true;
        }
        return false;
    }

    public void quitter(){
        if(arriveePort!=null){
            // On an harbor
            this.arriveePort.retirerBateau();
            this.arriveePort = null;
            //this.actualHarbor = null;
            this.x = 0.0f;
            this.y = 0.0f;
            this.enMer = true;
        }
    }

    public Port getDepartPort(){
        return departPort;
    }

    public Port getArriveePort(){
        return arriveePort;
    }

    public Port getActualHarbor() {
        return actualHarbor;
    }

    public void setActualHarbor(Port actualHarbor) {
        this.actualHarbor = actualHarbor;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setDepartPort(Port departPort) {
        this.departPort = departPort;
    }

    public Port getPortIndisponnible() {
        return portIndisponnible;
    }

    public void setPortIndisponnible(Port portIndisponnible) {
        this.portIndisponnible = portIndisponnible;
    }
}
