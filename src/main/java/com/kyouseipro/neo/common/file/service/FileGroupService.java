package com.kyouseipro.neo.common.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.file.entity.FileGroupEntity;
import com.kyouseipro.neo.common.file.repository.FileGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileGroupService {
    private final FileGroupRepository fileGroupRepository;
    
    @Transactional
    public Long createGroup(String parentType, Long parentId, String groupName) {

        FileGroupEntity group = new FileGroupEntity();
        group.setParentType(parentType);
        group.setParentId(parentId);
        group.setGroupName(groupName);

        return fileGroupRepository.insert(group);
    }

    @Transactional
    public void renameGroup(Long groupId, String newName) {
        if (newName == null || newName.isEmpty()) return;

        fileGroupRepository.updateGroupName(groupId, newName);
    }
}
