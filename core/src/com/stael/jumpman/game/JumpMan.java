package com.stael.jumpman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class JumpMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture[] jumpman;
	Texture background;
	int state = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int jumpmanY = 0;
	Rectangle jumpManRectangle;
	BitmapFont font;

	Random random;
	Random newRandom;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();

    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<Integer>();
    ArrayList<Integer> bombYs = new ArrayList<Integer>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
    Texture bomb;
    int bombCount;

    int score = 0;
    int gamestate = 0;

    Texture dead;


    @Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		jumpman = new Texture[4];
		jumpman[0] = new Texture("frame-1.png");
		jumpman[1] = new Texture("frame-2.png");
		jumpman[2] = new Texture("frame-3.png");
		jumpman[3] = new Texture("frame-4.png");

		jumpmanY = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		random = new Random();

		bomb = new Texture("bomb.png");
		newRandom = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dead = new Texture("dizzy-1.png");



	}

	public void spawnCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void spawnBomb() {
    	float height = newRandom.nextFloat() * Gdx.graphics.getHeight();
    	bombYs.add((int) height);
    	bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gamestate == 1) {
			//GAME IS RUNNING
			if(bombCount < 227) {
				bombCount++;
			} else {
				bombCount = 0;
				spawnBomb();
			}

			bombRectangles.clear();
			for (int j = 0; j < bombXs.size(); j++) {
				batch.draw(bomb, bombXs.get(j), bombYs.get(j));
				bombXs.set(j, bombXs.get(j)-7);
				//Rectangles used for collision detection with bombs
				bombRectangles.add(new Rectangle(bombXs.get(j), bombYs.get(j), bomb.getWidth(), bomb.getHeight()));
			}


			if(coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				spawnCoin();
			}

			coinRectangles.clear();
			for(int i = 0; i < coinXs.size(); i++ ) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i)-4);
				//Rectangles used for collision detection with coins
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));

				if(Gdx.input.justTouched()) { //becomes true when screen touched
					velocity = -10;   // will cause character to jump
				}

				if(pause < 8) {
					pause++;
				} else {
					pause = 0;
					if(state < 3) {
						state++;
					} else {
						state = 0;
					}
				}

				velocity += gravity;
				jumpmanY -= velocity;

				if(jumpmanY <= 0) {
					jumpmanY = 0;
				}
			}
		} else if(gamestate == 0) {
			//WAITING TO START
			if(Gdx.input.justTouched()) {
				gamestate = 1;
			}


		} else if(gamestate == 2) {
			//GAME OVER
			//want to get back to starting position
			if(Gdx.input.justTouched()) {
				gamestate = 1;
				jumpmanY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;

			}
		}


		if(gamestate == 2) {
			batch.draw(dead, Gdx.graphics.getWidth()/2 - jumpman[state].getWidth()/2 - jumpman[state].getWidth()/5, jumpmanY);
		} else {
			batch.draw(jumpman[state], Gdx.graphics.getWidth()/2 - jumpman[state].getWidth()/2 - jumpman[state].getWidth()/5, jumpmanY);

		}
		//Draw characters first so that they appear in front of the background
		batch.draw(jumpman[state], Gdx.graphics.getWidth()/2 - jumpman[state].getWidth()/2 - jumpman[state].getWidth()/5, jumpmanY);

		//Placing rectangle where the character is
		jumpManRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - jumpman[state].getWidth()/2 - jumpman[state].getWidth()/5, jumpmanY, jumpman[state].getWidth(), jumpman[state].getHeight());

		for(int l = 0; l < coinRectangles.size(); l++) {
			if(Intersector.overlaps(jumpManRectangle, coinRectangles.get(l))) {
				score++;
				coinRectangles.remove(l);
				coinXs.remove(l);
				coinYs.remove(l);
				break;

			}
		}

		for(int l = 0; l < bombRectangles.size(); l++) {
			if(Intersector.overlaps(jumpManRectangle, bombRectangles.get(l))) {
				Gdx.app.log("Bomb!", "Collision!");

				gamestate = 2;


			}
		}

		font.draw(batch, String.valueOf(score), 50, 200);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
