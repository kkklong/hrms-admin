package com.hrms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import java.util.Optional;

@OpenAPIDefinition
@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("HRMS Admin API")
                        .description("人事管理系統 Swagger Api")
                        .version("v0.0.1")
                        .license(new License())
                        .contact(new Contact())
                ).components(new Components()
                ).addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    @Bean
    public OperationCustomizer operationCustomizer(ConversionService conversionService, ObjectProvider<GroupedOpenApi> groupedOpenApis) {
        OperationCustomizer customizer = (operation, handlerMethod) -> {
            Optional.ofNullable(operation.getRequestBody())
                    .map(RequestBody::getContent)
                    .filter(content -> content.containsKey(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .map(content -> content.get(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .ifPresent(multipartFormData -> {
                        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
                            if (MultipartResolutionDelegate.isMultipartArgument(methodParameter)) {
                                // ignore MultipartFile parameters
                                continue;
                            }
                            RequestPart requestPart = methodParameter.getParameterAnnotation(RequestPart.class);
                            if (requestPart == null) {
                                // ignore parameters without @RequestPart annotation
                                continue;
                            }
                            if (conversionService.canConvert(TypeDescriptor.valueOf(String.class), new TypeDescriptor(methodParameter))) {
                                // ignore parameters that can be converted from String to a basic type by ObjectToStringHttpMessageConverter
                                continue;
                            }
                            String parameterName = requestPart.name();
                            if (!StringUtils.hasText(parameterName)) {
                                parameterName = methodParameter.getParameterName();
                            }
                            if (!StringUtils.hasText(parameterName)) {
                                parameterName = methodParameter.getParameter().getName();
                            }
                            if (StringUtils.hasText(parameterName)) {
                                multipartFormData.addEncoding(parameterName, new Encoding().contentType(MediaType.APPLICATION_JSON_VALUE));
                            }
                        }
                    });
            return operation;
        };
        groupedOpenApis.forEach(groupedOpenApi -> groupedOpenApi.getOperationCustomizers().add(customizer));
        return customizer;
    }
}
