package com.useditemmarket.service.api;

import com.useditemmarket.dto.WantedCreateRequest;
import com.useditemmarket.vo.WantedVo;

import java.util.List;

public interface WantedService {
    List<WantedVo> listOpen();

    List<WantedVo> listMine(String uid);

    WantedVo create(String uid, WantedCreateRequest request);

    WantedVo delete(String uid, Long id);
}
