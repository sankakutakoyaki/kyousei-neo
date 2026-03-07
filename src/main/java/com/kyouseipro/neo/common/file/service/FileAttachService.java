package com.kyouseipro.neo.common.file.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.file.entity.FileDto;
import com.kyouseipro.neo.common.file.repository.FileRepository;
import com.kyouseipro.neo.interfaces.FileAttachable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileAttachService {

    private final FileRepository fileRepository;

    public <T extends FileAttachable> void attachFiles(
            String parentType,
            List<T> list
    ) {

        if (list.isEmpty()) {
            return;
        }

        List<Long> ids = list.stream()
                .map(FileAttachable::getId)
                .toList();

        List<FileDto> files =
            fileRepository.findFiles(parentType, ids);

        Map<Long, List<FileDto>> fileMap =
            files.stream()
                .collect(Collectors.groupingBy(FileDto::getParentId));

        for (T item : list) {

            List<FileDto> f =
                fileMap.getOrDefault(item.getId(), new ArrayList<>());

            item.setFiles(f);

        }
    }
}