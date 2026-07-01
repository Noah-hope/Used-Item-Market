package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.WantedCreateRequest;
import com.TropicalFlavor.repository.PlatformRepository;
import com.TropicalFlavor.service.api.WantedService;
import com.TropicalFlavor.vo.WantedVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class WantedServiceImpl extends AbstractApiSupport implements WantedService {
    @Resource
    private PlatformRepository platformRepository;

    @Override
    public List<WantedVo> listOpen() {
        return platformRepository.listOpenWanted();
    }

    @Override
    public List<WantedVo> listMine(String uid) {
        requireUser(uid);
        return platformRepository.listMyWanted(uid);
    }

    @Override
    public WantedVo create(String uid, WantedCreateRequest request) {
        requireUser(uid);
        requireNotBlank(request.getTitle(), "求购标题不能为空");
        requireNotBlank(request.getCategory(), "分类不能为空");
        WantedVo wanted = new WantedVo();
        wanted.setTitle(request.getTitle().trim());
        wanted.setCategory(request.getCategory().trim());
        wanted.setBudget(request.getBudget() == null ? 0D : request.getBudget());
        wanted.setKeyword(request.getKeyword());
        wanted.setDescription(request.getDescription());
        return platformRepository.createWanted(uid, wanted);
    }

    @Override
    public WantedVo close(String uid, Long id) {
        requireUser(uid);
        return platformRepository.closeWanted(uid, id);
    }
}
