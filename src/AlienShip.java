import java.awt.Color;
import java.awt.Graphics;


public class AlienShip extends GameObject {

	double init_vel = 3;
	
	boolean accelerate = false;
	boolean turnLeft = false;
	boolean turnRight = false;
	boolean shotsFired;
	
	int [] xCords;
	int [] yCords;
	int [] origXCords = {10,20,10,0};
	int [] origYCords = {0,10,20,10};
	double angle = 0;
	double p1_angle;
	double p2_angle;
	
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
	
	int level;
	public AlienShip(int x, int y, int level)
	{
		this.isHit = false;
		this.shotsFired = false;
		this.pos.x = x; // initial positions of the ship
		this.pos.y = y;
		this.x_vel = init_vel; // initial velocities
		this.y_vel = init_vel;
		this.radius = 12;
		this.angle = 0;
		this.xCords = new int[4];
		this.yCords = new int[4];
		this.fireCnt = 0; //allows 4 bullets to be shot before a .2 pause]
		this.firePause = false;
		this.active = false;
		this.updatePos();
		this.gravObj = false;
		this.level = level;
	}
	
	public void drawShip(Graphics g) //drawing
	{
		if(this.active)
		{
			g.setColor(Color.blue);		
			g.fillPolygon(xCords,yCords,4);
		}
	}
	
	public void moveShip(double p1x, double p1y, double p2x, double p2y, boolean p2_active) // moves ship
	{
		if(this.active)
		{			

			
			int closer = checkDistance( p1x, p1y, p2x, p2y, p2_active); // figure out which one is closer
			
			if(closer == 1) // calculating angle based on that and moving spaceship
			{
				angle = getAngle(p1x, p1y);			
			}
			else
			{
				angle = getAngle(p2x, p2y);			
			}
			
			this.pos.x += (0.5*level)*this.x_vel;
			this.pos.y += (0.5*level)*this.y_vel;			
			
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
		if (fireCnt == 50*(0.5*level)) { //give a .2 delay
			fireCnt = 0;
		}
		if (fireCnt >= 4) {
			firePause = true;
			//fireCnt = 0;
			return null;
		}
		return new Bullet(this.pos.x, this.pos.y, this.angle);
	}
	
	public double getAngle(double posx, double posy){
		double angle = (double) Math.toDegrees(Math.atan2(posx-this.pos.x , posy-this.pos.y));
		
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
	
	public int checkDistance(double posx1, double posy1 , double posx2, double posy2, boolean p2_active) // to calculate distance from ships
	{
		//1's distance from the alien space ship
		double xdiff, ydiff;
		xdiff = Math.pow((this.pos.x-posx1),2);
		ydiff = Math.pow((this.pos.y-posy1),2);
		double distance1 = Math.sqrt(xdiff+ydiff);

		//1's distance from the alien space ship
		xdiff = Math.pow((this.pos.x-posx2),2);
		ydiff = Math.pow((this.pos.y-posy2),2);
		double distance2 = Math.sqrt(xdiff+ydiff);
				
		if(p2_active)
		{
			if(distance1 > distance2)
			{
				return 2; // 2 closer
			}
			else return 1; // 1 closer
		}
		return 1; // 1 closer
	}
}
