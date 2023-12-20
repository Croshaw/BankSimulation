package me.vlad.banksimulation.visual;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.vlad.banksimulation.core.Bank;
import me.vlad.banksimulation.util.DurationUtils;
import me.vlad.banksimulation.util.Pair;
import me.vlad.banksimulation.visual.base.Drawer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

public class BankDrawer extends Drawer<Bank, RequestDrawer> {
    private InfoBoardDrawer infoBoardDrawer;
    private ArrayList<ClerkDrawer> clerksDrawers;
    private LinkedList<Pair<Double, Double>> clerkCords;
    private LinkedList<Pair<Double, Double>> requestCords;
    private final double sx;
    private final double sy;
    public BankDrawer(Bank source, double startX, double startY, double width, double height) {
        super(source, startX, startY, width, height, Color.LIGHTGREEN);
        sx = startX;
        sy = startY;
        clerksDrawers = new ArrayList<>();
        clerkCords = new LinkedList<>();
        requestCords = new LinkedList<>();
        int maxClerkCount = source.getMaxClerksCount();

        double size = 50;
        double xOffset = 10;
        double yOffset = 5;
        double x = xOffset;
        double y = yOffset;

        for(int i = 0; i < maxClerkCount; i++) {
            if(x + size >= width) {
                x = xOffset;
                y += yOffset + size;
            }
            clerkCords.add(new Pair<>(startX + x, startY + y));
            x += size + xOffset;
        }
        infoBoardDrawer = new InfoBoardDrawer(source.getInfoBoard(), 0, 0);
        infoBoardDrawer.move(startX + x + 10, startY + y);
        int n = 0;
        for(var clerk : source.getClerks()) {
            var cD = new ClerkDrawer(clerk, 0, 0);
            cD.setSpeed(getSpeed());
            clerksDrawers.add(cD);
            var p = clerkCords.get(n++);
            cD.move(p.getFirst(), p.getSecond());
        }
        double ny = y + size + yOffset + 60;
        size = 20;
        xOffset = 10;
        yOffset = 5;
        x = xOffset;
        for(int i = 0; i < source.getQueueLimit(); i++) {
            if(x + size > width) {
                x = xOffset;
                ny += yOffset + size;
            }
            requestCords.add(new Pair<>(startX + x, startY + ny));
            x += size + xOffset;
        }
    }

    @Override
    public void setSpeed(double value) {
        super.setSpeed(value);
        for(var cl : clerksDrawers)
            cl.setSpeed(value);
        infoBoardDrawer.setSpeed(value);
    }

    private void syncWithSource() {
        var queue = source.getQueue();
        ArrayList<RequestDrawer> toRemove = new ArrayList<>();
        for(var drawer : childrens) {
            boolean contains = false;
            for(var request : queue) {
                if (drawer.source == request) {
                    contains = true;
                    break;
                }
            }
            if(!contains)
                toRemove.add(drawer);
        }
        toRemove.forEach(this::remove);
        for(var request : queue) {
            boolean contains = false;
            for(var drawer : childrens) {
                if(drawer.source == request) {
                    contains = true;
                    break;
                }
            }
            if(!contains)
                add(new RequestDrawer(request, 0, 0));
        }
    }

    @Override
    public void add(RequestDrawer drawer) {
        super.add(drawer);
        var pair = requestCords.get(childrens.size()-1);
        drawer.move(0, pair.getSecond());
        drawer.move(pair.getFirst(), pair.getSecond());
    }

    @Override
    public boolean mouseMovedEvent(MouseEvent event) {
        for(var cl : clerksDrawers) {
            if(cl.mouseMovedEvent(event)) {
                drawTooltip = false;
                return true;
            }
        }
        return super.mouseMovedEvent(event);
    }

    @Override
    public void draw(GraphicsContext g) {
        super.draw(g);
        String dur = "Длительность: %s".formatted(DurationUtils.toStringFormat("DD:HH:mm:ss",source.getCurrentDuration()));
        Drawer.drawTextOnBackground(g, (sx+rectangle.getWidth()) - Drawer.computeTextWidth(g.getFont(), dur) - 25, sy + rectangle.getHeight()- Drawer.computeTextHeight(g.getFont(), dur)-20, Color.BLACK, Color.LIGHTGREEN, dur);

        dur = "Дата: %s".formatted(source.getCurrentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        Drawer.drawTextOnBackground(g, (sx+rectangle.getWidth()) - Drawer.computeTextWidth(g.getFont(), dur) - 25, sy + rectangle.getHeight()- Drawer.computeTextHeight(g.getFont(), dur)-40, Color.BLACK, Color.LIGHTGREEN, dur);
        dur = "Длина очереди: %d".formatted(source.getQueue().size());
        Drawer.drawTextOnBackground(g, (sx+rectangle.getWidth()) - Drawer.computeTextWidth(g.getFont(), dur) - 25, sy + rectangle.getHeight()- Drawer.computeTextHeight(g.getFont(), dur)-60, Color.BLACK, Color.LIGHTGREEN, dur);
    }

    @Override
    protected void drawChildren(GraphicsContext g) {
        syncWithSource();
        super.drawChildren(g);
        for(var cl : clerksDrawers) {
            cl.draw(g);
        }
        infoBoardDrawer.draw(g);
    }

    @Override
    public void drawTooltip(GraphicsContext g) {
        for(var cl : clerksDrawers)
            cl.drawTooltip(g);
        super.drawTooltip(g);
    }

    @Override
    public void remove(RequestDrawer drawer) {
        for(var cl : clerksDrawers) {
            if(cl.source.getCurrentTask() == drawer.source) {
                cl.add(drawer);
                var rec = cl.getRectangle();
                drawer.move(rec.getX() + (rec.getWidth() - drawer.getRectangle().getWidth())/2, rec.getY() + rec.getHeight() - drawer.getRectangle().getHeight());
                break;
            }
        }
        super.remove(drawer);
        for(int i = 0; i < childrens.size();i++) {
            var p = requestCords.get(i);
            childrens.get(i).move(p.getFirst(), p.getSecond());
        }
    }

    @Override
    public boolean isFixit() {
        boolean res = true;
        for(var cl : clerksDrawers)
            res = res && cl.isFixit();
        return super.isFixit() && res && infoBoardDrawer.isFixit();
    }
}
