package com.useditemmarket.service.api.impl;

import com.useditemmarket.dto.WantedCreateRequest;
import com.useditemmarket.repository.WantedRepository;
import com.useditemmarket.service.api.WantedService;
import com.useditemmarket.vo.WantedVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class WantedServiceImpl extends AbstractApiSupport implements WantedService {
    @Resource
    private WantedRepository wantedRepository;

    @Override
    public List<WantedVo> listOpen() {
        return wantedRepository.listOpenWanted();
    }

    @Override
    public List<WantedVo> listMine(String uid) {
        requireNormalUser(uid);
        return wantedRepository.listMyWanted(uid);
    }

    @Override
    public WantedVo create(String uid, WantedCreateRequest request) {
        requireNormalUser(uid);
        requireNotBlank(request.getTitle(), "求购物品不能为空");
        requireNotBlank(request.getCategory(), "分类不能为空");
        WantedVo wanted = new WantedVo();
        wanted.setTitle(request.getTitle().trim());
        wanted.setCategory(request.getCategory().trim());
        wanted.setBudget(request.getBudget() == null ? 0D : request.getBudget());
        wanted.setKeyword(request.getKeyword() == null ? null : request.getKeyword().trim());
        wanted.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        return wantedRepository.createWanted(uid, wanted);
    }

    @Override
    public WantedVo delete(String uid, Long id) {
        requireNormalUser(uid);
        return wantedRepository.deleteWanted(uid, id);
    }
}
