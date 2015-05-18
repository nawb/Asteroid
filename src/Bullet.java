import java.awt.*;
import java.lang.*;
import java.util.*;

public class Bullet extends GameObject {

	final double fixedvelocity = 10; 
	int travelDist;
	
	public Bullet(int x, int y, double angle)
	{
		this.travelDist = 50; //almost ScreenWidth/FramePeriod = 500/25
		this.pos.x = x;
		this.pos.y = y;
		this.radius = 2;
		this.x_vel = fixedvelocity*Math.cos(angle);
		this.y_vel = fixedvelocity*Math.sin(angle);
		this.active = true;
	}
	
	public void drawBullet(Graphics g) //will draw a simple circle
	{
		if (this.travelDist > 0) {
			//if it goes out, don't draw it anymore
			g.setColor(Color.green);		
			g.fillOval(pos.x, pos.y, 2*this.radius,2*this.radius);
		}
	}
	public void move() {
		this.travelDist--;
		pos.x += x_vel;
		pos.y += y_vel;
		
		//wrap around the screen:
		if (pos.x < 0) { pos.x = 500; }
		if (pos.x > 500) { pos.x = 0; }
		if (pos.y < 0) { pos.y = 500; }
		if (pos.y > 500) { pos.y = 0; }
	}
}
