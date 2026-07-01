package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.GoodsCreateRequest;
import com.TropicalFlavor.dto.GoodsUpdateRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.model.DeliveryMode;
import com.TropicalFlavor.model.GoodsCategory;
import com.TropicalFlavor.model.GoodsStatus;
import com.TropicalFlavor.po.MarketGoods;
import com.TropicalFlavor.po.MarketUser;
import com.TropicalFlavor.service.api.SellerGoodsService;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SellerGoodsServiceImpl extends AbstractApiSupport implements SellerGoodsService {
    @Override
    public List<GoodsVo> listMine(String uid) {
        MarketUser user = requireUser(uid);
        return salesDao.ShowGoods(requireUser(uid)).stream()
                .map(item -> GoodsVo.from(item, uid, user.getUname()))
                .collect(Collectors.toList());
    }

    @Override
    public GoodsVo create(String uid, GoodsCreateRequest request) {
        MarketUser user = requireUser(uid);
        requireCampusVerified(user);
        validateGoodsRequest(request);
        MarketGoods goods = new MarketGoods(
                request.getName(),
                GoodsCategory.normalize(request.getCategory()),
                request.getPrice(),
                request.getStock(),
                request.getImage(),
                request.getComment()
        );
        goods.setGID(nextGid());
        goods.setStatus(GoodsStatus.PENDING_REVIEW.name());
        goods.setDeliveryMode(DeliveryMode.fromValue(request.getDeliveryMode()).name());
        goods.setPickupLocation(request.getPickupLocation());
        goods.setCampusOnly(1);
        goods.setPublishedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (!goodsDao.InsertGoods(goods) || !salesDao.InsertGoods(uid, goods)) {
            throw new BaseException(500, "发布商品失败");
        }
        return GoodsVo.from(goods, uid, user.getUname());
    }

    @Override
    public GoodsVo update(String uid, String gid, GoodsUpdateRequest request) {
        requireOwner(uid, gid);
        MarketUser user = requireUser(uid);
        MarketGoods goods = requireGoods(gid);
        if (request.getName() != null) {
            goods.setName(request.getName());
        }
        if (request.getCategory() != null) {
            goods.setKind(GoodsCategory.normalize(request.getCategory()));
        }
        if (request.getPrice() != null) {
            goods.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            goods.setNumber(request.getStock());
        }
        if (request.getImage() != null) {
            goods.setImage(request.getImage());
        }
        if (request.getComment() != null) {
            goods.setComment(request.getComment());
        }
        if (request.getDeliveryMode() != null) {
            goods.setDeliveryMode(DeliveryMode.fromValue(request.getDeliveryMode()).name());
        }
        if (request.getPickupLocation() != null) {
            goods.setPickupLocation(request.getPickupLocation());
        }
        goods.setStatus(GoodsStatus.PENDING_REVIEW.name());
        if (!goodsDao.ChangeInfo(goods)) {
            throw new BaseException(500, "更新商品失败");
        }
        return GoodsVo.from(requireGoods(gid), uid, user.getUname());
    }

    @Override
    public void delete(String uid, String gid) {
        requireOwner(uid, gid);
        MarketGoods goods = requireGoods(gid);
        goods.setNumber(0.0);
        goods.setStatus(GoodsStatus.OFF_SHELF.name());
        if (!goodsDao.ChangeInfo(goods)) {
            throw new BaseException(500, "下架商品失败");
        }
    }

    private void validateGoodsRequest(GoodsCreateRequest request) {
        requireNotBlank(request.getName(), "商品名称不能为空");
        requireNotBlank(request.getCategory(), "商品分类不能为空");
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new BaseException(400, "商品价格必须大于 0");
        }
        if (request.getStock() == null || request.getStock() <= 0) {
            throw new BaseException(400, "库存必须大于 0");
        }
        requireNotBlank(request.getDeliveryMode(), "交付方式不能为空");
    }
}
