package com.hrms.constant;

import java.util.Arrays;
import java.util.List;

public class FileConstants {

    public static final String PDF_MIME_TYPE = "application/pdf";
    public static final String TIKA_OOXML_MIME_TYPE = "application/x-tika-ooxml";
    public static final String TIKA_MSOFFICE_MIME_TYPE = "application/x-tika-msoffice";
    public static final String JPEG_MIME_TYPE = "image/jpeg";
    public static final String PNG_MIME_TYPE = "image/png";
    public static final String TXT_MIME_TYPE = "text/plain";
    public static final String DOC_MIME_TYPE = "description=Microsoft Word File";
    public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS_MIME_TYPE = "application/vnd.ms-excel";
    public static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            PDF_MIME_TYPE,
            JPEG_MIME_TYPE,
            PNG_MIME_TYPE,
            TIKA_OOXML_MIME_TYPE,
            TIKA_MSOFFICE_MIME_TYPE,
            TXT_MIME_TYPE,
            DOC_MIME_TYPE,
            DOCX_MIME_TYPE,
            XLS_MIME_TYPE,
            XLSX_MIME_TYPE
    );
}
