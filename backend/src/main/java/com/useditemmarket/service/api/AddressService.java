package com.useditemmarket.service.api;

import com.useditemmarket.dto.AddressSaveRequest;
import com.useditemmarket.vo.AddressVo;

import java.util.List;

public interface AddressService {
    List<AddressVo> list(String uid);

    AddressVo create(String uid, AddressSaveRequest request);

    AddressVo update(String uid, Long id, AddressSaveRequest request);

    void delete(String uid, Long id);
}
