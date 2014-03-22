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

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.pp.cryptoqr.component.interfaces.QRPrinter;
import org.pp.cryptoqr.component.types.CoinAddress;

/**
 * @author CryptoQR 
 *
 */
public class PrintAddressesWorker extends SwingWorker<Void, Void> {
	
	//request details and reference to addr printer component
	private final List<CoinAddress> addresses;
	
	//reference to last main window
	private final CryptoQRMainFrame applicationFrame;
	
	//printer
	private final QRPrinter addrPrinter;
	
	public PrintAddressesWorker(QRPrinter addrPrinter, List<CoinAddress> addresses, 
				CryptoQRMainFrame applicationFrame) {
		//keep for on doInBackground
		this.addrPrinter = addrPrinter;
		this.addresses = addresses;
		this.applicationFrame = applicationFrame;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		//set processing mode
		this.applicationFrame.setProcessing(true);
		
		//get the addresses from the Address Generator Component
		addrPrinter.printAddresses(addresses);

		return null;
	}

	@Override
	protected void done() {
		//set processing mode
		this.applicationFrame.setProcessing(false);
		
		try {
			get();
		} catch (InterruptedException e) { }	
		catch (ExecutionException e) {
			//something went wrong while trying to generate addresses. Just log it.
			applicationFrame.logMessage("Error while trying to generate addresses.\n");
			return;
		}
		
		//output the addresses to the text area
		applicationFrame.logMessage(""); //clear
		applicationFrame.logMessage("Printed "+addresses.size()+" addresses.\n");

	}
}
