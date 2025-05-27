package com.hrms.controller;


import com.hrms.common.ApiResponse;
import com.hrms.entity.Notice;
import com.hrms.entity.mapstruct.NoticeMapper;
import com.hrms.model.bo.NoticeBO;
import com.hrms.model.vo.FileDataVO;
import com.hrms.model.vo.NoticeVO;
import com.hrms.service.FileDataService;
import com.hrms.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notice")
@Tag(name = "公告管理", description = "公告管理API")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private FileDataService fileDataService;


    @RequiresPermissions("000201003")
    @Operation(summary = "創建公告", description = "創建一個新的公告API")
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> create(@Valid @RequestPart NoticeBO noticeBO, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        noticeService.create(noticeBO, file);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000201002")
    @Operation(summary = "更新公告", description = "更新公告API")
    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> update(@Valid @RequestPart NoticeBO noticeBO, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Notice notice = NoticeMapper.INSTANCE.noticeBoToNotice(noticeBO);
        noticeService.update(notice, file);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000201004")
    @Operation(summary = "刪除公告", description = "刪除公告API")
    @PostMapping(path = "/delete/{noticeId}")
    public ApiResponse<String> delete(@PathVariable Integer noticeId) {
        noticeService.deleteById(noticeId);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000201004") // 修改為對應的權限編號
    @Operation(summary = "批量刪除公告", description = "批量刪除公告API")
    @PostMapping(path = "/deleteBatch")
    public ApiResponse<String> deleteBatch(@RequestBody List<Integer> noticeIds) {
        noticeService.deleteBatch(noticeIds);
        return new ApiResponse<>();
    }


    @RequiresPermissions("000201005")
    @Operation(summary = "儲存並發送通知與公告", description = "儲存公告並發送郵件通知API")
    @PostMapping(path = "/saveAndSend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> saveAndSend(@Valid @RequestPart NoticeBO noticeBO, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        noticeService.saveAndSend(noticeBO, file);
        return new ApiResponse<>();
    }

    @RequiresPermissions("000201001")
    @GetMapping("/getAllNotices")
    @Operation(summary = "查詢所有通知與公告", description = "查詢所有通知與公告API")
    public ApiResponse<List<NoticeVO>> getAllNotices() {
        List<Notice> notices = noticeService.list();
        List<NoticeVO> noticeVOs = notices.stream()
                .map(notice -> {
                    NoticeVO noticeVO = NoticeMapper.INSTANCE.noticeToNoticeVO(notice);
                    List<FileDataVO> fileDataVOs = fileDataService.getFilesByCaseIdAndTableName(notice.getId(), "notice");
                    noticeVO.setFiles(fileDataVOs);
                    return noticeVO;
                })
                .toList();
        return new ApiResponse<>(noticeVOs);
    }

    @PostMapping(path = "/enable/{noticeId}")
    public ApiResponse<String> enable(@PathVariable Integer noticeId) {
        noticeService.enable(noticeId);
        return new ApiResponse<>();
    }

    @PostMapping(path = "/disable/{noticeId}")
    public ApiResponse<String> disable(@PathVariable Integer noticeId) {
        noticeService.disable(noticeId);
        return new ApiResponse<>();
    }
}
