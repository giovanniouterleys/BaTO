package fr.outerleys.giovanni.engine;

import fr.outerleys.giovanni.engine.items.SkyBox;
import fr.outerleys.giovanni.engine.items.GameItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.outerleys.giovanni.engine.graph.InstancedMesh;
import fr.outerleys.giovanni.engine.graph.Mesh;
import fr.outerleys.giovanni.engine.graph.particles.IParticleEmitter;
import fr.outerleys.giovanni.engine.graph.weather.Fog;
import fr.outerleys.giovanni.engine.items.Water;

public class Scene {

    private final Map<Mesh, List<GameItem>> meshMap;

    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

    private SkyBox skyBox;

    private Water water;

    private SceneLight sceneLight;

    private Fog fog;

    private boolean renderShadows;

    private IHud hud;

    private IParticleEmitter[] particleEmitters;

    public Scene() {
        meshMap = new HashMap();
        instancedMeshMap = new HashMap();
        fog = Fog.NOFOG;
        renderShadows = true;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

    public void setGameItems(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
            if(gameItem!=null){
                Mesh[] meshes = gameItem.getMeshes();
                for (Mesh mesh : meshes) {
                    boolean instancedMesh = mesh instanceof InstancedMesh;
                    List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
                    if (list == null) {
                        list = new ArrayList<>();
                        if (instancedMesh) {
                            instancedMeshMap.put((InstancedMesh)mesh, list);
                        } else {
                            meshMap.put(mesh, list);
                        }
                    }
                    list.add(gameItem);
                }
            }
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
        for (Mesh mesh : instancedMeshMap.keySet()) {
            mesh.cleanUp();
        }
        if (particleEmitters != null) {
            for (IParticleEmitter particleEmitter : particleEmitters) {
                particleEmitter.cleanup();
            }
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public Water getWater() {
        return water;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public void setWater(Water water) {
        this.water = water;
    }

    public IHud getHud() {
        return hud;
    }

    public void setHud(IHud hud) {
        this.hud = hud;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

}
