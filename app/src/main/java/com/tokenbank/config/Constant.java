package com.tokenbank.config;



public class Constant {

    public final static String wallet_prefs_prefix = "wallet_pref_";

    public final static String wallet_def_file = "wallet_default_pref";
    public final static String wid = "wid";
    public final static String wtype = "wtype";
    public final static String wname = "wname";
    public final static String waddress = "waddress";
    public final static String whash = "whash";
    public final static String wpk = "wpk";
    public final static String baked = "baked";
    public final static String words = "words";

    //common sp
    public final static String common_prefs = "common_prefs";
    public final static String asset_visible_key = "asset_visible";

    //什么是私钥
    public final static String privatekey_intro_url = "";

    public final static String sys_prefs = "sys_prefs";
    public final static String init_keys = "init_keys";

    //本地web3文件地址
    public final static String base_web3_url = "file:///android_asset/web3.html";

    //帮助
    public final static String help_url = "";

    //隐私策略
    public final static String privilege_url = "";

    //服务协议
    public final static String service_term_url = "";

    //交易查询
    public final static String eth_transaction_search_url = "https://etherscan.io/tx/";
    public final static String swt_transaction_search_url = "http://state.jingtum.com/#!/tx/";

    public final static String JC_EXCHANGE_SERVER = "https://e9joixcvsdvi4sf.jccdex.cn";

    public final static String ETHPLORER_SERVER = "http://api.ethplorer.io";

    public final static  String ETHERSCAN_SERVER = "https://api.etherscan.io";

    public final static String ETHERSCAN_API_TOKEN = "";

    //jt
    public final static String jt_base_url = "https://api.jingtum.com";

    public final static String MOAC_BROWSER = "http://explorer.moac.io/addr/";

    public final static String EOS_BROWSER = "https://eospark.com/account/";

    //activity requestCode
    public final static int CHOOSE_BLOCK_REQUEST_CODE = 1001;

    public final static String BLOCK_KEY = "BLOCK";

    public final static String ETHER_ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAaCAMAAACelLz8AAAApVBMVEVHcEz///////////////////////////////////////////////////////////////////////////99fn8pKioyMzMPDw80NTUUFBTd3d2nqKmOjo+Gh4iKi4xjZGQ3ODghIiK9vb3i4uKRkpI6OzwuLy90dXXo6OiZmptERUXY2NihoqLOz8/ExMTHx8fKy8tOTk6wsLCEhYbV1dXv7+9YWVlcbRqNAAAAE3RSTlMAwxIv58g/+gm+gZjGJ3aZQJt3YXhNTAAAARdJREFUKM+FUtmCgjAMrAgiCl605b5PETzZ/f9PW1ibKL6Yp5lMm07SECLC2GiyqsraxiDTWMxMjNniTViuzUmsl6BIuvkRuiTuKJBxr4CU5709Hg4bhPt/B0g7antIRi/o7cenNq/RJyFzPOcF1GYV0jlZATwFo1Sega+IBvD4CB9xWd6Aa0QW6B769JYwzn2RkIkqWsrDqGCWk5Rx/8yoILVVw0ruWIMYgSQKZhXj8SA5VtRBQWHDza6FPUgs7XuwIcxnx9TNE4v2fpSBeWj55LeXe+v9xh22jIO6hLlX2XH6GtRrvF5OCx4A242j3+I8giICvJ1+ZU2bevqVRALtDA8p0ve1Gb28bY6++9hE4/Bc0QOu6B8WJy4NGzMfaAAAAABJRU5ErkJggg==";

    public final static String MOAC_ICON = "http://explorer.moac.io/img/new-logo.png";

    public final static String EOS_ICON = "http://res.mobilecoinabcbene.nlren.cn/coinbene-upload/EOS.png" ;
    //abi data

    public final static String ABI_DATA = " [\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [],\n" +
            "            \"name\": \"name\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"\",\n" +
            "                    \"type\": \"string\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_spender\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"approve\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"success\",\n" +
            "                    \"type\": \"bool\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [],\n" +
            "            \"name\": \"totalSupply\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_from\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_to\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"transferFrom\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"success\",\n" +
            "                    \"type\": \"bool\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [],\n" +
            "            \"name\": \"decimals\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"\",\n" +
            "                    \"type\": \"uint8\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [],\n" +
            "            \"name\": \"version\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"\",\n" +
            "                    \"type\": \"string\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_owner\",\n" +
            "                    \"type\": \"address\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"balanceOf\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"balance\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [],\n" +
            "            \"name\": \"symbol\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"\",\n" +
            "                    \"type\": \"string\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_to\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"transfer\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"success\",\n" +
            "                    \"type\": \"bool\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_spender\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_extraData\",\n" +
            "                    \"type\": \"bytes\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"approveAndCall\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"success\",\n" +
            "                    \"type\": \"bool\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"constant\": true,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_owner\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_spender\",\n" +
            "                    \"type\": \"address\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"allowance\",\n" +
            "            \"outputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"remaining\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"function\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"name\": \"_initialAmount\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_tokenName\",\n" +
            "                    \"type\": \"string\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_decimalUnits\",\n" +
            "                    \"type\": \"uint8\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"_tokenSymbol\",\n" +
            "                    \"type\": \"string\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"type\": \"constructor\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"payable\": false,\n" +
            "            \"type\": \"fallback\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"anonymous\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"indexed\": true,\n" +
            "                    \"name\": \"_from\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"indexed\": true,\n" +
            "                    \"name\": \"_to\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"indexed\": false,\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"Transfer\",\n" +
            "            \"type\": \"event\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"anonymous\": false,\n" +
            "            \"inputs\": [\n" +
            "                {\n" +
            "                    \"indexed\": true,\n" +
            "                    \"name\": \"_owner\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"indexed\": true,\n" +
            "                    \"name\": \"_spender\",\n" +
            "                    \"type\": \"address\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"indexed\": false,\n" +
            "                    \"name\": \"_value\",\n" +
            "                    \"type\": \"uint256\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"name\": \"Approval\",\n" +
            "            \"type\": \"event\"\n" +
            "        }\n" +
            "    ]";

}
