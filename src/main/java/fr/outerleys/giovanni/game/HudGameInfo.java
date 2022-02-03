package fr.outerleys.giovanni.game;

import java.awt.Font;
import org.joml.Vector4f;
import fr.outerleys.giovanni.engine.items.GameItem;
import fr.outerleys.giovanni.engine.IHud;
import fr.outerleys.giovanni.engine.items.TextItem;
import fr.outerleys.giovanni.engine.Window;
import fr.outerleys.giovanni.engine.graph.FontTexture;

public class HudGameInfo implements IHud {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem;

    public HudGameInfo(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(0.5f,0.5f,0.5f,10.0f));

        gameItems = new GameItem[]{statusTextItem};
    }

    public void setStatusText(String statusText){this.statusTextItem.setText(statusText);}

    @Override
    public GameItem[] getGameItems(){return gameItems;}

    public void updateSize(Window window){this.statusTextItem.setPosition(10f, 10f,0);}
}
