package webstartComponentArch;

import java.io.*;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Tail implements LogFileTailerListener {
	/**
	 * The log file tailer
	 */
	private LogFileTailer tailer;
	private JTextArea jta;
	private String encoding;
	private int maxLen;

	/**
	 * Creates a new Tail instance to follow the specified file
	 */
	public Tail(String filename) {
		tailer = new LogFileTailer(new File(filename), 1000, false);
		tailer.addLogFileTailerListener(this);
	}

	public void setJtext(JTextArea jta) {
		this.jta = jta;
	}
	
	public void restart()
	{
		tailer.resume();
	}

	public void start() {
		tailer.start();
	}

	public void setMaxCharacter(int max) {
		maxLen = max;
	}

	/**
	 * A new line has been added to the tailed log file
	 * 
	 * @param line
	 *            The new line that has been added to the tailed log file
	 */
	int writeCount = 0;
	int flushCheck = 50;
//	int prevPos = 0;
	
	public void newLogFileLine(String line) {
		if (encoding != null && !encoding.isEmpty()) {
			try {
				line = new String(line.getBytes("8859_1"), encoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		synchronized (jta) {
			if (jta != null) {
				Document doc = jta.getDocument();
				jta.append(line + "\r\n");
				writeCount++;
				
				if(writeCount >= flushCheck){
					try {
						if (maxLen > 0) {
							if (doc.getLength() > maxLen){
								doc.remove(0,  maxLen);
//								prevPos = doc.getLength();
							}
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				// jPane.getVerticalScrollBar().setValue(jPane.getVerticalScrollBar().getMaximum());
				jta.setCaretPosition(jta.getDocument().getLength());
			} else
				System.out.println(line);
		}
	}

	/**
	 * this method can set file encoding
	 * 
	 * @param encodingStr
	 */
	public void setEncoding(String encodingStr) {
		encoding = encodingStr;
	}

	/**
	 * stop tailing
	 */
	public void stopTailing() {
		tailer.stopTailing();
	}

	/**
	 * Command-line launcher
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: Tail <filename>");
			System.exit(0);
		}
		Tail tail = new Tail(args[0]);
		tail.setJtext(null);
		tail.setEncoding("");
		tail.setMaxCharacter(10000);
		tail.start();
	}
}
