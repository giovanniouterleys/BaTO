package fr.outerleys.giovanni.game;

import fr.outerleys.giovanni.engine.items.GameItemType;
import org.joml.primitives.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import fr.outerleys.giovanni.engine.graph.Camera;
import fr.outerleys.giovanni.engine.items.GameItem;

import java.util.concurrent.TimeUnit;

public class CameraBoxSelectionDetector {

    private final Vector3f max;

    private final Vector3f min;

    private final Vector2f nearFar;

    private Vector3f dir;

    public CameraBoxSelectionDetector() {
        dir = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
        nearFar = new Vector2f();
    }

    public void selectGameItem(GameItem[] gameItems, Camera camera) {
        dir = camera.getViewMatrix().positiveZ(dir).negate();
        selectGameItem(gameItems, camera.getPosition(), dir);
    }

    protected void selectGameItem(GameItem[] gameItems, Vector3f center, Vector3f dir) {
        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        for (GameItem gameItem : gameItems) {
            if(gameItem!=null){
                gameItem.setSelected(false);
                min.set(gameItem.getPosition());
                max.set(gameItem.getPosition());
                if(gameItem.getItemType() == GameItemType.ROCKET){

                    min.add(-gameItem.getScale(), -gameItem.getScale()-25, -gameItem.getScale()-25);
                    max.add(gameItem.getScale(), gameItem.getScale()+25, gameItem.getScale()+25);
                } else if(gameItem.getItemType() == GameItemType.PLANET){
                    min.add(-gameItem.getScale(), -gameItem.getScale()-25, -gameItem.getScale()-25);
                    max.add(gameItem.getScale(), gameItem.getScale()+25, gameItem.getScale()+25);
                } else {
                    min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
                    max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
                }
                System.out.println("Reporting: " + Intersectionf.intersectRayAab(center, dir, min, max, nearFar) + "nearFar X value: " + nearFar.x + ", closesDistance: " + closestDistance);
                if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
                    closestDistance = nearFar.x;
                    selectedGameItem = gameItem;
                } else {
                    System.out.println("x"+nearFar.x+",y"+nearFar.y);
                }
            }
        }

        if (selectedGameItem != null) {
            selectedGameItem.setSelected(true);
            if(selectedGameItem.getItemType()==GameItemType.ROCKET){
                for(GameItem gameItem : gameItems){
                    if(gameItem!=null) {
                        gameItem.setLastSelected(false); // removing 4all
                    }
                }
                selectedGameItem.setLastSelected(true);
            } else if( selectedGameItem.getItemType() == GameItemType.PLANET){
                // planet +>
                for(GameItem gameItem : gameItems){
                    if(gameItem!=null){
                        gameItem.setLastHarborSelected(false);
                        if(gameItem.getItemType()==GameItemType.ROCKET){
                            if(gameItem.isLastSelected()){
                                // move ship

                                if(gameItem.getBateau().getArriveePort() != null && !(selectedGameItem.getPort().getQuais().getQuaisOcc()>=selectedGameItem.getPort().getQuais().getNbQuais())){
                                    gameItem.getBateau().setDepartPort(gameItem.getBateau().getArriveePort());
                                    gameItem.getBateau().quitter();
                                }

                                if(gameItem.getBateau().accoster(selectedGameItem.getPort())){
                                    gameItem.setMoving(true);
                                    selectedGameItem.setSelected(false);
                                    // Calc eucli dist


                                } else {
                                    gameItem.getBateau().setPortIndisponnible(selectedGameItem.getPort());
                                    float x = gameItem.getPosition().x;
                                    float y = gameItem.getPosition().y;
                                    float z = gameItem.getPosition().z;
                                    gameItem.setPositionBack(x,y,z);
                                  //  gameItem.setMovingButReturnToBase(true);
                                    selectedGameItem.setSelected(false);
                                }
                            }
                        }
                    }
                }
                selectedGameItem.setLastHarborSelected(true);
            }
        }
    }
}

