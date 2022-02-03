package fr.outerleys.giovanni.engine.items;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import fr.outerleys.giovanni.engine.graph.Mesh;

public class GameItem{

    private boolean selected;

    private boolean lastSelected;

    private boolean lastHarborSelected;

    private Mesh[] meshes;

    private final Vector3f position;

    private final Vector3f positionBack;

    private float scale;

    private final Quaternionf rotation;

    private int textPos;

    private boolean disableFrustumCulling;

    private boolean insideFrustum;

    private GameItemType itemType;

    private Port port;

    private Port portIndisponnible;

    private Bateau bateau;

    private boolean isMoving;

    private boolean isMovingButReturnToBase;

    private boolean returnToBase;

    /*
        S H I P
     */

    private String name;
    private int actualShip;
    private int maxShip;

    public GameItem() {
        selected = false;
        position = new Vector3f();
        positionBack = new Vector3f();
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
        itemType = GameItemType.undefined;

        /*
            S H  I P
         */
        name = "";
        actualShip = 0;
        maxShip = 0;
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes, int nbQuais, float x1, float y1, float z1) {
        /*
            USING THIS METHOD IF THE MESHES OBJECTS ARE AN HARBOR
         */
        this();
        this.meshes = meshes;
        if(nbQuais>0){
            port = new Port(x1, y1, z1, nbQuais);
        } else {
            port = new Port(x1, y1, z1);
        }
        setItemType(GameItemType.PLANET);
    }

    public GameItem(Mesh[] meshes, float x, float y, Port port) {
        /*
            USING THIS METHOD IF THE MESHES OBJECTS ARE AN SHIP
         */
        this();
        this.meshes = meshes;
        if(port==null){
            // Ship on the ocean
            bateau = new Bateau();
            bateau.setPosition(x,y);
        } else {
            bateau = new Bateau(port);
            bateau.setPosition(port.getX(),port.getY());
            setPosition(port.getX(), port.getY() + 25, port.getZ());
        }
        setItemType(GameItemType.ROCKET);
    }

    public Port getPort(){
        return port;
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isLastSelected() {
        return lastSelected;
    }

    public void setLastSelected(boolean lastSelected) {
        this.lastSelected = lastSelected;
    }

    public boolean isLastHarborSelected() {
        return lastHarborSelected;
    }

    public void setLastHarborSelected(boolean lastHarborSelected) {
        this.lastHarborSelected = lastHarborSelected;
    }

    public final void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public GameItemType getItemType(){
        return itemType;
    }

    public void setItemType(GameItemType itemType){
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActualShip() {
        return actualShip;
    }

    public void setActualShip(int actualShip) {
        this.actualShip = actualShip;
    }

    public int getMaxShip() {
        return maxShip;
    }

    public void setMaxShip(int maxShip) {
        this.maxShip = maxShip;
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }

    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }

    public Bateau getBateau(){
        return bateau;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean isMovingButReturnToBase() {
        return isMovingButReturnToBase;
    }

    public void setMovingButReturnToBase(boolean movingButReturnToBase) {
        isMovingButReturnToBase = movingButReturnToBase;
    }

    public boolean isReturnToBase() {
        return returnToBase;
    }

    public void setReturnToBase(boolean returnToBase) {
        this.returnToBase = returnToBase;
    }

    public Vector3f getPositionBack() {
        return positionBack;
    }

    public void setPositionBack(float x, float y, float z){
        this.positionBack.x = x;
        this.positionBack.y = y;
        this.positionBack.z = z;
    }
}
