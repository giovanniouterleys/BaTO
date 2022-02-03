package fr.outerleys.giovanni.game;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import fr.outerleys.giovanni.engine.Utils;
import fr.outerleys.giovanni.engine.graph.Camera;
import fr.outerleys.giovanni.engine.items.GameItem;
import fr.outerleys.giovanni.engine.items.GameItemType;
import org.lwjgl.nanovg.NVGColor;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import fr.outerleys.giovanni.engine.Window;

public class Hud {

    private static final String FONT_NAME = "BOLD";

    private long vg;

    private NVGColor colour;

    private ByteBuffer fontBuffer;

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private DoubleBuffer posx;

    private DoubleBuffer posy;

    private int counter;

    public void init(Window window) throws Exception {
        this.vg = window.getOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);

        counter = 0;
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, GameItem[] harbors) {
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
        nvgFontSize(vg, 20.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        for(GameItem gameItem : gameItems) {
           if(gameItem!=null){
               if(gameItem==null){return;}
               if (gameItem.isSelected() && gameItem.getItemType() == GameItemType.PLANET) {
                   nvgText(vg, 10.0f, 10.0f, "Infos: " + gameItem.getName() + ", " + gameItem.getItemType() + " Ships: " + gameItem.getPort().getQuais().getQuaisOcc() + "/" + gameItem.getPort().getQuais().getNbQuais());
               } else if (gameItem.isSelected()) {
                   // Other than an harbor
                   nvgText(vg, 10.0f, 10.0f, "Infos: " + gameItem.getName() + ", " + gameItem.getItemType());

               }

               if(gameItem.getBateau()!=null){
                   if(gameItem.getBateau().getArriveePort() != null && gameItem.getBateau().getActualHarbor() != null){
                       float distance = 0.0f;

                       float xHarbor = gameItem.getBateau().getArriveePort().getX();
                       float yHarbor = gameItem.getBateau().getArriveePort().getY();
                       float zHarbor = gameItem.getBateau().getArriveePort().getZ();
                       float xBateau = gameItem.getBateau().getActualHarbor().getX();
                       float yBateau = gameItem.getBateau().getActualHarbor().getY();
                       float zBateau = gameItem.getBateau().getActualHarbor().getZ();

                       float distCalc = distance(xBateau, xHarbor, yBateau, yHarbor, zBateau, zHarbor);

                       if (distance == 0.0f) {
                           // null
                           distance = distCalc;
                       } else {
                           if (distCalc < distance) {
                               // new challenger
                               distance = distCalc;
                           }
                       }
                       //nvgText(vg, 10.0f, 60.0f, "Distance euclidienne entre les deux planètes: " + distance);

                       if(gameItem.getBateau().getArriveePort()!=gameItem.getBateau().getActualHarbor()) {
                           nvgText(vg, 10.0f, 10.0f, "Distance euclidienne entre les deux planètes: " + distance);
                       }
                   }
               }

               if (gameItem.isMoving()) {
                   if(gameItem.getBateau()!=null){
                       List<Float> distances = new ArrayList<>();

                       float distance = 0.0f;
                       float firstHarbor = 0.0f;
                       String nameOfHarbor = "";
                       float xHarbor = gameItem.getBateau().getArriveePort().getX();
                       float yHarbor = gameItem.getBateau().getArriveePort().getY();
                       float zHarbor = gameItem.getBateau().getArriveePort().getZ();
                       float xBateau = gameItem.getPosition().x;
                       float yBateau = gameItem.getPosition().y;
                       float zBateau = gameItem.getPosition().z;
                       float distCalc = distance(xBateau, 20, yBateau, yHarbor, zBateau, zHarbor);

                       if (distance == 0.0f) {
                           // null
                           distance = distCalc;
                       } else {
                           if (distCalc < distance) {
                               // new challenger
                               distance = distCalc;
                           }
                       }
                       nvgText(vg, 10.0f,   30.0f, "Distance euclidienne: " + distance);
                   }
               }
               if(gameItem.isSelected() && gameItem.getItemType() == GameItemType.ROCKET){
           /*     float xBateau = gameItem.getPosition().x;
                float yBateau = gameItem.getPosition().y;
                float zBateau = gameItem.getPosition().z;

                List<Float> distances = new ArrayList<>();

                float distance = 0.0f;
                float firstHarbor = 0.0f;
                String nameOfHarbor = "";

                for(GameItem harbor : harbors){
                    float xHarbor = harbor.getPosition().x;
                    float yHarbor = harbor.getPosition().y;
                    float zHarbor = harbor.getPosition().z;
                    float distCalc = distance(xBateau, xHarbor, yBateau, yHarbor, zBateau, zHarbor);

                    if(distance==0.0f){
                        // null
                        distance = distCalc;
                        nameOfHarbor = harbor.getName();
                    } else {
                        if(distCalc < distance){
                          // new challenger
                            distance = distCalc;
                            nameOfHarbor = harbor.getName();
                        }
                    }
                }

                nvgText(vg, 10.0f,   30.0f, "Port le plus proche: " + nameOfHarbor + " à une distance euclidienne de " + distance);

*/
               }
               if(gameItem.isLastSelected()){
                   nvgText(vg, 10.0f,   60.0f, gameItem.getName());
               }
           }
        }

        float cameraX = camera.getPosition().x;
        float cameraY = camera.getPosition().y;
        float cameraZ = camera.getPosition().z;

        nvgFontSize(vg, 16.0f);
        nvgText(vg, 10.0f, window.getHeight() - 50.0f, "Position x " + cameraX + ", y " + cameraY + ", z " + cameraZ);

        // Upper ribbon
        //nvgBeginPath(vg);
        //nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
        //nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        //nvgFill(vg);

        // Lower ribbon
        //nvgBeginPath(vg);
        //nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
        //nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        //nvgFill(vg);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(vg);
        nvgCircle(vg, xcenter, ycenter, radius);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        // Clicks Text
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
        nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }

    public float distance(float xBateau, float xPort, float yBateau, float yPort, float zBateau, float zPort){
        float value = 0.0f;

        float xCalc = 0.0f;
        float yCalc = 0.0f;
        float zCalc = 0.0f;
        if(xBateau < xPort){
            xCalc = (float) Math.pow(2, (xPort - xBateau));
        } else {
            xCalc = (float) Math.pow(2, (xBateau - xPort));
        }

        if(yBateau < yPort){
            yCalc = (float) Math.pow(2, (yPort - yBateau));
        } else {
            yCalc = (float) Math.pow(2, (yBateau - yPort));
        }

        if(zBateau < zPort){
            zCalc = (float) Math.pow(2, (zPort - zBateau));
        } else {
            zCalc = (float) Math.pow(2, (zBateau - zPort));
        }

        value = (float) Math.sqrt(xCalc + yCalc + zCalc);

        return value;
    }

    public void incCounter() {
        counter++;
        if (counter > 99) {
            counter = 0;
        }
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }
}