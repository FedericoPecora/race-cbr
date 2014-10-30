package test;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.metacsp.multi.spatial.rectangleAlgebra.Point;

public class TestViewPoint {
	
	public static void main(String[] args) {
		
		//inputs are a1 and a2 two point defining starts and end of Q1 code
		//Teta is the angle defining viewPintTrapazoid
		//d1 and d2 maximon and min distance to the panel 
		
		Point a1 = new Point(10, 10);
		Point a2 = new Point(16, 18);
//		Point a1 = new Point(10, 10);
//		Point a2 = new Point(16, 10);
//		Point a1 = new Point(10, 13);
//		Point a2 = new Point(10, 14);
		double d1 = 2;
		double d2 = 4;
		double teta = 1.04; //60 degree
		

		
		
		//radios of circle
		double r1 = (double)d1/Math.cos(teta);
		double r2 = (double)d2/Math.cos(teta);
		

		
		Vector<Point> rights = getTrapazoid(d1, d2, a1, a2, r1, r2);
		System.out.println(rights);
		
		Vector<Point> lefts = getTrapazoid(-d1, -d2, a1, a2, r1, r2);
		System.out.println(lefts);

		
	}
	


	private static Vector<Point> getTrapazoid(double d1, double d2, Point a1, Point a2,
			double r1, double r2) {
		Vector<Point> ret = new Vector<Point>();
		double A = 0;
		double B  = 0;
		double C = 0;
		//First define QR line equation in the form of Ax + By + c = 0
		if(a1.y == a2.y){			
			A = 0;
			B  = 1;
			C = -a1.y;			
		}
		else if(a1.x == a2.x){
			A = 1;
			B  = 0;
			C = -a1.x;
		}
		else{
			//slope
			double m = (double)(a1.y - a2.y)/(a1.x - a2.x);		
			//First define QR line equation in the form of Ax + By + c = 0
			A = 1;
			//B = -1/m
			B  = (double)-1/m;
			//C = (-mx1 + y1)/m
			C = (double)(-m*a1.x + a1.y)/m;	
		}
		
		//The distance from a point (m, n) to the line Ax + By + C = 0 is given by:
		//|Ax+By+c| = d* SQRT(A^2+B^2)
		
		Point p1 = getPointsinParalellLine(d1, A, B, C , (double)2.0);
		Point p2 = getPointsinParalellLine(d1, A, B, C , (double)3.0);
		ret.addAll(getCircleLineIntersectionPoint(p1, p2, a1, r1));
		ret.addAll(getCircleLineIntersectionPoint(p1, p2, a2, r1));

		Point pp1 = getPointsinParalellLine(d2, A, B, C , (double)2.0);
		Point pp2 = getPointsinParalellLine(d2, A, B, C , (double)3.0);
		ret.addAll(getCircleLineIntersectionPoint(pp1, pp2, a1, r2));
		ret.addAll(getCircleLineIntersectionPoint(pp1, pp2, a2, r2));

		
		return ret;
	}



	private static Point getPointsinParalellLine(double d1, double A, double B, double C , double y1){
		if(B == 0.0){
			Point ret = new Point(-C + d1, y1);		
			return ret;
		}else if(A == 0.0){
			Point ret = new Point(y1, -C + d1);		
			return ret;
			
		}else{
			double x1 = (double)(d1 * Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)) - (B * y1) - C) / A;		
			Point ret = new Point(x1, y1);		
			return ret;
		}
	}
	
	private static List<Point> getCircleLineIntersectionPoint(Point pointA,
            Point pointB, Point center, double radius) {
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = center.x - pointA.x;
        double caY = center.y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }



}
