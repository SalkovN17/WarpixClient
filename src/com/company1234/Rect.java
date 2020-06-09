package com.company1234;

import java.awt.*;

public class Rect {
    private byte colorByte;
    private Rectangle rectangle;

    Rect(int x, int y, byte colorByte){
        rectangle = new Rectangle(x,y,30,30);
        this.colorByte = colorByte;
    }

    public byte getColorByte() {
        return colorByte;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

}
