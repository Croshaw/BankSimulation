package me.vlad.banksimulation.visual;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.vlad.banksimulation.core.Request;
import me.vlad.banksimulation.core.human.Clerk;
import me.vlad.banksimulation.visual.base.Drawer;

public class ClerkDrawer extends Drawer<Clerk, RequestDrawer> {

    public ClerkDrawer(Clerk source, double startX, double startY) {
        super(source, startX, startY, 50, 50, Color.LIGHTCORAL);
    }

    @Override
    protected void drawChildren(GraphicsContext g) {
        while(childrens.size() > 1)
            childrens.removeFirst();
        if(source.getCurrentTask() == Request.NULL)
            childrens.clear();
        else if(childrens.isEmpty()) {
            RequestDrawer req = new RequestDrawer(source.getCurrentTask(), 0, 0);
            double x = rectangle.getX() + (rectangle.getWidth() - req.getRectangle().getWidth()) / 2;
            double y = rectangle.getY() + rectangle.getHeight() - req.getRectangle().getHeight();
            req.move(0, y);
            req.move(x, y);
            add(req);
        }
        super.drawChildren(g);
    }
}
