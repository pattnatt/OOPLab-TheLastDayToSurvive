package com.thelastdaytosurvive.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
	public static final int ENEMY_DUMB = 0;
	
	public Player player;
	
	private EnemyDumb enemyDumb;
	
	public Enemy(){
		setUpEnemy();
		
		testCase();
	}
	
	public void newEnemy(int type, float xPosition,float yPosition){
		switch (type){
			case ENEMY_DUMB : enemyDumb.newEnemyDumb(xPosition, yPosition); break;
			default : System.out.println("Error at Enemy in newEnemy method.");
		}
	}
	public void update(float delta){
		enemyDumb.update(delta);
	}
	
	public void draw(SpriteBatch batch){
		enemyDumb.draw(batch);
	}
	
	public void isBulletHit(BulletInfo bulletInfo, Rectangle rectangle){
		enemyDumb.isBulletHit(bulletInfo, rectangle);
	}
	
	private void setUpEnemy(){
		enemyDumb = new EnemyDumb(player);
	}
	
	private void testCase(){
		newEnemy(ENEMY_DUMB, 800, 800);
	}
}