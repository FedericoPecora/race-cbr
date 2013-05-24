package test;

import time.Bounds;

public class TestingBounds {
//tets
	public static void main(String[] args) {
		Bounds b1 = new Bounds(2,3);
		Bounds b2 = new Bounds(0,0);
		System.out.println(b1.intersect(b2));
	}
}
