package com.hrms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrms.constant.FileConstants;
import com.hrms.entity.FileData;
import com.hrms.entity.mapstruct.FileDataMapper;
import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.vo.FileDataVO;
import com.hrms.repository.FileDataRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 服務實現類
 * </p>
 *
 * @author System
 * @since 2024-06-08
 */
@Slf4j
@Service
@Transactional
public class FileDataService extends ServiceImpl<FileDataRepository, FileData> {

    @Resource
    private FileDataRepository fileDataRepository;

    //存儲檔案的根目錄資料夾
    @Value("${file.root.dir}")
    private String ROOT_DIR ;

    public int uploadFile(MultipartFile file, String tableName, int caseId) throws IOException {

        //檢查檔名
        if (containsChinese(file.getOriginalFilename())) {
            throw new ServiceException(ErrorCode.INVALID_FILE_NAME);
        }

        // 檢查檔案大小和類型
        if (!validateFile(file)) {
            throw new ServiceException(ErrorCode.INVALID_FILE_TYPE_OR_SIZE);
        }

        // 將檔案存到server
        String fileUrl = storeFile(file, tableName, caseId);

        // 建立並保存檔案資料
        FileData fileData = new FileData();
        fileData.setFileName(file.getOriginalFilename());
        fileData.setFileUrl(fileUrl);
        fileData.setTableName(tableName);
        fileData.setCaseId(caseId);

        return fileDataRepository.insert(fileData);
    }

    private String storeFile(MultipartFile file, String tableName, int caseId) throws IOException {

        String fileName = file.getOriginalFilename();
        // 構建資料夾路徑
        Path tableDir = Paths.get(ROOT_DIR, tableName);
        Path caseDir = Paths.get(tableDir.toString(), String.valueOf(caseId));

        // 確保資料夾存在
        if (!Files.exists(caseDir)) {
            Files.createDirectories(caseDir);
        }
        // 獲取檔案路徑
        Path filePath = caseDir.resolve(fileName);

        // 存儲檔案到對應資料夾
        Files.copy(file.getInputStream(), filePath);

        // 返回文件的相對路徑
        return Paths.get("/files", tableName, String.valueOf(caseId), fileName).toString().replace("\\", "/");
    }

    private boolean validateFile(MultipartFile file) throws IOException {
        // 使用變數中允許的MIME類型
        List<String> allowedMimeTypes = FileConstants.ALLOWED_MIME_TYPES;

        // 使用Tika檢測文件的MIME類型
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());

        // 檢查MIME類型和文件大小
        if (!allowedMimeTypes.contains(mimeType)) {
            return false;
        }
        // 檔案大小最大為10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            return false;
        }
        return true;
    }

    public List<FileDataVO> getFilesByCaseIdAndTableName(int caseId, String tableName) {
        List<FileData> fileDataList = fileDataRepository.selectByCaseIdAndTableName(caseId, tableName);
        String serverAddress = getServerAddress();

        return fileDataList.stream()
                .map(fileData -> {
                    FileDataVO fileDataVO = FileDataMapper.INSTANCE.fileDataToFileDataVO(fileData);
                    String fileUrl = fileData.getFileUrl();
                    // 合成最終的URL
                    String fullUrl = serverAddress + fileUrl;
                    fileDataVO.setFileId(fileData.getId());
                    fileDataVO.setFileUrl(fullUrl);
                    return fileDataVO;
                })
                .toList();
    }

    private static boolean containsChinese(String str) {
        // 檢查字符串是否包含中文字符
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5\\u3400-\\u4DBF\\uF900-\\uFAFF]");
        return pattern.matcher(str).find();
    }

    private String getServerAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("無法取得伺服器位址");
        }
        HttpServletRequest request = attributes.getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            sb.append(":").append(request.getServerPort());
        }
        if (sb.isEmpty()) {
            throw new RuntimeException("無法取得伺服器位址");
        }
        return sb.toString();
    }
}


