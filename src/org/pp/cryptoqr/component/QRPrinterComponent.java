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
package org.pp.cryptoqr.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pp.cryptoqr.component.interfaces.QRPrinter;
import org.pp.cryptoqr.component.types.CoinAddress;
import org.pp.cryptoqr.util.ImagePrintable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


/**
 * Component that prints QR addresses to the printer
 * @author CryptoQR 
 *
 */
public class QRPrinterComponent implements QRPrinter {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	public QRPrinterComponent() {
		
	}
	
	public void printAddresses(List<CoinAddress> addresses) {

		if (addresses.size() > 5) {
			//multi page print not yet supported
			return;
		} else if (addresses.size() == 0){
			//empty
			return;
		} else {
			//OK go ahead with the printing
			BufferedImage combined = new BufferedImage(600, 1200, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = combined.createGraphics();
			graphics.setColor(Color.BLACK);
			int headerOffset = 50;
			graphics.drawString("PRIVATE KEY", 0, headerOffset-10);
			graphics.drawString("PUBLIC KEY", 300, headerOffset-10);


			for (int i=0;i<addresses.size();i++) {
				CoinAddress addr = addresses.get(i);
				
				try {
		        	BitMatrix bitMatrix = new MultiFormatWriter().encode(addr.getAddress(),BarcodeFormat.QR_CODE,150,150,null);
		            BufferedImage addrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
		        	BitMatrix bitMatrixPriv = new MultiFormatWriter().encode(addr.getPrivateKey(),BarcodeFormat.QR_CODE,150,150,null);
		            BufferedImage privKeyImage = MatrixToImageWriter.toBufferedImage(bitMatrixPriv);

		            //add images to global image
		            combined.getGraphics().drawImage(privKeyImage, 0, headerOffset+i*180, null);
		            combined.getGraphics().drawImage(addrImage, 300, headerOffset+i*180, null);

		        }catch (Exception e) {
		        	logger.log(Level.ERROR, "Cannot generate QR code", e);
		        }
			}

	        //print  
	        PrinterJob printJob = PrinterJob.getPrinterJob();
	        printJob.setPrintable(new ImagePrintable(printJob, combined));

	        if (printJob.printDialog()) {
	            try {
	                printJob.print();
	            } catch (PrinterException prt) {
	                prt.printStackTrace();
	            }
	        }
			
		}
	}

}
