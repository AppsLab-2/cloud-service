package com.appslab.CloudService.Services;

import com.appslab.CloudService.Models.Folder;

import java.util.List;
import java.util.Optional;

public interface FolderService {

    void createFolder(Folder folder);

    Optional<Folder> findFolderById(Long id);

    List<Folder> searchFoldersByFolderName(String keyword);

    List<Folder> getAllFolder();

    void deleteFolder(Long id);
}
