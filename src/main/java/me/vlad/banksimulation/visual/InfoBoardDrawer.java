package me.vlad.banksimulation.visual;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import me.vlad.banksimulation.core.InfoBoard;
import me.vlad.banksimulation.core.human.People;
import me.vlad.banksimulation.visual.base.Drawer;

public class InfoBoardDrawer extends Drawer<InfoBoard, Drawer> {
    public InfoBoardDrawer(InfoBoard source, double startX, double startY) {
        super(source, startX, startY, 120, 50, Color.DARKCYAN);
    }

    @Override
    protected void drawRectangle(GraphicsContext g) {
        super.drawRectangle(g);
        if(source.getRequest() == null)
            return;
        Font font = g.getFont();
        g.setFont(Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 16));
        String dur = String.valueOf(source.getRequest().getNumber());
        double h = Drawer.computeTextHeight(g.getFont(), dur);
        double w = Drawer.computeTextWidth(g.getFont(), dur);
        g.setFill(Color.WHITE);
        g.fillText(dur, rectangle.getX() + (rectangle.getWidth() - w)/2, rectangle.getY() + h);
        g.setFont(font);
        dur = ((People)source.getClerk()).toShortString();
        h = Drawer.computeTextHeight(g.getFont(), dur);
        w = Drawer.computeTextWidth(g.getFont(), dur);
        g.fillText(dur, rectangle.getX() + (rectangle.getWidth() - w)/2, rectangle.getY()+ rectangle.getHeight() - h);
    }
}
