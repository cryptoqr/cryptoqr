cryptoqr
========

CryptoQR Application.

- create offline wallet addresses for various crypto coins
- print the wallet addresses in QR format
- decode printed QR images via a webcam

Compiling
=========
To compile you will need:
- JDK 1.7 or higher
- Ant version 1.8 or higher

Download the source code ant type

ant -f buildProject.xml

this command will:
- compile
- create javadocs
- create the runtime distribution .zip file in the folder dist

Running
=======
To run the application you will need
- JDK 1.7

unzip the distribution and double click the .bat or .sh file to start the application.

Configuring different crypto coins
===========================
in folder resources edit the file
cryptoqr.properties

this file contains the necessary information to create addresses for various
crypto coins and is configurable.

each coin configuration is separated with the "|" character and each coin
parameter within the coin configuration is separated with the "," character.
The format for the coin parameters is:
<coin name>,<coin public key version>,<coin private key version>,<coin ecliptic curve>

For example :
BTC,00,80,secp256k1|LTC,30,B0,secp256k1

will configure 2 coins:
BTC 
public key version number = 0x00
private key version number = 0x80
EC curve = secp256k1

LTC
public key version number = 0x30
private key version number = 0xB0
EC curve = secp256k1

Important note:
the addresses generated follow the bitcoin algorithm.
https://en.bitcoin.it/wiki/Technical_background_of_Bitcoin_addresses

The coin EC curve should be by default secp256k1 as this is the curve
that (all?) coins following the bitcoin address generation
algotithm follow. However the software is configurable for potential
future coins that will use different EC curves.
