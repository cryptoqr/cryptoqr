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

import java.awt.Toolkit;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.github.sarxos.webcam.Webcam;

/**
 * Prompts the user for seleting an imaging device
 * @author CryptoQR 
 *
 */
public class WebcamChooserWindow {

	private static final Logger logger = Logger.getLogger(WebcamChooserWindow.class.getName());
	
	public static Webcam getImagingDevice() {
		Webcam webcam = null;
		
		List<Webcam> webcams = Webcam.getWebcams();
		if (webcams.size() == 0) {
			//no webcam
			logger.log(Level.ERROR, "No webcams installed");
			return null;
		} else {
			//there are some webcams
			logger.log(Level.INFO, "Found "+webcams.size()+" imaging devices");
			
			//prompt user to select webcam
			Webcam[] webcamArray = webcams.toArray(new Webcam[] {});

			webcam = (Webcam)JOptionPane.showInputDialog(
                    null,
                    "Please choose imaging device",
                    "Choose Device", 
                    JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(Toolkit.getDefaultToolkit().getImage(CryptoQRMainFrame.class.getResource("/com/github/sarxos/webcam/icons/camera-icon.png")), ""),
                    webcamArray,
                    null);

			
		}
	
		
		return webcam;
	}
}
