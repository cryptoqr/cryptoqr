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
 */
package org.pp.cryptoqr.component;

import java.awt.EventQueue;

import org.apache.log4j.Logger;
import org.pp.cryptoqr.component.interfaces.AddressGenerator;
import org.pp.cryptoqr.component.interfaces.LogEventRetriever;
import org.pp.cryptoqr.component.interfaces.QRImageProcessor;
import org.pp.cryptoqr.component.interfaces.QRPrinter;
import org.pp.cryptoqr.ui.CryptoQRMainFrame;

/**
 * @author CryptoQR 
 *
 */
public class UIComponent implements LogEventRetriever {

	private final Logger logger = Logger.getLogger(getClass());
	//the main application frame
	private volatile CryptoQRMainFrame applicationFrame;
	
	//address generator component
	private final AddressGenerator addressGenerator;
	
	//QR priter component
	private final QRPrinter qrPrinter;
	
	//QR Image Processor component
	private final QRImageProcessor qrImageProcessor;
	
	public UIComponent(	AddressGenerator addressGenerator,
						QRPrinter qrPrinter,
						QRImageProcessor qrImageProcessor)
			{
		
		//set component references
		this.addressGenerator = addressGenerator;
		this.qrPrinter = qrPrinter;
		this.qrImageProcessor = qrImageProcessor;
		
		//initialize the GUI through the GUI thread
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					logger.info("creating frame");
					applicationFrame = new CryptoQRMainFrame(UIComponent.this.addressGenerator, UIComponent.this.qrPrinter, UIComponent.this.qrImageProcessor);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				logger.info("initializing frame");
				applicationFrame.pack();
				applicationFrame.setVisible(true);	
				applicationFrame.setBounds(100, 100, 600, 500);
				applicationFrame.initialize();
			}
		});
	}
	
	public void logMessage(String message) {
		
		//forward to the user interface in order to show the msg to the user
		applicationFrame.logMessage(message);
	}
	

}
