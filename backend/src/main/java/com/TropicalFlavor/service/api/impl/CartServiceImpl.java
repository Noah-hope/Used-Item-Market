package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dao.CarDao;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.po.MarketGoods;
import com.TropicalFlavor.service.api.CartService;
import com.TropicalFlavor.vo.CartItemVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl extends AbstractApiSupport implements CartService {
    @Resource
    private CarDao carDao;

    @Override
    public List<CartItemVo> listCart(String uid) {
        return carDao.ShowGoods(requireUser(uid)).stream()
                .map(item -> CartItemVo.from(item, salesDao.WhoseGoods(item.getGID())))
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItemVo> addItem(String uid, String gid, Double quantity) {
        validateQuantity(quantity);
        MarketGoods goods = requireGoods(gid);
        if (uid.equals(salesDao.WhoseGoods(gid))) {
            throw new BaseException(400, "不能购买自己的商品");
        }
        MarketGoods existing = findCartItem(uid, gid);
        double newQuantity = quantity;
        if (existing != null) {
            newQuantity += existing.getNumber();
        }
        if (newQuantity > goods.getNumber()) {
            throw new BaseException(400, "超出最大可购买数量");
        }
        MarketGoods cartGoods = new MarketGoods(gid, goods.getName(), goods.getKind(), goods.getPrice(), newQuantity, goods.getImage());
        if (existing == null) {
            if (!carDao.InsertGoods(uid, cartGoods)) {
                throw new BaseException(500, "加入购物车失败");
            }
        } else if (!carDao.ChangeCart(uid, cartGoods)) {
            throw new BaseException(500, "更新购物车失败");
        }
        return listCart(uid);
    }

    @Override
    public List<CartItemVo> updateItem(String uid, String gid, Double quantity) {
        validateQuantity(quantity);
        MarketGoods goods = requireGoods(gid);
        if (quantity > goods.getNumber()) {
            throw new BaseException(400, "超出最大可购买数量");
        }
        MarketGoods cartGoods = new MarketGoods(gid, goods.getName(), goods.getKind(), goods.getPrice(), quantity, goods.getImage());
        if (!carDao.ChangeCart(uid, cartGoods)) {
            throw new BaseException(500, "更新购物车失败");
        }
        return listCart(uid);
    }

    @Override
    public List<CartItemVo> removeItem(String uid, String gid) {
        MarketGoods goods = requireGoods(gid);
        if (!carDao.DeleteGoods(uid, goods)) {
            throw new BaseException(500, "移除购物车失败");
        }
        return listCart(uid);
    }

    private MarketGoods findCartItem(String uid, String gid) {
        for (MarketGoods item : carDao.ShowGoods(requireUser(uid))) {
            if (gid.equals(item.getGID())) {
                return item;
            }
        }
        return null;
    }

    private void validateQuantity(Double quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BaseException(400, "数量必须大于 0");
        }
    }
}
