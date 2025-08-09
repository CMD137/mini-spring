package com.miniSpring.core.io;

import cn.hutool.core.lang.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ResourceLoader {

    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");

        if (isClasspathResource(location)) {
            return getClasspathResource(location);
        }

        if (isUrl(location)) {
            return getUrlResource(location);
        }

        return getFileSystemResource(location);
    }

    private boolean isClasspathResource(String location) {
        return location.startsWith(CLASSPATH_URL_PREFIX);
    }

    private Resource getClasspathResource(String location) {
        String path = location.substring(CLASSPATH_URL_PREFIX.length());
        return new ClassPathResource(path);
    }

    private boolean isUrl(String location) {
        try {
            new URL(location);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private Resource getUrlResource(String location) {
        try {
            URL url = new URL(location);
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            // 理论上这里不会异常，因为已经判断过了
            throw new IllegalArgumentException("Invalid URL: " + location, e);
        }
    }

    private Resource getFileSystemResource(String location) {
        return new FileSystemResource(location);
    }
}

