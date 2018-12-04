package com.tokenbank.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.tokenbank.config.AppConfig;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;



public class BlockChainData {

    private static final String TAG = "BlockChainData";
    private static BlockChainData instance = new BlockChainData();
    private List<Block> mBlock = new ArrayList<Block>();

    private BlockChainData() {

    }

    public static BlockChainData getInstance() {
        return instance;
    }

    public void init() {
        String data = FileUtil.getConfigFile(AppConfig.getContext(), "blockChains.json");

        GsonUtil json = new GsonUtil(data);

        GsonUtil chains = json.getArray("data", "[]");
        int len = chains.getLength();
        for (int i = 0; i < len; i++) {
            GsonUtil item = chains.getObject(i, "{}");
            if (blockOk(item)) {
                mBlock.add(getBlock(item));
            }
        }

    }

    public static Block getBlock(GsonUtil json) {
        Block block = new Block();
        block.hid = json.getInt("hid", 0);
        block.title = json.getString("title", "");
        block.desc = json.getString("desc", "");
        block.defaulttoken = json.getString("default_token", "");
        block.symbol = json.getString("symbol", "");
        block.url = json.getString("url", "");
        block.status = json.getInt("status", 0);
        block.createtime = json.getString("create_time", "");
        return block;
    }

    private boolean blockOk(GsonUtil item) {
        return (item.getLong("hid", 0l) > 0 &&
                !TextUtils.isEmpty(item.getString("title", "")) &&
                !TextUtils.isEmpty(item.getString("desc", "")) &&
                !TextUtils.isEmpty(item.getString("default_token", "")) &&
                (item.getInt("status", 1) == 0) && isHidSupport(item.getLong("hid", 0l)));
    }

    private boolean isHidSupport(long hid) {
        return TBController.getInstance().getSupportType().contains((int)hid);
    }

    public List<Block> getBlock() {
        if (mBlock == null || mBlock.size() == 0) {
            init();
        }
        return mBlock;
    }

    public Block getBolckByHid(long hid) {
        if(mBlock == null || mBlock.size() == 0) {
            init();
            return null;
        }
        int len = mBlock.size();
        for(int i =0; i < len; i++) {
            if(mBlock.get(i).hid == hid){
                return mBlock.get(i);
            }
        }
        return null;
    }

    public static class Block implements Parcelable {
        public long hid;
        public String title;
        public String desc;
        public String defaulttoken;
        public String symbol;
        public String url;
        public int status;
        public String createtime;

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Block)) {
                return false;
            }
            Block block = (Block) obj;
            return (block.hid == this.hid) && TextUtils.equals(block.title, this.title) &&
                    TextUtils.equals(block.desc, this.desc) && TextUtils.equals(block.defaulttoken, this.defaulttoken) &&
                    TextUtils.equals(block.symbol, this.symbol) && TextUtils.equals(block.createtime, this.createtime) &&
                    block.status == this.status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.hid);
            dest.writeString(this.title);
            dest.writeString(this.desc);
            dest.writeString(this.defaulttoken);
            dest.writeString(this.symbol);
            dest.writeString(this.url);
            dest.writeInt(this.status);
            dest.writeString(this.createtime);
        }

        public Block() {
        }

        protected Block(Parcel in) {
            this.hid = in.readLong();
            this.title = in.readString();
            this.desc = in.readString();
            this.defaulttoken = in.readString();
            this.symbol = in.readString();
            this.url = in.readString();
            this.status = in.readInt();
            this.createtime = in.readString();
        }

        public static final Parcelable.Creator<Block> CREATOR = new Parcelable.Creator<Block>() {
            @Override
            public Block createFromParcel(Parcel source) {
                return new Block(source);
            }

            @Override
            public Block[] newArray(int size) {
                return new Block[size];
            }
        };
    }
}
