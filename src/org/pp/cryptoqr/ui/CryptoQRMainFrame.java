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
package org.pp.cryptoqr.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.pp.cryptoqr.component.interfaces.AddressGenerator;
import org.pp.cryptoqr.component.interfaces.LogEventRetriever;
import org.pp.cryptoqr.component.interfaces.QRImageProcessor;
import org.pp.cryptoqr.component.interfaces.QRPrinter;
import org.pp.cryptoqr.component.types.CoinAddress;
import org.pp.cryptoqr.component.types.CryptoCoin;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class CryptoQRMainFrame extends JFrame implements LogEventRetriever{

	private Logger logger = Logger.getLogger(getClass());
	private JPanel contentPane;
	private JTextField patternTextField;
	private WebcamPanel webcamPanel;
	private JButton btnGenerate;
	private JButton btnGenAddr;
	private JTextArea outputTextArea;
	private JButton btnPrintLastGenerated;
	private JComboBox<CryptoCoin> coinComboBox;
	private Webcam selectedWebcam;
	
	private final AddressGenerator addressGenerator;
	private final QRPrinter qrPrinter;
	private final  QRImageProcessor qrImageProcessor;
	
	private final List<CoinAddress> lastGeneratedAddresses;
	
	private final WebcamImageAnalyzerThread webcamThread;
	private JCheckBox chckbxAutoPasteQr;
	/**
	 * Create the frame.
	 * @param qrImageProcessor 
	 * @param qrPrinter 
	 * @param addressGenerator 
	 */
	public CryptoQRMainFrame(AddressGenerator addressGenerator, QRPrinter qrPrinter, QRImageProcessor qrImageProcessor) {
		setResizable(false);
		//keep references to components
		this.addressGenerator = addressGenerator;
		this.qrImageProcessor = qrImageProcessor;
		this.qrPrinter = qrPrinter;
		
		lastGeneratedAddresses = new ArrayList<CoinAddress>();
		//init UI
		setIconImage(Toolkit.getDefaultToolkit().getImage(CryptoQRMainFrame.class.getResource("/com/github/sarxos/webcam/icons/camera-icon.png")));
		setTitle("CryptoQR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//make user choose webcam
		selectedWebcam = WebcamChooserWindow.getImagingDevice();

		if (selectedWebcam != null) {
			Dimension size = WebcamResolution.QVGA.getSize();
			selectedWebcam.setViewSize(size);
		}
		webcamPanel = new WebcamPanel(selectedWebcam);
		webcamPanel.setBounds(10, 11, 320, 240);
		contentPane.add(webcamPanel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 293, 572, 137);
		contentPane.add(scrollPane);
		
		outputTextArea = new JTextArea();
		outputTextArea.setToolTipText("Enter regular expression. Be careful, the more constrained the request is the more time it will take");
		scrollPane.setViewportView(outputTextArea);
		
		patternTextField = new JTextField();
		patternTextField.setToolTipText("Enter Regular Expression. Be careful this might take some time");
		patternTextField.setBounds(378, 139, 201, 20);
		contentPane.add(patternTextField);
		patternTextField.setColumns(10);
		
		JLabel lblAddressPattern = new JLabel("Address Pattern");
		lblAddressPattern.setBounds(378, 114, 201, 14);
		contentPane.add(lblAddressPattern);
		
		btnGenerate = new JButton("Generate Specific Address");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//create swing worker that will execute the command
				String regExpPattern = patternTextField.getText();
				try {
		            Pattern.compile(regExpPattern);
		        } catch (PatternSyntaxException exception) {
		           logMessage("Cannot decode regular expression. Error:"+exception.getDescription());
		           return;
		        }
				
				if (regExpPattern.trim().equals("")) {
					return;
				}
				GenerateSpecificAddressesWorker worker 
					= new GenerateSpecificAddressesWorker(CryptoQRMainFrame.this.addressGenerator, coinComboBox.getSelectedItem().toString(), patternTextField.getText(), CryptoQRMainFrame.this);
				worker.execute();			
			}
		});
		btnGenerate.setBounds(378, 171, 204, 23);
		contentPane.add(btnGenerate);
		
		coinComboBox = new JComboBox<CryptoCoin>();
		coinComboBox.setBounds(380, 36, 202, 20);
		contentPane.add(coinComboBox);
		
		JLabel lblChooseCoin = new JLabel("Choose Coin");
		lblChooseCoin.setBounds(378, 11, 201, 14);
		contentPane.add(lblChooseCoin);
		
		btnGenAddr = new JButton("Generate 5 Addresses");
		btnGenAddr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//create swing worker that will execute the command
				GenerateAddressesWorker worker 
					= new GenerateAddressesWorker(CryptoQRMainFrame.this.addressGenerator, coinComboBox.getSelectedItem().toString(), 5, CryptoQRMainFrame.this);
				worker.execute();
			}
		});

		btnGenAddr.setBounds(378, 67, 201, 23);
		contentPane.add(btnGenAddr);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(381, 101, 201, 2);
		contentPane.add(separator_1);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(10, 268, 148, 14);
		contentPane.add(lblOutput);
		
		btnPrintLastGenerated = new JButton("Print Last Generated");
		btnPrintLastGenerated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//create swing worker that will execute the command
				PrintAddressesWorker worker 
					= new PrintAddressesWorker(CryptoQRMainFrame.this.qrPrinter, lastGeneratedAddresses, CryptoQRMainFrame.this);
				worker.execute();				
			}
		});
		btnPrintLastGenerated.setBounds(400, 259, 182, 23);
		contentPane.add(btnPrintLastGenerated);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputTextArea.setText("");
			}
		});
		btnClear.setBounds(10, 441, 89, 23);
		contentPane.add(btnClear);
		
		chckbxAutoPasteQr = new JCheckBox("auto paste QR code");
		chckbxAutoPasteQr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//state chnged
				if (chckbxAutoPasteQr.isSelected()) {
					webcamThread.setAutoPaste(true);
				}else {
					webcamThread.setAutoPaste(false);
				}
			}
		});
		chckbxAutoPasteQr.setBounds(201, 263, 182, 23);
		contentPane.add(chckbxAutoPasteQr);
		
		//create the webcam monitoring thread
		webcamThread = new WebcamImageAnalyzerThread(CryptoQRMainFrame.this.selectedWebcam, CryptoQRMainFrame.this.qrImageProcessor, CryptoQRMainFrame.this);
	}

	
	public void initialize() {
		//extra initialization
		//get supported coins
		
		List<CryptoCoin> supportedCoins = addressGenerator.getSupportedCoins();
		for (CryptoCoin supportedCoin : supportedCoins) {
			coinComboBox.addItem(supportedCoin);
		}
		
		//start the webcam image analyzer thread
		webcamThread.start();
	}
	
	public void logMessage(final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				outputTextArea.append(message+"\n");
			}
		});
	}
	
	/**
	 * sets the last generated address(es).
	 * This is typically called by workers that perform some task i the background and then
	 * communicate to pass the results.
	 * @param addresses
	 */
	public void setLastGenerated(List<CoinAddress> addresses) {
		lastGeneratedAddresses.clear();
		lastGeneratedAddresses.addAll(addresses);
	}
	
	/**
	 * This sets the window in processing mode or not.
	 * It enables / disables window controls depending on the mode.
	 * This is typically called by workers that perform a background activity
	 * and we do not want the user to do something else while the activity is processed.
	 * 
	 * @param processing if the frame is in processing mode or not
	 */
	public void setProcessing(Boolean processing) {
		if (processing) {
			//disable controls
			this.btnGenAddr.setEnabled(false);
			this.btnGenerate.setEnabled(false);
			this.btnPrintLastGenerated.setEnabled(false);

			//suspend the processing on the webcam processing thread
			webcamThread.suspendAnalysis();

		}else {
			//enable the controls
			this.btnGenAddr.setEnabled(true);
			this.btnGenerate.setEnabled(true);
			this.btnPrintLastGenerated.setEnabled(true);

			//resume the processing on the webcam processing thread
			webcamThread.resumeAnalysis();
			
		}
	}
}
