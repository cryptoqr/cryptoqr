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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.pp.cryptoqr.component.interfaces.AddressGenerator;
import org.pp.cryptoqr.component.types.CoinAddress;

/**
 * @author CryptoQR 
 *
 */
public class GenerateSpecificAddressesWorker extends SwingWorker<CoinAddress, Void> {
	
	//request details and reference to addr generator component
	private final AddressGenerator addrGenerator;
	private final String coin;
	private final String pattern;
	
	//reference to last generated addresses and text area
	private final CryptoQRMainFrame applicationFrame;
	
	public GenerateSpecificAddressesWorker(AddressGenerator addrGenerator, String coin, String pattern,
			CryptoQRMainFrame applicationFrame) {
		//keep for ue on doInBackground
		this.addrGenerator = addrGenerator;
		this.coin = coin;
		this.pattern = pattern;
		this.applicationFrame = applicationFrame;
		
	}
	
	@Override
	protected CoinAddress doInBackground() throws Exception {
		//set processing mode
		this.applicationFrame.setProcessing(true);

		//get the addresses from the Address Generator Component
		CoinAddress address = addrGenerator.generateSpecificAddress(coin, pattern);
		return address;
	}

	@Override
	protected void done() {
		this.applicationFrame.setProcessing(false);

		//get results
		List<CoinAddress> results = new ArrayList<CoinAddress>();
		try {
			CoinAddress addr = get();
			results.add(addr);
		} catch (InterruptedException e) { }	
		catch (ExecutionException e) {
			//something went wrong while trying to generate addresses. Just log it.
			applicationFrame.logMessage("Error while trying to generate addresses.\n");
			return;
		}
		
		//clear list and add results for later, if the user wants to print them
		applicationFrame.setLastGenerated(results);
		
		//output the addresses to the text area
		applicationFrame.logMessage(""); //clear
		applicationFrame.logMessage("Generated "+results.size()+" addresses.\n");

		int i=0;
		for (CoinAddress coinAddr : results) {
			applicationFrame.logMessage("Address "+(++i)+" details:\n");
			applicationFrame.logMessage("Address: "+coinAddr.getAddress()+" \n");
			applicationFrame.logMessage("Private Key: "+coinAddr.getPrivateKey()+" \n");
		}
	}
}
