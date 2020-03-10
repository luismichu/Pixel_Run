package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class Fin implements Screen {
    private Score score;
    private FitViewport viewport;
    private Stage stage;
    private Skin skin;
    private PixelRun pixelRun;

    Fin(PixelRun pixelRun, int score){
        this.pixelRun = pixelRun;
        this.score = new Score(score);
    }
    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.position.set(MainMenu.BWIDTH / 2f, MainMenu.BHEIGHT / 2f, 0);
        camera.update();
        viewport = new FitViewport(MainMenu.BWIDTH, MainMenu.BHEIGHT, camera);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/star-soldier-ui.json"));

        fin();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    void fin(){
        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, MainMenu.BHEIGHT);
        Label lblFin = new Label("Fin de la partida", skin);
        lblFin.setFontScale(2.5f);
        Label lblScore = new Label("Score: " + score.score, skin);
        lblScore.setFontScale(1.5f);
        final TextField txtNombre = new TextField("", skin);
        TextButton btGuardar = new TextButton("Guardar", skin);
        btGuardar.getLabel().setFontScale(1.5f);

        table.padTop(280);

        table.add(lblFin).padBottom(90);

        table.row();
        table.add(lblScore).width(480).height(50).padBottom(40);

        table.row();
        table.add(txtNombre).width(480).height(50).padBottom(40);

        table.row();
        table.add(btGuardar).width(380).height(150).padBottom(40);

        btGuardar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!txtNombre.getText().equals("")){
                    score.name = txtNombre.getText();
                    Gdx.app.log("score", score.name);
                    PixelRun.db.insertar(score);
                    resultados();
                }
            }
        });

        stage.addActor(table);
    }

    void resultados(){
        stage.clear();

        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, MainMenu.BHEIGHT);
        table.padTop(280);

        Label lbl = new Label("Highscores", skin);
        lbl.setFontScale(2.75f);
        lbl.setWidth(200);
        table.add(lbl).padBottom(50);
        table.row();

        for(Score score : PixelRun.db.leer()){
            Label lblNombre = new Label(score.name, skin);
            Label lblScore = new Label(String.valueOf(score.score), skin);

            lblNombre.setWidth(100);
            lblScore.setWidth(100);

            lblNombre.setFontScale(1.75f);
            lblScore.setFontScale(1.75f);

            table.add(lblNombre).padBottom(20);
            table.add(lblScore).padBottom(20);
            table.row();
        }

        TextButton bt = new TextButton("Menu principal", skin);
        bt.getLabel().setFontScale(1.5f);
        bt.setWidth(380);
        bt.setHeight(150);
        bt.setPosition(MainMenu.BWIDTH / 2f - bt.getWidth() / 2f, bt.getHeight() + 10);
        bt.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pixelRun.setScreen(new MainMenu(pixelRun, null, true));
            }
        });

        stage.addActor(table);
        stage.addActor(bt);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
