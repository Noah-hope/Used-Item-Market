package com.useditemmarket.controller.api;

import com.useditemmarket.dto.AddressSaveRequest;
import com.useditemmarket.model.AuthContext;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.AddressService;
import com.useditemmarket.vo.AddressVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    @Resource
    private AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressVo>> list() {
        return ApiResponse.success(addressService.list(AuthContext.get().getUid()));
    }

    @PostMapping
    public ApiResponse<AddressVo> create(@RequestBody AddressSaveRequest request) {
        return ApiResponse.success("地址已新增", addressService.create(AuthContext.get().getUid(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressVo> update(@PathVariable Long id, @RequestBody AddressSaveRequest request) {
        return ApiResponse.success("地址已更新", addressService.update(AuthContext.get().getUid(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.delete(AuthContext.get().getUid(), id);
        return ApiResponse.success("地址已删除", null);
    }
}
