package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class MainMenu implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Texture background;
    private PixelRun pixelRun;
    private MainGame game;
    private ShaderProgram shader;
    private Stage stage;
    private Skin skin;
    private Preferences prefs;
    private Sound menuSelection, enterGame;
    private boolean menu;
    public static int BWIDTH = 720, BHEIGHT = 1280;

    MainMenu(PixelRun pixelRun, MainGame game, boolean menu){
        this.pixelRun = pixelRun;
        this.game = game;
        this.menu = menu;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.position.set(BWIDTH / 2f, BHEIGHT / 2f, 0);
        camera.update();
        viewport = new FitViewport(BWIDTH, BHEIGHT, camera);

        prefs = Gdx.app.getPreferences("myPrefs");

        menuSelection = Gdx.audio.newSound(Gdx.files.internal("sound/menuSelection.wav"));
        enterGame = Gdx.audio.newSound(Gdx.files.internal("sound/enterGame.wav"));

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        background = new Texture("img/spacefield.png");

        shader = new ShaderProgram(
                Gdx.files.internal("shaders/greyScaleVert.glsl"),
                Gdx.files.internal("shaders/darkFrag.glsl"));

        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());

        skin = new Skin(Gdx.files.internal("ui/star-soldier-ui.json"));
        if(menu)
            menu();
        else
            opciones();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        batch.setShader(shader);
        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        batch.draw(background, 0, 0);

        batch.end();

        stage.act();
        stage.draw();
    }

    private void menu(){
        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, BHEIGHT);
        TextButton btStart = new TextButton("Nueva partida",skin);
        btStart.getLabel().setFontScale(1.5f);
        TextButton btOpciones = new TextButton("Opciones",skin);
        btOpciones.getLabel().setFontScale(1.5f);
        TextButton btSalir = new TextButton("Salir",skin);
        btSalir.getLabel().setFontScale(1.5f);
        Label lbl = new Label("Pixel Run", skin);

        lbl.setFontScale(3.5f);
        table.padTop(280);

        table.add(lbl).padBottom(90);

        table.row();
        table.add(btStart).width(480).height(150).padBottom(40);

        table.row();
        table.add(btOpciones).width(480).height(150).padBottom(40);

        table.row();
        table.add(btSalir).width(480).height(150).padBottom(40);

        btStart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music"))
                    enterGame.play(prefs.getFloat("volume"));
                pixelRun.setScreen(new MainGame(pixelRun));
                dispose();
            }
        });

        btSalir.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });
        btOpciones.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music"))
                    menuSelection.play(prefs.getFloat("volume"));
                opciones();
            }
        });

        stage.addActor(table);
    }

    public void opciones(){
        stage.clear();

        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);

        table.setPosition(0, BHEIGHT);

        final CheckBox chkMusic = new CheckBox("   Musica", skin);
        chkMusic.getLabel().setFontScale(1.5f);
        chkMusic.setChecked(prefs.getBoolean("music"));
        final ProgressBar pbVolume = new ProgressBar(0, 1, 0.01f, false, skin);
        pbVolume.setValue(prefs.getFloat("volume"));
        final CheckBox chkBordes = new CheckBox("   Bordes de objetos", skin);
        chkBordes.getLabel().setFontScale(1.5f);
        chkBordes.setChecked(prefs.getBoolean("bordes"));
        TextButton btAtras = new TextButton("Atras",skin);
        btAtras.getLabel().setFontScale(1.5f);

        table.padTop(280);

        table.add(chkMusic).width(480).height(50).padBottom(30);

        table.row();
        table.add(pbVolume).width(480).height(50).padBottom(50);

        table.row();
        table.add(chkBordes).width(480).height(150).padBottom(40);

        table.row();
        table.add(btAtras).width(480).height(150).padBottom(40);

        stage.addActor(table);

        chkMusic.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music"))
                    menuSelection.play(prefs.getFloat("volume"));
                pbVolume.setDisabled(!chkMusic.isChecked());
                prefs.putBoolean("music", chkMusic.isChecked());
                prefs.flush();
            }
        });

        pbVolume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music")) {
                    pbVolume.setValue(x / pbVolume.getWidth());
                    prefs.putFloat("volume", x / pbVolume.getWidth());
                    prefs.flush();
                }
            }
        });

        chkBordes.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music"))
                    menuSelection.play(prefs.getFloat("volume"));
                prefs.putBoolean("bordes", chkBordes.isChecked());
                prefs.flush();
            }
        });

        btAtras.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(prefs.getBoolean("music"))
                    menuSelection.play(prefs.getFloat("volume"));
                if(!menu && game != null) {
                    pixelRun.setScreen(game);
                    dispose();
                }
                else {
                    stage.clear();
                    menu();
                }
            }
        });
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
        stage.dispose();
        shader.dispose();
        batch.dispose();
        menuSelection.dispose();
        enterGame.dispose();
        skin.dispose();

    }
}
