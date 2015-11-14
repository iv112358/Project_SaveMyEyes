package com.i112358.savemyeyes.Graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.i112358.savemyeyes.BrightnessPoint;
import com.i112358.savemyeyes.Utilites;

// Left {16/4 16/125}
// Right { 16/4 304/30 }
// for time total 288 / per Hour 12 / per Minute 0.2f
// for percent total 121 / per Percent 1.21f
public class Line {

    private final float FACTOR_X = 0.2f;
    private final float FACTOR_Y = 1.21f;
    private final float[] START_POINT = { 16.0f, 4.0f };

    private Paint m_paint = new Paint();
    private int m_color = 0xFF33B5E5;
    private int m_width = (int)Utilites.convertDpToPixel(2);
    private float[] m_position = {0,0,0,0};

    public Line( final BrightnessPoint previous, final BrightnessPoint current )
    {
        setColor(m_color);
        setLineWidth(m_width);
        setPosition(Utilites.convertDpToPixel(START_POINT[0] + previous.getAbsoluteValue() * FACTOR_X),
                Utilites.convertDpToPixel(START_POINT[1] + previous.getBrightnessRelative() * FACTOR_Y),
                Utilites.convertDpToPixel(START_POINT[0] + current.getAbsoluteValue() * FACTOR_X),
                Utilites.convertDpToPixel(START_POINT[1] + previous.getBrightnessRelative() * FACTOR_Y));
    }

    public Line( final float time )
    {
        setColor(m_color);
        setLineWidth(m_width);
        setPosition(Utilites.convertDpToPixel(START_POINT[0] + time * FACTOR_X),
                Utilites.convertDpToPixel(START_POINT[1]),
                Utilites.convertDpToPixel(START_POINT[0] + time * FACTOR_X),
                Utilites.convertDpToPixel(125));
    }

    public void setPosition( final float startX, final float startY, final float endX, final float endY )
    {
        m_position[0] = startX;
        m_position[1] = startY;
        m_position[2] = endX;
        m_position[3] = endY;
    }

    public void setColor( final int r, final int g, final int b )
    {
        m_paint.setColor(Color.rgb(r, g, b));
    }

    public void setColor( final int hexColor )
    {
        m_color = hexColor;
        m_paint.setColor(m_color);
    }

    public void setLineWidth( final int width )
    {
        m_width = (int)Utilites.convertDpToPixel(width);
        m_paint.setStrokeWidth(m_width);
    }

    public void draw( final Canvas canvas )
    {
        canvas.drawLine(m_position[0], canvas.getHeight() - m_position[1], m_position[2], canvas.getHeight() - m_position[3], m_paint);
    }
}
