package com.gangchu.gangchutrip.mission.pose.domain;

import static java.lang.Math.*;

public class Vec2 {

    public final double x;
    public final double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 o) { return new Vec2(this.x + o.x, this.y + o.y); }
    public Vec2 sub(Vec2 o) { return new Vec2(this.x - o.x, this.y - o.y); }
    public Vec2 mul(double s) { return new Vec2(this.x * s, this.y * s); }
    public double dot(Vec2 o) { return this.x * o.x + this.y * o.y; }
    public double norm() { return sqrt(x * x + y * y); }
    public Vec2 unit() { double n = norm(); return n == 0 ? new Vec2(0, 0) : new Vec2(x/n,  y/n); }
    public Vec2 rotate(double rad) {
        double c = cos(rad), s = sin(rad);
        return new Vec2(c*x - s*y, s*x + c*y);
    }
    public double angleTo(Vec2 o) {
        Vec2 a = this.unit(); Vec2 b = o.unit();
        double d = max(-1.0, min(1.0, a.dot(b)));
        return acos(d);
    }
}
