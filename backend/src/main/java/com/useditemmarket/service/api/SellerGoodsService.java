package com.useditemmarket.service.api;

import com.useditemmarket.dto.GoodsCreateRequest;
import com.useditemmarket.dto.GoodsUpdateRequest;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SellerGoodsService {
    List<GoodsVo> listMine(String uid);

    GoodsVo detail(String uid, String gid);

    GoodsVo create(String uid, GoodsCreateRequest request);

    GoodsVo update(String uid, String gid, GoodsUpdateRequest request);

    void delete(String uid, String gid);

    void permanentDelete(String uid, String gid);

    String uploadImage(MultipartFile file, String uid);
}
