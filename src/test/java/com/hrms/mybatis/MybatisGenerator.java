package com.hrms.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * MybatisPlus代碼生成，
 * 生成後所有的檔案會放在tmp這個資料夾底下，自行複製需要的項目到專案中
 */
@Slf4j
public class MybatisGenerator {
    public static void main(String[] args) {
        // 如須生成特定的entity可以改成List.of("正則批配的表明")
        List<String> includeList = List.of();
        Path envPath = Paths.get("config/dev/docker/.env");
        if (!envPath.toFile().exists()) {
            envPath = Paths.get("config/docker/.env");
            if (!envPath.toFile().exists()) {
                log.warn("找不到env配置");
                return;
            }
        }
        try (InputStream inputStream = Files.newInputStream(envPath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String url = "jdbc:mysql://localhost:"
                    + properties.getProperty("MYSQL_PORT")
                    + "/" + properties.getProperty("MYSQL_DATABASE");
            log.info("MYSQL_URL: {}", url);
            log.info("MYSQL_USER: {}", properties.getProperty("MYSQL_USER"));
            log.info("MYSQL_PASSWORD: {}", properties.getProperty("MYSQL_PASSWORD"));
            FastAutoGenerator.create(url,
                            properties.getProperty("MYSQL_USER"),
                            properties.getProperty("MYSQL_PASSWORD")
                    )
                    .globalConfig(builder -> builder
                            .author("System")
                            .outputDir(Paths.get(System.getProperty("user.dir")) + "/tmp/src/main/java")
                            .commentDate("yyyy-MM-dd")
                            .disableOpenDir()
                    )
                    .dataSourceConfig(builder -> builder
                            .typeConvertHandler((globalConfig, typeRegistry, metaInfo) ->
                                    metaInfo.getJdbcType() == JdbcType.REAL ? DbColumnType.FLOAT : typeRegistry.getColumnType(metaInfo)
                            )
                            .build()
                    )
                    .packageConfig(builder -> builder
                            .parent("com.hrms")
                            .entity("entity")
                            .mapper("repository")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "tmp/src/main/resources/mapper"))
                            .serviceImpl("service")
                            .xml("mapper")
                    )
                    .strategyConfig(builder -> {
                        if (!includeList.isEmpty()) {
                            builder.addInclude(includeList);
                        } else {
                            builder.addExclude("flyway_schema_history");
                        }

                        builder.entityBuilder()
                                .enableLombok()
                                .enableRemoveIsPrefix()
                                .addTableFills(new Column("created_id", FieldFill.INSERT))
                                .addTableFills(new Column("created_date", FieldFill.INSERT))
                                .addTableFills(new Column("updated_id", FieldFill.INSERT_UPDATE))
                                .addTableFills(new Column("updated_date", FieldFill.INSERT_UPDATE))
                                .javaTemplate("mybatis/templates/entity.java")
                                .enableFileOverride();

                        builder.mapperBuilder()
                                .formatMapperFileName("%sRepository")
                                .enableFileOverride();

                        builder.serviceBuilder()
                                .disableService()
                                .formatServiceImplFileName("%sService")
                                .serviceImplTemplate("mybatis/templates/serviceImpl.java")
                                .enableFileOverride();

                        builder.controllerBuilder()
                                .enableRestStyle()
                                .template("mybatis/templates/controller.java")
                                .enableFileOverride();
                    })
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();
        } catch (IOException e) {
            log.error("生成失敗", e);
        }
    }
}

