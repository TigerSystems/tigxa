package org.chromium.gameobject;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import de.MarkusTieger.Tigxa.Browser;
import org.chromium.util.Animation;
import org.chromium.util.Resource;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MainCharacter {

	public static final int LAND_POSY = 80;
	public static final float GRAVITY = 0.4f;
	
	private static final int NORMAL_RUN = 0;
	private static final int JUMPING = 1;
	private static final int DOWN_RUN = 2;
	private static final int DEATH = 3;
	
	private float posY;
	private float posX;
	private float speedX;
	private float speedY;
	private Rectangle rectBound;
	
	public int current = 0;

	public static int score = 0;
	
	private int state = NORMAL_RUN;
	
	private Animation normalRunAnim;
	private BufferedImage jumping;
	private Animation downRunAnim;
	private BufferedImage deathImage;

	private AudioClip jumpSound, deadSound, scoreUpSound;

	public MainCharacter() {
		posX = 50;
		posY = LAND_POSY;
		rectBound = new Rectangle();
		normalRunAnim = new Animation(90);
		normalRunAnim.addFrame(Resource.getResouceImage("res/gui/screens/dino/main-character1.png"));
		normalRunAnim.addFrame(Resource.getResouceImage("res/gui/screens/dino/main-character2.png"));
		jumping = Resource.getResouceImage("res/gui/screens/dino/main-character3.png");
		downRunAnim = new Animation(90);
		downRunAnim.addFrame(Resource.getResouceImage("res/gui/screens/dino/main-character5.png"));
		downRunAnim.addFrame(Resource.getResouceImage("res/gui/screens/dino/main-character6.png"));
		deathImage = Resource.getResouceImage("res/gui/screens/dino/main-character4.png");
		
		try {
			jumpSound =  Applet.newAudioClip(Resource.getResourceURL("res/gui/screens/dino/jump.wav"));
			deadSound =  Applet.newAudioClip(Resource.getResourceURL("res/gui/screens/dino/dead.wav"));
			scoreUpSound =  Applet.newAudioClip(Resource.getResourceURL("res/gui/screens/dino/scoreup.wav"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}
	
	public void draw(Graphics g) {
		switch(state) {
			case NORMAL_RUN:
				g.drawImage(normalRunAnim.getFrame(), (int) posX, (int) posY, null);
				break;
			case JUMPING:
				g.drawImage(jumping, (int) posX, (int) posY, null);
				break;
			case DOWN_RUN:
				g.drawImage(downRunAnim.getFrame(), (int) posX, (int) (posY + 20), null);
				break;
			case DEATH:
				g.drawImage(deathImage, (int) posX, (int) posY, null);
				break;
		}
//		Rectangle bound = getBound();
//		g.setColor(Color.RED);
//		g.drawRect(bound.x, bound.y, bound.width, bound.height);
	}

	public void update() {

		normalRunAnim.updateFrame();
		downRunAnim.updateFrame();
		if(posY >= LAND_POSY) {
			posY = LAND_POSY;
			if(state != DOWN_RUN) {
				state = NORMAL_RUN;
			}
		} else {
			speedY += GRAVITY;
			posY += speedY;
		}
	}
	
	public void jump() {
		if(posY >= LAND_POSY) {
			if(jumpSound != null) {
				jumpSound.play();
			}
			speedY = -7.5f;
			posY += speedY;
			state = JUMPING;
		}
	}
	
	public void down(boolean isDown) {
		if(state == JUMPING) {
			return;
		}
		if(isDown) {
			state = DOWN_RUN;
		} else {
			state = NORMAL_RUN;
		}
	}
	
	public Rectangle getBound() {
		rectBound = new Rectangle();
		if(state == DOWN_RUN) {
			rectBound.x = (int) posX + 5;
			rectBound.y = (int) posY + 20;
			rectBound.width = downRunAnim.getFrame().getWidth() - 10;
			rectBound.height = downRunAnim.getFrame().getHeight();
		} else {
			rectBound.x = (int) posX + 5;
			rectBound.y = (int) posY;
			rectBound.width = normalRunAnim.getFrame().getWidth() - 10;
			rectBound.height = normalRunAnim.getFrame().getHeight();
		}
		return rectBound;
	}
	
	public void dead(boolean isDeath) {
		if(isDeath) {
			state = DEATH;
		} else {
			state = NORMAL_RUN;
		}
	}
	
	public void reset() {
		posY = LAND_POSY;
	}
	
	public void playDeadSound() {
		deadSound.play();
	}
	
	public void upScore() {
		/*score += 20;
		if(score % 100 == 0) {
			scoreUpSound.play();
		}*/
	}

	public void current(int current) {
		if(current > score){
			score = current;
		}
		this.current = current;

		if(current % 100 == 0) {
			scoreUpSound.play();
		}
	}

	public void updateCurrent(long start) {
		if(state != DEATH) {
			long difference = System.currentTimeMillis() - start;
			difference /= 100L;
			current((int)difference);
		}
	}
}
