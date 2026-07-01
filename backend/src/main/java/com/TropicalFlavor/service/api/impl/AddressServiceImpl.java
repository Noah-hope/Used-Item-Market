package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.AddressSaveRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.service.api.AddressService;
import com.TropicalFlavor.vo.AddressVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl extends AbstractApiSupport implements AddressService {
    @Resource
    private com.TropicalFlavor.repository.PlatformRepository platformRepository;

    @Override
    public List<AddressVo> list(String uid) {
        requireUser(uid);
        return platformRepository.listAddresses(uid);
    }

    @Override
    public AddressVo create(String uid, AddressSaveRequest request) {
        requireUser(uid);
        validate(request);
        AddressVo address = new AddressVo();
        address.setReceiverName(request.getReceiverName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setCampusArea(request.getCampusArea().trim());
        address.setDetailAddress(request.getDetailAddress().trim());
        address.setDefaultAddress(Boolean.TRUE.equals(request.getDefaultAddress()));
        return platformRepository.insertAddress(uid, address);
    }

    @Override
    public AddressVo update(String uid, Long id, AddressSaveRequest request) {
        requireUser(uid);
        validate(request);
        AddressVo address = new AddressVo();
        address.setReceiverName(request.getReceiverName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setCampusArea(request.getCampusArea().trim());
        address.setDetailAddress(request.getDetailAddress().trim());
        address.setDefaultAddress(Boolean.TRUE.equals(request.getDefaultAddress()));
        return platformRepository.updateAddress(uid, id, address);
    }

    @Override
    public void delete(String uid, Long id) {
        requireUser(uid);
        platformRepository.deleteAddress(uid, id);
    }

    private void validate(AddressSaveRequest request) {
        requireNotBlank(request.getReceiverName(), "收货人不能为空");
        requireNotBlank(request.getPhoneNumber(), "手机号不能为空");
        requireNotBlank(request.getCampusArea(), "校区不能为空");
        requireNotBlank(request.getDetailAddress(), "详细地址不能为空");
    }
}
