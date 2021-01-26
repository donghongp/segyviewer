package com.ghc.app.general;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import javax.swing.JPanel;

/**
 * Color bar.
 * Plots color bar as a JPanel.
 */
public class ColorBarPanel extends JPanel {
  public static final int ORIENT_HORIZONTAL   = 1;
  public static final int ORIENT_VERTICAL     = 2;
  public static final int ANNOTATION_SIMPLE   = 3;
  public static final int ANNOTATION_ADVANCED = 4;

  public static final int NUM_COLOR_RESOLUTION = 1001;
  
	private ColorMap _map;
  private int _orientation;
  private int _annotation;
  private int _fontSize = 12;

	public ColorBarPanel(ColorMap map) {
		this(map, ColorBarPanel.ORIENT_VERTICAL);
  }

	public ColorBarPanel(ColorMap map, int orientation) {
		this(map, ColorBarPanel.ORIENT_VERTICAL, ANNOTATION_SIMPLE);
  }

	public ColorBarPanel(ColorMap map, int orientation, int annotation) {
    super( new BorderLayout() );
		_map = new ColorMap(map);
    _map.setNumColorResolution( NUM_COLOR_RESOLUTION );
    _orientation = orientation;
    _annotation  = annotation;
    setPreferredSize( new Dimension(50,0) );
    setMinimumSize(new Dimension(0,0));
  }
  public void setFontSize( int fontSize ) {
    _fontSize = fontSize;
  }
  public void setMinMax( double minValue, double maxValue ) {
    _map.setMinMax( minValue, maxValue );
  }

	public void setColorMap(ColorMap map) {
    if( map.getMinValue() == _map.getMinValue() && map.getMaxValue() == _map.getMaxValue() ) {
      Color[] colors1 = _map.getColorKneePoints();
      Color[] colors2 = map.getColorKneePoints();
      if( colors1.length == colors2.length ) {
        boolean isEqual = true;
        for( int i = 0; i < colors1.length; i++ ) {
          if( colors1[i] != colors2[i] ) {
            isEqual = false;
            break;
          }
        }
        if( isEqual ) return;
      }
    }
		_map = new ColorMap(map);
    _map.setNumColorResolution( NUM_COLOR_RESOLUTION );
    revalidate();
    repaint();
  }
  public void paintComponent( Graphics g1 ) {
    super.paintComponent(g1);
    double minValue = _map.getMinValue();
    double maxValue = _map.getMaxValue();

    Graphics2D g = (Graphics2D)g1;
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    g.setStroke( new BasicStroke(1.0f) );
    g.setFont(new Font("SansSerif", Font.PLAIN, _fontSize));
    g.setColor(Color.white);

    int dist = 10;
    int height = this.getHeight();
    int width = this.getWidth();
    g.fillRect(0, 0, width, height);

    // Set annotation steps etc
      double valueStep = maxValue-minValue;
      String format = "";
		if (_map.getSmoothMode() == ColorMap.SMOOTH_MODE_DISCRETE) {
        valueStep = (double)_map.getDiscreteStep();
        format = "0";
      }
      else if( valueStep > 100 ) {
        valueStep = 10;
        format = "0";
      }
      else if( valueStep > 10 ) {
        valueStep = 0.5;
        format = "0.0";
      }
      else if( valueStep > 1 ) {
        valueStep = 0.05;
        format = "0.00";
      }
      else if( valueStep > 0.1 ) {
        valueStep = 0.005;
        format = "0.000";
      }
      else if( valueStep > 0.01 ) {
        valueStep = 0.0005;
        format = "0.0000";
      }
      else if( valueStep > 0.001 ) {
        format = "0.00000";
        valueStep = 0.00005;
      }
      else {
        format = "";
        valueStep /= 10.0;
      }
      DecimalFormat _formatter = new DecimalFormat(format);

    if( _annotation == ANNOTATION_ADVANCED ) {
      // Draw black box
      g.setColor( Color.black );
      g.drawRect( dist-1, dist-1, width/3+2, height-(2*dist)+2);
  
      int minY = dist;
      int maxY = height-dist;
      int diff = maxY - minY;
      if( diff > 0 ) {
        for( int yPos = minY; yPos <= maxY; yPos++ ) {
          float s = (float)(yPos - minY) / (float)diff;
          float value = (float)(maxValue + s*( minValue - maxValue ));
          g.setColor( _map.getColor( value ) );
          g.drawLine( dist, yPos, dist+width/3, yPos);
        }
      }
      int labelInc = 120;
      int maxSteps = (int)( (float)(maxY-minY)/(float)(labelInc) );
      int nn = (int)((maxValue-minValue)/((float)maxSteps*valueStep));
      if( nn <= 0 ) nn = 1;
      valueStep = nn*valueStep;
      double yStep = (double)(maxY-minY)/(maxValue-minValue)*valueStep-0.3;
      int nSteps = (int)((maxValue-minValue)/valueStep + 0.5) + 1;
      if( nSteps < 0 ) nSteps = 0;

      g.setColor(Color.black);
      int fontHeight = g.getFontMetrics().getHeight();
      for( int istep = 0; istep < nSteps; istep++ ) {
        int yPos = (int)(-istep*yStep) + maxY+1;
        double value = minValue + istep*valueStep;
        if( istep == nSteps-1 ) {
          yPos = minY-1;
          value = maxValue;
        }
        String text = _formatter.format( value );
        g.drawLine( dist+width/3+2, yPos, (dist+width/3+2+5), yPos);
        if( istep == 0 ) yPos -= fontHeight/3;
        g.drawString( text, 2*dist+width/3, yPos+(int)(fontHeight/2.5) );
      }
    }
    else {  // Simple annotation
      // Draw black box
      g.setColor( Color.black );
      g.drawRect( dist-1, 2*dist-1, width-2*(dist-1), height-4*dist+2);
  
      int minY = 2*dist;
      int maxY = height-2*dist;
      int diff = maxY - minY;
      if( diff > 0 ) {
        for( int yPos = minY; yPos <= maxY; yPos++ ) {
          float s = (float)(yPos - minY) / (float)diff;
          float value = (float)(maxValue + s*( minValue - maxValue ));
          g.setColor( _map.getColor( value ) );
          g.drawLine( dist, yPos, width-dist, yPos);
        }
      }
      
      g.setColor(Color.black);
      int fontHeight = g.getFontMetrics().getHeight();

      String textMin = _formatter.format( minValue );
      String textMax = _formatter.format( maxValue );

      int yPos = dist;
      g.drawString( textMax, dist, yPos+(int)(fontHeight/2.5) );
      yPos = height - dist;
      g.drawString( textMin, dist, yPos+(int)(fontHeight/2.5) );
    }
  }
}
