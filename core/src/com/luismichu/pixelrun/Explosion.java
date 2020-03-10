package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Explosion {
    private Animation<TextureRegion> animacion;
    private Vector2 pos;
    private float elapsedTime;

    Explosion(String ruta, Vector2 pos){
        initImage(ruta);
        this.pos = pos;
        elapsedTime = 0;

        Preferences prefs = Gdx.app.getPreferences("myPrefs");
        if(prefs.getBoolean("music")) {
            MainGame.explosion.play(prefs.getFloat("volume") + 0.1f);
        }
    }

    private void initImage(String ruta){
        float tam = 7.5f;
        Pixmap pMap = new Pixmap(Gdx.files.internal(ruta));
        Pixmap pMapReescalado = new Pixmap((int)(tam * pMap.getWidth()), (int)(tam * pMap.getHeight() / 4), pMap.getFormat());
        pMapReescalado.setFilter(Pixmap.Filter.NearestNeighbour);
        pMapReescalado.drawPixmap(pMap,
                0, pMap.getHeight() / 4 + 2, pMap.getWidth(), pMap.getHeight() / 4,
                0, 0, pMapReescalado.getWidth(), pMapReescalado.getHeight()
        );
        Texture imagen = new Texture(pMapReescalado);
        pMap.dispose();
        pMapReescalado.dispose();
        Array<TextureRegion> regiones = new Array<>();
        for(int i=0;i<6;i++) {
            TextureRegion t = new TextureRegion(imagen);
            t.setRegion(imagen.getWidth() / 6 * i, 0, imagen.getWidth() / 6, imagen.getHeight());
            regiones.add(t);
        }
        animacion = new Animation<>(0.1f, regiones, Animation.PlayMode.NORMAL);
    }

    public void draw(SpriteBatch batch){
        if(!isFinished())
            batch.draw(animacion.getKeyFrame(elapsedTime), pos.x, pos.y);
    }

    public void update(){
        elapsedTime += Gdx.graphics.getDeltaTime();
    }

    public boolean isFinished(){
        return animacion.isAnimationFinished(elapsedTime);
    }
}
