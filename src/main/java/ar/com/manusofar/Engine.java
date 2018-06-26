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

public class Engine extends JPanel implements KeyListener, Runnable {

	float fPlayerX = 8.0f;
	float fPlayerY = 8.0f;
	float fPlayerA = 0.0f;
	float fOldAngle = 0.01f;

	int height = 16;
	int width = 16;

	float fFOV = (float) (Math.PI / 4.0f);

	float fDepth = 16.0f;

	int nFactor = 30; // Cuantas líneas horizontales por columna

	int screenWidth = width * nFactor;
	int screenHeight = height * nFactor;

	String mapa = "";

	BufferedImage level;
	
	float fElapsedTime; 
	long lTime1, lTime2;
	
	float techos[] = new float[screenWidth];
	

	Thread render;

	public Engine() {
		mapa =  "################";
		mapa += "#              #";
		mapa += "#              #";
		mapa += "#              #";
		mapa += "#        #     #";
		mapa += "#       #      #";
		mapa += "#      #       #";
		mapa += "#              #";
		mapa += "#         #    #";
		mapa += "#    #     #   #";
		mapa += "#           #  #";
		mapa += "#              #";
		mapa += "#     ###      #";
		mapa += "#  ######      #";
		mapa += "#              #";
		mapa += "################";

		this.setSize(width * nFactor, height * nFactor);
		this.setVisible(true);
		this.addKeyListener(this);

		// fPlayerA = 10.0f;
		level = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		render = new Thread(this);
		
		lTime1 = System.nanoTime();
		lTime2 = System.nanoTime();
		
		//render.start();
		
		
	}

	public float getPlayerAngle() {
		return fPlayerA;
	}

	public void setPlayerAngle(float angle) {
		fPlayerA = angle;
	}

	@Override
	public void paintComponent(Graphics g) {
		// this.update();
		//if (level != null) {
		lTime2 = System.nanoTime();
		fElapsedTime = (lTime2 - lTime1)/100000000.0f;
		lTime1 = lTime2;
		
		//System.out.println("fElapsedTime: " + fElapsedTime);
		
		boolean bHitWall = false;

		int screenWidth, screenHeight;
		screenWidth = width * nFactor;
		screenHeight = height * nFactor;

		Graphics2D big2d = (Graphics2D) level.getGraphics();
		big2d.clearRect(0, 0, screenWidth, screenHeight);
		big2d.setStroke(new BasicStroke(1.0f));
		// if (fPlayerA != fOldAngle) {
		// fOldAngle = fPlayerA;
		//float fDistanceToWall = 0.0f;

		int separacion = 0;
		for (int x = 0; x < screenWidth; x++) {
			float fDistanceToWall = 0.0f;
			float fRayAngle = (fPlayerA - fFOV / 2.0f) + ((float) x / (float) screenWidth) * fFOV;
			System.out.println("fRayAngle: " + fRayAngle);
			float fStepSize = 0.1f;
			// fDistanceToWall = 0.0f;

			float fEyeX = (float) Math.sin(fRayAngle);
			float fEyeY = (float) Math.cos(fRayAngle);
			bHitWall = false;
			while (!bHitWall && fDistanceToWall < fDepth) {
				fDistanceToWall += fStepSize;

				int nTestX = (int) (fPlayerX + fEyeX * fDistanceToWall);
				int nTestY = (int) (fPlayerY + fEyeY * fDistanceToWall);		
				
				if (nTestX < 0 || nTestX >= width || nTestY < 0 || nTestY >= height) {
					bHitWall = true;
					fDistanceToWall = fDepth;
				} else {
					if (mapa.charAt(nTestY * width + nTestX) == '#') {
						bHitWall = true;
					}
				}
			}
			
						
			int nCeiling = (int)(screenHeight / 2.0f - screenHeight / fDistanceToWall);
			int nFloor = screenHeight - nCeiling;
			techos[x] = nCeiling;
//			for (int y = 0; y < screenHeight; y++) {
//				// for (int n=0; n<screenWidth; n++) {
//
//				if (y <= nCeiling) {
//					Line2D ceiling = new Line2D.Float(x, y, x, y);
//					big2d.setColor(Color.BLUE);
//					big2d.draw(ceiling);
//				}
//				if (y > nCeiling && y <= nFloor) {
//					Line2D wall = new Line2D.Float(x, y, x, y);
//					Color cWallColor;
////					if (fDistanceToWall <= fDepth / 4.0f) {					
////						cWallColor = Color.getHSBColor(0, 0, 0.25f);
////					} else if (fDistanceToWall < fDepth / 3.0f) {
////						cWallColor = Color.getHSBColor(0, 0, 0.50f);
////					} else if (fDistanceToWall < fDepth / 2.0f) {
////						cWallColor = Color.getHSBColor(0, 0, 0.75f);
////					} else {
////						cWallColor = Color.getHSBColor(0, 0, 1.0f);
////					}
//					cWallColor = Color.getHSBColor(0.0f, 0.0f, ((float)fDistanceToWall/1000.0f));
//					
//					big2d.setColor(cWallColor);
//					big2d.draw(wall);
//				} else {
//					Line2D floor = new Line2D.Float(x, y, x, y);
//					big2d.setColor(Color.white);
//					big2d.draw(floor);
//				}
//			}
			
			Line2D ceiling = new Line2D.Float(x,0,x,nCeiling);
			Line2D wall = new Line2D.Float(x,nCeiling,x,nFloor);
			Line2D floor = new Line2D.Float(x,nFloor,x,screenHeight);
			
			big2d.setColor(Color.BLUE);
			big2d.draw(ceiling);
			big2d.setColor(Color.darkGray);
			big2d.draw(wall);
			big2d.setColor(Color.GREEN);
			big2d.draw(floor);
			
			
			
		}
		//repaint();

		//Graphics2D g2d = (Graphics2D) this.getGraphics();
		//big2d.drawImage(level, 0, 0, screenWidth, screenHeight, this);
		
//		System.out.println("Altura techos:");
//		for (int i=0; i<techos.length; i++) {
//			System.out.print(techos[i]+ " | ");				
//		}
				
		
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(level, 0, 0, null);
	}

	private void update() {

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
		// System.out.println(e.getKeyCode());
		if (e.getKeyCode() == 65) { // Si se presionó la A entonces giro en
									// sentido horario
			this.setPlayerAngle(this.getPlayerAngle() - 0.05f );
		}
		if (e.getKeyCode() == 68) { // Si se presionó la A entonces giro en
									// sentido horario
			this.setPlayerAngle(this.getPlayerAngle() + 0.05f );
		}
		if (e.getKeyCode() == 87) { //W
			fPlayerX += (float)(Math.sin(fPlayerA)) * 0.5f ;
			fPlayerY += (float)(Math.cos(fPlayerA)) * 0.5f ;
		}
		if (e.getKeyCode() == 83) { //S
			fPlayerX -= (float)(Math.sin(fPlayerA)) * 0.5f ;
			fPlayerY -= (float)(Math.cos(fPlayerA)) * 0.5f ;
		}
		repaint();
	    //System.out.println(e.getKeyCode());
		//render.start();
		//run(); 
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private float[] HSVtoRGB(float h, float s, float v)  {
        // H is given on [0->6] or -1. S and V are given on [0->1].
        // RGB are each returned on [0->1].
        float m, n, f;
        int i;

        float[] hsv = new float[3];
        float[] rgb = new float[3];

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;

        if (hsv[0] == -1)
        {
            rgb[0] = rgb[1] = rgb[2] = hsv[2];
            return rgb;
        }
        i = (int) (Math.floor(hsv[0]));
        f = hsv[0] - i;
        if (i % 2 == 0)
        {
            f = 1 - f; // if i is even
        }
        m = hsv[2] * (1 - hsv[1]);
        n = hsv[2] * (1 - hsv[1] * f);
        switch (i)
        {
            case 6:
            case 0:
                rgb[0] = hsv[2];
                rgb[1] = n;
                rgb[2] = m;
                break;
            case 1:
                rgb[0] = n;
                rgb[1] = hsv[2];
                rgb[2] = m;
                break;
            case 2:
                rgb[0] = m;
                rgb[1] = hsv[2];
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = m;
                rgb[1] = n;
                rgb[2] = hsv[2];
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = m;
                rgb[2] = hsv[2];
                break;
            case 5:
                rgb[0] = hsv[2];
                rgb[1] = m;
                rgb[2] = n;
                break;
        }

        return rgb;

    }
	
	
	public void run() {
		while (true) {
			
			lTime2 = System.nanoTime();
			fElapsedTime = (lTime2 - lTime1)/100000000.0f;
			lTime1 = lTime2;
			
			//System.out.println("fElapsedTime: " + fElapsedTime);
			
			boolean bHitWall = false;

			int screenWidth, screenHeight;
			screenWidth = width * nFactor;
			screenHeight = height * nFactor;

			Graphics2D big2d = (Graphics2D) level.getGraphics();
			big2d.clearRect(0, 0, screenWidth, screenHeight);
			big2d.setStroke(new BasicStroke(1.0f));
			// if (fPlayerA != fOldAngle) {
			// fOldAngle = fPlayerA;
			//float fDistanceToWall = 0.0f;

			int separacion = 0;
			for (int x = 0; x < screenWidth; x++) {
				float fDistanceToWall = 0.0f;
				float fRayAngle = (fPlayerA - fFOV / 2.0f) + ((float) x / (float) screenWidth) * fFOV;
				System.out.println("fRayAngle: " + fRayAngle);
				float fStepSize = 0.1f;
				// fDistanceToWall = 0.0f;

				float fEyeX = (float) Math.sin(fRayAngle);
				float fEyeY = (float) Math.cos(fRayAngle);
				bHitWall = false;
				while (!bHitWall && fDistanceToWall < fDepth) {
					fDistanceToWall += fStepSize;

					int nTestX = (int) (fPlayerX + fEyeX * fDistanceToWall);
					int nTestY = (int) (fPlayerY + fEyeY * fDistanceToWall);		
					
					if (nTestX < 0 || nTestX >= width || nTestY < 0 || nTestY >= height) {
						bHitWall = true;
						fDistanceToWall = fDepth;
					} else {
						if (mapa.charAt(nTestY * width + nTestX) == '#') {
							bHitWall = true;
						}
					}
				}
				
							
				int nCeiling = (int)(screenHeight / 2.0f - screenHeight / fDistanceToWall);
				int nFloor = screenHeight - nCeiling;
				techos[x] = nCeiling;
				for (int y = 0; y < screenHeight; y++) {
					// for (int n=0; n<screenWidth; n++) {

					if (y <= nCeiling) {
						Line2D ceiling = new Line2D.Float(x, y, x, y);
						big2d.setColor(Color.BLUE);
						big2d.draw(ceiling);
					}
					if (y > nCeiling && y <= nFloor) {
						Line2D wall = new Line2D.Float(x, y, x, y);
						Color cWallColor;
//						if (fDistanceToWall <= fDepth / 4.0f) {					
//							cWallColor = Color.getHSBColor(0, 0, 0.25f);
//						} else if (fDistanceToWall < fDepth / 3.0f) {
//							cWallColor = Color.getHSBColor(0, 0, 0.50f);
//						} else if (fDistanceToWall < fDepth / 2.0f) {
//							cWallColor = Color.getHSBColor(0, 0, 0.75f);
//						} else {
//							cWallColor = Color.getHSBColor(0, 0, 1.0f);
//						}
						cWallColor = Color.getHSBColor(0.0f, 0.0f, ((float)fDistanceToWall/1000.0f));
						
						big2d.setColor(cWallColor);
						big2d.draw(wall);
					} else {
						Line2D floor = new Line2D.Float(x, y, x, y);
						big2d.setColor(Color.white);
						big2d.draw(floor);
					}
				}
			}
			//repaint();

			Graphics2D g2d = (Graphics2D) this.getGraphics();
			g2d.drawImage(level, 0, 0, screenWidth, screenHeight, this);
			
			System.out.println("Altura techos:");
			for (int i=0; i<techos.length; i++) {
				System.out.print(techos[i]+ " | ");				
			}
			
		}
	}
}
