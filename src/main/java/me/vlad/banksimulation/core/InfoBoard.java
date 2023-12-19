package me.vlad.banksimulation.core;

import me.vlad.banksimulation.core.human.Clerk;

public class InfoBoard {
    private Request request;
    private Clerk clerk;
    public void work(Clerk clerk, Request request) {
        this.clerk = clerk;
        this.request = request;
        clerk.takeRequest(request);
    }
    public Request getRequest() {
        return request;
    }
    public Clerk getClerk() {
        return clerk;
    }
}
