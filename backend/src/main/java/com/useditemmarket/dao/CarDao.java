package com.useditemmarket.dao;

import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.vo.CartItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarDao {
    boolean DeleteGoods(@Param("UID") String UID, @Param("marketGoods") MarketGoods marketGoods);

    boolean DeleteByGid(@Param("GID") String GID);

    boolean InsertGoods(@Param("UID") String UID, @Param("marketGoods") MarketGoods marketGoods);

    boolean ChangeCart(@Param("UID") String UID, @Param("marketGoods") MarketGoods marketGoods);

    List<CartItemVo> ShowGoods(MarketUser marketUser);
}
