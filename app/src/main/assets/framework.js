//eth
function init() {
    web3 = new Web3();
    web3.setProvider(new web3.providers.HttpProvider("https://api.myetherapi.com/eth"));
}
function version() {
    alert(web3.version);
}

//wallet
function createWallet(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var numberOfAccounts = paramsjson.numberOfAccounts;
    var entropy = paramsjson.entropy;
    var wallet = web3.eth.accounts.wallet.create(numberOfAccounts, entropy);
    var accountInfo = new Object();
    accountInfo.privatekey = wallet[0].privateKey;
    accountInfo.address = wallet[0].address;
    notifyClient(callid, 0, accountInfo);
}

function createWalletWithWord(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var mnemonic = bip39.generateMnemonic();
    var seed = bip39.mnemonicToSeed(mnemonic);
    var hdWallet = hdkey.fromMasterSeed(seed);
    var key1 = hdWallet.derivePath("m/44'/60'/0'/0/0");
    var accountInfo = new Object();
    accountInfo.blockType = paramsjson.blockType;
    accountInfo.privatekey = key1.getWallet().getPrivateKeyString();
    accountInfo.address = key1.getWallet().getAddressString();
    accountInfo.words = mnemonic;
    notifyClient(callid, 0, accountInfo);
}

function importWalletWithPK(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privatekey = paramsjson.privateKey;
    var account = web3.eth.accounts.privateKeyToAccount(privatekey);
    account.blockType = 1;
    notifyClient(callid, 0, account);
}
function importWalletWithWords(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var words = paramsjson.words;
    var seed = bip39.mnemonicToSeed(words);
    var hdWallet = hdkey.fromMasterSeed(seed);
    var key1 = hdWallet.derivePath("m/44'/60'/0'/0/0");
    var accountInfo = new Object();
    accountInfo.blockType = 1;
    accountInfo.privateKey = key1.getWallet().getPrivateKeyString();
    accountInfo.address = key1.getWallet().getAddressString();
    notifyClient(callid, 0, accountInfo);
}

function accountSignTransaction(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privateKey = paramsjson.privateKey;
    var transactionToSign = paramsjson.transactionToSign;
    if (transactionToSign.abi == null || transactionToSign.abi == undefined) {
        web3.eth.accounts.signTransaction(transactionToSign, privateKey).then(function (result) {
            var callbackData = new Object();
            callbackData.signedTransaction = result;
            notifyClient(callid, 0, callbackData)
        });

    } else {
        var abi = transactionToSign.abi;
        var contractAddress = transactionToSign.to;
        var from = transactionToSign.from;
        var value = transactionToSign.value;
        var toAddress = transactionToSign.toAddress;
        var gasLimit = transactionToSign.gas;
        var gasPrice = transactionToSign.gasPrice;
        console.log('gasPrice:' + gasPrice);
        console.log('gasLimit:' + gasLimit);
        var contract = new web3.eth.Contract(abi, contractAddress, {from: from});
        web3.eth.getTransactionCount(from).then(function (result) {
            var rawTransaction = {
                "from": from,
                "gasPrice": web3.utils.toHex(gasPrice),
                "gasLimit": web3.utils.toHex(gasLimit),
                "to": contractAddress,
                "data": contract.methods.transfer(toAddress, value).encodeABI(),
                "nonce": web3.utils.toHex(result)
            };
            web3.eth.accounts.signTransaction(rawTransaction, privateKey).then(function (result) {
                var callbackData = new Object();
                callbackData.signedTransaction = result;
                notifyClient(callid, 0, callbackData);
            });
        });
    }
}

function sendTransaction(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;

    var rawTransaction = paramsjson.rawTransaction; //由signedTransaction产生

    web3.eth.sendSignedTransaction(rawTransaction)
        .on('receipt', function (receipt) {
            var callbackData = new Object();
            callbackData.receipt = receipt;
            notifyClient(callid, 0, callbackData)
        })
        .on('error', function (error) {
            console.log('转账失败:' + error.message)
            var callbackData = new Object();
            callbackData.error = error;
            notifyClient(callid, -1, callbackData)
        });
}

function toIbanAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var ethAddress = paramsjson.ethAddress;
    var ibanAddress = web3.eth.Iban.toIban(ethAddress);
    var callbackData = new Object();
    callbackData.ibanAddress = ibanAddress;
    notifyClient(callid, 0, callbackData);
}
function toEthAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var ibanAddress = paramsjson.ibanAddress;
    var ethAddress = web3.eth.Iban.toAddress(ibanAddress);
    var callbackData = new Object();
    callbackData.ethAddress = ethAddress;
    notifyClient(callid, 0, callbackData);
}
function getGasPrice(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    web3.eth.getGasPrice().then(function (gasPrice) {
        notifyClient(callid, 0, gasPrice)
    });
}

function getBalance(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    web3.eth.getBalance(paramsjson.address).then(function(balance) {
        var obj = {};
        obj.balance = balance
        notifyClient(callid, 0, obj);
    })
}

//end eth

//jt
function createJtWallet(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var wallet = jingtum.Wallet.generate();
    var accountInfo = new Object();
    accountInfo.blockType = paramsjson.blockType;
    accountInfo.privatekey = wallet.secret;
    accountInfo.address = wallet.address;
    notifyClient(callid, 0, accountInfo);
}

function retrieveWalletFromPk(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privateKey = paramsjson.privateKey;
    var wallet = jingtum.Wallet.fromSecret(privateKey);
    var accountInfo = new Object();
    accountInfo.blockType = paramsjson.blockType;
    accountInfo.privatekey = wallet.secret;
    accountInfo.address = wallet.address;
    notifyClient(callid, 0, accountInfo);
}

function isValidAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var address = paramsjson.address;
    var isAddressValid = jingtum.Wallet.isValidAddress(data);
    var checkAddress = new Object();
    checkAddress.isAddressValid = isAddressValid;
    checkAddress.address = wallet.address;
    notifyClient(callid, 0, checkAddress);
}

function jtSingtx(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var tx = {};
    tx.Fee = paramsjson.fee;
    tx.Flags = 0;
    tx.Sequence = paramsjson.sequence;//通过balance接口拿到这个值
    tx.TransactionType = 'Payment';
    tx.Account = paramsjson.account;
    tx.Amount = {"value": paramsjson.value, "currency": paramsjson.currency, "issuer": paramsjson.issuer};
    tx.Destination = paramsjson.destination;
    var sign = jingtumLib.jingtum_sign_tx(tx, {"seed": paramsjson.seed});
    var callbackData = new Object();
    var signedTransaction = new Object();
    signedTransaction.rawTransaction = sign;
    callbackData.signedTransaction = signedTransaction;
    notifyClient(callid, 0, callbackData);
}

//end jt

// moac

function createMoacWallet(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    Moac.createWallet(uuidv4(), uuidv4()).then(function(wallet) {
        var accountInfo = {};
        accountInfo.privatekey = wallet.secret;
        accountInfo.address = wallet.address;
        accountInfo.blockType = paramsjson.blockType;
        notifyClient(callid, 0, accountInfo);
    }).catch(function(error) {
       notifyClient(callid, -1, null);
    })
}

function importMoacSecret(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var secret = paramsjson.privateKey;
    var address = Moac.getAddress(secret);
    if (address === null) {
       notifyClient(callid, -1, null);
    } else {
       notifyClient(callid, 0, {
          address: address,
          secret: secret,
          blockType: paramsjson.blockType
       })
    }
}

function getMoacBalance(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    moacInstance.getBalance(paramsjson.address).then(function(balance) {
        var obj = {};
        obj.balance = balance + "";
        notifyClient(callid, 0, obj);
    })
}

function getMoacGasPrice(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    moacInstance.getGasPrice().then(function(gas) {
        var obj = {
           gasPrice: gas
        };
        notifyClient(callid, 0, obj);
    })
}

function signMoacTransaction(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privateKey = paramsjson.privateKey;
    var transactionToSign = paramsjson.transactionToSign;
    if (transactionToSign.abi == null || transactionToSign.abi == undefined) {
        var to = transactionToSign.to;
        var from = transactionToSign.from;
        var value = transactionToSign.value;
        var gasLimit = transactionToSign.gas;
        var gasPrice = transactionToSign.gasPrice;
        moacInstance.getNonce(from).then(function(nonce) {
           var tx = moacInstance.getTx(from, to, nonce, gasLimit, gasPrice, value);
           var signedTransaction = moacInstance._chain3.signTransaction(tx, privateKey);
           var callbackData = {
              signedTransaction: {
                 rawTransaction: signedTransaction
              }
           };
           notifyClient(callid, 0, callbackData)
        })
    }
}

function sendMoacTransaction(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;

    var rawTransaction = paramsjson.rawTransaction; //由signedTransaction产生

    moacInstance.sendRawSignedTransaction(rawTransaction).then(function(hash) {
       var callbackData = new Object();
       callbackData.receipt = hash;
       notifyClient(callid, 0, callbackData)
    }).catch(function(error) {
       console.log("墨客转账错误:"+error.message);
       var callbackData = new Object();
       callbackData.error = error;
       notifyClient(callid, -1, callbackData)
    })
}

function toIbanMoacAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var address = paramsjson.moacAddress;
    var ibanAddress = moacInstance._chain3.mc.iban.fromAddress(address)
    var callbackData = new Object();
    callbackData.ibanAddress = ibanAddress.toString();
    notifyClient(callid, 0, callbackData);
}

function toMoacAddress(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var ibanAddress = paramsjson.ibanAddress;
    var moacAddress = moacInstance._chain3.mc.iban.fromBban(ibanAddress);
    var callbackData = new Object();
    callbackData.moacAddress = moacAddress.toString();
    notifyClient(callid, 0, callbackData);
}


// end moac

// eos

function importEosByPK(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var wif = paramsjson.privateKey;
    var publicKey = ecc.privateToPublic(wif);
    if (publicKey === null) {
       notifyClient(callid, -1, null);
    } else {
       var accountInfo = new Object();
       accountInfo.blockType = paramsjson.blockType;
       accountInfo.privatekey = wif;
       eos.getKeyAccounts(publicKey)
           .then(result =>  {
           accountInfo.address = result.account_names[0];
            notifyClient(callid, 0,accountInfo);
           })
           .catch(error => console.error(error));


    }
}

function getEosBalance(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    eos.getCurrencyBalance({
        account: paramsjson.account,
        code: 'eosio.token',
        symbol: 'EOS'
    }).then(result => {
     console.log('eos balance:'+JSON.stringify(result));
     var obj = {};
     obj.balance = result[0].split(" ")[0] + "";
      notifyClient(callid, 0, obj);
    })
    .catch(error => console.error(error));
}
// end eos


function notifyClient(callid, ret, extra) {
    var result = new Object();
    result.ret = ret;
    result.callid = callid;
    result.extra = extra;
    var resultStr = toJsonString(result);
    window.client.notifyWeb3Result(resultStr);
}


function toJsonString(obj) {
    if (obj == undefined) {
        return "{}";
    } else {
        return JSON.stringify(obj);
    }
}

function outputObj(obj) {
    var description = "";
    for (var i in obj) {
        description += i + " = " + (obj[i]) + "\n";
    }
    console.log(description);
}
function printAccount(account) {
    var description = "";
    for (var i in account) {
        description += i + " = " + account[i] + "\n";
    }
    console.log(description);
}

function testJson() {
    var result = new Object();
    result.ret = -1;
    alert(toJsonString(result));
}

async function eosSignTransaction(params) {
    var paramsjson = JSON.parse(params);
    var callid = paramsjson.callid;
    var privateKey = paramsjson.privateKey;

    var teos = Eos({
          keyProvider: privateKey,
           httpEndpoint: eosHttpEndpoint,
           chainId:  eosChainId,
           broadcast: true,
           debug: false,
           sign: true
      });

  teos.transaction({
     actions: [
       {
         account: paramsjson.contract,
         name: 'transfer',
         authorization: [
           {
             actor: paramsjson.from,
             permission: 'active'
           }
         ],
         data: {
           from: paramsjson.from,
           to: paramsjson.to,
           quantity: paramsjson.value,
           memo: paramsjson.memo
         }
       }
     ]
   }).then(result => {
           console.log("eos result:"+result.transaction_id);
           var callbackData = new Object();
           callbackData.receipt = result.transaction_id;
           notifyClient(callid, 0, callbackData);
         })
         .catch(error => console.error(error));


}


