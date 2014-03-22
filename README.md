cryptoqr
========

CryptoQR Application.

- create offline wallet addresses for various crypto coins
- print the wallet addresses in QR format
- decode printed QR images via a webcam

Using the software
==================
Initially you select the webcam to be used for decoding QR codes.
From the selecton box you choose the crypto coin to be used.
Then you have the following options
- generate 5 addresses --> this will generate 5 new wallet addresses. 
- generate specific address --> this will try and generate an address containing a pattern.
The pattern follows regular expressions (see http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html).
for example the pattern.
.*BTC.* will generate an address containing the characters BTC somewhere in the address and
5BTC.* will generate an address starting with 5BTC .

After generating addresses, you can click the "Print Last Generated" button which
will print directly to the printer the last generated address(es). 

decoding QR codes
-----------------
At any time, show a QR code to the webcam and it will decode it. 
It will keep it in the clipboard so that you can paste it and
optionally it will automatically paste it for you in the focused window if you have
selected the "auto past QR code" checkbox. This is useful if you 
are in the wallet debug console as it will automatically paste the private
key for you in order not to lose time. 

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
- public key version number = 0x00
- private key version number = 0x80
- EC curve = secp256k1

LTC
- public key version number = 0x30
- private key version number = 0xB0
- EC curve = secp256k1

You can find these parameters in the source code of
each coin, usually in file 
Base58.h 
- class CBase58Data , PUBKEY_ADDRESS
- class CBitcoinSecret , PRIVKEY_ADDRESS
or 

chainparams.cpp
- base58Prefixes[PUBKEY_ADDRESS]
- base58Prefixes[SECRET_KEY]

Important note:
the addresses generated follow the bitcoin algorithm.
https://en.bitcoin.it/wiki/Technical_background_of_Bitcoin_addresses

The coin EC curve should be by default secp256k1 as this is the curve
that (all?) coins following the bitcoin address generation
algotithm follow. However the software is configurable for potential
future coins that will use different EC curves.

