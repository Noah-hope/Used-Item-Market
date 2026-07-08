package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.RecordDao;
import com.useditemmarket.dto.ChatSendRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.TradeRecord;
import com.useditemmarket.repository.ChatRepository;
import com.useditemmarket.service.api.ChatService;
import com.useditemmarket.vo.ChatConversationVo;
import com.useditemmarket.vo.ChatMessageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ChatServiceImpl extends AbstractApiSupport implements ChatService {
    @Resource
    private ChatRepository chatRepository;
    @Resource
    private RecordDao recordDao;

    @Override
    public List<ChatConversationVo> listConversations(String uid) {
        requireNormalUser(uid);
        return chatRepository.listConversations(uid);
    }

    @Override
    public List<ChatMessageVo> listMessages(String uid, String conversationKey) {
        requireNormalUser(uid);
        requireNotBlank(conversationKey, "会话不存在");
        return chatRepository.listMessages(uid, conversationKey.trim());
    }

    @Override
    public ChatMessageVo send(String uid, ChatSendRequest request) {
        requireNormalUser(uid);
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        requireNormalUser(request.getReceiverUid());
        requireNotBlank(request.getContent(), "消息内容不能为空");
        if (uid.equals(request.getReceiverUid())) {
            throw new BaseException(400, "不能给自己发送消息");
        }
        if (request.getOrderPid() != null && !request.getOrderPid().trim().isEmpty()) {
            TradeRecord tradeRecord = recordDao.SelectTradeRecord(request.getOrderPid().trim());
            if (tradeRecord == null) {
                throw new BaseException(404, "订单不存在");
            }
            boolean isBuyer = uid.equals(tradeRecord.getBuyerID());
            boolean isSeller = uid.equals(tradeRecord.getSellerID());
            if (!isBuyer && !isSeller) {
                throw new BaseException(403, "无权查看该订单会话");
            }
            String expectedPeer = isBuyer ? tradeRecord.getSellerID() : tradeRecord.getBuyerID();
            if (!expectedPeer.equals(request.getReceiverUid())) {
                throw new BaseException(400, "订单会话对象不匹配");
            }
            request.setOrderPid(tradeRecord.getPID());
            request.setGoodsId(tradeRecord.getGID());
        } else {
            request.setOrderPid(null);
        }
        if (request.getGoodsId() != null && !request.getGoodsId().trim().isEmpty()) {
            MarketGoods goods = requireGoods(request.getGoodsId().trim());
            if (request.getOrderPid() == null) {
                requireGoodsActive(goods, "该商品当前不可发送商品咨询");
            }
            request.setGoodsId(goods.getGID());
        } else {
            request.setGoodsId(null);
        }
        return chatRepository.sendMessage(uid, request.getReceiverUid(), request.getGoodsId(), request.getOrderPid(), request.getContent().trim());
    }

    @Override
    public void deleteConversation(String uid, String conversationKey) {
        requireNormalUser(uid);
        requireNotBlank(conversationKey, "会话不存在");
        String normalizedKey = conversationKey.trim();
        if (chatRepository.findConversation(uid, normalizedKey) == null) {
            throw new BaseException(404, "会话不存在");
        }
        chatRepository.deleteConversation(uid, normalizedKey);
    }
}
