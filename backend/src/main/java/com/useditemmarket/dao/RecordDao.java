package com.useditemmarket.dao;

import com.useditemmarket.po.*;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecordDao {
    public boolean DeleteRecord(PurchaseRecord purchaseRecord);

    public boolean DeleteByPid(@Param("PID") String PID);

    public boolean DeleteByGid(@Param("GID") String GID);

    //public boolean SelectRecord(PurchaseRecord purchaseRecord);

    public boolean InsertRecord(@Param("UID") String UID, @Param("purchaseRecord") PurchaseRecord purchaseRecord);

    public boolean InsertTradeRecord(TradeRecord tradeRecord);

    public boolean UpdateRecord(@Param("PID") String PID,@Param("IsSent") boolean IsSent,@Param("IsGot") boolean IsGot);

    public boolean UpdateStatus(@Param("PID") String PID,
                                @Param("Status") String status,
                                @Param("IsSent") boolean isSent,
                                @Param("IsGot") boolean isGot);

    public TradeRecord SelectTradeRecord(String PID);

    public List<PurchaseRecord> ShowRecord(@Param("marketUser") MarketUser marketUser,
                                           @Param("Status") String status);

    public List<TradeRecord> ShowAllRecord();
}
