package com.useditemmarket.service.api.impl;

import com.useditemmarket.dto.ChatSendRequest;
import com.useditemmarket.exception.BaseException;
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

    @Override
    public List<ChatConversationVo> listConversations(String uid) {
        requireNormalUser(uid);
        return chatRepository.listConversations(uid);
    }

    @Override
    public List<ChatMessageVo> listMessages(String uid, String peerUid) {
        requireNormalUser(uid);
        requireNormalUser(peerUid);
        return chatRepository.listMessages(uid, peerUid);
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
        return chatRepository.sendMessage(uid, request.getReceiverUid(), request.getGoodsId(), request.getContent().trim());
    }
}
