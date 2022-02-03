package fr.outerleys.giovanni.engine.items;

import fr.outerleys.giovanni.engine.loaders.assimp.StaticMeshesLoader;
import org.joml.Vector4f;
import fr.outerleys.giovanni.engine.graph.Material;
import fr.outerleys.giovanni.engine.graph.Mesh;
import fr.outerleys.giovanni.engine.loaders.obj.OBJLoader;
import fr.outerleys.giovanni.engine.graph.Texture;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "")[0];
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

    public SkyBox(String objModel, Vector4f colour) throws Exception {
        super();
        Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "", 0)[0];
        Material material = new Material(colour, 1);
        skyBoxMesh.setMaterial(material);
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}
