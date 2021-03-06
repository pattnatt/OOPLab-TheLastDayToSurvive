package com.thelastdaytosurvive.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class EnemySmart {
	private Player player;
	private Map map;
	private Texture texture;
	private Texture healthBarTexture;
	private Texture healthTexture;
	private BitmapFont font;
	private MainGameTracker tracker;
	private ItemDropper itemDropper;
	
	private Array<EnemySmartInfo> enemySmartInfoArray;
	private Array<Rectangle> enemySmartRectangleArray;
	private Array<Rectangle> enemySmartMovementRectangleArray;
	
	private int maxHealth = 150;
	private int damage = 10;
	private long attackDelay = 1000;
	private float speed = 200f;
	private float picSize = 76;
	private float hitBoxSize = 40;
	private float movementSize = 36;
	
	private boolean isShowHealth = true;
	
	public EnemySmart(Player player, Map map,MainGameTracker tracker, ItemDropper itemDropper){
		this.player = player;
		this.map = map;
		this.tracker = tracker;
		this.itemDropper = itemDropper;
		texture = new Texture("Enemy/SmartReal.png");
		healthBarTexture = new Texture("FenceHealthBar/Bar.png");
		healthTexture = new Texture("FenceHealthBar/EnemyHealth.png");
		font = new BitmapFont(Gdx.files.internal("Font/Cloud32.fnt"));
		
		enemySmartInfoArray = new Array<EnemySmartInfo>();
		enemySmartRectangleArray = new Array<Rectangle>();
		enemySmartMovementRectangleArray = new Array<Rectangle>();
	}
	
	public void newEnemySmart(float xPosition, float yPosition){
		setupInfo(xPosition, yPosition);
		setupRectangle(xPosition, yPosition);
		System.out.println("Spawn Smart at " + xPosition + " / " + yPosition);
	}
	
	public void update(float delta){
		updateMovement(delta);
		checkDead();
		checkAttack();
	}
	
	public void draw(SpriteBatch batch){
		Iterator<EnemySmartInfo> iterInfo = enemySmartInfoArray.iterator();
		
		while (iterInfo.hasNext()){
			EnemySmartInfo info = iterInfo.next();
			batch.draw(texture, info.xPosition, info.yPosition, texture.getWidth() / 2
					, texture.getHeight() / 2, texture.getWidth(), texture.getHeight()
					, 1, 1, info.rotation + 90, 0, 0, texture.getWidth(), texture.getHeight()
					, false, false);
			
			if (isShowHealth){
				batch.draw(healthBarTexture, info.xPosition, info.yPosition - 10);
				batch.draw(healthTexture, info.xPosition + 2, info.yPosition - 8
						, (float) info.health / (float) maxHealth * 50f, 4);
			}
		}
	}
	
	public void isBulletHit(BulletInfo bulletInfo, Rectangle bulletRectangle){
		Iterator<EnemySmartInfo> iterInfo = enemySmartInfoArray.iterator();
		Iterator<Rectangle> iterRectangle = enemySmartRectangleArray.iterator();
		
		while (iterInfo.hasNext() && iterRectangle.hasNext() && (bulletInfo.hitCount > 0)){
			EnemySmartInfo info = iterInfo.next();
			Rectangle rectangle = iterRectangle.next();
			
			if (rectangle.overlaps(bulletRectangle)){
				info.health -= Bullet.typeDamage(bulletInfo.type);
				bulletInfo.hitCount--;
			}
		}
	}
	
	private void setupInfo(float xPosition, float yPosition){
		EnemySmartInfo newInfo = new EnemySmartInfo();
		newInfo.health = maxHealth;
		newInfo.xPosition = xPosition;
		newInfo.yPosition = yPosition;
		newInfo.rotation = 0;
		newInfo.lastAttackTime = 0;
		newInfo.isJustHitPlayer = false;
		
		enemySmartInfoArray.add(newInfo);
	}
	
	private void setupRectangle(float xPosition, float yPosition){
		Rectangle newRectangle = new Rectangle();
		newRectangle.x = xPosition + (picSize / 2) - (hitBoxSize / 2);
		newRectangle.y = yPosition + (picSize / 2) - (hitBoxSize / 2);
		newRectangle.width = hitBoxSize;
		newRectangle.height = hitBoxSize;
		enemySmartRectangleArray.add(newRectangle);
		
		Rectangle newMovementRectangle = new Rectangle();
		newMovementRectangle.x = xPosition + (picSize / 2) - (movementSize / 2);
		newMovementRectangle.y = yPosition + (picSize / 2) - (movementSize / 2);
		newMovementRectangle.width = movementSize;
		newMovementRectangle.height = movementSize;
		enemySmartMovementRectangleArray.add(newMovementRectangle);
	}
	
	private void updateMovement(float deltaTime){
		Iterator<EnemySmartInfo> iterInfo = enemySmartInfoArray.iterator();
		Iterator<Rectangle> iterRectangle = enemySmartRectangleArray.iterator();
		Iterator<Rectangle> iterMovementRectangle = enemySmartMovementRectangleArray.iterator();
		
		while (iterInfo.hasNext()){
			EnemySmartInfo info = iterInfo.next();
			Rectangle rectangle = iterRectangle.next();
			Rectangle movementRectangle = iterMovementRectangle.next();
			//Vector3 movementInfo = calculateMovement(deltaTime, info.xPosition, info.yPosition);
			char direction = map.aiDirection(info.xPosition + (picSize / 2)
					, info.yPosition + (picSize / 2));
					
			if(direction == Map.MAP_AI_UNKNOWN || direction == Map.MAP_AI_PLAYER){
				dumbMove(deltaTime, info, rectangle, movementRectangle);
			} else {
				smartMove(deltaTime, info, rectangle, movementRectangle, direction);
			}
			
		}
	}
	
	private void dumbMove(float deltaTime, EnemySmartInfo info, Rectangle rectangle, Rectangle movementRectangle){
		Vector3 movementInfo = calculateMovement(deltaTime, info.xPosition, info.yPosition,  info.isJustHitPlayer);
		Vector3 fenceMovementInfo;
		if ((TimeUtils.millis() - info.lastAttackTime) > attackDelay){
			fenceMovementInfo = map.enemyDumbMapMovement(movementRectangle, movementInfo, damage);
		} else {
			fenceMovementInfo = map.enemyDumbMapMovement(movementRectangle, movementInfo, 0);
		}
		
		info.xPosition += fenceMovementInfo.x;
		info.yPosition += fenceMovementInfo.y;
		info.rotation = movementInfo.z;
		rectangle.x += fenceMovementInfo.x;
		rectangle.y += fenceMovementInfo.y;
		movementRectangle.x += fenceMovementInfo.x;
		movementRectangle.y += fenceMovementInfo.y;
		info.isJustHitPlayer = false;
		
		if (fenceMovementInfo.z > 0){
			info.lastAttackTime = TimeUtils.millis();
		}
	}
	
	private void smartMove(float deltaTime, EnemySmartInfo info
			, Rectangle rectangle, Rectangle movementRectangle, char direction){
		float xMovement = 0;
		float yMovement = 0;
		float rotation = 0;
		
		
		if (direction == Map.MAP_AI_DOWN){
			yMovement = -1f;
			rotation = 90f;
		} else if (direction == Map.MAP_AI_UP){
			yMovement = 1f;
			rotation = 270f;
		} else if (direction == Map.MAP_AI_RIGHT){
			xMovement = 1f;
			rotation = 180f;
		} else if (direction == Map.MAP_AI_LEFT){
			xMovement = -1f;
			rotation = 0f;
		}
		
		xMovement *= (speed * deltaTime); 
		yMovement *= (speed * deltaTime);
		
		info.xPosition += xMovement;
		info.yPosition += yMovement;
		info.rotation = rotation;
		rectangle.x += xMovement;
		rectangle.y += yMovement;
		movementRectangle.x += xMovement;
		movementRectangle.y += yMovement;
		info.isJustHitPlayer = false;
	}
	
	
	private Vector3 calculateMovement(float deltaTime, float x, float y, boolean isJustAttacked){
		float deltaDistanceX;
		float deltaDistanceY;
		float deltaDistance;
		float deltaX;
		float deltaY;
		float rotateTarget;
		
		deltaDistanceX = player.getSprite().getX() - x;
		deltaDistanceY = player.getSprite().getY() - y;
		deltaDistance = (float)Math.sqrt( (double) ((deltaDistanceX * deltaDistanceX) 
				+ (deltaDistanceY * deltaDistanceY)));
		if(isJustAttacked == false){
			deltaX = speed * deltaTime * (deltaDistanceX / deltaDistance);
			deltaY = speed * deltaTime * (deltaDistanceY / deltaDistance);
		} else {
			deltaX = 0;
			deltaY = 0;
		}
		
		rotateTarget =  (float) (Math.atan2((double)(-deltaDistanceY) 
				,(double) (-deltaDistanceX)) * 180.0d / Math.PI);
		
		return new Vector3(deltaX, deltaY, rotateTarget);
	}
	
	private void checkDead(){
		Iterator<EnemySmartInfo> iterInfo = enemySmartInfoArray.iterator();
		Iterator<Rectangle> iterRectangle = enemySmartRectangleArray.iterator();
		Iterator<Rectangle> iterMovementRectangle = enemySmartMovementRectangleArray.iterator();
		
		while (iterInfo.hasNext() && iterRectangle.hasNext() && iterMovementRectangle.hasNext()){
			EnemySmartInfo info = iterInfo.next();
			Rectangle rectangle = iterRectangle.next();
			Rectangle movementRectangle = iterMovementRectangle.next();
			
			if (info.health <= 0){
				itemDropper.dropItem(Enemy.ENEMY_SMART, info.xPosition, info.yPosition);
				tracker.enemyDown(Enemy.ENEMY_SMART);
				iterInfo.remove();
				iterRectangle.remove();
				iterMovementRectangle.remove();
				
			}
		}
	}
	
	private void checkAttack(){
		Iterator<EnemySmartInfo> iterInfo = enemySmartInfoArray.iterator();
		Iterator<Rectangle> iterRectangle = enemySmartRectangleArray.iterator();
		
		while (iterInfo.hasNext() && iterRectangle.hasNext()){
			EnemySmartInfo info = iterInfo.next();
			Rectangle rectangle = iterRectangle.next();
			
			if(rectangle.overlaps(player.getRectangle())){
				if (TimeUtils.millis() - info.lastAttackTime > attackDelay){
					player.getHit(damage);
					info.lastAttackTime = TimeUtils.millis();
				}
				
				info.isJustHitPlayer = true;
			}
		}
	}
}
