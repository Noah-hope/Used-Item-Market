package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.ChatSendRequest;
import com.TropicalFlavor.vo.ChatConversationVo;
import com.TropicalFlavor.vo.ChatMessageVo;

import java.util.List;

public interface ChatService {
    List<ChatConversationVo> listConversations(String uid);

    List<ChatMessageVo> listMessages(String uid, String peerUid);

    ChatMessageVo send(String uid, ChatSendRequest request);
}
