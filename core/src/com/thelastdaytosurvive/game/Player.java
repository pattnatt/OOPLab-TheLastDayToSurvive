package com.thelastdaytosurvive.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
	
	public static int WEAPON_PRIMARY = 0;
	public static int WEAPON_SECONDARY = 1;
	
	private MainGameWorld mainGameWorld;
	private Texture playerSheet;
	private TextureRegion[] playerFrames;
	private int frameCols = 2;
	private int frameRows = 1;
	
	
	public Sprite playerSprite;
	public float playerSpeed = 160f;
	private float sqrt2 = 1.41421356237f;
	
	private int currentWeapon;
	private int selectedWeapon;
	
	private int counter1;
	private int counter2;
	private int counter3;
	
	public Player(MainGameWorld mainGameWorld){
		
		this.mainGameWorld = mainGameWorld;
		setTextureRegion();
		setUpSprite();
	}
	
	public void update(){
		updatePosition();
		updateRotation();
		updateWeapon();
		updateAttack();
	}
	
	private void setTextureRegion(){
		playerSheet = new Texture("Player/Player.png");
		TextureRegion[][] tmp = TextureRegion.split(playerSheet, playerSheet.getWidth()/frameCols, playerSheet.getHeight()/frameRows);
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
		playerSprite.setPosition(Gdx.graphics.getWidth()/2 - playerSprite.getWidth()/2, Gdx.graphics.getHeight()/2 - playerSprite.getHeight()/2);
	}
	
	private void updatePosition(){
		float vertiSpeed = 0f;
		float horiSpeed = 0f;
		boolean isVertiMove = false;
		boolean isHoriMove = false;

		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			isVertiMove = true;
			vertiSpeed = 1;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.S)){
			isVertiMove = true;
			vertiSpeed = -1;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			isHoriMove = true;
			horiSpeed = 1;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.A)){
			isHoriMove = true;
			horiSpeed = -1;
		}
		
		if(isHoriMove && isVertiMove){
			playerSprite.translate(horiSpeed * playerSpeed / sqrt2 * Gdx.graphics.getDeltaTime(), vertiSpeed * playerSpeed / sqrt2* Gdx.graphics.getDeltaTime());
		}
		
		else if(isHoriMove || isVertiMove){
			playerSprite.translate(horiSpeed * playerSpeed * Gdx.graphics.getDeltaTime(), vertiSpeed * playerSpeed * Gdx.graphics.getDeltaTime());
		}
	}
	
	private void updateRotation(){
		float playerPosiX = playerSprite.getX() + playerSprite.getWidth() / 2;
		float playerPosiY = ( (playerSprite.getY() + playerSprite.getHeight() / 2) - Gdx.graphics.getHeight() / 2) * (-1f) + Gdx.graphics.getHeight() / 2;
		float playerRotate = playerSprite.getRotation() - 90f;
		float mouseX = Gdx.input.getX();
		float mouseY = Gdx.input.getY();
		float rotateTarget = (float) (Math.atan2((double)(mouseY - playerPosiY) ,(double) (playerPosiX - mouseX)) * 180.0d / Math.PI);
		
		playerSprite.rotate(rotateTarget - playerRotate);
	}
	
	private void updateWeapon(){
		selectedWeapon = -1;
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
			selectedWeapon = WEAPON_PRIMARY;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			selectedWeapon = WEAPON_SECONDARY;
		}
		
		if(selectedWeapon != -1 && selectedWeapon != currentWeapon){
			currentWeapon = selectedWeapon;
			playerSprite.setRegion(playerFrames[currentWeapon]);
		}
	}
	
	private void updateAttack(){
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			float x = (float) (playerSprite.getX() + 27 + 30 * Math.cos((playerSprite.getRotation() + 90) / 180 * Math.PI));
			float y = (float) (playerSprite.getY() + 27 + 30 * Math.sin((playerSprite.getRotation() + 90) / 180 * Math.PI));
			
			mainGameWorld.bullet.newBullet(currentWeapon, x, y, playerSprite.getRotation() + 90);
		}
	}
}
