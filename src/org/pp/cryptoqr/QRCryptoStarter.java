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
package org.pp.cryptoqr;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.pp.cryptoqr.component.AddressGeneratorComponent;
import org.pp.cryptoqr.component.QRImageProcessorComponent;
import org.pp.cryptoqr.component.QRPrinterComponent;
import org.pp.cryptoqr.component.UIComponent;

/**
 * Application entry class.
 * Initializes the logging infrastructure, the component framework
 * and starts the application.
 * 
 * @author CryptoQR 
 *
 */
public class QRCryptoStarter {

	private final Logger logger = Logger.getLogger(getClass());
	
	public static void main(String[] args) throws Exception {
		new QRCryptoStarter().startApplication();
		
	}

	private void startApplication() throws Exception {
		//configure logger
		try {
			PropertyConfigurator.configure("resources/log4j.properties");
			logger.debug("Logger initialized");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//load properties
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream("resources/cryptoqr.properties");
        props.loadFromXML(fis);
                
        //connect components
        QRPrinterComponent qrPrinter = new QRPrinterComponent();
        QRImageProcessorComponent qrImageProcessor = new QRImageProcessorComponent();
        AddressGeneratorComponent addrGenerator = new AddressGeneratorComponent();
        UIComponent userInterface = new UIComponent(addrGenerator, qrPrinter, qrImageProcessor);
        addrGenerator.init(props.getProperty("supportedCoins"), userInterface);
        userInterface.initUI();
	}
}
