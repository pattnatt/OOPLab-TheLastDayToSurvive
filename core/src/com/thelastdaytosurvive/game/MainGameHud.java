package com.thelastdaytosurvive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGameHud {
	private BitmapFont font32;
	private BitmapFont font14;
	private Texture reloadBarTexture;
	private Texture progressTexture;
	
	private MainGameScreen mainGameScreen;
	private MainGameWorld mainGameWorld;
	
	public MainGameHud(MainGameWorld mainGameWorld, MainGameScreen mainGameScreen){
		font32 = new BitmapFont(Gdx.files.internal("Font/Cloud32.fnt"));
		font14 = new BitmapFont(Gdx.files.internal("Font/Cloud14.fnt"));
		reloadBarTexture = new Texture(Gdx.files.internal("ReloadBar/ReloadBar.png"));
		progressTexture = new Texture(Gdx.files.internal("ReloadBar/Progress.png"));
		
		this.mainGameWorld = mainGameWorld;
		this.mainGameScreen = mainGameScreen;
		font32.setColor(Color.BLACK);
	}
	
	public void draw(SpriteBatch batch){
		fenceHealth(batch);
		healthBar(batch);
		ammoCount(batch);
		reloadBar(batch);
		resourceBar(batch);
		phaseBar(batch);
		scoreBar(batch);
		//aiMap(batch);
	}
	
	private void fenceHealth(SpriteBatch batch){
		for(int i = 0 ; i < Map.MAP_YNUM ; i++){
			for(int j = 0 ; j < Map.MAP_XNUM ; j++){
				if(mainGameWorld.getMap().getWorldMap()[i][j] == Map.MAP_WFENCE_ORIGIN_VERTICAL
						|| mainGameWorld.getMap().getWorldMap()[i][j] == Map.MAP_MFENCE_ORIGIN_VERTICAL
						|| mainGameWorld.getMap().getWorldMap()[i][j] == Map.MAP_WFENCE_ORIGIN_HORIZONTAL
						|| mainGameWorld.getMap().getWorldMap()[i][j] == Map.MAP_MFENCE_ORIGIN_HORIZONTAL){
					font32.draw(batch, "" + mainGameWorld.getMap().getFenceHealth()[i][j]
							, mainGameWorld.getMap().xGamePosition(j), mainGameWorld.getMap().yGamePosition(i));
				}
			}
		}
	}
	
	private void healthBar(SpriteBatch batch){
		font32.draw(batch, "" + mainGameWorld.getPlayer().getHealth(), mainGameScreen.screenPositionX(1480)
				, mainGameScreen.screenPositionY(80));
	}
	
	private void ammoCount(SpriteBatch batch){
		int currentAmmo = mainGameWorld.getPlayer().getAmmoCount();
		int pocketAmmo = mainGameWorld.getPlayer().getPocketCount();
		
		font32.draw(batch, currentAmmo + " / " + pocketAmmo, mainGameScreen.screenPositionX(1480)
					, mainGameScreen.screenPositionY(40));
	}
	
	private void reloadBar(SpriteBatch batch){
		if (mainGameWorld.getPlayer().isReloading()){
			float x = mainGameWorld.getPlayer().getSprite().getX() - 20;
			float y = mainGameWorld.getPlayer().getSprite().getY() - 20;
			float value = mainGameWorld.getPlayer().reloadProgress();
			
			batch.draw(reloadBarTexture, x, y);
			batch.draw(progressTexture, x + 2, y + 2, value, 4);
		} else if (mainGameWorld.getPlayer().getAmmoCount() <= 0){
			float x = mainGameWorld.getPlayer().getSprite().getX() - 20;
			float y = mainGameWorld.getPlayer().getSprite().getY() - 20;
			font32.draw(batch, "Reload", x, y);
		}	
	}
	
	private void resourceBar(SpriteBatch batch){
		font32.draw(batch, "Wood : " + mainGameWorld.getCrafting().getCurrentWood()
				, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(80));
		font32.draw(batch, "Metal : " + mainGameWorld.getCrafting().getCurrentMetal()
				, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(40));
	}
	
	private void phaseBar(SpriteBatch batch){
		if (mainGameWorld.getTracker().getPhase() == MainGameTracker.PHASE_PREP_TYPE){
			font32.draw(batch, "PREP PHASE"
					, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(860));
			font32.draw(batch, "Time : " + mainGameWorld.getTracker().getPrepTime()
					, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(820));
		}
		else if (mainGameWorld.getTracker().getPhase() == MainGameTracker.PHASE_COMBAT_TYPE){
			font32.draw(batch, "COMBAT PHASE"
					, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(860));
			font32.draw(batch, "Zombies : " + mainGameWorld.getTracker().getRemainEnemy()
					, mainGameScreen.screenPositionX(20), mainGameScreen.screenPositionY(820));
		}
	}
	
	private void scoreBar(SpriteBatch batch){
		font32.draw(batch, "Score : " + mainGameWorld.getTracker().getScore()
				, mainGameScreen.screenPositionX(1300), mainGameScreen.screenPositionY(860));
	}
	
	private void aiMap(SpriteBatch batch){
		for(int i = 0; i < Map.MAP_YNUM ; i++){
			for(int j = 0 ; j < Map.MAP_XNUM ; j++){
				font32.draw(batch, "" + mainGameWorld.getMap().getAIMap()[i][j]
						, mainGameWorld.getMap().xGamePosition(j), mainGameWorld.getMap().yGamePosition(i));
			}
		}
	}
}
