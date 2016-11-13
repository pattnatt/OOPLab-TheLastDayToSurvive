package com.thelastdaytosurvive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {
	
	public static int WEAPON_PRIMARY = 0;
	public static int WEAPON_SECONDARY = 1;
	
	private MainGameScreen mainGameScreen;
	private Weapon weapon;
	private Texture playerSheet;
	private TextureRegion[] playerFrames;
	private int frameCols = 2;
	private int frameRows = 1;
	
	
	private Sprite playerSprite;
	private Rectangle playerRectangle;
	
	private int playerMaxHealth = 100;
	private int playerCurrentHealth;
	private float playerSpeed = 160f;
	private float sqrt2 = 1.41421356237f;
	private float minPositionX = 0;
	private float maxPositionX = MainGameWorld.MAP_X - 64;
	private float minPositionY = 0;
	private float maxPositionY = MainGameWorld.MAP_Y - 64;
	private float hitboxSize = 40;
	
	
	private int currentWeapon;
	private int selectedWeapon;
	
	private int counter1;
	private int counter2;
	private int counter3;
	
	public Player(MainGameScreen mainGameScreen, Weapon weapon){
		this.mainGameScreen = mainGameScreen;
		this.weapon = weapon;
		playerCurrentHealth = playerMaxHealth;
		setTextureRegion();
		setUpSprite();
		setUpRectangle();
	}
	
	public void update(){
		updatePosition();
		updateRotation();
		updateWeapon();
		updateAttack();
	}
	
	public int getAmmoCount(){
		return weapon.getAmmoCount(currentWeapon);
	}
	
	public int getPocketCount(){
		return weapon.getPocketCount(currentWeapon);
	}
	
	public boolean isReloading(){
		return weapon.isReloading(currentWeapon);
	}
	
	public float reloadProgress(){
		return weapon.reloadProgress(currentWeapon);
	}
	
	public float getHealth(){
		return (float)playerCurrentHealth / (float)playerMaxHealth;
	}
	
	public Sprite getSprite(){
		return playerSprite;
	}
	
	public Rectangle getRectangle(){
		return playerRectangle;
	}
	
	public void getHit(int value){
		playerCurrentHealth -= value;
		if(playerCurrentHealth < 0){
			playerCurrentHealth = 0;
		}
	}
	
	private void setTextureRegion(){
		playerSheet = new Texture("Player/Player.png");
		TextureRegion[][] tmp = TextureRegion.split(playerSheet, playerSheet.getWidth()/frameCols
								, playerSheet.getHeight()/frameRows);
		playerFrames = new TextureRegion[frameRows * frameCols];
		counter1 = 0;
		for(counter2 = 0; counter2 < frameRows; counter2++){
			for(counter3 = 0; counter3 < frameCols; counter3++){
				playerFrames[counter1] = tmp[counter2][counter3];
				counter1++;
			}
		}
		
	}
	
	private void setUpSprite(){
		currentWeapon = WEAPON_PRIMARY;
		playerSprite = new Sprite(playerFrames[currentWeapon]);
		playerSprite.setPosition(1600, 900);
	}
	
	private void setUpRectangle(){
		playerRectangle = new Rectangle();
		playerRectangle.x = playerSprite.getX() + (playerSprite.getWidth() / 2) - hitboxSize;
		playerRectangle.y = playerSprite.getY() + (playerSprite.getHeight() / 2) - hitboxSize;
		playerRectangle.width = hitboxSize;
		playerRectangle.height = hitboxSize;
	}
	
	private void updatePosition(){
		float vertiSpeed = 0f;
		float horiSpeed = 0f;
		boolean isVertiMove = false;
		boolean isHoriMove = false;

		if (Gdx.input.isKeyPressed(Input.Keys.W)){
			isVertiMove = true;
			vertiSpeed = 1;
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)){
			isVertiMove = true;
			vertiSpeed = -1;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)){
			isHoriMove = true;
			horiSpeed = 1;
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)){
			isHoriMove = true;
			horiSpeed = -1;
		}
		
		if (isHoriMove && isVertiMove){
			playerSprite.translate(horiSpeed * playerSpeed / sqrt2 * Gdx.graphics.getDeltaTime()
									, vertiSpeed * playerSpeed / sqrt2* Gdx.graphics.getDeltaTime());
		} else if (isHoriMove || isVertiMove){
			playerSprite.translate(horiSpeed * playerSpeed * Gdx.graphics.getDeltaTime()
									, vertiSpeed * playerSpeed * Gdx.graphics.getDeltaTime());
		}
		
		checkPlayerOutOfBound();
	}
	
	private void updateRotation(){
		float playerPosiX = playerSprite.getX() + (playerSprite.getWidth() ) / 2;
		float playerPosiY = playerSprite.getY() + (playerSprite.getHeight() ) / 2;
		float playerRotate = playerSprite.getRotation() - 90f;
		float mouseX = mainGameScreen.gamePositionX(Gdx.input.getX() *
				(MainGameWorld.CAMERA_X / (float)Gdx.graphics.getWidth()));
		float mouseY = mainGameScreen.gamePositionY( ((-1)*Gdx.input.getY()) * 
				(MainGameWorld.CAMERA_Y /(float)Gdx.graphics.getHeight()) + (MainGameWorld.CAMERA_Y) );
		
//		System.out.println(playerPosiX + " " + playerPosiY + " / " + mouseX + " " + mouseY 
//		  + " / " + Gdx.input.getX() + " " + Gdx.input.getY() + " / " + (playerSprite.getWidth() * ((float)Gdx.graphics.getWidth() / 1600f)) + " " + (playerSprite.getHeight() * ((float)Gdx.graphics.getHeight() / 900f)));
//		System.out.println(Gdx.input.getX() * (MainGameWorld.CAMERA_X / (float)Gdx.graphics.getWidth()) + "/" + (((-1)*Gdx.input.getY()) * (MainGameWorld.CAMERA_Y /(float)Gdx.graphics.getHeight()) + (MainGameWorld.CAMERA_Y)));
		
		float rotateTarget = (float) (Math.atan2((double)(playerPosiY - mouseY) 
								,(double) (playerPosiX - mouseX)) * 180.0d / Math.PI);
		
		playerSprite.rotate(rotateTarget - playerRotate);
	}
	
	private void updateWeapon(){
		selectedWeapon = -1;
		
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
			selectedWeapon = WEAPON_PRIMARY;
		} else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			selectedWeapon = WEAPON_SECONDARY;
		}
		
		if (selectedWeapon != -1 && selectedWeapon != currentWeapon){
			weapon.cancleReload(currentWeapon);
			currentWeapon = selectedWeapon;
			playerSprite.setRegion(playerFrames[currentWeapon]);
		}
	}
	
	private void updateAttack(){
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			float x = (float) (playerSprite.getX() + 27 + 30 * Math.cos((playerSprite.getRotation() + 90) / 180 * Math.PI));
			float y = (float) (playerSprite.getY() + 27 + 30 * Math.sin((playerSprite.getRotation() + 90) / 180 * Math.PI));
			
			weapon.pullTrigger(currentWeapon, x, y, playerSprite.getRotation() + 90);
		} else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			weapon.releaseTrigger(currentWeapon);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.R)){
			weapon.reload(currentWeapon);
		}
	}
	
	private void checkPlayerOutOfBound(){
		if (playerSprite.getX() < minPositionX){
			playerSprite.setPosition(minPositionX, playerSprite.getY());
		} else if (playerSprite.getX() > maxPositionX){
			playerSprite.setPosition(maxPositionX, playerSprite.getY());
		}
		
		if(playerSprite.getY() < minPositionY){
			playerSprite.setPosition(playerSprite.getX(), minPositionY);
		} else if (playerSprite.getY() > maxPositionY){
			playerSprite.setPosition(playerSprite.getX(), maxPositionY);
		}
	}
}
