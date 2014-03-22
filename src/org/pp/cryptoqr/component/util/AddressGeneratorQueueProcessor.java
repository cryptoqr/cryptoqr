/**
 * 
 */
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
package org.pp.cryptoqr.component.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.pp.cryptoqr.component.interfaces.LogEventRetriever;
import org.pp.cryptoqr.component.types.AddressGeneratorRequestType;
import org.pp.cryptoqr.component.types.CoinAddress;
import org.pp.cryptoqr.component.types.CryptoCoin;
import org.pp.cryptoqr.component.types.GetAddressesRequest;
import org.pp.cryptoqr.component.types.GetSpecificAddressRequest;
import org.pp.cryptoqr.component.types.GetSupportedCoinsRequest;
import org.pp.cryptoqr.util.AbstractQueueProcessor;
import org.pp.cryptoqr.util.ByteArrayUtil;
import org.pp.cryptoqr.util.CryptoCoinECKeyGenerator;
import org.pp.cryptoqr.util.GenericProcessorRequest;

/**
 * This is the main processor of the AddressGenerator Component.
 * It implements all the logic
 * 
 * @author CryptoQR 
 *
 */
public class AddressGeneratorQueueProcessor extends AbstractQueueProcessor<GenericProcessorRequest<AddressGeneratorRequestType> > {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	//list of supported coins (through configuration)
	private final List<CryptoCoin> suppCoins;
	
	//the currently set up coin that isused to generate addresses
	private CryptoCoin currentlyCoifiguredCoin = null;
	
	//map of key generators per EC curve. 
	private final Map<String, CryptoCoinECKeyGenerator> keyGenerators;

	//interface to the UI component for sending log messages that the user needs to see.
	private final LogEventRetriever uiLogger;
	
	public AddressGeneratorQueueProcessor(String supportedCoins, LogEventRetriever uiLogger) {
		super(AddressGeneratorQueueProcessor.class.getName());
		
		//init
		suppCoins = new ArrayList<CryptoCoin>();
		this.uiLogger = uiLogger;
		
		//decode supported coins list
		//each coin definition is split by | 
		String[] coins = supportedCoins.replaceAll("\\n", "").replaceAll("\\t", "").replaceAll(" ", "").trim().split("\\|");
		//create EC curve address factories
		keyGenerators = Collections.synchronizedMap(new HashMap<String, CryptoCoinECKeyGenerator>());

		for (int i=0;i<coins.length;i++) {
			//for each coin, split the coin parameters which are seprated by ,
			String[] coinParams = coins[i].split(",");
			//coinname,public_key_version_byte,private_key_version_byte
			String coinName = coinParams[0];
			byte pubkVer = (byte)(Integer.parseInt(coinParams[1], 16) & 0xFF);
			byte privkVer = (byte)(Integer.parseInt(coinParams[2], 16) & 0xFF);
			String curveName = coinParams[3];
			//check that curve exists
			if (SECNamedCurves.getByName(curveName) == null) {
				logger.error("Do not adding coin:"+coinName+" cannot recognise curve:"+curveName);
			} else{ 
				suppCoins.add(new CryptoCoin(privkVer, pubkVer, coinName, curveName));
				keyGenerators.put(curveName, new CryptoCoinECKeyGenerator(curveName));
			}

		}
		
	}

	/**
	 * This method processes requests sent to this processor. 
	 */
	public void processEvent(
			GenericProcessorRequest<AddressGeneratorRequestType> request)
			throws Exception {
		
		//see what type of request we have
		switch (request.getRequestType()) {
		case GET_SUPPORTED_COINS : {
			//request to send the supported coins
			logger.debug("handling request to report on supported coins");

			//cast to actual request type
			GetSupportedCoinsRequest getSupCoinsReq = (GetSupportedCoinsRequest) request;
			//reply with the coin list
			getSupCoinsReq.insertResponse(suppCoins);
			break;
		}
		
		case GENERATE_ADDRESSES : {
			//generate a number of addresses
			//cast to actual request type
			GetAddressesRequest getAddrReq = (GetAddressesRequest) request;
			int howMany = getAddrReq.getAddressNumber();
			String coinType = getAddrReq.getCoinType();
			//set params of the requested coin
			setCoin(coinType);
			
			logger.debug("handling request to generate "+howMany+" addresses for "+coinType);

			List<CoinAddress> ret = new ArrayList<CoinAddress>();
			for (int i=0;i<howMany;i++) {
				//create new address
				ret.add(generateAddress(currentlyCoifiguredCoin.getPubVersion(), 
										currentlyCoifiguredCoin.getPrivVersion()));
			}
			
			//return addresses
			getAddrReq.insertResponse(ret);
			break;

		}
		
		case GENERATE_SPECIFIC_ADDRESS : {
			//we want to generate a specific address that follows a pattern
			
			//cast
			GetSpecificAddressRequest getSpecAddrReq = (GetSpecificAddressRequest) request;
			
			logger.debug("handling request to generate specific address of pattern: "+getSpecAddrReq.getPattern());

			//validate reg exp
			String regExpPattern = getSpecAddrReq.getPattern();
			try {
	            Pattern.compile(regExpPattern);
	        } catch (PatternSyntaxException exception) {
	           uiLogger.logMessage("Cannot decode regular expression. Error:"+exception.getDescription());
	           //getSpecAddrReq.insertResponse(null);
	           return;
	        }
	        
			//set coin
			setCoin(getSpecAddrReq.getCoin());

	        //we have a valid regular expression
			//try to generate addresses until they match
			int howManyGenerated = 0;
			
			while(true) {
				howManyGenerated++;
				//generate until an address is found
				CoinAddress potentialMatch = generateAddress(currentlyCoifiguredCoin.getPubVersion(), 
						currentlyCoifiguredCoin.getPrivVersion());
				if (potentialMatch.getAddress().matches(regExpPattern)) {
					//we gounf it!
					getSpecAddrReq.insertResponse(potentialMatch);
					//return
					break;
				}
				if (howManyGenerated % 100 == 0) {
					//report every 100 generations to the user
					uiLogger.logMessage("Trying to find address. Tried "+howManyGenerated+" so far");
				}
			}
			break;

		}
		default : {
			throw new Exception("cannot handle request of type"+request.getRequestType().name());
		}
		}
		
	}

	/**
	 * reconfigures for a selected coin
	 * @param coinType the coin type
	 */
	private void setCoin(String coinType) {
	
		boolean mustChange = false;
		if (currentlyCoifiguredCoin == null) {
			//not yet initialized
			mustChange = true;
		} else {
			//check what was the previously cofigured coin
			if (!currentlyCoifiguredCoin.getName().equals(coinType)) {
				mustChange = true;
			}
		}
		
		logger.debug("setting coin to "+coinType+" Must change:"+mustChange);

		if (mustChange) {
			//we must reconfigure
			for (CryptoCoin supCoin : suppCoins) {
				if (supCoin.getName().equals(coinType)) {
					//set the coin to use
					currentlyCoifiguredCoin = supCoin;
					break;
				}
			}
			
			
		}
	}
	
	public CoinAddress generateAddress(byte addrVersionByte, byte privKeyVersionByte) {	
		
		logger.debug("generating address for"+currentlyCoifiguredCoin.getName());

		try {
			byte[][] keypair = null;

			//get the key pair from the correct generator
			keypair = keyGenerators.get(currentlyCoifiguredCoin.getAddressCurve()).getECKeyPair();
			
	        byte[] privateKey = keypair[0];
	        byte[] publicKeyX = keypair[1];
	        byte[] publicKeyY = keypair[2];
	        
	        byte[] walletAdress = getCoinAddress(publicKeyX, publicKeyY, addrVersionByte);
	        
	    	byte[] privateKeyWIF = getPrivateKeyWIF(privateKey, privKeyVersionByte);
	    	
	        return new CoinAddress(privateKeyWIF, walletAdress);
	        
		}catch (Exception e) {
			logger.log(Level.ERROR, "Error during generation of Crypto coin address", e);
		}
		
		return null;  		
	}
	

	/**
	 * returns an  Address in raw format (byte[])
	 * @param pointX the EC X point of the public key
	 * @param pointY the EC Y point of the public key
	 * @return BTC raw address
	 */
	private byte[] getCoinAddress(byte[] pointX, byte[] pointY, byte versionByte) throws Exception{    

        //create the 65 byte 0x04 + public X + public Y
		byte[] start = new byte[] {0x04};
        byte[] pk_transformation1 = ByteArrayUtil.serialAdd (
        			ByteArrayUtil.serialAdd(start, pointX), pointY);
        
        //SHA-256 the 65 bytes
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(pk_transformation1);
        byte[] pk_transformation2 = md.digest();
        
        //RIPEMD-160 the result
        MessageDigest md2 = MessageDigest.getInstance("RIPEMD160");
        md2.update(pk_transformation2);
        byte[] pk_transformation3 = md2.digest();

        //add version byte in front
        byte[] pk_transformation4 = ByteArrayUtil.serialAdd(new byte[] {versionByte}, pk_transformation3);
        
        //SHA-256 the result
        md.update(pk_transformation4);
        byte[] pk_transformation5 = md.digest();
        
        //SHA-256 the result
        md.update(pk_transformation5);
        byte[] pk_transformation6 = md.digest();
        
        //get first 4 bytes
        byte[] checksum = ByteArrayUtil.getSubset(pk_transformation6, 0, 3);
        
        //create final address in bytes
        byte[] finalAddress = ByteArrayUtil.serialAdd(pk_transformation4, checksum);

        return finalAddress;		
	}
	
	/**
	 * returns the private key in the WIF format in raw bytes
	 * @param privateKey
	 * @return private key in WIF
	 * @throws Exception 
	 */
	private byte[] getPrivateKeyWIF(byte[] privateKey, byte versionByte) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");		
        //add version in front
        byte[] privRound1 = ByteArrayUtil.serialAdd(new byte[]{(byte) versionByte}, privateKey);

        //SHA-256 the result
        md.update(privRound1);
        byte[] privRound2 = md.digest();

        //SHA-256 the result
        md.update(privRound2);
        byte[] privRound3 = md.digest();

        //append checksum to initial version and get final key
        byte[] privChecksum = ByteArrayUtil.getSubset(privRound3, 0, 3);
        byte[] finalKeyWIF = ByteArrayUtil.serialAdd(privRound1, privChecksum);

    	return finalKeyWIF;
	}


	
}
