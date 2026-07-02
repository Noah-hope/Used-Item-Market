package com.useditemmarket.service.api.impl;

import com.useditemmarket.dto.AddressSaveRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.repository.AddressRepository;
import com.useditemmarket.service.api.AddressService;
import com.useditemmarket.vo.AddressVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl extends AbstractApiSupport implements AddressService {
    @Resource
    private AddressRepository addressRepository;

    @Override
    public List<AddressVo> list(String uid) {
        requireNormalUser(uid);
        return addressRepository.listAddresses(uid);
    }

    @Override
    public AddressVo create(String uid, AddressSaveRequest request) {
        requireNormalUser(uid);
        validate(request);
        AddressVo address = new AddressVo();
        address.setReceiverName(request.getReceiverName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setCampusArea(request.getCampusArea().trim());
        address.setDetailAddress(request.getDetailAddress().trim());
        address.setDefaultAddress(Boolean.TRUE.equals(request.getDefaultAddress()));
        return addressRepository.insertAddress(uid, address);
    }

    @Override
    public AddressVo update(String uid, Long id, AddressSaveRequest request) {
        requireNormalUser(uid);
        validate(request);
        AddressVo address = new AddressVo();
        address.setReceiverName(request.getReceiverName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setCampusArea(request.getCampusArea().trim());
        address.setDetailAddress(request.getDetailAddress().trim());
        address.setDefaultAddress(Boolean.TRUE.equals(request.getDefaultAddress()));
        return addressRepository.updateAddress(uid, id, address);
    }

    @Override
    public void delete(String uid, Long id) {
        requireNormalUser(uid);
        addressRepository.deleteAddress(uid, id);
    }

    private void validate(AddressSaveRequest request) {
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        requireNotBlank(request.getReceiverName(), "收货人不能为空");
        requireNotBlank(request.getPhoneNumber(), "手机号不能为空");
        requireNotBlank(request.getCampusArea(), "校区不能为空");
        requireNotBlank(request.getDetailAddress(), "详细地址不能为空");
    }
}
