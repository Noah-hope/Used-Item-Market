package com.useditemmarket.repository;

import com.useditemmarket.exception.BaseException;
import com.useditemmarket.vo.AddressVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AddressRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<AddressVo> listAddresses(String uid) {
        return jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? order by IsDefault desc, Id desc",
                addressMapper(),
                uid
        );
    }

    public AddressVo findAddress(String uid, Long id) {
        List<AddressVo> list = jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? and Id = ?",
                addressMapper(),
                uid, id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public AddressVo insertAddress(String uid, AddressVo address) {
        clearDefaultAddress(uid, Boolean.TRUE.equals(address.getDefaultAddress()));
        jdbcTemplate.update(
                "insert into user_address (UID, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault, CreatedAt) values (?, ?, ?, ?, ?, ?, now())",
                uid, address.getReceiverName(), address.getPhoneNumber(), address.getCampusArea(), address.getDetailAddress(),
                Boolean.TRUE.equals(address.getDefaultAddress()) ? 1 : 0
        );
        Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        return findAddress(uid, id);
    }

    public AddressVo updateAddress(String uid, Long id, AddressVo address) {
        clearDefaultAddress(uid, Boolean.TRUE.equals(address.getDefaultAddress()));
        int updated = jdbcTemplate.update(
                "update user_address set ReceiverName = ?, PhoneNumber = ?, CampusArea = ?, DetailAddress = ?, IsDefault = ? where UID = ? and Id = ?",
                address.getReceiverName(), address.getPhoneNumber(), address.getCampusArea(), address.getDetailAddress(),
                Boolean.TRUE.equals(address.getDefaultAddress()) ? 1 : 0, uid, id
        );
        if (updated == 0) {
            throw new BaseException(404, "地址不存在");
        }
        return findAddress(uid, id);
    }

    public void deleteAddress(String uid, Long id) {
        jdbcTemplate.update("delete from user_address where UID = ? and Id = ?", uid, id);
    }

    public String getAddressSnapshot(String uid, Long id) {
        AddressVo address = findAddress(uid, id);
        if (address == null) {
            throw new BaseException(404, "请选择有效的收货地址");
        }
        return toSnapshot(address);
    }

    public String getDefaultAddressSnapshot(String uid) {
        List<AddressVo> list = jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? order by IsDefault desc, Id desc limit 1",
                addressMapper(),
                uid
        );
        return list.isEmpty() ? null : toSnapshot(list.get(0));
    }

    private void clearDefaultAddress(String uid, boolean shouldClear) {
        if (shouldClear) {
            jdbcTemplate.update("update user_address set IsDefault = 0 where UID = ?", uid);
        }
    }

    private String toSnapshot(AddressVo address) {
        return address.getReceiverName() + " / " + address.getPhoneNumber() + " / " + address.getCampusArea() + " / " + address.getDetailAddress();
    }
}
