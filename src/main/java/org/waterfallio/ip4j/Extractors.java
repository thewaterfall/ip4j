package org.waterfallio.ip4j;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Pattern;

public class Extractors {
  private static final Pattern IPV4_PATTERN =
      Pattern.compile("^(?:(?:\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(?:\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$", Pattern.CASE_INSENSITIVE);

  private static final Pattern IPV6_PATTERN =
      Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))", Pattern.CASE_INSENSITIVE);

  protected static String extractIp(String headerName, HttpServletRequest request) {
    String ip =
        formatIp(request.getHeader(headerName));

    return isValidIp(ip) ? ip : null;
  }

  protected static String extractFirstIp(String headerName, HttpServletRequest request) {
    String headerValue = request.getHeader(headerName);

    if (headerValue == null) {
      return null;
    }

    String firstIp =
        formatIp(headerValue.split(",")[0]);

    return isValidIp(firstIp) ? firstIp : null;
  }

  protected static String extractForwardedIp(String headerName, HttpServletRequest request) {
    String headerValue = request.getHeader(headerName);

    if (headerValue == null) {
      return null;
    }

    for (String directive : headerValue.split(";")) {
      for (String part : directive.trim().split(",")) {
        String[] keyValue = part.trim().split("=", 2);

        if (keyValue.length == 2 && "for".equalsIgnoreCase(keyValue[0].trim())) {
          String value = formatIp(keyValue[1].trim());

          if (isValidIp(value)) {
            return value;
          }
        }
      }
    }

    return null;
  }

  protected static String extractRemoteAddr(HttpServletRequest request) {
    String remoteAddr =
        request.getRemoteAddr();

    return isValidIp(remoteAddr) ? remoteAddr : null;
  }

  protected static String formatIp(String value) {
    if (value != null) {
      value = removeBrackets(removeQuotes(value)).trim();
    }

    return value;
  }

  protected static boolean isValidIp(String value) {
    return value != null && !value.isEmpty() && (isValidIp4(value) || isValidIp6(value));
  }

  protected static boolean isValidIp4(String value) {
    return IPV4_PATTERN.matcher(value).matches();
  }

  protected static boolean isValidIp6(String value) {
    return IPV6_PATTERN.matcher(value).matches();
  }

  protected static String removeBrackets(String value) {
    if (value.startsWith("[") && value.endsWith("]")) {
      value = value.substring(1, value.length() - 1);
    }

    return value.trim();
  }

  protected static String removeQuotes(String value) {
    if (value.startsWith("\"") && value.endsWith("\"")) {
      value = value.substring(1, value.length() - 1);
    }

    if (value.startsWith("'") && value.endsWith("'")) {
      value = value.substring(1, value.length() - 1);
    }

    return value.trim();
  }
}
