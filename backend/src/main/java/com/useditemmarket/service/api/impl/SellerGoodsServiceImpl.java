package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.CarDao;
import com.useditemmarket.dao.RecordDao;
import com.useditemmarket.dto.GoodsCreateRequest;
import com.useditemmarket.dto.GoodsUpdateRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.DeliveryMode;
import com.useditemmarket.model.GoodsCategory;
import com.useditemmarket.model.GoodsStatus;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.repository.ChatRepository;
import com.useditemmarket.repository.FavoriteRepository;
import com.useditemmarket.service.api.SellerGoodsService;
import com.useditemmarket.service.support.FileStorageService;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SellerGoodsServiceImpl extends AbstractApiSupport implements SellerGoodsService {

    @Resource
    private FileStorageService fileStorageService;
    @Resource
    private CarDao carDao;
    @Resource
    private RecordDao recordDao;
    @Resource
    private FavoriteRepository favoriteRepository;
    @Resource
    private ChatRepository chatRepository;

    @Override
    public List<GoodsVo> listMine(String uid) {
        MarketUser user = requireNormalUser(uid);
        return salesDao.ShowGoods(user).stream()
                .map(item -> GoodsVo.from(item, uid, user.getUname()))
                .collect(Collectors.toList());
    }

    @Override
    public GoodsVo detail(String uid, String gid) {
        MarketUser user = requireNormalUser(uid);
        requireOwner(uid, gid);
        return GoodsVo.from(requireGoods(gid), uid, user.getUname());
    }

    @Override
    public GoodsVo create(String uid, GoodsCreateRequest request) {
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        MarketUser user = requireNormalUser(uid);
        requireCampusVerified(user);
        validateGoodsRequest(request);

        MarketGoods goods = new MarketGoods(
                request.getName().trim(),
                GoodsCategory.normalize(request.getCategory()),
                request.getPrice(),
                request.getStock(),
                normalizeOptional(request.getImage()),
                normalizeOptional(request.getComment())
        );
        goods.setGID(nextGid());
        goods.setStatus(GoodsStatus.PENDING_REVIEW.name());
        goods.setDeliveryMode(DeliveryMode.fromValue(request.getDeliveryMode()).name());
        goods.setPickupLocation(normalizeOptional(request.getPickupLocation()));
        goods.setCampusOnly(1);
        goods.setReviewNote(null);
        goods.setReviewNoteTouched(true);
        goods.setPublishedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (!goodsDao.InsertGoods(goods) || !salesDao.InsertGoods(uid, goods)) {
            throw new BaseException(500, "发布商品失败");
        }
        return GoodsVo.from(goods, uid, user.getUname());
    }

    @Override
    public GoodsVo update(String uid, String gid, GoodsUpdateRequest request) {
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        MarketUser user = requireNormalUser(uid);
        requireOwner(uid, gid);

        MarketGoods goods = requireGoods(gid);
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            goods.setName(request.getName().trim());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            goods.setKind(GoodsCategory.normalize(request.getCategory()));
        }
        if (request.getPrice() != null) {
            if (request.getPrice() <= 0) {
                throw new BaseException(400, "商品价格必须大于 0");
            }
            goods.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                throw new BaseException(400, "库存不能小于 0");
            }
            goods.setNumber(request.getStock());
        }
        if (request.getImage() != null) {
            goods.setImage(normalizeOptional(request.getImage()));
        }
        if (request.getComment() != null) {
            goods.setComment(normalizeOptional(request.getComment()));
        }
        if (request.getDeliveryMode() != null) {
            goods.setDeliveryMode(DeliveryMode.fromValue(request.getDeliveryMode()).name());
        }
        if (request.getPickupLocation() != null) {
            goods.setPickupLocation(normalizeOptional(request.getPickupLocation()));
        }

        GoodsStatus currentStatus = GoodsStatus.fromValue(goods.getStatus());
        Double stock = goods.getNumber();
        if (stock != null && stock > 0) {
            goods.setStatus(GoodsStatus.PENDING_REVIEW.name());
        } else if (currentStatus == GoodsStatus.REJECTED || currentStatus == GoodsStatus.BANNED) {
            goods.setStatus(currentStatus.name());
        } else {
            goods.setStatus(GoodsStatus.OFF_SHELF.name());
        }
        goods.setReviewNote(null);
        goods.setReviewNoteTouched(true);
        if (!goodsDao.ChangeInfo(goods)) {
            throw new BaseException(500, "更新商品失败");
        }
        return GoodsVo.from(requireGoods(gid), uid, user.getUname());
    }

    @Override
    public void delete(String uid, String gid) {
        requireNormalUser(uid);
        requireOwner(uid, gid);
        MarketGoods goods = requireGoods(gid);
        goods.setNumber(0.0);
        goods.setStatus(GoodsStatus.OFF_SHELF.name());
        goods.setReviewNote(null);
        goods.setReviewNoteTouched(true);
        if (!goodsDao.ChangeInfo(goods)) {
            throw new BaseException(500, "下架商品失败");
        }
    }

    @Override
    public void permanentDelete(String uid, String gid) {
        requireNormalUser(uid);
        requireOwner(uid, gid);
        MarketGoods goods = requireGoods(gid);
        GoodsStatus status = GoodsStatus.fromValue(goods.getStatus());
        if (status == GoodsStatus.ACTIVE) {
            throw new BaseException(400, "在售商品不能直接删除，请先下架");
        }
        if (status != GoodsStatus.PENDING_REVIEW
                && status != GoodsStatus.OFF_SHELF
                && status != GoodsStatus.REJECTED
                && status != GoodsStatus.BANNED) {
            throw new BaseException(400, "当前状态的商品不允许删除");
        }

        fileStorageService.deleteImage(goods.getImage());
        carDao.DeleteByGid(gid);
        favoriteRepository.deleteByGid(gid);
        recordDao.DeleteByGid(gid);
        chatRepository.clearGoodsId(gid);
        if (!salesDao.DeleteGoods(uid, goods)) {
            throw new BaseException(500, "删除商品关联失败");
        }
        if (!goodsDao.DeleteGoods(goods)) {
            throw new BaseException(500, "删除商品失败");
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String uid) {
        requireNormalUser(uid);
        return fileStorageService.saveImage(file, uid);
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

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
