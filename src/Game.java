import java.applet.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JLabel;

public class Game extends Applet implements Runnable, KeyListener{
	//image stuff
	Graphics g;
	Dimension dim;
	Image img;
	
	//thread
	Thread thread;
	long startTime, endTime, framePeriod;
	int f_width, f_height;
	
	//directions
	public final int UP = 1;
	public final int DOWN = 2;
	public final int LEFT = 3;
	public final int RIGHT = 4;
	public int dir = 0; //<-? 
	
	//Menu flags
	boolean paused = false;
	int pause_cnt = 0; // to toggle the pause in an escape key press
	//unlim lives
	boolean unlimLives = false;
	int unlimCnt = 0;
	//gobject
	boolean gravObj;
	boolean gravVisible = false;
	int gCnt = 0;
	//player 2 toggle cnt
	int twoCnt = 0;
	
	//alien ship flag
	boolean alien_ship = false;
	int alienCnt = 0;
	double alienrand;
	//GAME OBJECTS:
	//All Objects for redrawing
	
	//Score
	int score = 0;
	LinkedList<HighScore> highscores; //<-- nabeel
	
	//Ships
	public Ship p1;
	public Ship p2;
	public AlienShip a1;
	boolean p2_active = false;
	//Bullets
	//int numBulletsOnScreen; //keep track of no. of bullets on screen
	ArrayList<Bullet> bulletsOnScreen; //store bullets currently on screen in array
	//Boolean shotsFired; //whether one of the ships is currently shooting...if it is, draw the array
	
	//Asteroids
	int numAst = 0;
	ArrayList<Asteroid> allAsteroids;
	
	int level; // the level
	
	//initializing the applet
	public void init()
	{
		this.resize(500, 500);
		
		level = 0;
		score = 0;
		
		highscores = new LinkedList<HighScore>(); //<-- nabeel
		for (int i = 0; i < 10; i++) {
			highscores.add(new HighScore("PARTH", 10-i)); //add 10 empty high scores
		}
		
		//Ships
		p1 = new Ship(280,250);
		p2 = new Ship(200,250);
		a1 = new AlienShip((int)(500*Math.random()),(int)(500*Math.random()),1); // Alien at random position
		alienrand = 500;//Math.random();
		p2.active = false;
		a1.active = false;
		//Bullets
		bulletsOnScreen = new ArrayList<Bullet>(); //initialize array to max no of bullets possible on screen
		p1.shotsFired = false;
		p2.shotsFired = false;
		//Asteroids
		allAsteroids = new ArrayList<Asteroid>();		
		newLevel(true);
		
		startTime = 0;
		endTime = 0;
		framePeriod = 25;
		addKeyListener(this);
		dim=getSize(); //set dim equal to the size of the applet
		img=createImage(dim.width, dim.height);//create the back buffer
		g=img.getGraphics(); //retrieve Graphics object for back buffer
		setBackground(Color.black);
		createMenu(); //<--nabeel
		thread = new Thread(this);
		thread.start();
	}
	
	//run function for thread
	public void run()
	{
		while(true)
		{
			startTime = System.currentTimeMillis();			
			if(!paused)
			{
				requestFocus(); //<-nabeel
				
				if(alienCnt == (int)alienrand && a1.active == false)
				{
					a1.active = true;
				}
				else
				{
					alienCnt++;
				}
				
				if(p1.numLives == 0)
				{
					p1.active = false;
					
				}
				if(p2.numLives == 0)
				{
					p2.active = false;
				}
				//Ships
				p1.moveShip(dir);
				p2.moveShip(dir);
				a1.moveShip(p1.pos.x,p1.pos.y,p2.pos.x,p2.pos.y,p2_active);
				
				//Bullets
				//p1
				if (p1.shotsFired) { 
					Bullet b1 = p1.fireBullet();
					if (b1 != null) {
						bulletsOnScreen.add(b1);
					}
				}
				//p2
				if (p2.shotsFired && p2.active) { 
					Bullet b2 = p2.fireBullet();
					if (b2 != null) {
						bulletsOnScreen.add(b2);
					}
				}
				//alien always fires 
				if (a1.active) { 
					Bullet b3 = a1.fireBullet();
					if (b3 != null) {
						bulletsOnScreen.add(b3);
					}
				}				
				moveBullets();
				
				//Asteroids
				detectAsteroidCollisions();
				detectBulletShipCollisions();				
				//collision detection
				moveAsteroids();
				
				//update level
				if(!checkAllAsteroids() )
				{
					newLevel(true); // new level
				}
				else if( p1.numLives == 0 && p2_active && p2.numLives == 0)
				{
					newLevel(false);// same level
					paused = true;
					p1 = new Ship(280, 250);
					p2 = new Ship(230, 250);
					highscores.addFirst(new HighScore("PLAY1", score));
					showMenu();
				}
				else if( p1.numLives == 0 && !p2_active)
				{
					newLevel(false);// same level
					paused = true;
					p1 = new Ship(280, 250);
					highscores.addFirst(new HighScore("PLAY1", score));
					showMenu();
				}
								
			}
			repaint();
			try{ 
				endTime=System.currentTimeMillis(); 
				if(framePeriod-(endTime-startTime)>0) { 
					Thread.sleep(framePeriod-(endTime-startTime));
				}
			} catch(InterruptedException e) {
			} finally {				
			}
		}
	}	

	//Paint function 	
	public void paint(Graphics gfx)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, 500, 500);
	
		if (!hideScore) { //<-- nabeel
			drawScore(g);
		}		
		drawTime(g); //<-- nabeel
		
		//grav object visible or not
		if(gravVisible)
		{
			g.setColor(Color.yellow);
			g.fillRect(230, 230, 40, 40);			
		}
		
		p1.drawShip(g);
		p2.drawShip(g);
		a1.drawShip(g);
		//g.add(label);
		drawBullets(g);
		drawAsteroids(g);
		
		if (this.menu.isVisible()) { //<-- nabeel
			//if the menu is up, that means game has been paused, so...
			drawPaused(g);
		}		
		gfx.drawImage(img,0,0,this);
		//System.out.println("Out draw()");
	}
	
	//updating the screen
	public void update(Graphics gfx)
	{
		paint(gfx);
	}
	
	//Game Objects 
	
	//
	public void detectBulletShipCollisions() // Bullet ship Collision
	{
		for (int i = 0; i < bulletsOnScreen.size(); i++)
		{
			if(!unlimLives && detectCollision(p1,bulletsOnScreen.get(i))) // ship1 - bullets
			{
				//System.exit(0);
				//numAst = 0;
				System.out.println("SHIP1 hit");
				p1.hitWait += 1;
				bulletsOnScreen.get(i).active = false;
				if(p1.hitWait == 15)
				{
					p1.numLives--;
					p1.hitWait = 0;
					System.out.println("SHIP1 hit");	
					//allAsteroids.get(j).hitNum++;// increment hit
				}
				//return;
			}
			if(!unlimLives && detectCollision(p2,bulletsOnScreen.get(i))) // ship2 - bullets
			{
				//System.exit(0);
				//numAst = 0;
				p2.hitWait += 1;
				
				bulletsOnScreen.get(i).active = false;
				if(p2.hitWait == 15)
				{
					p2.numLives--;
					p2.hitWait = 0;
					score+= 100;
					System.out.println("SHIP2 hit");	
					//allAsteroids.get(j).hitNum++;// increment hit
				}
				//return;
			}
			if( detectCollision(a1,bulletsOnScreen.get(i))) // alien - bullets
			{
				//System.exit(0);
				//numAst = 0;
				a1.hitWait += 1;
				bulletsOnScreen.get(i).active = false;
				if(a1.hitWait == 10)
				{
					a1.numLives--;
					a1.hitWait = 0;
					score += 100;
					System.out.println("ALIEN hit");	
					//allAsteroids.get(j).hitNum++;// increment hit
				}
				//return;
			}			
			
			
		}
	}
	
	//Bullet functions
	public void drawBullets(Graphics g) {
		for (int i = 0; i < bulletsOnScreen.size(); i++) {
			if(bulletsOnScreen.get(i).active == true)
			bulletsOnScreen.get(i).drawBullet(g);
		}
	}
	public void moveBullets() {
		for (int i = 0; i < bulletsOnScreen.size(); i++) {
			bulletsOnScreen.get(i).move();
			if (bulletsOnScreen.get(i).travelDist <= 0) { //if it has traveled the length of its allowable distance (one screen width)
				bulletsOnScreen.remove(i);
			}
		}
	}
	
	//Asteroid functions
	public void drawAsteroids(Graphics g) {
		for (int i = 0; i < allAsteroids.size(); i++) {
			if(allAsteroids.get(i).active == true)
			allAsteroids.get(i).drawAsteroid(g);
		}
	}
	public void moveAsteroids() {
		for (int i = 0; i < allAsteroids.size(); i++) {
			allAsteroids.get(i).moveAsteroid();
//			if (allAsteroids.get(i).hitNum == 3) { //if the number of hits have reached the limit of 3
//				allAsteroids.remove(i);
//			}
		}
	}		
	
	public Asteroid genSmallAsteroid()
	{
		return new Asteroid((int)Math.random()*500, (int)Math.random()*500, 10, level); 
	}
	
	public void detectAsteroidCollisions()
	{
		for (int j = 0; j < allAsteroids.size(); j++)
		{
			if(!unlimLives && detectCollision(p1,allAsteroids.get(j))) // ship1 - Asteroid
			{
				//System.exit(0);
				//numAst = 0;
				p1.hitWait += 1;
				if(p1.hitWait == 10)
				{
					p1.numLives--;
					p1.hitWait = 0;
					System.out.println("SHIP1 collision");	
					//allAsteroids.get(j).hitNum++;// increment hit
					
					if(allAsteroids.get(j).radius > 10)// if big one is hit
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the bigger one
						for(int  k = 0 ; k < 3; k++) //  add three smaller ones
						{
							Asteroid temp = genSmallAsteroid();
							allAsteroids.add(temp);
						}
						//continue;
						score+=5;
						System.out.println("BIG COLLISION");
					}
					else if(allAsteroids.get(j).radius <= 10)// if smaller one is hit 
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the smaller one
						score+=5;
						//System.out.println("small COLLISION");
					}					
				}

				//return;
			}
			if(!unlimLives && detectCollision(p2,allAsteroids.get(j))) // ship2 - Asteroid
			{
				//System.exit(0);
				//numAst = 0;
				p2.hitWait += 1;
				if(p2.hitWait == 10)
				{
					p2.numLives--;
					p2.hitWait = 0;
					System.out.println("SHIP2 collision");
					//allAsteroids.get(j).hitNum++;// increment hit
					
					if(allAsteroids.get(j).radius > 10)// if big one is hit
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the bigger one
						for(int  k = 0 ; k < 3; k++) //  add three smaller ones
						{
							Asteroid temp = genSmallAsteroid();
							allAsteroids.add(temp);
						}
						//continue;
						score+=5;
						//System.out.println("BIG COLLISION");
					}
					else if(allAsteroids.get(j).radius <= 10)// if smaller one is hit 
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the smaller one
						score+=5;
						//System.out.println("small COLLISION");
					}					
				}				

				//return;
			}
			
			if(detectCollision(a1,allAsteroids.get(j))) // alienship - Asteroid
			{
				//System.exit(0);
				//numAst = 0;
				a1.hitWait += 1;
				if(a1.hitWait == 10)
				{
					a1.numLives--;
					a1.hitWait = 0;
					System.out.println("SHIP2 collision");
					//allAsteroids.get(j).hitNum++;// increment hit
					
					if(allAsteroids.get(j).radius > 10)// if big one is hit
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the bigger one
						for(int  k = 0 ; k < 3; k++) //  add three smaller ones
						{
							Asteroid temp = genSmallAsteroid();
							allAsteroids.add(temp);
						}
						//continue;
						
						System.out.println("BIG COLLISION");
					}
					else if(allAsteroids.get(j).radius <= 10)// if smaller one is hit 
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the smaller one
						System.out.println("small COLLISION");
					}					
				}				

				//return;
			}			
			
			//Bullet Asteroid collisions
			for (int i = 0; i < bulletsOnScreen.size(); i++)
			{				
				if(detectCollision(bulletsOnScreen.get(i),allAsteroids.get(j)) == true)
				{
					//bulletsOnScreen.remove(i);
					bulletsOnScreen.get(i).active = false; // deactivating the bullet
					
					//allAsteroids.get(j).hitNum++;// increment hit
					
					if(allAsteroids.get(j).radius > 10)// if big one is hit
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the bigger one
						for(int  k = 0 ; k < 3; k++) //  add three smaller ones
						{
							Asteroid temp = genSmallAsteroid();
							allAsteroids.add(temp);
						}
						//continue;
						
						System.out.println("BIG COLLISION");
					}
					else if(allAsteroids.get(j).radius <= 10)// if smaller one is hit 
					{
						//allAsteroids.remove(j); 
						allAsteroids.get(j).active = false;// delete the smaller one
						score+=5;
						//System.out.println("small COLLISION");
					}
				}
			}
			
		}
	}
	
	public boolean checkAllAsteroids()
	{
		for (int j = 0; j < allAsteroids.size(); j++)
		{
			if(allAsteroids.get(j).active ==  true)
			{
				return true;
			}
		}
		return false;
	}
	//general functions
	public boolean detectCollision(GameObject g1, GameObject g2)//detects collision between any two game objects
	{
		double xdiff, ydiff;
		xdiff = Math.pow((g1.pos.x-g2.pos.x),2);
		ydiff = Math.pow((g1.pos.y-g2.pos.y),2);
		double distance = Math.sqrt(xdiff+ydiff);
		
		if(g1.active && g2.active && distance <= (g1.radius + g2.radius + 5)) // if the distance is less than the sum of the radii
		{
			return true;
		}
		return false;
	}
	
	public void newLevel(boolean newLevel)
	{
		//removing the existing asteroids in the list
		for(int i = 0; i < allAsteroids.size(); i++)
		{
			allAsteroids.get(i).active = false;
			allAsteroids.remove(i);
		}
		
		//incrementing level and creating new asteroids
		if(newLevel)
		{level++;
		score+=level*100;
		}
		
		numAst = 3*level; // increase of three asteroids per level
		
		//reactivating p1 and p2
		p1.active = true;
		p1.numLives = 3;

		
		if(twoCnt%2 == 1)
		{	
			p2.active = true;
			System.out.println("Player 2 active!");
			p2.numLives = 3;
		}
		else
		{	
			p2.active = false;
		}
		
		a1.active = false;
		alienCnt = 0;
		alienrand = 500;		
		a1.level = level;
		
		System.out.println("Level :"+ level);
		System.out.println("Number Ast :"+ numAst);
		
		for(int i = 0; i < allAsteroids.size(); i++)
		{
			allAsteroids.get(i).active = false;
			allAsteroids.remove(i);
		}
		
		for(int i = 0; i < numAst; i++)
		{
			Asteroid a = new Asteroid((int)Math.random()*500, (int)Math.random()*500, 22, level);
			allAsteroids.add(a);
		}		
	}	
	

	//v--nabeel
	menuPanel menu;
	//JPanel menu;
	public void createMenu() {/*
		menuButton b1 = new menuButton("Hi");
        b1.setVerticalTextPosition(AbstractButton.CENTER);
        b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        b1.setMnemonic(KeyEvent.VK_E);
        b1.setActionCommand("disable");
        b1.addActionListener(this);
        add(b1);*/

		this.menu = new menuPanel(this);
		add(menu);
	}
	public void showMenu() {
		//this.menu.requestFocus();
		//this.menu.setVisible(true);
		this.menu.showMenu();
	}
	public void hideMenu() {
		//requestFocus(); //give focus back to main image
		//this.menu.setVisible(false);
		this.menu.hideMenu();
	}
	int paused_fade_timer = 0; 
	public void drawPaused(Graphics g) {
		g.setFont(new Font("default", Font.BOLD, 30));
		
		//control its blinking
		paused_fade_timer++;
		if (paused_fade_timer < 3) {
			g.setColor(Color.darkGray); }
		else if (paused_fade_timer < 6) {
			g.setColor(Color.lightGray); }
		else if (paused_fade_timer < 9) {
			g.setColor(Color.white); }
		else if (paused_fade_timer < 12) {
			g.setColor(Color.lightGray); }
		else {
			g.setColor(Color.darkGray); }
		if (paused_fade_timer == 15) { paused_fade_timer = 0; } //reset it 
		
		g.drawString("GAME PAUSED", 150, 400);
	}
	boolean hideScore = false;
	public void drawScore(Graphics g) {
		g.setColor(Color.GREEN);
		g.setFont(new Font("Courier New", Font.CENTER_BASELINE, 13));
		g.drawString(Integer.toString(score), 5, 15);
		String p1String = "";
		String p2String = "";
		if (p1.numLives == 3) {
			p1String = "\u2665 \u2665 \u2665 | P1"; }
		else if (p1.numLives == 2){
			p1String = "       \u2665 \u2665 | P1"; }
		else if (p1.numLives == 1){
			p1String = "              \u2665 | P1"; }
		else if (p1.numLives == 0){
			p1String = "                     | P1"; }
		
		if (p2.active) {
			if (p2.numLives == 3) {
				p2String = "P2 | \u2665 \u2665 \u2665"; }
			else if (p2.numLives == 2){
				p2String = "P2 | \u2665 \u2665 "; }
			else if (p2.numLives == 1){
				p2String = "P2 | \u2665  "; }
			else if (p2.numLives == 0){
				p2String = "P2 | "; }
		}
		g.drawString(p1String, 5, 490);
		g.drawString(p2String, 420, 490);
	}
	public void drawTime(Graphics g) {
		g.setColor(Color.GREEN);
		g.setFont(new Font("Courier New", Font.CENTER_BASELINE, 15));
		int currTime = 0;
		
		g.drawString(String.format("%03d", currTime), 465, 15);
	}
	public boolean toggleUnlimitedLives(){
		//returns the current state of the setting
		if (unlimLives) {
			unlimLives = false;
		}
		else {
			unlimLives = true;
		}
		return unlimLives;
	}
	public boolean toggleGravVisible(){
		//returns the current state of the setting
		if (gravVisible) {
			gravVisible = false;
		}
		else {
			gravVisible = true;
		}
		return gravVisible;
	}
	public boolean toggleGravOn(){
		//returns the current state of the setting
		if (gravObj) {
			gravObj = false;
		}
		else {
			gravObj = true;
		}
		return gravObj;
	}
	public String getSettingImmortal(){
		//returns the current state of the setting
		if (unlimLives)
			return "true";
		else
			return "false";
	}
	public String getSettingGravVisible(){
		//returns the current state of the setting
		if (gravVisible)
			return "Blazing";
		else
			return "Hidden";
	}
	public String getSettingGravOn(){
		//returns the current state of the setting
		if (gravObj)
			return "ON";
		else
			return "OFF";
	}
	public String getSettingLevel(){
		//returns the current state of the setting
		return Integer.toString(level);
	}
	public String getSettingAst(){
		//returns the current state of the setting
		return Integer.toString(numAst);
	}
	public int toggleLevel() {
		newLevel(true);
		if (level == 6) { level = 1; }
		return level;
	}
	public int toggleAst() {
		numAst++;
		if (numAst == 7) { numAst = 1; allAsteroids.clear(); }
		Asteroid a = new Asteroid((int)Math.random()*500, (int)Math.random()*500, 22, level);
		allAsteroids.add(a);
		return numAst;
	}
	public boolean togglePlayers(){
		//returns the current state of the setting
		if (p2.active) {
			p2 = new Ship(200,250); //reset ship 2 from start
			p2.active = false;
		}
		else {
			p2.active = true;
		}
		return p2.active;
	}
	public boolean toggleScore(){
		//returns the current state of the setting
		if (hideScore) {
			hideScore = false;
		}
		else {
			hideScore = true;
		}
		return hideScore;
	}
	public String getSettingScore(){
		//returns the current state of the setting
		if (hideScore)
			return "Show score";
		else
			return "Hide score";
	}
	public String getSettingPlayers(){
		//returns the current state of the setting
		if (p2.active)
			return "Buddy";
		else
			return "No Buddy";
	}
	public void loadGame() {
		loadGame("../game.save"); //default game save file
	}
	public void loadGame(String filename) {
		BufferedReader br = null;
	    
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			allAsteroids.clear();
			numAst = 0;
			while (line!=null) {
				processLine(line.trim());
				line = br.readLine();
			}
		    br.close();
		} catch (IOException ex) {
		  System.err.println("Could not read file");
		}
	}
	public void saveGame() {
		Writer writer = null;
	    
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("../game.save"), "utf-8"));
		    writer.write(writeSettings());
		    writer.write(writeShips());
		    writer.write(writeAsteroids());
		    writer.close();
		} catch (IOException ex) {
		  System.err.println("Could not find file");
		}
	}
	public String writeSettings() {
		String settings = "";
		settings = settings+"level:"+Integer.toString(level)+"\n";
		
		if (p2.active) {
			settings = settings+"players:2\n";
		}
		else {
			settings = settings+"players:1\n";
		}
		if (unlimLives) {
			settings = settings+"immortal:t\n";
		}
		else {
			settings = settings+"immortal:f\n";
		}
		if (gravObj) {
			settings = settings+"gravon:t\n";
		}
		else {
			settings = settings+"gravon:f\n";
		}
		if (gravVisible) {
			settings = settings+"gravvisible:t\n";
		}
		else {
			settings = settings+"gravvisible:f\n";
		}
		
		return settings;
	}
	public String writeShips() {
		String info = "";
		
		info = info+"p1:";
		if (p1.active) {
			info = info+"1,";
		}
		else {
			info = info+"0,";
		}/*//WITH BRACKETS:
		info = info+"("+Integer.toString(p1.pos.x)+","+Integer.toString(p1.pos.y)+")";
		info = info+"("+Long.toString(Math.round(p1.x_vel))+","+Long.toString(Math.round(p1.y_vel))+")";
		info = info+"("+Integer.toString(p1.pos.x)+","+Integer.toString(p1.pos.y)+")\n";
		info = info+Long.toString(Math.round(p1.angle))+"\n";
		*/
		info = info+Integer.toString(p1.numLives)+",";
		info = info+Integer.toString(p1.pos.x)+","+Integer.toString(p1.pos.y)+",";
		info = info+Long.toString(Math.round(p1.x_vel))+","+Long.toString(Math.round(p1.y_vel))+",";
		info = info+Long.toString(Math.round(p1.angle))+",";
		info = info+Long.toString(Math.round(p1.gx_vel))+","+Long.toString(Math.round(p1.gy_vel))+",";
		info = info+Long.toString(Math.round(p1.gangle))+",";
		
		info = info+"\np2:";
		if (p2.active) {
			info = info+"1,";
		}
		else {
			info = info+"0,";
		}
		info = info+Integer.toString(p2.numLives)+",";
		info = info+Integer.toString(p2.pos.x)+","+Integer.toString(p2.pos.y)+",";
		info = info+Long.toString(Math.round(p2.x_vel))+","+Long.toString(Math.round(p2.y_vel))+",";
		info = info+Long.toString(Math.round(p2.angle))+",";
		info = info+Long.toString(Math.round(p2.gx_vel))+","+Long.toString(Math.round(p2.gy_vel))+",";
		info = info+Long.toString(Math.round(p2.gangle))+",";
		
		
		info = info+"\n";
		return info;
	}
	public String writeAsteroids() {
		String info = "";
		for (int i = 0; i < allAsteroids.size(); i++) {
			Asteroid a = allAsteroids.get(i);
			info = info+"asteroid:";
			if (a.active) {
				info = info+"1,";
			}
			else {
				info = info+"0,";
			}
			info = info+Integer.toString(a.pos.x)+","+Integer.toString(a.pos.y)+",";
			info = info+Long.toString(Math.round(a.x_vel))+","+Long.toString(Math.round(a.y_vel))+",";
			info = info+Long.toString(Math.round(a.angle))+",";
			info = info+Long.toString(Math.round(a.minVel))+","+Long.toString(Math.round(a.maxVel))+",";
			info = info+Integer.toString(p1.radius)+"\n";
		}
		return info;
	}
	public void processLine(String line) {
		String[] fields = line.split(":");
			//is a setting field
			if (fields[0].equals("players")) {
				if (fields[1].equals("2")) { p2.active = true; }
				else { p2.active = false; }
			}
			if (fields[0].equals("immortal")) {
				if (fields[1].equals("t")) {unlimLives = true; }
				else { unlimLives = false; }
			}
			if (fields[0].equals("gravon")) {
				if (fields[1].equals("t")) { gravObj = true; }
				else { gravObj = false; }
			}
			if (fields[0].equals("gravvisible")) {
				if (fields[1].equals("t")) { gravVisible = true; }
				else { gravVisible = false; }
			}
			if (fields[0].equals("level")) {
				level = Integer.parseInt(fields[1]);
			}
		if (line.startsWith("p1")) { //p1 info
			String[] pfields = fields[1].split(",");
			if (pfields[0].equals("1")) {
				p1.active = true; }
			else { p1.active = false; }
			p1.numLives = Integer.parseInt(pfields[1]);
			p1.pos.x = Integer.parseInt(pfields[2]);
			p1.pos.y = Integer.parseInt(pfields[3]);
			p1.x_vel = Double.parseDouble(pfields[4]);
			p1.y_vel = Double.parseDouble(pfields[5]);
			p1.angle = Double.parseDouble(pfields[6]);
			p1.gx_vel = Double.parseDouble(pfields[7]);
			p1.gy_vel = Double.parseDouble(pfields[8]);
			p1.gangle = Double.parseDouble(pfields[9]);
		}
		if (line.startsWith("p2")) { //p1 info
			String[] pfields = fields[1].split(",");
			if (pfields[0].equals("1")) {
				p2.active = true; }
			else { p2.active = false; }
			p2.numLives = Integer.parseInt(pfields[1]);
			p2.pos.x = Integer.parseInt(pfields[2]);
			p2.pos.y = Integer.parseInt(pfields[3]);
			p2.x_vel = Double.parseDouble(pfields[4]);
			p2.y_vel = Double.parseDouble(pfields[5]);
			p2.angle = Double.parseDouble(pfields[6]);
			p2.gx_vel = Double.parseDouble(pfields[7]);
			p2.gy_vel = Double.parseDouble(pfields[8]);
			p2.gangle = Double.parseDouble(pfields[9]);
		}
		if (line.startsWith("asteroids")) {
			numAst++;
			Asteroid a = new Asteroid(0, 0, 0, level);
			String[] afields = fields[1].split(",");
			if (afields[0].equals("1")) {
				a.active = true; }
			else { a.active = false; }
			a.pos.x = Integer.parseInt(afields[1]);
			a.pos.y = Integer.parseInt(afields[2]);
			a.x_vel = Double.parseDouble(afields[3]);
			a.y_vel = Double.parseDouble(afields[4]);
			a.angle = Double.parseDouble(afields[5]);
			a.minVel = Double.parseDouble(afields[6]);
			a.maxVel = Double.parseDouble(afields[7]);
			a.radius = Integer.parseInt(afields[8]);
			
			allAsteroids.add(a);
		}
	}
	public void displayHighScores(Graphics g) {
		//int h = 50;
		
		System.out.println("===============");
		System.out.println("  HIGH SCORES  ");
		System.out.println("===============");
		for (int i = 0; i < 10; i++) {
			System.out.println(highscores.get(i).printScore());
			//g.drawString(highscores.get(i).toString(), 65, 50+h);
			//h += 10;
		}
	}
	
	
	
	
	//Key Press detection
	
	public void keyPressed(KeyEvent e)
	{ 
		if(e.getKeyCode()==KeyEvent.VK_UP) 
		{
			//p1.moveShip(UP);
			dir = UP;
			p1.accelerate = true;
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			//p1.moveShip(LEFT);
			dir = LEFT;
			p1.turnLeft = true;
			p1.turnRight = false;
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT) 
		{
			//p1.moveShip(RIGHT);
			dir = RIGHT;
			p1.turnRight = true;
			p1.turnLeft = false;
		}
		//player 2 movement
		else if(e.getKeyCode()==KeyEvent.VK_W) 
		{
			dir = UP;
			p2.accelerate = true;
		}
		else if(e.getKeyCode()==KeyEvent.VK_A)
		{
			dir = LEFT;
			p2.turnLeft = true;
			p2.turnRight = false;
		}
		else if(e.getKeyCode()==KeyEvent.VK_D) 
		{
			dir = RIGHT;
			p2.turnRight = true;
			p2.turnLeft = false;
		}
		//p1 shoot
		else if(e.getKeyCode()==KeyEvent.VK_CONTROL) {
			p1.shotsFired=true; //Start firing when ctrl is pushed
		}
		//p2 shoot
		else if(e.getKeyCode()==KeyEvent.VK_B) {
			p2.shotsFired=true; //Start firing when ctrl is pushed
		}
				
		//pause
		else if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			pause_cnt++;
			if(pause_cnt%2 == 1) // just to toggle pauses on the escape			
			{
				paused = true;
				showMenu();
			}
			else
			{
				paused = false;
				hideMenu();
			}
		}
		//unlimited lives
		else if(e.getKeyCode()==KeyEvent.VK_U){ // setting unlimited lives
			unlimCnt++;
			if(unlimCnt%2 == 1)
			{	
				unlimLives = true;
				System.out.println("Unlim Lives");
			}
			else
			{	unlimLives = false;
			
			}
		}
		//g object visible & active
		else if(e.getKeyCode()==KeyEvent.VK_G){ // setting unlimited lives
			gCnt++;
			if(gCnt%2 == 1)
			{	
				p1.gravObj = true;
				p2.gravObj = true;
				gravVisible = true;
				System.out.println("GravOBJ");
			}
			else
			{	
				p2.gravObj = true;
				p1.gravObj = false;
				gravVisible = false;
			}
		}
		//player two active
		else if(e.getKeyCode()==KeyEvent.VK_T){ // setting unlimited lives
			twoCnt++;
			if(twoCnt%2 == 1)
			{	
				p2_active = true;
				p2.active = true;
				System.out.println("Player 2 active!");
			}
			else
			{	
				p2_active = false;
				p2.active = false;
			}
		}
				
	} 
	public void keyReleased(KeyEvent e)
	{ 
		if(e.getKeyCode()==KeyEvent.VK_UP) 
		{
			p1.accelerate = false;		
		} 
		else if(e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			p1.turnLeft = false;
			p1.turnRight = false;
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			p1.turnRight = false;
			p1.turnLeft = false;
		}
		else if(e.getKeyCode()==KeyEvent.VK_W) 
		{
			p2.accelerate = false;		
		} 
		else if(e.getKeyCode()==KeyEvent.VK_A)
		{
			p2.turnLeft = false;
			p2.turnRight = false;
		}
		else if(e.getKeyCode()==KeyEvent.VK_D)
		{
			p2.turnRight = false;
			p2.turnLeft = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			p1.shotsFired = false;
		}
		//p1 shoot
		else if(e.getKeyCode()==KeyEvent.VK_B) {
			p2.shotsFired=false; //Start firing when ctrl is pushed
		}
				
	} 
	public void keyTyped(KeyEvent e)
	{
		//keep empty. needed for fulfilling interface implementation requirements
	}	
	
}
