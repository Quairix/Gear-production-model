package com.quarix;

import java.util.Random;

public class Iteration {
    private float c[]={0.149273f, 0.447172f, 0.447255f, 0.14887f};
    private float m=10f;
    private float q[];
    private Random rnd;
    public Iteration() {
        q = new float[c.length];
        rnd = new Random();
        for(int i=0;i<q.length;i++)
            q[i]=(float)rnd.nextGaussian();
    }

    public float process(){
        float res = m;
        for(int i=0; i<c.length;i++)
            res+=c[i]*q[i];
        for(int i=0; i<q.length-1;i++)
            q[i]=q[i+1];
        q[q.length-1]=(float)rnd.nextGaussian();

        return res;
    }
}
