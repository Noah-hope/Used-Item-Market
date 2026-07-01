package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.WantedCreateRequest;
import com.TropicalFlavor.vo.WantedVo;

import java.util.List;

public interface WantedService {
    List<WantedVo> listOpen();

    List<WantedVo> listMine(String uid);

    WantedVo create(String uid, WantedCreateRequest request);

    WantedVo close(String uid, Long id);
}
