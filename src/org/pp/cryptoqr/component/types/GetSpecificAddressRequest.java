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
/**
 * 
 */
package org.pp.cryptoqr.component.types;

import org.pp.cryptoqr.util.GenericRequestReplyReq;


/**
 * @author CryptoQR 
 *
 */
public class GetSpecificAddressRequest extends GenericRequestReplyReq<CoinAddress, 
											AddressGeneratorRequestType> {

	private final String coin;
	
	private final String pattern;
	
	public GetSpecificAddressRequest( String pattern, String coin) {
		super(AddressGeneratorRequestType.GENERATE_SPECIFIC_ADDRESS);
		this.coin = coin;
		this.pattern = pattern;
	}

	public String getCoin() {
		return coin;
	}

	public String getPattern() {
		return pattern;
	}
	
	
	

}
