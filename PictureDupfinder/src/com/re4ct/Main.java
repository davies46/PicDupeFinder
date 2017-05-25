package com.re4ct;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

public class Main {
	final static Logger	log			= Logger.getLogger(Main.class);

	JFrame				frame;
	private JLabel		pic2;
	final int			FRAME		= 100;
	final int			maxW		= FRAME;
	final int			maxH		= FRAME;
	String				dst			= "D:/Flat pics";

	File[]				files;

	private JLabel		pic1;
	int					WINDOWSIZE	= 50;
	int					currentImage;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	class Deets {
		JLabel		orig, copy;
		String		origName;
		String		copyName;
		JCheckBox	different;

		public Deets(String origName, String copyName) {
			super();
			this.origName = origName;
			this.copyName = copyName;
		}

		public Deets(JLabel orig, JLabel copy, JCheckBox chk) {
			this.different = chk;
			this.orig = orig;
			this.copy = copy;
		}
	}

	Deets[] deets = new Deets[WINDOWSIZE];

	public Main() {
		frame = new JFrame("Duplicate finder");
		frame.setBounds(100, 100, 650, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel boardPanel = new JPanel();

		BoxLayout mainLyt = new BoxLayout(boardPanel, BoxLayout.PAGE_AXIS);
		JScrollPane scrollPane = new JScrollPane(boardPanel);
		boardPanel.setLayout(mainLyt);
		// frame.add(boardPanel);
		frame.add(scrollPane);

		JButton allSame = new JButton("All Same");
		JButton allDifferent = new JButton("All Different");
		JButton done = new JButton("Go");
		JButton next = new JButton("Next Page");

		allSame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < WINDOWSIZE; i++) {
					deets[i].different.setSelected(false);
				}
				preserveSelected();
			}
		});
		allDifferent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < WINDOWSIZE; i++) {
					deets[i].different.setSelected(true);
				}
				preserveSelected();
			}
		});
		done.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				preserveSelected();
			}
		});
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentImage += WINDOWSIZE;
				displayCurrentImages();
			}
		});

		for (int r = 0; r < WINDOWSIZE; r++) {
			JPanel linePanel = new JPanel();
			BoxLayout lineLayout = new BoxLayout(linePanel, BoxLayout.LINE_AXIS);
			linePanel.setLayout(lineLayout);
			pic1 = new JLabel();
			pic2 = new JLabel();
			pic1.setMaximumSize(new Dimension(FRAME, FRAME));
			pic1.setMinimumSize(new Dimension(FRAME, FRAME));
			pic2.setMaximumSize(new Dimension(FRAME, FRAME));
			pic2.setMinimumSize(new Dimension(FRAME, FRAME));
			JCheckBox btn2 = new JCheckBox("Different");
			btn2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					log.info("Different");
					// action(false);
				}
			});
			deets[r] = new Deets(pic1, pic2, btn2);
			linePanel.add(pic1);
			linePanel.add(pic2);
			linePanel.add(btn2);
			boardPanel.add(linePanel);
		}
		boardPanel.add(allSame);
		boardPanel.add(allDifferent);
		boardPanel.add(done);
		boardPanel.add(next);
		File dir = new File(dst);

		files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("-0.jpg");
			}
		});

		currentImage = 0;
		displayCurrentImages();

		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				log.info("Key typed " + e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				log.info("Key pressed " + e.getKeyCode());

			}
		});

		boardPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				log.info("Key typed " + e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				log.info("Key pressed " + e.getKeyCode());

			}
		});

	}

	protected void preserveSelected() {
		for (int i = 0; i < WINDOWSIZE; i++) {
			boolean same = !deets[i].different.isSelected();
			if (same) {
				File copy = files[currentImage + i];
				copy.delete();
			}
		}
		currentImage += WINDOWSIZE;
		displayCurrentImages();
	}

	protected void action(boolean same) {
		if (same) {
			File copy = files[currentImage];
			copy.delete();
		}
		currentImage++;
		displayCurrentImages();
	}

	void displayCurrentImages() {
		for (int i = 0; i < WINDOWSIZE; i++) {
			File copy = files[currentImage + i];
			String copyName = copy.getName();
			String origName = copyName.substring(0, copyName.length() - 6) + ".jpg";
			log.info("Copy name " + copyName + ", orig " + origName);
			File orig = new File(dst + "/" + copyName);
			BufferedImage copyImage;
			try {
				copyImage = loadScaledImage(copy);
				BufferedImage origImage = loadScaledImage(orig);
				Deets deet = deets[i];
				deet.orig.setIcon(new ImageIcon(origImage));
				deet.copy.setIcon(new ImageIcon(copyImage));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private BufferedImage loadScaledImage(File file) throws IOException {
		BufferedImage finalImage = null;
		BufferedImage srcFileImage = ImageIO.read(file);
		if (srcFileImage == null) {
			throw new IOException("Bad file");
		}
		int rawW = srcFileImage.getWidth();
		int rawH = srcFileImage.getHeight();
		double underWidth = 1.0 * maxW / rawW;
		double underHeight = 1.0 * maxH / rawH;
		// if it's overwidth by more than overheight then scale according to overwidth
		// if it's under both, same
		double scale = (underWidth < underHeight) ? underWidth : underHeight;
		int newW = (int) (rawW * scale + 0.5);
		int newH = (int) (rawH * scale + 0.5);

		if (newH == 0 || newW == 0) {
			log.error("Bad pic size for " + file.getName());
			log.error("Max " + maxW + "," + maxH);
		} else {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

			finalImage = gc.createCompatibleImage(newW, newH);// new BufferedImage(newW, newH,
																// BufferedImage.TYPE_INT_ARGB);

			Graphics2D gr = finalImage.createGraphics();
			gr.drawImage(srcFileImage, 0, 0, newW, newH, null);
			gr.dispose();
			log.info(file.getName() + ": " + finalImage.getWidth() + "," + finalImage.getHeight());
			if (finalImage.getWidth() != maxW && finalImage.getHeight() != maxH) {
				log.error("Image size wrong! " + finalImage.getWidth() + "," + finalImage.getHeight());
				System.exit(0);
			}
		}
		return finalImage;
	}
}
