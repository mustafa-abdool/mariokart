import java.awt.geom.*;
class Vect2D{
    double mag,ang;
    
    Vect2D(double x,double y)
    {
	mag = Math.sqrt(x*x + y*y);
	if(mag > 0)
	    if(x>=0)
		ang = Math.asin(y/mag);
	    else
		ang = Math.PI - Math.asin(y/mag);
	else
	    ang=0;
    }

    public Point2D.Double toPoint()
    {
	Point2D.Double ans = new Point2D.Double();
	
	ans.x = (Math.cos(ang))*mag;
	ans.y = (Math.sin(ang))*mag;
	return ans;
    }
    
    public void rotate(double d)
    {
	ang += d;
    }
}
