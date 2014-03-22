/**
 * Copyright 2013 CryptoQR.
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

/**
 * The parent of all requests sent to as Queue Processor.
 * This is a generic processor request class that can also be used in other contexts
 * where the Abstract Queue Processor is used.
 * 
 * This class assumes a pattern where actual requests extend the GenericProcessorRequest
 * and pass the specific type of the request as an enumeration.
 * 
 * @param <T> the type of the request type, typically an enumeration.
 * @author CryptoQR
 *
 */

public abstract class GenericProcessorRequest <T>{
	
	private final T requestType;
	
	public GenericProcessorRequest(T rType) {
		requestType = rType;
	}

	public T getRequestType() {
		return requestType;
	}
}
