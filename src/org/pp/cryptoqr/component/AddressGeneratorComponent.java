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

import java.security.Security;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.pp.cryptoqr.component.interfaces.AddressGenerator;
import org.pp.cryptoqr.component.interfaces.LogEventRetriever;
import org.pp.cryptoqr.component.types.CoinAddress;
import org.pp.cryptoqr.component.types.CryptoCoin;
import org.pp.cryptoqr.component.types.GetAddressesRequest;
import org.pp.cryptoqr.component.types.GetSpecificAddressRequest;
import org.pp.cryptoqr.component.types.GetSupportedCoinsRequest;
import org.pp.cryptoqr.component.util.AddressGeneratorQueueProcessor;

/**
 * 
 * @author CryptoQR 
 *
 */
public class AddressGeneratorComponent implements AddressGenerator{

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private volatile AddressGeneratorQueueProcessor processor;
		
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public AddressGeneratorComponent() {

	}
	
	public void init(String supportedCoins, LogEventRetriever uiLogger) {
		//init the processor
		//create mai request processor
		processor = new AddressGeneratorQueueProcessor(supportedCoins, uiLogger);

		processor.initialize();
	}
	
	
	public CoinAddress generateSpecificAddress(String coin, String pattern) {
		//create request
		GetSpecificAddressRequest request = new GetSpecificAddressRequest(pattern, coin);
		processor.addRequest(request);
		CoinAddress ret = null;
		
		//get asynchronous reply and return
		try {
			ret = request.getResponse();
		} catch (InterruptedException e) {
			logger.log(Level.ERROR, "interrpted", e);
			return null;
		}
		return ret;
	}
	
	public List<CoinAddress> generateAddress(String coin, Integer addrNum) {		
		//gen addresses
		GetAddressesRequest request = new GetAddressesRequest(coin, addrNum);
		processor.addRequest(request);

		List<CoinAddress> ret;
		//get asynchronous reply and return
		try {
			ret = request.getResponse();
		} catch (InterruptedException e) {
			logger.log(Level.ERROR, "interrpted", e);
			return null;
		}
		return ret;
	}

	public List<CryptoCoin> getSupportedCoins() {
		logger.info("handling getSupportedCoins request");
		GetSupportedCoinsRequest request = new GetSupportedCoinsRequest();
		processor.addRequest(request);

		List<CryptoCoin> ret;
		//get asynchronous reply and return
		try {
			ret = request.getResponse();
		} catch (InterruptedException e) {
			logger.log(Level.ERROR, "interrpted", e);
			return null;
		}
		return ret;
	}



		
}
