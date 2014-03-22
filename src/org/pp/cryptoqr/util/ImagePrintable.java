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
 * Copied from 
 * http://stackoverflow.com/questions/18404553/proper-way-of-printing-a-bufferedimage-in-java
 *  answered Aug 23 '13 at 17:27 Gilbert Le Blanc
 */
package org.pp.cryptoqr.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class ImagePrintable implements Printable {

    private double          x, y, width;

    private int             orientation;

    private BufferedImage   image;

    public ImagePrintable(PrinterJob printJob, BufferedImage image) {
        PageFormat pageFormat = printJob.defaultPage();
        this.x = pageFormat.getImageableX();
        this.y = pageFormat.getImageableY();
        this.width = pageFormat.getImageableWidth();
        this.orientation = pageFormat.getOrientation();
        this.image = image;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        if (pageIndex == 0) {
            int pWidth = 0;
            int pHeight = 0;
            if (orientation == PageFormat.PORTRAIT) {
                pWidth = (int) Math.min(width, (double) image.getWidth());
                pHeight = pWidth * image.getHeight() / image.getWidth();
            } else {
                pHeight = (int) Math.min(width, (double) image.getHeight());
                pWidth = pHeight * image.getWidth() / image.getHeight();
            }
            g.drawImage(image, (int) x, (int) y, pWidth, pHeight, null);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }



}
