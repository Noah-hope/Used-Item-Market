package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.CarDao;
import com.useditemmarket.dao.RecordDao;
import com.useditemmarket.dao.SRecordDao;
import com.useditemmarket.dto.OrderCreateRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.OrderStatus;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.TradeRecord;
import com.useditemmarket.repository.AddressRepository;
import com.useditemmarket.service.api.OrderService;
import com.useditemmarket.vo.OrderVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl extends AbstractApiSupport implements OrderService {
    @Resource
    private RecordDao recordDao;
    @Resource
    private SRecordDao saleRecordDao;
    @Resource
    private CarDao carDao;
    @Resource
    private AddressRepository addressRepository;

    @Override
    public OrderVo createOrder(String uid, OrderCreateRequest request) {
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BaseException(400, "购买数量必须大于 0");
        }

        requireNormalUser(uid);
        MarketGoods goods = requireGoods(request.getGid());
        String sellerUid = salesDao.WhoseGoods(request.getGid());
        if (sellerUid == null) {
            throw new BaseException(404, "商品卖家不存在");
        }
        if (isAdminUid(sellerUid)) {
            throw new BaseException(400, "该商品不可下单");
        }
        if (uid.equals(sellerUid)) {
            throw new BaseException(400, "不能购买自己的商品");
        }
        if (!"ACTIVE".equals(goods.getStatus())) {
            throw new BaseException(400, "该商品当前不可下单");
        }
        if (request.getQuantity() > goods.getNumber()) {
            throw new BaseException(400, "超出最大可购买数量");
        }

        String[] current = now();
        String pid = nextPid();

        MarketGoods updated = new MarketGoods(
                goods.getGID(),
                goods.getName(),
                goods.getKind(),
                goods.getPrice(),
                goods.getNumber() - request.getQuantity(),
                goods.getImage()
        );
        updated.setComment(goods.getComment());
        updated.setStatus(updated.getNumber() > 0 ? goods.getStatus() : "OFF_SHELF");
        updated.setDeliveryMode(goods.getDeliveryMode());
        updated.setPickupLocation(goods.getPickupLocation());
        updated.setCampusOnly(goods.getCampusOnly());
        updated.setReviewNote(goods.getReviewNote());
        updated.setPublishedAt(goods.getPublishedAt());
        if (!goodsDao.ChangeInfo(updated)) {
            throw new BaseException(500, "扣减库存失败");
        }

        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setPID(pid);
        tradeRecord.setBuyerID(uid);
        tradeRecord.setDate(current[0]);
        tradeRecord.setTime(current[1]);
        tradeRecord.setGID(goods.getGID());
        tradeRecord.setSellerID(sellerUid);
        tradeRecord.setGname(goods.getName());
        tradeRecord.setGkind(goods.getKind());
        tradeRecord.setGprice(goods.getPrice());
        tradeRecord.setGnumber(request.getQuantity());
        tradeRecord.setSent(false);
        tradeRecord.setGot(false);
        tradeRecord.setStatus(OrderStatus.PENDING_CONTACT.name());
        tradeRecord.setDeliveryMode(goods.getDeliveryMode());
        tradeRecord.setPickupLocation(goods.getPickupLocation());
        tradeRecord.setAppointmentTime(current[0] + " " + current[1]);
        tradeRecord.setRemark(Boolean.TRUE.equals(request.getFromCart()) ? "来自购物车下单" : "立即下单");
        String addressSnapshot = request.getAddressId() == null
                ? addressRepository.getDefaultAddressSnapshot(uid)
                : addressRepository.getAddressSnapshot(uid, request.getAddressId());
        tradeRecord.setAddressSnapshot(addressSnapshot);

        if (!recordDao.InsertTradeRecord(tradeRecord)) {
            throw new BaseException(500, "创建订单失败");
        }
        if (Boolean.TRUE.equals(request.getFromCart())) {
            carDao.DeleteGoods(uid, goods);
        }
        return OrderVo.fromTrade(tradeRecord);
    }

    @Override
    public List<OrderVo> listPurchases(String uid, OrderStatus status) {
        return recordDao.ShowRecord(requireNormalUser(uid), status.isSent(), status.isGot()).stream()
                .filter(record -> !isAdminUid(salesDao.WhoseGoods(record.getGID())))
                .map(record -> OrderVo.fromPurchase(record, salesDao.WhoseGoods(record.getGID()), status))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderVo> listSales(String uid, OrderStatus status) {
        return saleRecordDao.ShowRecord(requireNormalUser(uid), status.isSent(), status.isGot()).stream()
                .collect(Collectors.toList())
                .stream()
                .map(record -> OrderVo.fromSale(record, status))
                .collect(Collectors.toList());
    }

    @Override
    public OrderVo ship(String uid, String pid) {
        requireNormalUser(uid);
        TradeRecord tradeRecord = requireTradeRecord(pid);
        if (!uid.equals(tradeRecord.getSellerID())) {
            throw new BaseException(403, "无权发货");
        }
        if (isAdminUid(tradeRecord.getSellerID())) {
            throw new BaseException(403, "管理员账号不能处理卖家订单");
        }
        if (!recordDao.UpdateRecord(pid, true, false)) {
            throw new BaseException(500, "发货失败");
        }
        tradeRecord = requireTradeRecord(pid);
        tradeRecord.setStatus(OrderStatus.PENDING_PICKUP.name());
        return OrderVo.fromTrade(tradeRecord);
    }

    @Override
    public OrderVo receive(String uid, String pid) {
        requireNormalUser(uid);
        TradeRecord tradeRecord = requireTradeRecord(pid);
        if (!uid.equals(tradeRecord.getBuyerID())) {
            throw new BaseException(403, "无权确认收货");
        }
        if (isAdminUid(tradeRecord.getBuyerID())) {
            throw new BaseException(403, "管理员账号不能处理买家订单");
        }
        if (!recordDao.UpdateRecord(pid, true, true)) {
            throw new BaseException(500, "确认收货失败");
        }
        tradeRecord = requireTradeRecord(pid);
        tradeRecord.setStatus(OrderStatus.COMPLETED.name());
        return OrderVo.fromTrade(tradeRecord);
    }

    private TradeRecord requireTradeRecord(String pid) {
        TradeRecord tradeRecord = recordDao.SelectTradeRecord(pid);
        if (tradeRecord == null) {
            throw new BaseException(404, "订单不存在");
        }
        return tradeRecord;
    }

    private String[] now() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] dateTime = formatter.format(new Date()).split(" ");
        return new String[]{dateTime[0], dateTime[1]};
    }
}
