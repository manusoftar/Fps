package ar.com.manusofar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public class Engine extends JPanel implements KeyListener, Runnable {

	float fPlayerX = 1.0f;
	float fPlayerY = 1.0f;
	float fPlayerA = 0.0f;
	float fOldAngle = 0.01f;

	int height = 16;
	int width = 16;

	int textureWidth = 32;
	int textureHeight = 32;

	float fFOV = (float) (Math.PI / 4.0f);

	float fDepth = 16.0f;

	int nFactorX = 50; // Cuantas líneas horizontales por columna
	int nFactorY = 30;

	int screenWidth = width * nFactorX;
	int screenHeight = height * nFactorY;

	String mapa = "";
	String wallTexture = "";

	BufferedImage level;

	float fElapsedTime;
	long lTime1, lTime2;

	float techos[] = new float[screenWidth];
	boolean bBoundary = false;
	float fWallHeight = 3.0f;
	float fStepSize = 0.025f;
	float fSampleX = 0.0f;

	Thread render;

	public Engine() {
		mapa = "################";
		mapa += "#    #     #   #";
		mapa += "#### # ####### #";
		mapa += "# #            #";
		mapa += "# ###########  #";
		mapa += "# #     #      #";
		mapa += "# ####  #  #####";
		mapa += "## #       #   #";
		mapa += "#  ######  ##  #";
		mapa += "#  #       #   #";
		mapa += "#  # #######  ##";
		mapa += "#  # #   #     #";
		mapa += "#  #   #   #   #";
		mapa += "#  ##########  #";
		mapa += "#              #";
		mapa += "################";

		
		wallTexture =  "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "RBRRRRBRRRRBRRRRBRRRRBRRRRBRRRRB";
		wallTexture += "RBRRRRBRRRRBRRRRBRRRRBRRRRBRRRRB";
		wallTexture += "RBRRRRBRRRRBRRRRBRRRRBRRRRBRRRRB";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "RBRRRBRRRBRRRBRRRBRRRBRRRBRRRBRR";
		wallTexture += "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
		wallTexture += "BRRRBRRRBRRRBRRRBRRRBRRRBRRRBRRR";
		this.setSize(width * nFactorX, height * nFactorY);
		this.setVisible(true);
		this.addKeyListener(this);

		// fPlayerA = 10.0f;
		level = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		render = new Thread(this);

		lTime1 = System.nanoTime();
		lTime2 = System.nanoTime();

		float[] color = Color.RGBtoHSB(138, 137, 137, null);
		System.out.println("Color -> [ " + color[0] + ", " + color[1] + ", " + color[2] + " ]");

		// render.start();

	}

	public float getPlayerAngle() {
		return fPlayerA;
	}

	public void setPlayerAngle(float angle) {
		fPlayerA = angle;
	}

	private Color getSampleColour(float x, float y) {
		int sx = (int) (x * (float) textureWidth);
		int sy = (int) (y * (float) textureHeight - 1.0f);

		// System.out.println(wallTexture.charAt(sy * textureWidth + sx));

		if (sx < 0 || sx >= textureWidth || sy < 0 || sy >= textureHeight) {
			return Color.black;
		} else {
			String color = wallTexture.charAt(sy * textureWidth + sx) + "";
			if (color.equalsIgnoreCase("b")) {
				return Color.white;
			}
			if (color.equalsIgnoreCase("r")) {
				return Color.red;
			}
		}
		return Color.black;
	}

	@Override
	public void paintComponent(Graphics g) {
		// this.update();
		// if (level != null) {
		lTime2 = System.nanoTime();
		fElapsedTime = (lTime2 - lTime1) / 100000000.0f;
		lTime1 = lTime2;

		// System.out.println("fElapsedTime: " + fElapsedTime);

		boolean bHitWall = false;

		int screenWidth, screenHeight;
		screenWidth = width * nFactorX;
		screenHeight = height * nFactorY;

		Graphics2D big2d = (Graphics2D) level.getGraphics();
		big2d.clearRect(0, 0, screenWidth, screenHeight);
		// RenderingHints rh = new
		// RenderingHints(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// big2d.setRenderingHints(rh);
		big2d.setStroke(new BasicStroke(1f));
		// if (fPlayerA != fOldAngle) {
		// fOldAngle = fPlayerA;
		// float fDistanceToWall = 0.0f;

		int separacion = 0;
		for (int x = 0; x < screenWidth; x++) {
			float fDistanceToWall = 0.0f;
			// float fRayAngle = (fPlayerA - fFOV / 2.0f) + ((float) x / (float)
			// screenWidth) * fFOV;
			float fRayAngle = (float) (fPlayerA
					+ Math.atan((2.0f * (float) x / (float) screenWidth - 1.0f) * Math.tan(fFOV / 2.0f)));
			// System.out.println("fRayAngle: " + fRayAngle);
			// fStepSize = 0.025f;
			// fDistanceToWall = 0.0f;

			float fEyeX = (float) Math.sin(fRayAngle);
			float fEyeY = (float) Math.cos(fRayAngle);
			bHitWall = false;
			while (!bHitWall && fDistanceToWall < fDepth) {
				fDistanceToWall += fStepSize;
				bBoundary = false;
				int nTestX = (int) (fPlayerX + fEyeX * fDistanceToWall);
				int nTestY = (int) (fPlayerY + fEyeY * fDistanceToWall);

				if (nTestX < 0 || nTestX >= width || nTestY < 0 || nTestY >= height) {
					bHitWall = true;
					fDistanceToWall = fDepth;
				} else {
					if (mapa.charAt(nTestY * width + nTestX) == '#') {
						bHitWall = true;

						// Busco las esquinas para resaltarlas

						List<Point2D> esquinas = new LinkedList<>();

						for (int tx = 0; tx < 2; tx++) {
							for (int ty = 0; ty < 2; ty++) {
								// Angle of corner to eye
								float vy = (float) nTestY + ty - fPlayerY;
								float vx = (float) nTestX + tx - fPlayerX;
								float d = (float) Math.sqrt(vx * vx + vy * vy);
								float dot = (fEyeX * vx / d) + (fEyeY * vy / d);
								Point2D punto = new Point2D.Float(d, dot);
								esquinas.add(punto);
							}
						}

						Collections.sort(esquinas, new Comparator<Point2D>() {
							public int compare(Point2D p1, Point2D p2) {
								return Double.compare(p1.getX(), p2.getX());
							}
						});

						float fBound = 0.001f;
						if (Math.acos(esquinas.get(0).getY()) < fBound)
							bBoundary = true;
						if (Math.acos(esquinas.get(1).getY()) < fBound)
							bBoundary = true;
						if (Math.acos(esquinas.get(2).getY()) < fBound)
							bBoundary = true;

						float fBlockMidX = (float) nTestX + 0.5f;
						float fBlockMidY = (float) nTestY + 0.5f;

						float fTestPointX = fPlayerX + fEyeX * fDistanceToWall;
						float fTestPointY = fPlayerY + fEyeY * fDistanceToWall;

						float fTestAngle = (float) Math.atan2((fTestPointY - fBlockMidY), (fTestPointX - fBlockMidX));

						if (fTestAngle >= -3.14159f * 0.25f && fTestAngle < 3.14159f * 0.25f)
							fSampleX = fTestPointY - (float) nTestY;
						if (fTestAngle >= 3.14159f * 0.25f && fTestAngle < 3.14159f * 0.75f)
							fSampleX = fTestPointX - (float) nTestX;
						if (fTestAngle < -3.14159f * 0.25f && fTestAngle >= -3.14159f * 0.75f)
							fSampleX = fTestPointX - (float) nTestX;
						if (fTestAngle >= 3.14159f * 0.75f || fTestAngle < -3.14159f * 0.75f)
							fSampleX = fTestPointY - (float) nTestY;

					}
				}
			}

			// int nCeiling = (int)(screenHeight / 2.0f - screenHeight /
			// fDistanceToWall);
			float fDistance = ((fEyeX * (float) Math.sin(fPlayerA)) + (fEyeY * (float) Math.cos(fPlayerA)))
					* fDistanceToWall;
			float nCeiling = (((float) screenHeight / fWallHeight) * (1.0f - 1.0f / fDistance));
			float nFloor = screenHeight - nCeiling;
			techos[x] = nCeiling;

<<<<<<< HEAD
			
			Line2D ceiling = new Line2D.Float(x,0,x,nCeiling);
			Line2D wall = new Line2D.Float(x,nCeiling,x,nFloor);
			
			//Line2D floor = new Line2D.Float(x,nFloor,x,screenHeight);
			
=======
			Line2D ceiling = new Line2D.Float(x, 0, x, nCeiling);
			Line2D wall = new Line2D.Float(x, nCeiling, x, nFloor);

			// Line2D floor = new Line2D.Float(x,nFloor,x,screenHeight);

>>>>>>> origin/master
			big2d.setColor(Color.black);
			big2d.draw(ceiling);
			bBoundary = false;
			for (int y = (int) nCeiling; y < screenHeight; y++) {

				if (y >= nCeiling && y < nFloor) {
					if (!bBoundary) {

						if (fDistanceToWall < fDepth) {
							float fSampleY = ((float) y - (float) nCeiling) / ((float) nFloor - (float) nCeiling);
							big2d.setColor(getSampleColour(fSampleX, fSampleY));
							// System.out.println("fSampleX: " + fSampleX + " |
							// fSampleY: " + fSampleY);
						} else {
							big2d.setColor(Color.black);
						}
						big2d.drawLine(x, y, x, y);

						// if (fDistanceToWall <= fDepth / 4.0f ) { //Very close
						// big2d.setColor(Color.white);
						// } else if (fDistanceToWall < fDepth / 3.0f) {
						// big2d.setColor(Color.lightGray);
						// } else if (fDistanceToWall < fDepth / 2.0f) {
						// big2d.setColor(Color.gray);
						// } else if (fDistanceToWall < fDepth ){
						// big2d.setColor(Color.darkGray);
						// } else {
						// big2d.setColor(Color.black);
						// }
					} else {
						big2d.setColor(Color.black);
					}
				} else {
					// big2d.draw(wall);
					// big2d.setColor(Color.GREEN);
					// big2d.draw(floor);
					// for (int y=(int)nFloor; y<screenHeight; y++ ) {
					float b = 1.0f - (((float) y - screenHeight / 2.0f) / ((float) screenHeight / 2.0f));
					if (b < 0.25) {
						big2d.setColor(Color.getHSBColor(0.29190207f, 0.99f, 1.0f));
					} else if (b < 0.5) {
						big2d.setColor(Color.getHSBColor(0.29190207f, 0.99f, 0.70f));
					} else if (b < 0.75) {
						big2d.setColor(Color.getHSBColor(0.29190207f, 0.99f, 0.60f));
					} else if (b < 0.9) {
						big2d.setColor(Color.getHSBColor(0.29190207f, 0.99f, 0.50f));
					} else {
						big2d.setColor(Color.getHSBColor(0.29190207f, 0.99f, 0.40f));
					}
					big2d.drawLine(x, y, x, y);
				}
			}

		}

		Graphics2D g2d = (Graphics2D) g;
		// RenderingHints rh = new
		// RenderingHints(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		// g2d.setRenderingHints(rh);
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
			this.setPlayerAngle(this.getPlayerAngle() - 0.05f);
		}
		if (e.getKeyCode() == 68) { // Si se presionó la A entonces giro en
									// sentido horario
			this.setPlayerAngle(this.getPlayerAngle() + 0.05f);
		}
		if (e.getKeyCode() == 87) { // W
			fPlayerX += (float) (Math.sin(fPlayerA)) * 0.15f;
			fPlayerY += (float) (Math.cos(fPlayerA)) * 0.15f;
			if (mapa.charAt((int) fPlayerY * width + (int) fPlayerX) == '#') {
				fPlayerX -= (float) (Math.sin(fPlayerA)) * 0.15f;
				fPlayerY -= (float) (Math.cos(fPlayerA)) * 0.15f;
			}

		}
		if (e.getKeyCode() == 83) { // S
			fPlayerX -= (float) (Math.sin(fPlayerA)) * 0.15f;
			fPlayerY -= (float) (Math.cos(fPlayerA)) * 0.15f;
			if (mapa.charAt((int) fPlayerY * width + (int) fPlayerX) == '#') {
				fPlayerX += (float) (Math.sin(fPlayerA)) * 0.15f;
				fPlayerY += (float) (Math.cos(fPlayerA)) * 0.15f;
			}
		}
		if (e.getKeyCode() == 107) { // Numpad +
			fFOV += 0.1;
		}
		if (e.getKeyCode() == 109) { // Numpad -
			fFOV -= 0.1;
		}
		if (e.getKeyCode() == 106) { // Numpad *
			fStepSize += 0.01;
		}
		if (e.getKeyCode() == 111) { // Numpad /
			fStepSize -= 0.01;
		}
		System.out.println("FOV: " + fFOV);
		System.out.println("fStepSize: " + fStepSize);

		// System.out.println(e.getKeyCode());
		repaint();
		// System.out.println(e.getKeyCode());
		// render.start();
		// run();

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private float[] HSVtoRGB(float h, float s, float v) {
		// H is given on [0->6] or -1. S and V are given on [0->1].
		// RGB are each returned on [0->1].
		float m, n, f;
		int i;

		float[] hsv = new float[3];
		float[] rgb = new float[3];

		hsv[0] = h;
		hsv[1] = s;
		hsv[2] = v;

		if (hsv[0] == -1) {
			rgb[0] = rgb[1] = rgb[2] = hsv[2];
			return rgb;
		}
		i = (int) (Math.floor(hsv[0]));
		f = hsv[0] - i;
		if (i % 2 == 0) {
			f = 1 - f; // if i is even
		}
		m = hsv[2] * (1 - hsv[1]);
		n = hsv[2] * (1 - hsv[1] * f);
		switch (i) {
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
			fElapsedTime = (lTime2 - lTime1) / 100000000.0f;
			lTime1 = lTime2;

			// System.out.println("fElapsedTime: " + fElapsedTime);

			boolean bHitWall = false;

			int screenWidth, screenHeight;
			screenWidth = width * nFactorX;
			screenHeight = height * nFactorY;

			Graphics2D big2d = (Graphics2D) level.getGraphics();
			big2d.clearRect(0, 0, screenWidth, screenHeight);
			big2d.setStroke(new BasicStroke(1.0f));
			// if (fPlayerA != fOldAngle) {
			// fOldAngle = fPlayerA;
			// float fDistanceToWall = 0.0f;

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

				int nCeiling = (int) (screenHeight / 2.0f - screenHeight / fDistanceToWall);
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
						// if (fDistanceToWall <= fDepth / 4.0f) {
						// cWallColor = Color.getHSBColor(0, 0, 0.25f);
						// } else if (fDistanceToWall < fDepth / 3.0f) {
						// cWallColor = Color.getHSBColor(0, 0, 0.50f);
						// } else if (fDistanceToWall < fDepth / 2.0f) {
						// cWallColor = Color.getHSBColor(0, 0, 0.75f);
						// } else {
						// cWallColor = Color.getHSBColor(0, 0, 1.0f);
						// }
						cWallColor = Color.getHSBColor(0.0f, 0.0f, ((float) fDistanceToWall / 1000.0f));

						big2d.setColor(cWallColor);
						big2d.draw(wall);
					} else {
						Line2D floor = new Line2D.Float(x, y, x, y);
						big2d.setColor(Color.white);
						big2d.draw(floor);
					}
				}
			}
			// repaint();

			Graphics2D g2d = (Graphics2D) this.getGraphics();
			g2d.drawImage(level, 0, 0, screenWidth, screenHeight, this);

			System.out.println("Altura techos:");
			for (int i = 0; i < techos.length; i++) {
				System.out.print(techos[i] + " | ");
			}

		}
	}
}
