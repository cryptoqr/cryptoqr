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
import java.io.UnsupportedEncodingException;


public class ByteArrayUtil {

    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

	/**
	 * returns a sub array from position start included to end included
	 * @param array the initial array
	 * @param start start position
	 * @param end end position
	 * @return the sub array
	 */
	public static byte[] getSubset(byte[] array, int start, int end) {
		
		int size = end - start + 1;
		byte[] ret = new byte[size];
		
		for (int i=0;i<size;i++) {
			ret[i] = array[start+i];
		}
		
		return ret;
	}
	
	/**
	 * serial combine 2 byte arrays. The return is the concatenation of the 2 arrays
	 * @param first the first array
	 * @param second the second array.
	 * @return the concatenation of the arrays : first[0] , ..., first[n], second[0], ... , second[y] 
	 */
	public static byte[] serialAdd(byte[] first, byte[] second) {
		int size = first.length + second.length;
		byte[] ret = new byte[size];
		
		int posCounter = 0;
		for (int i=0;i<first.length;i++) {
			ret[posCounter++] = first[i];
		}
		for (int i=0;i<second.length;i++) {
			ret[posCounter++] = second[i];
		}
		return ret;
	}
	
	/**
	 * returns a string representing the byte array in the specified format format
	 * @param array the array
	 * @return the string representation
	 */
	public static String getAsHex(byte[] array) {
	    byte[] bytes = array;
	    StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X", b));
	    }	
	   
		return sb.toString();
	 }


    /** Encodes the given bytes in base58. No checksum is appended. */
    public static String encodeBase58(byte[] input) {
        if (input.length == 0) {
            return "";
        }      
        input = copyOfRange(input, 0, input.length);
        // Count leading zeroes.
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }
        // The actual encoding.
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divmod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = (byte) ALPHABET[mod];
        }

        // Strip extra '1' if there are some after decoding.
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }
        // Add as many leading '1' as there were leading zeros.
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = copyOfRange(temp, j, temp.length);
        try {
            return new String(output, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }

   
    //
    // number -> number / 58, returns number % 58
    //
    private static byte divmod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * 256 + digit256;

            number[i] = (byte) (temp / 58);

            remainder = temp % 58;
        }

        return (byte) remainder;
    }



    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }	
}
