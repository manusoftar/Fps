package ar.com.manusofar;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Ide extends JFrame {
	public Ide() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		Engine motor = new Engine();
		motor.setSize(200, 200);
		this.addKeyListener(motor);
		this.getContentPane().add(motor);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Ide ide = new Ide();
		ide.setSize(640, 640);
		ide.setVisible(true);
		ide.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//ide.addKeyListener(ide);
		
	}

}
