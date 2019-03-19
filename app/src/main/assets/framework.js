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
        accountInfo.privatekey = wif;
        eos.getKeyAccounts(publicKey)
            .then(result => {
                accountInfo.address = result.account_names[0];
                notifyClient(callid, 0, accountInfo);
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
            console.log('eos balance:' + JSON.stringify(result));
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
        chainId: eosChainId,
        broadcast: true,
        debug: false,
        sign: true
    });

    teos.transaction({
            actions: [{
                account: paramsjson.contract,
                name: 'transfer',
                authorization: [{
                    actor: paramsjson.from,
                    permission: 'active'
                }],
                data: {
                    from: paramsjson.from,
                    to: paramsjson.to,
                    quantity: paramsjson.value,
                    memo: paramsjson.memo
                }
            }]
        }).then(result => {
            console.log("eos result:" + result.transaction_id);
            var callbackData = new Object();
            callbackData.receipt = result.transaction_id;
            notifyClient(callid, 0, callbackData);
        })
        .catch(error => console.error(error));


}