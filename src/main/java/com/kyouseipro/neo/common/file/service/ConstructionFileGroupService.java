package com.kyouseipro.neo.common.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.file.entity.ConstructionFileGroupEntity;
import com.kyouseipro.neo.common.file.repository.ConstructionFileGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConstructionFileGroupService {
    private final ConstructionFileGroupRepository constructionFileGroupRepository;
    
    @Transactional
    public Long createGroup(Long constructionId, String groupName) {

        ConstructionFileGroupEntity group = new ConstructionFileGroupEntity();
        group.setConstructionId(constructionId);
        group.setGroupName(groupName);

        return constructionFileGroupRepository.insert(group);
    }

    @Transactional
    public void renameGroup(Long groupId, String newName) {
        if (newName == null || newName.isEmpty()) return;

        constructionFileGroupRepository.updateGroupName(groupId, newName);
    }
}
