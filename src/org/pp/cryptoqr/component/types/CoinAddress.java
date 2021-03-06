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
package org.pp.cryptoqr.component.types;

import org.pp.cryptoqr.util.ByteArrayUtil;

/**
 * Representa a coin address.
 * 
 * @author CryptoQR 
 *
 */
public class CoinAddress {

	private final byte[] privateKey;
		
	private final byte[] address;
	
	public CoinAddress(byte[] privKey,  byte[] addr) {
		this.privateKey = privKey;
		this.address = addr;
	}
	
	public String getPrivateKey() {
		return ByteArrayUtil.encodeBase58(privateKey);
	}

	public String getAddress() {
		return ByteArrayUtil.encodeBase58(address);
	}

	
}
