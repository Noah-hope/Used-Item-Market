package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.CarDao;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.service.api.CartService;
import com.useditemmarket.vo.CartItemVo;
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
        requireNormalUser(uid);
        return carDao.ShowGoods(requireUser(uid)).stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItemVo> addItem(String uid, String gid, Double quantity) {
        validateQuantity(quantity);
        requireNormalUser(uid);
        MarketGoods goods = requireGoods(gid);
        String sellerUid = salesDao.WhoseGoods(gid);
        if (isAdminUid(sellerUid)) {
            throw new BaseException(400, "该商品不可加入购物车");
        }
        if (uid.equals(sellerUid)) {
            throw new BaseException(400, "不能购买自己的商品");
        }
        requireGoodsActive(goods, "该商品当前不可加入购物车");
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
        requireNormalUser(uid);
        MarketGoods goods = requireGoods(gid);
        requireGoodsActive(goods, "该商品当前不可购买");
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
        requireNormalUser(uid);
        MarketGoods goods = requireGoods(gid);
        if (!carDao.DeleteGoods(uid, goods)) {
            throw new BaseException(500, "移除购物车失败");
        }
        return listCart(uid);
    }

    private MarketGoods findCartItem(String uid, String gid) {
        requireNormalUser(uid);
        for (CartItemVo item : carDao.ShowGoods(requireUser(uid))) {
            if (gid.equals(item.getGid())) {
                MarketGoods goods = new MarketGoods(item.getGid(), item.getName(), item.getCategory(), item.getPrice(), item.getQuantity(), item.getImage());
                goods.setStatus(item.getStatus());
                return goods;
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
