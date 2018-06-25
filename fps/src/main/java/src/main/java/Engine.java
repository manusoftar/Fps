package src.main.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class Engine extends JPanel implements KeyListener {
	
	float fPlayerX = 8.0f;
	float fPlayerY = 8.0f;
	float fPlayerA = 0.0f;
	float fOldAngle = 0.01f;
	
	int height = 16;
	int width = 16;
	
	float fFOV = (float) (Math.PI/4.0f);
	
	float fDepth = 16.0f;
	
	int nFactor = 20; //Cuantas líneas horizontales por columna
	
	String mapa = "";
	
	public Engine() {
		   mapa = "################";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#       ## #   #";
		   mapa	+="#        # #   #";
		   mapa	+="#        # #   #";
		   mapa	+="#        ###   #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#     ###      #";
		   mapa	+="#  ######      #";
		   mapa	+="#              #";
		   mapa +="################";
		   
		   this.setSize(width*nFactor, height*nFactor);
		   this.setVisible(true);
		   this.addKeyListener(this);
	}
	
	public float getPlayerAngle() {
		return fPlayerA;
	}
	
	public void setPlayerAngle(float angle) {
		fPlayerA = angle;
	}
	
		
	@Override
	public void paintComponent(Graphics g) {
		   boolean bHitWall = false;	
		   Graphics2D g2d = (Graphics2D)g;
		   super.paintComponent(g);
		   int screenWidth, screenHeight;
		   screenWidth = width * nFactor;
		   screenHeight = height * nFactor;
		   BufferedImage bi = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		   Graphics2D big2d = (Graphics2D)bi.createGraphics();
		   big2d.setStroke(new BasicStroke(6.0f));
		   if (fPlayerA != fOldAngle) {
			   fOldAngle = fPlayerA;
			   float fDistanceToWall = 0.0f;
			   for (int x=0; x<screenWidth; x++) {
				   float fRayAngle = (fPlayerA - fFOV / 2.0f) + ((float)x/(float)screenWidth) * fFOV;
				   float fStepSize = 0.1f; 
				   
				   
				   float fEyeX = (float)Math.sin(fRayAngle);
				   float fEyeY = (float)Math.cos(fRayAngle);
				   
				   while (!bHitWall && fDistanceToWall < fDepth) {
					   fDistanceToWall += fStepSize;
					   
					   int nTestX = (int)(fPlayerX + fEyeX * fDistanceToWall);
					   int nTestY = (int)(fPlayerY + fEyeY * fDistanceToWall);
					   
					   //System.out.println("nTestX: " + nTestX);
					   //System.out.println("nTestY: " + nTestY);
					   
					   if (nTestX < 0 || nTestX >= width || nTestY < 0 || nTestY > height) {
						   bHitWall = true;
						   fDistanceToWall = fDepth;
					   } else {
						   if (mapa.charAt(nTestX * width + nTestY) == '#') {
							   bHitWall = true;
						   }
					   }
				   }
				   //fDistanceToWall = (fDistanceToWall == 0.0f) ? fDepth : fDistanceToWall;
				   System.out.println("fDistanceToWall: " + fDistanceToWall);
				   int nCeiling = (int)(screenHeight / 2.0f - screenHeight/fDistanceToWall);
				   fDistanceToWall = 0.0f;
				   //System.out.println("nCeiling: " + nCeiling);
				   int nFloor = screenHeight - nCeiling;
				   
				   Line2D ceiling = new Line2D.Float(x,0,x,nCeiling);
				   Line2D wall = new Line2D.Float(x,nCeiling,x,nFloor);
				   Line2D floor = new Line2D.Float(x,nFloor,x,screenHeight);
				   big2d.setColor(Color.BLUE);
				   big2d.draw(ceiling);
				   big2d.setColor(Color.DARK_GRAY);
				   big2d.draw(wall);
				   big2d.setColor(Color.white);
				   big2d.draw(floor);
				   
				   /*for (int y=0; y<height; y++) {		   
				   }*/
				   
	//			   for (int x1=x*nFactor; x1<x*nFactor+nFactor; x1++) {
	//				   Line2D ceiling = new Line2D.Float(x1,0,x1,nCeiling*nFactor);
	//				   Line2D wall = new Line2D.Float(x1,nCeiling*nFactor,x1,nFloor*nFactor);
	//				   Line2D floor = new Line2D.Float(x1,nFloor*nFactor,x1,height*nFactor);
	//				   Line2D separador = new Line2D.Float(x*nFactor+nFactor,0,x*nFactor+nFactor,height*nFactor);
	//				   			   
	//				   big2d.setColor(Color.BLUE);
	//				   big2d.draw(ceiling);
	//				   big2d.setColor(Color.DARK_GRAY);
	//				   big2d.draw(wall);
	//				   big2d.setColor(Color.white);
	//				   big2d.draw(floor);
	//				   big2d.setColor(Color.pink);
	//				   big2d.draw(separador);
	//			   }
				   
			   }
			   
			   g2d.drawImage(bi,0,0,this);
		   }
	}

	@Override
	public void update(Graphics g) {
		repaint();
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getKeyCode());
		if (e.getKeyCode()==65) { //Si se presionó la A entonces giro en sentido horario
			this.setPlayerAngle(this.getPlayerAngle()+0.1f);
		}
		if (e.getKeyCode()==68) { //Si se presionó la A entonces giro en sentido horario
			this.setPlayerAngle(this.getPlayerAngle()-0.1f);
		}
		repaint();
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}
