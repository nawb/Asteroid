import java.awt.*;
import java.lang.*;
import java.math.*;
public class Asteroid extends GameObject{

	int hitNum;
	int splitNum;
	
	double angle;
	double minVel = 1;
	double maxVel = 5; 
	
	public Asteroid(int x, int y, int radius, int level)
	{
		this.pos.x = x;
		this.pos.y = y;
		this.hitNum = 0;
		this.splitNum = 3;
		this.minVel += (0.5*level);
		this.maxVel += (0.5*level);
		this.angle = 2*Math.random()*Math.PI; // random angle
		this.x_vel = (Math.random()*this.minVel+(this.maxVel-this.minVel))*Math.cos(angle);
		this.y_vel = (Math.random()*this.minVel+(this.maxVel-this.minVel))*Math.sin(angle);
		this.radius = radius; // experimental value
		this.active = true;
	}
	
	public void drawAsteroid(Graphics g)
	{
		g.setColor(Color.white);
		g.fillOval((int)(this.pos.x/*-this.radius*/), (int)(this.pos.y/*-this.radius*/),(int)2*this.radius, (int)2*this.radius);
	}
	
	public void moveAsteroid()
	{
		this.pos.x += this.x_vel;
		this.pos.y += this.y_vel;
		
		//wrapping around the asteroid
		if(this.pos.x < (-this.radius))
		{
			this.pos.x = 500 + this.radius;
			changeVelRandom();
		}
		else if(this.pos.x > 500 + this.radius)
		{
			this.pos.x = (-this.radius);
			changeVelRandom();
		}
		
		if(this.pos.y < (-this.radius))
		{
			this.pos.y = 500 + this.radius;
			changeVelRandom();
		}
		else if(this.pos.y > 500 + this.radius)
		{
			this.pos.y = (-this.radius);
			changeVelRandom();
		}		
		
		
		
	}
	
	public void changeVelRandom()
	{
		this.angle = 2*Math.random()*Math.PI; // random angle
		this.x_vel = (Math.random()*this.minVel+(this.maxVel-this.minVel))*Math.cos(angle);
		this.y_vel = (Math.random()*this.minVel+(this.maxVel-this.minVel))*Math.sin(angle);		
	}
}
