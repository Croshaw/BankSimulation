package me.vlad.banksimulation.visual;

import javafx.scene.paint.Color;
import me.vlad.banksimulation.core.Request;
import me.vlad.banksimulation.visual.base.Drawer;

public class RequestDrawer extends Drawer<Request, Drawer> {
    public RequestDrawer(Request source, double startX, double startY) {
        super(source, startX, startY, 20, 20, Color.WHITE);
    }
}
