package com.appslab.CloudService.Services.Services_Impl;

import com.appslab.CloudService.FileProperty.DocumentStorageProperty;
import com.appslab.CloudService.Models.CustomUser;
import com.appslab.CloudService.Models.UploadedFile;
import com.appslab.CloudService.Repositories.FileRepositoryDB;
import com.appslab.CloudService.Repositories.UserRepository;
import com.appslab.CloudService.Services.UploadFileService;
import com.appslab.CloudService.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class UploadFileServiceImpl implements UploadFileService {
    private FileRepositoryDB fileRepositoryDB;
    private Path docStorageLocation;
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public UploadFileServiceImpl(FileRepositoryDB fileRepositoryDB, DocumentStorageProperty documentStorageProperty, UserService userService, UserRepository userRepository) throws Exception {
        this.fileRepositoryDB = fileRepositoryDB;
        this.userService = userService;
        this.docStorageLocation = Paths.get(documentStorageProperty.getUploadDirectory()).toAbsolutePath().normalize();
        Files.createDirectories(this.docStorageLocation);
        this.userRepository = userRepository;
    }

    @Override
    public void deleteFile(Long id) {
        fileRepositoryDB.deleteById(id);
    }

    @Override
    public List<UploadedFile> listOfFiles(Long customUserId) {
        return fileRepositoryDB.findByCustomUserId(customUserId);
    }

    @Override
    public Path getDocStorageLocation() {
        return docStorageLocation;
    }

    @Override
    public UploadedFile uploadedFile(MultipartFile multipartFile,Boolean access) {
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(multipartFile.getOriginalFilename());
            uploadedFile.setFileSize(multipartFile.getSize());
            uploadedFile.setMimeType(multipartFile.getContentType());
            uploadedFile.setDate();
            uploadedFile.setCustomUserId(userService.getSpecifyUserId());
            uploadedFile.setUuid();
            if (access==null){
                uploadedFile.setAccess(false);
            }
            else{uploadedFile.setAccess(access);}
            return uploadedFile;
    }

    @Override
    public void saveUploadedFileToDB(UploadedFile uploadedFile){
        fileRepositoryDB.save(uploadedFile);
    }

    @Override
    public Optional<UploadedFile> findFileById(Long fileID) {
        return fileRepositoryDB.findById(fileID);
    }

    @Override
    public Path pathToSpecificFile(UploadedFile uploadedFile) {
        return getDocStorageLocation().resolve(uploadedFile.getUuid().toString());
    }

    @Override
    public void saveFileToStorage(UploadedFile uploadedFile, MultipartFile multipartFile) throws Exception{
        File file = getDocStorageLocation().resolve(uploadedFile.getUuid().toString()).toFile();
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        multipartFile.getInputStream().transferTo(outputStream);
        outputStream.close();
    }

    @Override
    public List<UploadedFile> findSearchFiles(String keyword,Long customUserId) {
        return fileRepositoryDB.findByFileName(keyword,customUserId);
    }

    @Override
    public void saveEditFile(UploadedFile uploadedFile) {
        fileRepositoryDB.save(uploadedFile);
    }

    @Override
    public List<UploadedFile> findSearchFilesInPublicList(String keyword,Boolean access) {
        return fileRepositoryDB.findByFileName(keyword,access);
    }

    @Override
    public ResponseEntity getFile(UploadedFile uploadedFile) throws Exception{
        if(uploadedFile.getCustomUserId().equals(userService.getSpecifyUserId())&uploadedFile.getAccess().equals(false))
        {
            FileSystemResource file = new FileSystemResource(pathToSpecificFile(uploadedFile));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(uploadedFile.getMimeType())).body(new InputStreamResource(file.getInputStream()));
        }
        else if(uploadedFile.getAccess().equals(true))
        {
            FileSystemResource file = new FileSystemResource(pathToSpecificFile(uploadedFile));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(uploadedFile.getMimeType())).body(new InputStreamResource(file.getInputStream()));
        }
        return null;
    }

    @Override
    public void saveEditFileWithUser(String username, UploadedFile uploadedFile) {
        CustomUser user = userRepository.findByUsername(username).get();
        UploadedFile uploadedFile1 = findFileById(uploadedFile.getId()).get();
        if(!uploadedFile1.getCustomUsers().contains(user))
        {
            uploadedFile1.setCustomUsers(user);
            saveEditFile(uploadedFile1);
        }
    }

    @Override
    public Object returnUploadedFileOrLink(UploadedFile uploadedFile) {
        if(uploadedFile.getLink()==null){
            try {
                return getFile(uploadedFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            return uploadedFile;
        }
        return null;
    }

    @Override
    public List<UploadedFile> returnShareFiles() {
        CustomUser customUser = userRepository.findById(userService.getSpecifyUserId()).get();
        return fileRepositoryDB.findByCustomUsers(customUser);
    }

}