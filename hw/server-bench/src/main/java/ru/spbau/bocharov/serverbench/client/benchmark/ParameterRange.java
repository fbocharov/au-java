package ru.spbau.bocharov.serverbench.client.benchmark;

public class ParameterRange {

    public final int from;
    public final int to;
    public final int step;

    public ParameterRange(int f, int t, int s) {
        from = f;
        to = t;
        step = s;
    }
}
