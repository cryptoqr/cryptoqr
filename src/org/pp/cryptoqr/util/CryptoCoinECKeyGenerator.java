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
package org.pp.cryptoqr.util;

import java.security.SecureRandom;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/**
 * this class sets up the EC crypto infrastructure
 * and then generates key pairs when requested.
 * 
 * @author CryptoQR 
 *
 */
public class CryptoCoinECKeyGenerator {

	private final Logger logger = Logger.getLogger(this.getClass());

	X9ECParameters params;
	ECDomainParameters ecParams; 
	ECKeyPairGenerator generator;
	SecureRandom rand = new SecureRandom(); 
	
	public CryptoCoinECKeyGenerator(String curve)  {
		//setup the EC generation parameters
		params = SECNamedCurves.getByName(curve);
		ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(),  params.getH());
		generator = new ECKeyPairGenerator();
		ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(ecParams, rand);
		generator.init(keygenParams);
	}
	
	/**
	 * Generates returns the public and private keys of a specific EC curve (secp256k1). 
	 * @return 3 32 bit arrays. { private key, public key X, public key Y }
	 */
	public byte[][] getECKeyPair() {
		byte[][] ret = new byte[3][];

		//generate key pair
		AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();

        //get private key
        byte[] priv = privParams.getD().toByteArray();
        
        // The public key is an encoded point on the elliptic curve. It has no meaning independent of the curve.
        byte[] pub = pubParams.getQ().getEncoded();
        

        if (priv[0] == 0x00 && priv.length == 33) {
        	//sometimes the private key is 33 bytes with the first byte = 0x00...
        	priv = ByteArrayUtil.getSubset(priv, 1, 32);
        }
        
        //do a sanity check
        /*
        if( priv.length != 32 ) {
            //sometimes the private key is 33 bytes with the first byte being 00. 
            //not sure what is this...?
        	//for safety re-run. since I don't know more details.
        	logger.info("Re-runnin key generation, priv key length:"+priv.length+" pub key length:"+pub.length);
        	return getECKeyPair();
        }*/

        byte[] publicKeyX = ByteArrayUtil.getSubset(pub, 1, 1+32-1);
        byte[] publicKeyY = ByteArrayUtil.getSubset(pub, 33, 33+32-1);
		
        ret[0] = priv;
        ret[1] = publicKeyX;
        ret[2] = publicKeyY;

        return ret;
	}	
	
}
