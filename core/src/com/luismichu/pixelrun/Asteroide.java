package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Asteroide {
    private Animation<TextureAtlas.AtlasRegion> animacion;
    private Vector2 pos;
    private float elapsedTime;
    private int velocidad, width, height, lastWidth, lastHeight;
    private final int W = 128, H = 128;
    private Rectangle rect;

    Asteroide(String ruta){
        velocidad = (int)(Math.random() * 100) + 50;

        TextureAtlas asteroides = new TextureAtlas(Gdx.files.internal(ruta));
        Array<TextureAtlas.AtlasRegion> arrayAsteroides = new Array<>();
        for(int i=0;i<10;i++)
            arrayAsteroides.add(asteroides.findRegion("Asteroid-A-10-0" + String.valueOf(i)));
        for(int i=10;i<60;i++)
            arrayAsteroides.add(asteroides.findRegion("Asteroid-A-10-" + String.valueOf(i)));
        animacion = new Animation<>((200 - velocidad) / 1000f, arrayAsteroides, Animation.PlayMode.LOOP);

        elapsedTime = 0;
        width = animacion.getKeyFrame(elapsedTime).getRegionWidth();
        height = animacion.getKeyFrame(elapsedTime).getRegionHeight();
        initPos();
        pos.y = (float)(Math.random() * MainMenu.BHEIGHT / 4) + MainMenu.BHEIGHT * 0.75f;
        rect = new Rectangle(pos.x, pos.y, width, height);
    }

    public void initPos(){
        pos = new Vector2((float)(Math.random() * MainMenu.BWIDTH), MainMenu.BHEIGHT * 1.25f);
    }

    public void draw(SpriteBatch batch){
        lastWidth = width;
        lastHeight = height;

        TextureRegion t = animacion.getKeyFrame(elapsedTime);

        width = t.getRegionWidth();
        height = t.getRegionHeight();

        pos.x += (lastWidth - width) / 2f;
        pos.y += (lastHeight - height) / 2f;

        batch.draw(t, pos.x, pos.y);
        rect.set(pos.x, pos.y, t.getRegionWidth(), t.getRegionHeight());
        if(pos.y <= -height)
            initPos();
    }

    public void update(){
        elapsedTime += Gdx.graphics.getDeltaTime();
        pos.y -= velocidad * Gdx.graphics.getDeltaTime();
    }

    public Rectangle getRect() {
        return rect;
    }

    public Vector2 getPos() { return pos; }
}
