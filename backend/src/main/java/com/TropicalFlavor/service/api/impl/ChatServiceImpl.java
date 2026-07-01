package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.ChatSendRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.repository.PlatformRepository;
import com.TropicalFlavor.service.api.ChatService;
import com.TropicalFlavor.vo.ChatConversationVo;
import com.TropicalFlavor.vo.ChatMessageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ChatServiceImpl extends AbstractApiSupport implements ChatService {
    @Resource
    private PlatformRepository platformRepository;

    @Override
    public List<ChatConversationVo> listConversations(String uid) {
        requireUser(uid);
        return platformRepository.listConversations(uid);
    }

    @Override
    public List<ChatMessageVo> listMessages(String uid, String peerUid) {
        requireUser(uid);
        requireUser(peerUid);
        return platformRepository.listMessages(uid, peerUid);
    }

    @Override
    public ChatMessageVo send(String uid, ChatSendRequest request) {
        requireUser(uid);
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        requireUser(request.getReceiverUid());
        requireNotBlank(request.getContent(), "消息内容不能为空");
        if (uid.equals(request.getReceiverUid())) {
            throw new BaseException(400, "不能给自己发送消息");
        }
        return platformRepository.sendMessage(uid, request.getReceiverUid(), request.getGoodsId(), request.getContent().trim());
    }
}
