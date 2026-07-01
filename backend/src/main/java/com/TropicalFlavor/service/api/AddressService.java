package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.AddressSaveRequest;
import com.TropicalFlavor.vo.AddressVo;

import java.util.List;

public interface AddressService {
    List<AddressVo> list(String uid);

    AddressVo create(String uid, AddressSaveRequest request);

    AddressVo update(String uid, Long id, AddressSaveRequest request);

    void delete(String uid, Long id);
}
