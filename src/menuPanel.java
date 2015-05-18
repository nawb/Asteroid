import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class menuPanel extends JPanel implements KeyListener,ActionListener {
	boolean visible = false;
	Game gm; //main game image
	
	Button btnSave;
	Button btnLoad;
	Button btnImm;
	Button btnGravOn;
	Button btnGravVisible;
	Button btnPlayer;
	Button btnScore;
	Button btnHS; //highscores
	Button btnLevel;
	Button btnAst;
	Button btnBack;
	Button btnQuit;
	
	public menuPanel(Game gm) {
		super();
		this.gm = gm;
		
		btnSave = new Button("SAVE");
		btnLoad = new Button("LOAD");
		btnImm = new Button("Unlimited lives: "+gm.getSettingImmortal());
		btnGravOn = new Button("Chimney of Doom: "+gm.getSettingGravOn());
		btnGravVisible = new Button("Chimney visibility: "+gm.getSettingGravVisible());
		btnPlayer = new Button(gm.getSettingPlayers());
		btnScore = new Button(gm.getSettingScore());
		btnHS = new Button("High Scores");
		btnLevel = new Button("Level: "+gm.getSettingLevel());
		btnAst = new Button("Asteroids: "+gm.getSettingAst());
		btnBack = new Button("BACK TO GAME");
		btnQuit = new Button("QUIT");
		
		btnSave.setFocusable(false);
		btnLoad.setFocusable(false);
		btnImm.setFocusable(false);
		btnGravOn.setFocusable(false);
		btnGravVisible.setFocusable(false);
		btnPlayer.setFocusable(false);
		btnScore.setFocusable(false);
		btnHS.setFocusable(false);
		btnLevel.setFocusable(false);
		btnAst.setFocusable(false);
		btnBack.setFocusable(false);
		btnQuit.setFocusable(false);
		
		btnSave.setActionCommand("save");
		btnLoad.setActionCommand("load");
		btnImm.setActionCommand("immortal");
		btnGravOn.setActionCommand("gravon");
		btnGravVisible.setActionCommand("gravvisible");
		btnPlayer.setActionCommand("player");
		btnScore.setActionCommand("score");
		btnHS.setActionCommand("hs");
		btnLevel.setActionCommand("level");
		btnAst.setActionCommand("ast");
		btnBack.setActionCommand("back");
		btnQuit.setActionCommand("quit");
		
		btnSave.addActionListener(this);
		btnLoad.addActionListener(this);
		btnImm.addActionListener(this);
		btnGravOn.addActionListener(this);
		btnGravVisible.addActionListener(this);
		btnPlayer.addActionListener(this);
		btnScore.addActionListener(this);
		btnHS.addActionListener(this);
		btnLevel.addActionListener(this);
		btnAst.addActionListener(this);
		btnBack.addActionListener(this);
		btnQuit.addActionListener(this);
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(btnHS);
		add(btnSave);
		add(btnLoad);
		add(btnLevel);
		add(btnAst);
		add(btnImm);
		add(btnGravOn);
		add(btnGravVisible);
		add(btnPlayer);
		add(btnScore);
		add(btnBack);
		add(btnQuit);
		
		setVisible(false);		
	}
	public void showMenu() {
		visible = true;
		this.requestFocus();
		this.setVisible(visible);
	}
	public void hideMenu() {
		visible = false;
		gm.requestFocus(); //give focus back to main image
		this.setVisible(visible);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "save") {
			gm.saveGame();
		}
		else if (e.getActionCommand() == "load") {
			gm.loadGame();
			this.hideMenu();
		}
		else if (e.getActionCommand() == "immortal") {
			gm.toggleUnlimitedLives();
			btnImm.setLabel("Unlimited lives: "+gm.getSettingImmortal());
		}
		else if (e.getActionCommand() == "gravvisible") {
			gm.toggleGravVisible();
			btnGravVisible.setLabel("Hole visibility: "+gm.getSettingGravVisible());
		}
		else if (e.getActionCommand() == "gravon") {
			gm.toggleGravOn();
			btnGravOn.setLabel("Hole of Doom: "+gm.getSettingGravOn());
		}
		else if (e.getActionCommand() == "player") {
			gm.togglePlayers();
			btnPlayer.setLabel(gm.getSettingPlayers());
		}
		else if (e.getActionCommand() == "score") {
			gm.toggleScore();
			btnScore.setLabel(gm.getSettingScore());
		}
		else if (e.getActionCommand() == "level") {
			gm.toggleLevel();
			btnLevel.setLabel("Level: "+gm.getSettingLevel());
		}
		else if (e.getActionCommand() == "ast") {
			gm.toggleAst();
			btnAst.setLabel("Asteroids: "+gm.getSettingAst());
		}
		else if (e.getActionCommand() == "hs") {
			gm.displayHighScores(gm.g);
		}
		else if (e.getActionCommand() == "back") {
			this.hideMenu();
		}
		else if (e.getActionCommand() == "quit") {
			
			System.exit(0);
		}
		
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE) 
			this.hideMenu();
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
	}
}