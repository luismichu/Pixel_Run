package com.luismichu.pixelrun;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame implements InputProcessor, Screen {
	private PixelRun pixelRun;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private FitViewport viewport;
	private Texture background, backgroundFlipped;
	private ShapeRenderer sRender;
	private static int BWIDTH = 720, BHEIGHT = 1280;
	private Nave jugador;
	private Preferences prefs;
	private Array<Asteroide> asteroides;
	private Array<Explosion> explosiones;
	private Array<Integer> teclas;
	private boolean gamePaused;
	private Stage stage;
	private ProgressBar pb;
	private Label lbl;
	private int puntuacion;
	private float offset;
	private Music pxRunMusic;
	public static Sound explosion, laser;

	MainGame(PixelRun pixelRun){
		this.pixelRun = pixelRun;
		gamePaused = false;
	}

	@Override
	public void show() {
		if(!gamePaused) {
			batch = new SpriteBatch();

			camera = new OrthographicCamera();
			camera.position.set(BWIDTH / 2f, BHEIGHT / 2f, 0);
			camera.update();
			viewport = new FitViewport(BWIDTH, BHEIGHT, camera);

			background = new Texture("img/spacefield.png");
			backgroundFlipped = new Texture("img/spacefieldFlipped.png");
			jugador = new Nave("img/ships/ship1.png", new Vector2(BWIDTH / 2f, 150));
			asteroides = new Array<>();
			for (int i = 0; i < 15; i++)
				asteroides.add(new Asteroide("img/asteroides/asteroides.atlas"));

			sRender = new ShapeRenderer();

			prefs = Gdx.app.getPreferences("myPrefs");

			pxRunMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/PixelRun.mp3"));
			pxRunMusic.setVolume(Math.max(prefs.getFloat("volume") - 0.1f, 0));
			pxRunMusic.setLooping(true);
			if (prefs.getBoolean("music"))
				pxRunMusic.play();

			explosiones = new Array<>();

			teclas = new Array<>();

			gamePaused = false;
			stage = new Stage(viewport);

			game();

			puntuacion = 0;
			offset = 0;

			explosion = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.mp3"));
			laser = Gdx.audio.newSound(Gdx.files.internal("sound/laser.wav"));
		}
		else
			game();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(!gamePaused) update();
		draw();

		stage.act();
		stage.draw();
	}

	void update(){
		if(teclas.size > 0) {
			switch (teclas.get(teclas.size - 1)) {
				case Input.Keys.LEFT:
				case Input.Keys.A:
					jugador.getPos().x -= 5;
					if(jugador.getPos().x < 0)
						jugador.getPos().x = 0;
					break;

				case Input.Keys.RIGHT:
				case Input.Keys.D:
					jugador.getPos().x += 5;
					if(jugador.getPos().x > BWIDTH - jugador.getRect().width)
						jugador.getPos().x = BWIDTH - jugador.getRect().width;
					break;

				case Input.Keys.SPACE:
					jugador.disparar();
					break;

				case Input.Keys.ESCAPE:
					pauseMenu();
					break;
			}
		}
		else if(Gdx.input.getAccelerometerX() != 0){
			jugador.getPos().x -= Gdx.input.getAccelerometerX();
			if(jugador.getPos().x < 0)
				jugador.getPos().x = 0;
			else if(jugador.getPos().x > BWIDTH - jugador.getRect().width)
				jugador.getPos().x = BWIDTH - jugador.getRect().width;
		}

		for(Asteroide asteroide : asteroides)
			asteroide.update();

		for(Disparo disparo : jugador.getDisparos())
			disparo.update();

		for(Explosion explosion : explosiones)
			explosion.update();

		pb.setValue(jugador.getVidas());
		lbl.setText(String.valueOf(puntuacion));
		offset -= 45 * Gdx.graphics.getDeltaTime();
		if(offset < -background.getHeight() * 2)
			offset = 0;
	}

	void draw(){
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeRenderer.ShapeType.Line);
		sRender.setColor(Color.RED);

		batch.draw(background, 0, background.getHeight() * 2 + offset);
		batch.draw(backgroundFlipped, 0, background.getHeight() + offset);
		batch.draw(background, 0, offset);

		Array<Asteroide> destroyedAsteroids = new Array<>();
		for(Asteroide asteroide : asteroides) {
			for(Disparo disparo : jugador.getDisparos()){
				if(disparo.getRect().overlaps(asteroide.getRect())){
					explosiones.add(new Explosion("img/explosion.png", asteroide.getPos()));
					asteroide.initPos();
					destroyedAsteroids.add(asteroide);
					disparo.destroy();
					puntuacion += 125;
				}
			}
			if(!jugador.isInmortal() && asteroide.getRect().overlaps(jugador.getRect())) {
				explosiones.add(new Explosion("img/explosion.png", asteroide.getPos()));
				asteroide.initPos();
				destroyedAsteroids.add(asteroide);
				jugador.quitarVida();
				if(!jugador.isAlive())
					break;
			}
			else if(!destroyedAsteroids.contains(asteroide, false)){
				asteroide.draw(batch);
				if (prefs.getBoolean("bordes"))
					sRender.rect(asteroide.getRect().x, asteroide.getRect().y, asteroide.getRect().width, asteroide.getRect().height);
			}
		}

		if(!jugador.isAlive()) {
			pixelRun.setScreen(new Fin(pixelRun, puntuacion));
			dispose();
		}
		else {
			Array<Explosion> finishedExplosions = new Array<>();
			for (Explosion explosion : explosiones) {
				explosion.draw(batch);
				if (explosion.isFinished())
					finishedExplosions.add(explosion);
			}
			explosiones.removeAll(finishedExplosions, false);

			sRender.setColor(Color.GREEN);
			if (prefs.getBoolean("bordes"))
				sRender.rect(jugador.getRect().x, jugador.getRect().y, jugador.getRect().width, jugador.getRect().height);

			jugador.draw(batch);

			sRender.end();
		}
	}

	void pauseMenu(){
		gamePaused = true;

		Skin skin = new Skin(Gdx.files.internal("ui/star-soldier-ui.json"));
		Gdx.input.setInputProcessor(stage);
		Table table = new Table();
		table.setWidth(stage.getWidth());
		table.align(Align.center | Align.top);

		table.setPosition(0, BHEIGHT);
		TextButton btContinuar = new TextButton("Continuar",skin);
		btContinuar.getLabel().setFontScale(1.5f);
		TextButton btOpciones = new TextButton("Opciones",skin);
		btOpciones.getLabel().setFontScale(1.5f);
		TextButton btSalir = new TextButton("Menu principal",skin);
		btSalir.getLabel().setFontScale(1.5f);

		table.padTop(380);

		table.add(btContinuar).width(480).height(150).padBottom(40);

		table.row();
		table.add(btOpciones).width(480).height(150).padBottom(40);

		table.row();
		table.add(btSalir).width(480).height(150).padBottom(40);

		btContinuar.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game();
			}
		});

		btOpciones.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pixelRun.setScreen(new MainMenu(pixelRun, MainGame.this, false));
			}
		});

		btSalir.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pixelRun.setScreen(new MainMenu(pixelRun, null, true));
				dispose();
			}
		});

		stage.addActor(table);
		stage.addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Input.Keys.ESCAPE)
					game();
				return super.keyDown(event, keycode);
			}
		});
	}

	void game(){
		stage.clear();
		Gdx.input.setInputProcessor(this);
		gamePaused = false;
		pb = new ProgressBar(0, 3, 1, false, new Skin(Gdx.files.internal("ui/star-soldier-ui.json")));
		pb.setWidth(250);
		lbl = new Label("0", new Skin(Gdx.files.internal("ui/star-soldier-ui.json")));
		lbl.setFontScale(2.5f);
		lbl.setWidth(300);
		lbl.setAlignment(Align.right, Align.right);
		lbl.setPosition(BWIDTH - 320, 50);
		Label pausa = new Label("| |", new Skin(Gdx.files.internal("ui/star-soldier-ui.json")));
		pausa.setFontScale(2.5f);
		pausa.setPosition(BWIDTH - 90, BHEIGHT - 90);
		stage.addActor(pb);
		stage.addActor(lbl);
		stage.addActor(pausa);
		teclas.clear();
		if(prefs.getBoolean("music"))
			pxRunMusic.play();
		else
			pxRunMusic.pause();
		pxRunMusic.setVolume(prefs.getFloat("volume"));
	}

	@Override
	public void resize (int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		gamePaused = true;
	}

	@Override
	public void resume() {
		pauseMenu();
	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		pxRunMusic.dispose();
		batch.dispose();
		sRender.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.SPACE)
			jugador.disparar();
		else
			teclas.add(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		teclas.removeValue(keycode, false);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if((float)screenX / Gdx.graphics.getWidth() > 0.8f && (float)screenY / Gdx.graphics.getHeight() < 0.1f)
			pauseMenu();
		else
			jugador.disparar();
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
