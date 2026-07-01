package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dao.CarDao;
import com.TropicalFlavor.dao.RecordDao;
import com.TropicalFlavor.dao.SRecordDao;
import com.TropicalFlavor.dto.OrderCreateRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.model.OrderStatus;
import com.TropicalFlavor.model.OrderStatus;
import com.TropicalFlavor.po.MarketGoods;
import com.TropicalFlavor.po.TradeRecord;
import com.TropicalFlavor.service.api.OrderService;
import com.TropicalFlavor.vo.OrderVo;
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

    @Override
    public OrderVo createOrder(String uid, OrderCreateRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BaseException(400, "购买数量必须大于 0");
        }
        requireUser(uid);
        MarketGoods goods = requireGoods(request.getGid());
        String sellerUid = salesDao.WhoseGoods(request.getGid());
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
        MarketGoods updated = new MarketGoods(goods.getGID(), goods.getName(), goods.getKind(), goods.getPrice(), goods.getNumber() - request.getQuantity(), goods.getImage());
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
        String addressSnapshot = request.getAddressId() == null ? platformRepository.getDefaultAddressSnapshot(uid) : platformRepository.getAddressSnapshot(uid, request.getAddressId());
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
        return recordDao.ShowRecord(requireUser(uid), status.isSent(), status.isGot()).stream()
                .map(record -> OrderVo.fromPurchase(record, platformRepository.findSellerUidByGoods(record.getGID()), status))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderVo> listSales(String uid, OrderStatus status) {
        return saleRecordDao.ShowRecord(requireUser(uid), status.isSent(), status.isGot()).stream()
                .map(record -> OrderVo.fromSale(record, status))
                .collect(Collectors.toList());
    }

    @Override
    public OrderVo ship(String uid, String pid) {
        TradeRecord tradeRecord = requireTradeRecord(pid);
        if (!uid.equals(tradeRecord.getSellerID())) {
            throw new BaseException(403, "无权发货");
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
        TradeRecord tradeRecord = requireTradeRecord(pid);
        if (!uid.equals(tradeRecord.getBuyerID())) {
            throw new BaseException(403, "无权确认收货");
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
