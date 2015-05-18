import java.awt.*;

import java.lang.*;
import java.math.*;
public class Ship extends GameObject {

	boolean accelerate = false;
	boolean turnLeft = false;
	boolean turnRight = false;
	boolean shotsFired;
	
	int [] xCords;
	int [] yCords;
	int [] origXCords = {16,-8,-4,-8};//{10,0,10,20};
	int [] origYCords = {2,-6,2,10};//{0,30,20,30};
	double angle = 0;
	
	int fireCnt;
	boolean firePause;
	
	// g object stuff
	boolean gravObj;
	double gx_vel;
	double gy_vel;
	double gangle;
	int pull = 1;
	
	int numLives = 3;
	boolean isHit;
	int hitWait = 0 ;
	public Ship(int x, int y)
	{
		this.isHit = false;
		this.shotsFired = false;
		this.pos.x = x; // initial positions of the ship
		this.pos.y = y;
		this.x_vel = 0; // initial velocities
		this.y_vel = 0;
		this.radius = 12;
		this.angle = 0;
		this.xCords = new int[4];
		this.yCords = new int[4];
		this.fireCnt = 0; //allows 4 bullets to be shot before a .2 pause]
		this.firePause = false;
		this.active = true;
		this.updatePos();
		this.gravObj = false;
	}
	
	public void drawShip(Graphics g) //drawing
	{
		if(this.active)
		{
			g.setColor(Color.red);		
			g.fillPolygon(xCords,yCords,4);
		}
	}
	
	public void moveShip(int keyPress) // moves ship
	{
		if(this.active)
		{
			//updating velocity on acceleration
			if(this.accelerate)
			{
				//System.out.println("accelerating");
				this.x_vel += 0.3*Math.cos(angle);
				this.y_vel += 0.3*Math.sin(angle);
			}
			else {
				//System.out.println("not acccelerating");
				//velocity dampening
				this.x_vel = this.x_vel*0.80;		
				this.y_vel = this.y_vel*0.80;
			}
			
			//updating position wrt to velocity
			this.pos.x += this.x_vel;
			this.pos.y += this.y_vel;
			
			//if G Object present
			
			if(this.gravObj)
			{
				this.gangle = getAngle();
				this.gx_vel = pull*Math.cos(gangle);
				this.gy_vel = pull*Math.sin(gangle);
				if(this.pos.x > 230)
				{
					this.pos.x -= this.gx_vel;
				}
				else if(this.pos.x < 230)
				{
					this.pos.x += this.gx_vel;
				}
				
				if(this.pos.y > 230)
				{
					this.pos.y -= this.gy_vel;
				}
				else if(this.pos.y < 230)
				{
					this.pos.y += this.gy_vel;
				}	
			}
			
			//so that the ship remains hovers over from the other side
			if(this.pos.x<0)
			{
				this.pos.x += 500;
			}
			else if(this.pos.x > 500)
			{
				this.pos.x -= 500;
			}
			if(this.pos.y < 0)
			{
				this.pos.y += 500;
			}
			else if(this.pos.y > 500)
			{
				this.pos.y -= 500;
			}
			
			if(this.turnLeft) //LEFT
			{
				angle = angle-Math.PI/16;
			}
			else if(this.turnRight) //RIGHT
			{
				angle = angle+Math.PI/16;
			}
			
			if(angle>(2*Math.PI)) // check if angle is within bounds (wrap around)
			{
				angle = 0;
			}
			else if(angle<0)
			{	
				angle = 2*Math.PI;
			}
			
			this.updatePos();
		}
	}
	
	public void updatePos() // updates the draw coordinates
	{		
		for(int i = 0; i < 4; i++)
		{
			this.xCords[i] = (int)(this.origXCords[i]*Math.cos(angle)-this.origYCords[i]*Math.sin(angle)+this.pos.x+.5);
			this.yCords[i] = (int)(this.origXCords[i]*Math.sin(angle)+this.origYCords[i]*Math.cos(angle)+this.pos.y+.5);
		}
		//System.out.println("("+this.x_vel+","+this.y_vel+") <-speed");
	}
	
	public Bullet fireBullet() {
		fireCnt++;
		if (fireCnt == 10) { //give a .2 delay
			fireCnt = 0;
		}
		if (fireCnt >= 4) {
			firePause = true;
			//fireCnt = 0;
			return null;
		}
		return new Bullet(this.xCords[0], this.yCords[0], this.angle);
	}
	
	public double getAngle(){
		double angle = (double) Math.toDegrees(Math.atan2(230-this.pos.x , 230-this.pos.y));
		
		if(angle>(2*Math.PI)) // check if angle is within bounds (wrap around)
		{
			angle = 0;
		}
		else if(angle<0)
		{	
			angle = 2*Math.PI;
		}
		return angle;
	}
}
