package com.useditemmarket.dao;

import com.useditemmarket.po.*;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecordDao {
    public boolean DeleteRecord(PurchaseRecord purchaseRecord);

    //public boolean SelectRecord(PurchaseRecord purchaseRecord);

    public boolean InsertRecord(@Param("UID") String UID, @Param("purchaseRecord") PurchaseRecord purchaseRecord);

    public boolean InsertTradeRecord(TradeRecord tradeRecord);

    public boolean UpdateRecord(@Param("PID") String PID,@Param("IsSent") boolean IsSent,@Param("IsGot") boolean IsGot);

    public TradeRecord SelectTradeRecord(String PID);

    public List<PurchaseRecord> ShowRecord(@Param("marketUser") MarketUser marketUser,
                                           @Param("Status") String status);

    public List<TradeRecord> ShowAllRecord();
}
