package fr.outerleys.giovanni.game;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import fr.outerleys.giovanni.engine.*;
import fr.outerleys.giovanni.engine.graph.*;
import fr.outerleys.giovanni.engine.items.*;
import fr.outerleys.giovanni.engine.loaders.assimp.StaticMeshesLoader;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.stb.STBImage.*;

import org.lwjgl.system.MemoryStack;
import fr.outerleys.giovanni.engine.graph.lights.DirectionalLight;
import fr.outerleys.giovanni.engine.graph.weather.Fog;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private HudGameInfo hudGameInfo;

    private static final float CAMERA_POS_STEP = 0.40f;

    private float angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private MouseBoxSelectionDetector selectDetector;

    private enum Sounds {
        FIRE
    };

    private GameItem[] gameItems;
    private GameItem[] harbors;


    public DummyGame() {
        renderer = new Renderer();
        hud = new Hud();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;
    }

    @Override
    public void init(Window window) throws Exception {
        hud.init(window);
        renderer.init(window);
        scene = new Scene();

        float reflectance = 1f;

        float blockScale = 0.5f;
        float skyBoxScale = 100.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;

        selectDetector = new MouseBoxSelectionDetector();

        ByteBuffer buf;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load("textures/heightmap.png", w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        gameItems = new GameItem[100];
        harbors = new GameItem[3];

        addPlanet("models/BlueMars/13903_Mars_v1_l3.obj", "models/BlueMars", 1, 75, 2, 2, 0.05f, GameItemType.PLANET, "La planet bleu",0,0);
        addPlanet("models/Mars/13903_Mars_v1_l3.obj", "models/Mars", 1, 0, 0, 0, 0.05f, GameItemType.PLANET, "La planet rouge",1,1);
        addPlanet("models/GreenMars/13903_Mars_v1_l3.obj", "models/GreenMars", 3, 100, 0, 75, 0.05f, GameItemType.PLANET, "La planet verte",2,2);
        addShip("models/Falcon9/Falcon9.obj", "models/Falcon9", 25, 8, 0, null/*gameItems[0].getPort()*/, GameItemType.ROCKET,0.5f,3, "Falcon 9 E340");
        addShip("models/Falcon9/Falcon9.obj", "models/Falcon9", 0, 8, 25, null/*gameItems[1].getPort()*/, GameItemType.ROCKET,0.5f,4, "Falcon 9 E660");

        //addShip("models/Boat/woodboat/source/boat.obj", "models/Boat/woodboat/source", 45, 8, 0, null, GameItemType.ROCKET, 0.35f, 4);
        //addShip("models/Boat/fishingboat/58SZWD0K029UFWTBVX5CXJR48.obj", "models/Boat/fishingboat", -45, 8, 0, null, GameItemType.ROCKET, 25.0f, 5);
       // addShip("models/Boat/warboat/Bourrasque.obj", "models/Boat/warboat", 0, 8, 0, null, GameItemType.ROCKET, 5.0f, 6);
        //Sound needed ? https://www.youtube.com/watch?v=f53fti1kwgc
/*
        Mesh[] voyagerMesh = StaticMeshesLoader.load("models/satellite/voyager.obj", "models/satellite");//StaticMeshesLoader.load("models/Earth/voyager.obj", "models/Earth");
        GameItem voyager = new GameItem(voyagerMesh);
        voyager.setItemType(GameItemType.SATELLITE);
        voyager.setName("Voyager");
        voyager.setMaxShip(5);
        voyager.setPosition(10,0,0);
        voyager.setScale(0.25f);
        Quaternionf q = voyager.getRotation();
        q.rotateY(0.00090f);
        voyager.setRotation(q);
        gameItems[3] = voyager;

        Mesh[] jwstMesh = StaticMeshesLoader.load("models/satellite/jwst.obj", "models/satellite");
        GameItem jwst = new GameItem(jwstMesh);
        jwst.setItemType(GameItemType.SATELLITE);
        jwst.setName("James Web");
        jwst.setMaxShip(0);
        jwst.setPosition(10, 0, 0);
        jwst.setScale(0.25f);
        gameItems[4] = jwst;

*/
        scene.setGameItems(gameItems);
        // Shadows
        //scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        //scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("models/skybox/skybox.obj", "models/skybox/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        camera.getPosition().x = 60.0f;
        camera.getPosition().y =  33.0f;
        camera.getPosition().z = 9.0f;
        camera.getRotation().x = 20.0f;
        camera.getRotation().y = 140.f;

      //  hud = new Hud("DEMO");
       // hudGameInfo = new HudGameInfo("OUAIS");

        stbi_image_free(buf);
    }

    public void addShip(String ressourcePath, String texturesDir, float x1, float y1, float z1, Port port, GameItemType type, float scale, int gameItemPlace, String name) throws Exception {
        Mesh[] mesh = StaticMeshesLoader.load(ressourcePath, texturesDir);
        GameItem gameItem = new GameItem(mesh, x1, y1, port);
        gameItem.setItemType(type);
        gameItem.setScale(scale);
        gameItem.setName(name);
        if(port==null){gameItem.setPosition(x1, y1, z1);}
        int textPos = Math.random() > 0.5f ? 0 : 1;
        gameItem.setTextPos(textPos);
        gameItems[gameItemPlace] = gameItem;
    }
    public void addPlanet(String ressourcePath, String texturesDir, int nbQuais, float x1, float y1, float z1, float scale, GameItemType type, String name,int harborPlace, int gameItemPlace) throws Exception {
        Mesh[] mesh1 = StaticMeshesLoader.load(ressourcePath, texturesDir);
        GameItem gameItem1 = new GameItem(mesh1, nbQuais, x1, y1+25,z1);
        gameItem1.setScale(scale);
        gameItem1.setItemType(type);
        gameItem1.setMaxShip(20);
        gameItem1.setName(name);
        gameItem1.setPosition(x1,y1,z1);
        harbors[harborPlace] = gameItem1;
        gameItems[gameItemPlace] = gameItem1;
    }

    public int getGameItemsMap(){
        int i = 0;
        for(GameItem gameItem : gameItems){
            if(gameItem!=null){
                i++;
            }
        }
        return i;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);
    }

    float countX = 0.0f,countY=0.0f,countZ=0.0f;

    @Override
    public void input(Window window, MouseInput mouseInput) {
        sceneChanged = false;
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            sceneChanged = true;
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            sceneChanged = true;
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            sceneChanged = true;
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            sceneChanged = true;
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_X)) {
            sceneChanged = true;
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            sceneChanged = true;
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            sceneChanged = true;
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            sceneChanged = true;
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }

        if(window.isKeyPressed(GLFW_KEY_R)){
            Quaternionf q = gameItems[3].getRotation();
            q.rotateX(countX);
            countX+=0.10f;
            gameItems[3].setRotation(q);
        }

        if(window.isKeyPressed(GLFW_KEY_P)){
            for(GameItem gameItem : gameItems){
                if(gameItem.isSelected()){
                    gameItem.setSelected(false);
                }
            }
        }

        if(window.isKeyPressed(GLFW_KEY_O)){
            Quaternionf q = gameItems[3].getRotation();
            q.rotateZ(countZ);
            countZ+=0.10f;
            gameItems[3].setRotation(q);
        }

        if(window.isKeyPressed(GLFW_KEY_E)){
            for(GameItem gameItem : gameItems){
                if(gameItem!=null){
                    if(gameItem.isSelected()&&gameItem.getItemType()==GameItemType.PLANET){
                        if(gameItem.getPort().getQuais().getQuaisOcc()>=gameItem.getPort().getQuais().getNbQuais()){
                            return;
                        }
                        float x1 = gameItem.getPort().getX();
                        float y1 = gameItem.getPort().getY()+25;
                        float z1 = gameItem.getPort().getZ();
                        try {
                            addShip("models/Falcon9/Falcon9.obj","models/Falcon9",x1,y1,z1,gameItem.getPort(),GameItemType.ROCKET, 0.5f, getGameItemsMap()+1,"New Rocket");
                            scene.setGameItems(gameItems);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if(window.isKeyPressed(GLFW_KEY_R)){
            float x1 = camera.getPosition().x;
            float y1 = camera.getPosition().y;
            float z1 = camera.getPosition().z;
            try {
                addShip("models/Falcon9/Falcon9.obj","models/Falcon9",x1,y1,z1,null,GameItemType.ROCKET, 0.5f, getGameItemsMap()+1,"New Rocket");
                scene.setGameItems(gameItems);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        float cameraX = camera.getPosition().x;
        float cameraY = camera.getPosition().y;
        float cameraZ = camera.getPosition().z;

        //hud.setStatusText("Position x " + cameraX + ", y " + cameraY + ", z " + cameraZ);

       /* for(GameItem gameItem : gameItems){
            if(gameItem.isSelected()){
                hudGameInfo.setStatusText("Infos: " + gameItem.getName() +", " + gameItem.getItemType() + "\nShips: " + gameItem.getActualShip() +"/"+gameItem.getMaxShip());
            }
        }*/

    }
    float count = 0.0F;
    private GameItem lastGameItemSelected;
    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            sceneChanged = true;
        }

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        // Update view matrix
        camera.updateViewMatrix();

        float cameraX = camera.getPosition().x;
        float cameraY = camera.getPosition().y;
        float cameraZ = camera.getPosition().z;

        //gameItems[0].setPosition(cameraX, cameraY - 10, cameraZ);

        double x = Math.sin(0.39269908169872414D * count) * -70;
        double y = 0.3D;
        double z = Math.cos(0.39269908169872414D * count) * -70;
       // gameItems[3].setPosition((float)x, (float)y, (float)z);
     /*  Quaternionf q = gameItems[3].getRotation();
        if(countY>0.0010f){
            countY = 0.0f;
        }
        countY+=0.00090f;
       gameItems[3].setRotation(q);
*/
        count+=0.010f;
        for(GameItem gameItem : gameItems){
/*
            if(gameItem.isSelected() && gameItem.getItemType() == GameItemType.ROCKET){
                gameItem.setPosition(cameraX,cameraY-10, cameraZ);
                // On check ses coordonnés si, il est prêt de la planète rouge, on le dépose
                for(GameItem harbor: harbors){
                    if((cameraX >= harbor.getPosition().x - 50 && cameraX <= harbor.getPosition().x + 50) && (cameraZ >= harbor.getPosition().z - 50 && cameraZ <= harbor.getPosition().z + 50)){
                        // near
                        if(gameItem.getBateau().accoster(harbor.getPort())){ // If ship can accost this harbor..
                            // gameItem.setPosition(gameItems[1].getPosition().x, gameItems[1].getPosition().y + 25, gameItems[1].getPosition().z);
                            gameItem.setSelected(false);
                            gameItem.setMoving(true);

                        } else {
                            System.out.println("Nombre de bateaux: "  + harbor.getPort().getQuais().getQuaisOcc());
                        }
                    }
                }
            }*/
            // moving boat
            if(gameItem!=null){
                if(gameItem.isMoving()){
                    //   float xAdd = gameItem.
                    float xPort = gameItem.getBateau().getArriveePort().getX();
                    float yPort = gameItem.getBateau().getArriveePort().getY();
                    float zPort = gameItem.getBateau().getArriveePort().getZ();

                    float xBateau = gameItem.getPosition().x;
                    float yBateau = gameItem.getPosition().y;
                    float zBateau = gameItem.getPosition().z;

                    // If shipDist < xPort
                    float xDistanceAdd = 0.0f;
                    float yDistanceAdd = 0.0f;
                    float zDistanceAdd = 0.0f;
                    float xFinal = 0.0f;
                    float yFinal = 0.0f;
                    float zFinal = 0.0f;
                    if(xBateau < xPort){
                        xDistanceAdd = (xPort - xBateau) / 100;
                        xFinal = xBateau+xDistanceAdd;
                        // gameItem.setPosition(xBateau+xDistanceAdd, yBateau, zBateau);
                    } else if( xBateau > xPort){
                        xDistanceAdd = (xBateau - xPort) / 100;
                        xFinal = xBateau-xDistanceAdd;
                        //gameItem.setPosition(xBateau-xDistanceAdd, yBateau, zBateau);
                    }

                    if(yBateau < yPort){
                        yDistanceAdd = (yPort - yBateau) / 100;
                        yFinal = yBateau+yDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau+yDistanceAdd, zBateau);
                    } else if( yBateau > yPort){
                        yDistanceAdd = (yBateau - yPort) / 100;
                        yFinal = yBateau-yDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau-yDistanceAdd, zBateau);
                    }

                    if(zBateau < zPort){
                        zDistanceAdd = (zPort - zBateau) / 100;
                        zFinal = zBateau+zDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau, zBateau+zDistanceAdd);
                    } else if( zBateau > zPort){
                        zDistanceAdd = (zBateau - zPort) / 100;
                        zFinal = zBateau-zDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau, zBateau-zDistanceAdd);
                    }
                    sceneChanged = true;

                    gameItem.setPosition(xFinal, yFinal, zFinal);

                    // near method to removing the moving status of the ship
                    if(xBateau>=xPort-3&&xBateau<=xPort+3){
                        if(yBateau>=yPort-3&&yBateau<=yPort+3){
                            if(zBateau>=zPort-3&&zBateau<=zPort+3){
                                gameItem.setMoving(false);
                                gameItem.setLastSelected(false);
                                gameItem.getBateau().setActualHarbor(gameItem.getBateau().getArriveePort());
                            }
                        }
                    }
                    //System.out.println("Je suis en déplacement " + gameItem.getName() + " x: " + gameItem.getPosition().x + " Voici le X du port"  + gameItem.getPort().getX());

                } else if(gameItem.isMovingButReturnToBase()){
                    //   float xAdd = gameItem.
                    float xPort = gameItem.getBateau().getPortIndisponnible().getX();
                    float yPort = gameItem.getBateau().getPortIndisponnible().getY();
                    float zPort = gameItem.getBateau().getPortIndisponnible().getZ();

                    float xBateau = gameItem.getPosition().x;
                    float yBateau = gameItem.getPosition().y;
                    float zBateau = gameItem.getPosition().z;

                    // If shipDist < xPort
                    float xDistanceAdd = 0.0f;
                    float yDistanceAdd = 0.0f;
                    float zDistanceAdd = 0.0f;
                    float xFinal = 0.0f;
                    float yFinal = 0.0f;
                    float zFinal = 0.0f;
                    if(xBateau < xPort){
                        xDistanceAdd = (xPort - xBateau) / 100;
                        xFinal = xBateau+xDistanceAdd;
                        // gameItem.setPosition(xBateau+xDistanceAdd, yBateau, zBateau);
                    } else if( xBateau > xPort){
                        xDistanceAdd = (xBateau - xPort) / 100;
                        xFinal = xBateau-xDistanceAdd;
                        //gameItem.setPosition(xBateau-xDistanceAdd, yBateau, zBateau);
                    }

                    if(yBateau < yPort){
                        yDistanceAdd = (yPort - yBateau) / 100;
                        yFinal = yBateau+yDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau+yDistanceAdd, zBateau);
                    } else if( yBateau > yPort){
                        yDistanceAdd = (yBateau - yPort) / 100;
                        yFinal = yBateau-yDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau-yDistanceAdd, zBateau);
                    }

                    if(zBateau < zPort){
                        zDistanceAdd = (zPort - zBateau) / 100;
                        zFinal = zBateau+zDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau, zBateau+zDistanceAdd);
                    } else if( zBateau > zPort){
                        zDistanceAdd = (zBateau - zPort) / 100;
                        zFinal = zBateau-zDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau, zBateau-zDistanceAdd);
                    }
                    sceneChanged = true;

                    gameItem.setPosition(xFinal, yFinal, zFinal);

                    // near method to removing the moving status of the ship
                    if(xBateau>=xPort-3&&xBateau<=xPort+3){
                        if(yBateau>=yPort-3&&yBateau<=yPort+3){
                            if(zBateau>=zPort-3&&zBateau<=zPort+3){
                                gameItem.setMovingButReturnToBase(false);
                                gameItem.setReturnToBase(true);
                               /* gameItem.setMoving(false);
                                gameItem.setLastSelected(false);
                                gameItem.getBateau().setActualHarbor(gameItem.getBateau().getArriveePort());*/
                            }
                        }
                    }
                } else if(gameItem.isReturnToBase()){
                    //   float xAdd = gameItem.
                    float xPort = gameItem.getPosition().x;
                    float yPort = gameItem.getPosition().y;
                    float zPort = gameItem.getPosition().z;

                    float xBateau = gameItem.getPositionBack().x;
                    float yBateau = gameItem.getPositionBack().y;
                    float zBateau = gameItem.getPositionBack().z;

                    // If shipDist < xPort
                    float xDistanceAdd = 0.0f;
                    float yDistanceAdd = 0.0f;
                    float zDistanceAdd = 0.0f;
                    float xFinal = 0.0f;
                    float yFinal = 0.0f;
                    float zFinal = 0.0f;
                    System.out.println("Called");
                    if(xBateau < xPort){
                        xDistanceAdd = (xPort - xBateau) / 100;
                        xFinal = xPort-xDistanceAdd;
                        // gameItem.setPosition(xBateau+xDistanceAdd, yBateau, zBateau);
                    } else if( xBateau > xPort){
                        xDistanceAdd = (xBateau - xPort) / 100;
                        xFinal = xPort+xDistanceAdd;
                        //gameItem.setPosition(xBateau-xDistanceAdd, yBateau, zBateau);
                    }

                    if(yBateau < yPort){
                        yDistanceAdd = (yPort - yBateau) / 100;
                        yFinal = yPort-yDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau+yDistanceAdd, zBateau);
                    } else if( yBateau > yPort){
                        yDistanceAdd = (yBateau - yPort) / 100;
                        yFinal = yPort+yDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau-yDistanceAdd, zBateau);
                    }

                    if(zBateau < zPort){
                        zDistanceAdd = (zPort - zBateau) / 100;
                        zFinal = zPort-zDistanceAdd;
                        // gameItem.setPosition(xBateau, yBateau, zBateau+zDistanceAdd);
                    } else if( zBateau > zPort){
                        zDistanceAdd = (zBateau - zPort) / 100;
                        zFinal = zPort+zDistanceAdd;
                        //gameItem.setPosition(xBateau, yBateau, zBateau-zDistanceAdd);
                    }
                    sceneChanged = true;

                    gameItem.setPosition(xFinal, yFinal, zFinal);

                    // near method to removing the moving status of the ship
                    if(xPort>=xBateau-3&&xPort<=xBateau+3){
                        if(yPort>=yBateau-3&&yPort<=yBateau+3){
                            if(zPort>=zBateau-3&&zPort<=zBateau+3){
                                gameItem.setReturnToBase(false);
                               /* gameItem.setMoving(false);
                                gameItem.setLastSelected(false);
                                gameItem.getBateau().setActualHarbor(gameItem.getBateau().getArriveePort());*/
                            }
                        }
                    }
                }
            }
        }
  /*      if(gameItems[0].isSelected()){
            // If the boat is selected, adding coords

            gameItems[0].setPosition(cameraX,cameraY-10, cameraZ);
            Quaternionf quaternionf = new Quaternionf();
            //quaternionf.rotateX(camera.getRotation().x);
            quaternionf.rotateY(-(camera.getRotation().y/60));
           // quaternionf.rotateZ(camera.getRotation().z);
            gameItems[0].setRotation(quaternionf);


        }
     //   System.out.println("Float"+count+", x"+x+",y"+y+",z"+z);

*/

        if (mouseInput.isLeftButtonPressed()) {
           this.selectDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera, lastGameItemSelected);
            if(lastGameItemSelected!=null){
                System.out.println("lastGameItem: " + lastGameItemSelected.getItemType());
            }
       }
    }

    @Override
    public void render(Window window) {
      /*  if (hud != null) {
            hud.updateSize(window);
        }*/
        if (hudGameInfo != null) {
            hudGameInfo.updateSize(window);
        }
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        //waterRenderer.render(waters, camera);
        renderer.render(window, camera, scene, sceneChanged);
        hud.render(window, camera, gameItems, harbors);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();

        scene.cleanup();

        if (hud != null) {
            hud.cleanup();
        }
        if(hudGameInfo != null){
            hudGameInfo.cleanup();
        }
    }
}

