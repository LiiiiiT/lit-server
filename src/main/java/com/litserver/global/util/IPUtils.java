package com.litserver.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtils {
    protected static Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-connecting-ip");
        // 获取IP，防止篡改
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }

        Pattern p = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
        String str = ipAddress;
        Matcher m = p.matcher(str);
        int start = 0;
        int end = str.length();
        List<String> list = new ArrayList<>();
        while (m.find()) {
            if (m.start() > start) {
                list.add(str.substring(start, m.start()));
            }
            list.add(m.group());
            start = end = m.end();
        }
        if (end < str.length()) {
            list.add(str.substring(end));
        }
        Collections.reverse(list);
        String lastIp = null;
        int status = 0;
        for (String ip : list) {
            if (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
                if (ip.equals(", ")) {
                    status = 0;
                    continue;
                }
                break;
            } else if (ip.equals(", ")) {
                if (status == 1) {
                    status = 0;
                }
                break;
            } else if (status != 0) {
                break;
            }
            status = 1;
            lastIp = ip;
        }
        ipAddress = lastIp;
        return ipAddress;
    }
}

