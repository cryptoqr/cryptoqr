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

import org.pp.cryptoqr.component.interfaces.QRImageProcessor;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;

/**
 * This class implements the QR Imae Processor
 * component. It provides an interface in order to scan
 * an image and return the QR code in String format.
 * @author CryptoQR 
 *
 */
public class QRImageProcessorComponent implements QRImageProcessor{

	public QRImageProcessorComponent() {
		
	}
	
	public String decodeQRImage(BinaryBitmap bitmap) {
		try {
			Result result = new MultiFormatReader().decode(bitmap);
			String qrText = result.getText();
			return qrText;
		} catch (NotFoundException e) {
			// fall thru, it means there is no QR code in image
			return null;
		}
	}

}
