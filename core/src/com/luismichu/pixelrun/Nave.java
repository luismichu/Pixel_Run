package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class Nave {
    private Texture imagen;
    private Vector2 pos;
    private Rectangle rect;
    private float factor = 0.8f;
    private int vidas, blink;
    private boolean inmortal;
    private Array<Disparo> disparos;

    Nave(String ruta, Vector2 pos){
        initImage(ruta);
        this.pos = new Vector2(pos.x - imagen.getWidth() / 2f, pos.y);
        rect = new Rectangle(pos.x + imagen.getWidth() - imagen.getWidth() * factor / 2f, pos.y, imagen.getWidth() * factor, imagen.getHeight());
        vidas = 3;
        blink = 1;
        inmortal = false;
        disparos = new Array<>();
    }

    private void initImage(String ruta){
        float tam = 2.5f;
        Pixmap pMap = new Pixmap(Gdx.files.internal(ruta));
        Pixmap pMapReescalado = new Pixmap((int)(tam * pMap.getWidth()), (int)(tam * pMap.getHeight()), pMap.getFormat());
        pMapReescalado.setFilter(Pixmap.Filter.NearestNeighbour);
        pMapReescalado.drawPixmap(pMap,
                0, 0, pMap.getWidth(), pMap.getHeight(),
                0, 0, pMapReescalado.getWidth(), pMapReescalado.getHeight()
        );
        imagen = new Texture(pMapReescalado);
        pMap.dispose();
        pMapReescalado.dispose();
    }

    public void draw(SpriteBatch batch){
        if(inmortal){
            rect.setPosition(-100, -100);
            Sprite s = new Sprite(imagen);
            s.setPosition(pos.x, pos.y);
            s.draw(batch, blink);
        }
        else {
            batch.draw(imagen, pos.x, pos.y);
            rect.setPosition(pos.x + (imagen.getWidth() - imagen.getWidth() * factor) / 2, pos.y);
        }

        batch.end();
        Array<Disparo> disparosDestroyed = new Array<>();
        for(Disparo disparo : disparos){
            disparo.draw(batch);
            if(disparo.isDestroyed())
                disparosDestroyed.add(disparo);
        }
        disparos.removeAll(disparosDestroyed, false);
    }

    public Vector2 getPos() {
        return pos;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void quitarVida(){
        if(!inmortal) {
            vidas--;
            inmortal = true;
            final Timer.Task blinking = new Timer.Task() {
                @Override
                public void run() {
                    if (blink++ >= 1)
                        blink = 0;
                }
            };
            Timer.schedule(blinking, 0, 0.16f, 6);
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   inmortal = false;
                               }
                           },
                    1,
                    0,
                    0
            );
        }
    }

    public int getVidas(){ return vidas; }

    public boolean isInmortal(){ return inmortal; }

    public boolean isAlive(){ return vidas > 0; }

    public Array<Disparo> getDisparos() { return disparos; }

    public void disparar(){
        if(disparos.size < 5)
            disparos.add(new Disparo(pos));
    }
}

class Disparo{
    private Vector2 pos;
    private ShapeRenderer sRender;
    private boolean destroyed;
    private Rectangle rect;
    private final int W = 15, H = 50;

    Disparo(Vector2 pos){
        this.pos = new Vector2(pos.x + 48, pos.y + 130);
        sRender = new ShapeRenderer();
        destroyed = false;
        rect = new Rectangle(this.pos.x, this.pos.y, W, H);

        Preferences prefs = Gdx.app.getPreferences("myPrefs");
        if(prefs.getBoolean("music"))
            Gdx.audio.newSound(Gdx.files.internal("sound/laser.wav")).play(prefs.getFloat("volume"));
    }

    public void draw(SpriteBatch batch){
        if(pos.y < MainMenu.BHEIGHT) {
            sRender.setProjectionMatrix(batch.getProjectionMatrix());
            sRender.begin(ShapeRenderer.ShapeType.Filled);
            sRender.setColor(Color.RED);
            sRender.rect(pos.x, pos.y, W, H);
            sRender.end();
            rect.setPosition(pos);
        }
        else
            destroyed = true;
    }

    public void update(){
        pos.y += 500 * Gdx.graphics.getDeltaTime();
    }

    public void destroy(){
        destroyed = true;
    }

    public boolean isDestroyed() { return destroyed; }

    public Rectangle getRect() { return rect; }
}
