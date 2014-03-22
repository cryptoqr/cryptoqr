/**
 * Copyright 2014 CryptoQR.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//**
 * 
 */
package org.pp.cryptoqr.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.pp.cryptoqr.component.interfaces.LogEventRetriever;
import org.pp.cryptoqr.component.interfaces.QRImageProcessor;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author CryptoQR 
 *
 */
public class WebcamImageAnalyzerThread extends Thread implements ClipboardOwner {

	private boolean shouldRun = true;
	
	private final Webcam webcam;
	
	private final QRImageProcessor qrProcessor;
	
	private final LogEventRetriever uiLogger;
	
	private String lastDecodedQR;
	
	private boolean autoPaste = false;
	
	//used for pressing CTRL + V
	private Robot robot ;

	public WebcamImageAnalyzerThread(Webcam webcam, QRImageProcessor qrProcessor, LogEventRetriever uiLogger) {
		this.webcam = webcam;
		this.qrProcessor = qrProcessor;
		this.uiLogger = uiLogger;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			robot = null;
		}
	}
	
	@Override
	public void run() {
		
		while (true) {
			//foverer
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (shouldRun()) {
				//take image and do an analysis
				if (webcam.isOpen()) {

					BufferedImage image = null;
					
					if ((image = webcam.getImage()) == null) {
						//did not get image
						continue;
					}

					LuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					
					//get the analysis from the QR processor component
					String qRcode = qrProcessor.decodeQRImage(bitmap);
					if (qRcode != null) {
						if (!qRcode.equals(lastDecodedQR)) {
							lastDecodedQR = qRcode;
							
							uiLogger.logMessage("Decoded QR code: "+qRcode);
							
							//copy to clipboard
						    StringSelection stringSelection = new StringSelection(qRcode);
						    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						    clipboard.setContents(stringSelection, this);
							uiLogger.logMessage("Copied QR to clipboard");
							
							//check if we should paste (press CTRL + V)
							if (isAutoPaste() && robot != null) {
								robot.keyPress(KeyEvent.VK_CONTROL); 
								robot.keyPress(KeyEvent.VK_V); 
								robot.keyRelease(KeyEvent.VK_V); 
								robot.keyRelease(KeyEvent.VK_CONTROL); 
							}
						} else {
							//do nothing, we have already decoded this
						}


					}
				}
			}else {
				// do nothing, wait for the time being
			}
		}
	}
	
	//called by workers to stop doing analysis while workig on other things	
	public synchronized void suspendAnalysis() {
		shouldRun = false;
		//webcam.close();
	}
	
	//called by workers to resume processing when they are finished
	public synchronized void resumeAnalysis() {
		shouldRun = true;
		//webcam.open();
	}
	
	//called internally to see if it should do image analysis
	private synchronized boolean shouldRun() {
		return shouldRun;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		//do nothing
	}

	public synchronized boolean isAutoPaste() {
		return autoPaste;
	}

	public synchronized void setAutoPaste(boolean autoPaste) {
		this.autoPaste = autoPaste;
	}
	
}
