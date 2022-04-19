package org.chromium.userinterface;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.chromium.gameobject.Clouds;
import org.chromium.gameobject.EnemiesManager;
import org.chromium.gameobject.Land;
import org.chromium.gameobject.MainCharacter;
import org.chromium.util.Resource;

public class GameScreen extends JPanel implements Runnable, KeyListener, MouseListener {

	private static final int START_GAME_STATE = 0;
	private static final int GAME_PLAYING_STATE = 1;
	private static final int GAME_OVER_STATE = 2;
	
	private Land land;
	private MainCharacter mainCharacter;
	private EnemiesManager enemiesManager;
	private Clouds clouds;
	private Thread thread;

	private boolean isKeyPressed;

	private int gameState = START_GAME_STATE;

	private BufferedImage replayButtonImage;
	private BufferedImage gameOverButtonImage;

	private final Container contentPane;

	public GameScreen(Container contentPane) {
		this.contentPane = contentPane;
		mainCharacter = new MainCharacter();
		land = new Land(GameWindow.SCREEN_WIDTH, mainCharacter);
		mainCharacter.setSpeedX(4);
		replayButtonImage = Resource.getResouceImage("res/gui/screens/dino/replay_button.png");
		gameOverButtonImage = Resource.getResouceImage("res/gui/screens/dino/gameover_text.png");
		enemiesManager = new EnemiesManager(mainCharacter);
		clouds = new Clouds(GameWindow.SCREEN_WIDTH, mainCharacter);
	}

	@Override
	public Dimension getPreferredSize() {
		return contentPane.getSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public void startGame() {
		thread = new Thread(this);
		thread.start();
	}

	private long start = -1L;

	public void scoreUpdate(){
		if(gameState == GAME_PLAYING_STATE){
			if(start == -1L) start = System.currentTimeMillis();
			mainCharacter.updateCurrent(start);
		}
		if(gameState == GAME_OVER_STATE){
			start = -1L;
		}
	}

	public void gameUpdate() {
		if (gameState == GAME_PLAYING_STATE) {
			clouds.update();
			land.update();
			mainCharacter.update();
			enemiesManager.update();
			if (enemiesManager.isCollision()) {
				mainCharacter.playDeadSound();
				gameState = GAME_OVER_STATE;
				mainCharacter.dead(true);
				start = -1L;
			}
		}
	}

	public void paint(Graphics rg) {

		rg.setColor(Color.decode("#f7f7f7"));
		rg.fillRect(0, 0, getWidth(), getHeight());

		BufferedImage image = new BufferedImage(GameWindow.SCREEN_WIDTH, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		switch (gameState) {
		case START_GAME_STATE:
			mainCharacter.draw(g);
			break;
		case GAME_PLAYING_STATE:
		case GAME_OVER_STATE:
			clouds.draw(g);
			land.draw(g);
			enemiesManager.draw(g);
			mainCharacter.draw(g);

			g.setFont(g.getFont().deriveFont(Font.BOLD).deriveFont(g.getFont().getSize() + 2F));
			g.setColor(Color.BLACK);
			g.drawString("HI " + score(mainCharacter.score) + " " + score(mainCharacter.current), 450, 20);
			if (gameState == GAME_OVER_STATE) {
				g.drawImage(gameOverButtonImage, 200, 30, null);
				g.drawImage(replayButtonImage, 283, 50, null);
				
			}
			break;
		}

		rg.drawImage(image, (getWidth() / 2) - (GameWindow.SCREEN_WIDTH / 2), (getHeight() / 2) - (200 / 2), GameWindow.SCREEN_WIDTH, 200, this);
	}

	private String score(int score) {
		String s = "" + score;
		while(s.length() < 5){
			s = "0" + s;
		}
		return s;
	}

	@Override
	public void run() {

		int fps = 100;
		long msPerFrame = 1000 * 1000000 / fps;
		long lastTime = 0;
		long elapsed;
		
		int msSleep;
		int nanoSleep;

		long endProcessGame;
		long lag = 0;

		while (true) {
			gameUpdate();
			scoreUpdate();
			repaint();
			endProcessGame = System.nanoTime();
			elapsed = (lastTime + msPerFrame - System.nanoTime());
			msSleep = (int) (elapsed / 1000000);
			nanoSleep = (int) (elapsed % 1000000);
			if (msSleep <= 0) {
				lastTime = System.nanoTime();
				continue;
			}
			try {
				Thread.sleep(msSleep, nanoSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lastTime = System.nanoTime();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		press(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		release(e.getKeyCode());
	}

	private void press(int code){
		if (!isKeyPressed) {
			isKeyPressed = true;
			switch (gameState) {
				case START_GAME_STATE:
					if (code == KeyEvent.VK_SPACE) {
						gameState = GAME_PLAYING_STATE;
					}
					break;
				case GAME_PLAYING_STATE:
					if (code == KeyEvent.VK_SPACE) {
						mainCharacter.jump();
					} else if (code == KeyEvent.VK_DOWN) {
						mainCharacter.down(true);
					}
					break;
				case GAME_OVER_STATE:
					if (code == KeyEvent.VK_SPACE) {
						gameState = GAME_PLAYING_STATE;
						resetGame();
					}
					break;

			}
		}
	}

	private void release(int code){
		isKeyPressed = false;
		if (gameState == GAME_PLAYING_STATE) {
			if (code == KeyEvent.VK_DOWN) {
				mainCharacter.down(false);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void resetGame() {
		enemiesManager.reset();
		mainCharacter.dead(false);
		mainCharacter.reset();
		start = -1L;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		e.getComponent().requestFocus();
		press(KeyEvent.VK_SPACE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.getComponent().requestFocus();
		release(KeyEvent.VK_SPACE);
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
