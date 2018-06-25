package ar.com.manusofar;

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
	
	int nFactor = 25; //Cuantas líneas horizontales por columna
	
	int screenWidth = width * nFactor;
	int screenHeight = height * nFactor;
	
	String mapa = "";
	
	BufferedImage level;
	
	public Engine() {
		   mapa = "################";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
		   mapa	+="#              #";
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
		   
		   //fPlayerA = 10.0f;
		   level = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		   
		   
		   
		   
	}
	
	public float getPlayerAngle() {
		return fPlayerA;
	}
	
	public void setPlayerAngle(float angle) {
		fPlayerA = angle;
	}
	
		
	@Override
	public void paintComponent(Graphics g) {
		   this.update();
		   if (level != null) {
			   Graphics2D g2d = (Graphics2D)g;
			   g2d.drawImage(level, 0, 0, null);
		   }		  
	}

	private void update() {
		 boolean bHitWall = false;	
		 
		   int screenWidth, screenHeight;
		   screenWidth = width * nFactor;
		   screenHeight = height * nFactor;

		   Graphics2D big2d = (Graphics2D)level.getGraphics();
		   big2d.setStroke(new BasicStroke(1.0f));
//		   if (fPlayerA != fOldAngle) {
//			   fOldAngle = fPlayerA;
			   float fDistanceToWall = 1.0f;
			   int separacion = 0;
			   for (int x=0; x<screenWidth; x++) {
				   //fDistanceToWall = 0.1f;
				   float fRayAngle = (fPlayerA - fFOV / 2.0f) + ((float)x/(float)screenWidth) * fFOV;
				   float fStepSize = 0.1f; 
				   //float fDistanceToWall = 0.0f;
				   
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
				   //fDistanceToWall = Math.max(fDistanceToWall, 0.01f);
				   System.out.println("fRayAngle: " + fRayAngle);
				   System.out.println("fDistanceToWall: " + fDistanceToWall);
				   int nCeiling = (int)(screenHeight / 2.0f - screenHeight/fDistanceToWall);
				   //nCeiling = (nCeiling < 0) ? 1 : nCeiling; 
				   //fDistanceToWall = 0.0f;
				   //System.out.println("nCeiling: " + nCeiling);
				   int nFloor = screenHeight - nCeiling;
				   
				   //for (int n=0; n<screenWidth; n++) {
					   Line2D ceiling = new Line2D.Float(x,0,x,nCeiling); 
					   Line2D wall = new Line2D.Float(x,nCeiling,x,nFloor);
					   Line2D floor = new Line2D.Float(x,nFloor,x,screenHeight);
					   //g2d.setColor(Color.BLUE);
					   //g2d.draw(new Line2D.Float(x,0,x,nCeiling));
					   //g2d.draw(ceiling);
					   //Color.HSBtoRGB(0, 0, 100-fDistanceToWall);
					   
					   Color cWallColor;
					   
					   if (fDistanceToWall < fDepth / 4.0f) {
						   cWallColor = Color.getHSBColor(0, 0, 100);
					   } else {
					   if (fDistanceToWall < fDepth / 3.0f) {
						   cWallColor = Color.getHSBColor(0, 0, 75);
					   } else {
					   if (fDistanceToWall < fDepth / 2.0f) {
						   cWallColor = Color.getHSBColor(0, 0, 50);
					   } else {
						   cWallColor = Color.getHSBColor(0, 0, 0);
					   }
					   
					   
					   big2d.setColor(cWallColor);
					   //g2d.draw(new Line2D.Float(x,nCeiling,x,nFloor));
					   big2d.draw(wall);
					   big2d.setColor(Color.white);
					   //g2d.draw(new Line2D.Float(x,nFloor,x,screenHeight));
					   big2d.draw(floor);
					   //g2d.setBackground(Color.white);
				   //}
				   
				   separacion+=nFactor;
				   
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
					   }
			   //g2d.drawImage(bi,0,0,screenHeight,screenWidth,null);
		   }
	}
	
	
	@Override
	public void update(Graphics g) {
		paintComponent(g);
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getKeyCode());
		if (e.getKeyCode()==65) { //Si se presionó la A entonces giro en sentido horario
			this.setPlayerAngle(this.getPlayerAngle()+0.05f);
		}
		if (e.getKeyCode()==68) { //Si se presionó la A entonces giro en sentido horario
			this.setPlayerAngle(this.getPlayerAngle()-0.05f);
		}
		repaint();
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}
