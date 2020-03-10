package com.luismichu.pixelrun;

import com.badlogic.gdx.utils.Array;

public abstract class Database {
    protected static String database_name="scores";
    protected static int version=1;

    abstract void insertar(Score score);
    abstract void drop();
    abstract Array<Score> leer();
}
