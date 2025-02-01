package org.waterfallio.ip4j;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtractorsTest {
  @Mock
  private HttpServletRequest request;

  @Test
  void extractIp_ReturnsHeaderValue_WhenValidIPv4() {
    when(request.getHeader("X-Test-Header")).thenReturn("192.168.1.1");
    assertEquals("192.168.1.1", Extractors.extractIp("X-Test-Header", request));
  }

  @Test
  void extractIp_ReturnsNull_WhenInvalidIP() {
    when(request.getHeader("X-Test-Header")).thenReturn("invalid-ip");
    assertNull(Extractors.extractIp("X-Test-Header", request));
  }

  @Test
  void extractIp_ReturnsNull_WhenHeaderNotPresent() {
    when(request.getHeader("X-Test-Header")).thenReturn(null);
    assertNull(Extractors.extractIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_ReturnsValidSingleIp() {
    when(request.getHeader("X-Test-Header")).thenReturn("192.168.1.1");
    assertEquals("192.168.1.1", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_ReturnsFirstValidIp_WhenMultiplePresent() {
    when(request.getHeader("X-Test-Header")).thenReturn("192.168.1.1, 10.0.0.1");
    assertEquals("192.168.1.1", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_ReturnsNull_WhenInvalidIP() {
    when(request.getHeader("X-Test-Header")).thenReturn("invalid-ip, 192.168.1.1");
    assertNull(Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_ReturnsNull_WhenHeaderNotPresent() {
    when(request.getHeader("X-Test-Header")).thenReturn(null);
    assertNull(Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_HandlesWhitespace() {
    when(request.getHeader("X-Test-Header")).thenReturn("  192.168.1.1  ,  10.0.0.1  ");
    assertEquals("192.168.1.1", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_ReturnsNull_WhenOnlyWhitespace() {
    when(request.getHeader("X-Test-Header")).thenReturn("   ");
    assertNull(Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_HandlesIPv6() {
    when(request.getHeader("X-Test-Header")).thenReturn("2001:db8:85a3:8d3:1319:8a2e:370:7348");
    assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_HandlesIPv6WithIPv4() {
    when(request.getHeader("X-Test-Header"))
        .thenReturn("2001:db8:85a3:8d3:1319:8a2e:370:7348, 192.168.1.1");
    assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_HandlesEmptyParts() {
    when(request.getHeader("X-Test-Header")).thenReturn(",,192.168.1.1,,10.0.0.1,,");
    assertNull(Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractFirstIp_HandlesSpecialCharacters() {
    when(request.getHeader("X-Test-Header")).thenReturn("192.168.1.1\n,\t10.0.0.1");
    assertEquals("192.168.1.1", Extractors.extractFirstIp("X-Test-Header", request));
  }

  @Test
  void extractForwardedIp_ReturnsValidForValue() {
    when(request.getHeader("Forwarded")).thenReturn("for=192.168.1.1;proto=https");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_ReturnsNull_WhenNoFor() {
    when(request.getHeader("Forwarded")).thenReturn("by=192.168.1.1;proto=https");
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_ReturnsNull_WhenInvalidIP() {
    when(request.getHeader("Forwarded")).thenReturn("for=invalid-ip;proto=https");
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_ReturnsNull_WhenNoForOrByPresent() {
    when(request.getHeader("Forwarded")).thenReturn("proto=https");
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_ReturnsNull_WhenHeaderNotPresent() {
    when(request.getHeader("Forwarded")).thenReturn(null);
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesComplexForwarded() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=192.168.1.1;by=203.0.113.60;proto=http;host=example.com");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesIPv6() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=[2001:db8:85a3:8d3:1319:8a2e:370:7348];proto=https");
    assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesMultipleForwardedValues() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=192.168.1.1, for=192.168.1.2;by=203.0.113.60");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesWhitespace() {
    when(request.getHeader("Forwarded"))
        .thenReturn("  for = 192.168.1.1  ;  by = 203.0.113.60  ");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesEmptyParts() {
    when(request.getHeader("Forwarded"))
        .thenReturn(";;for=192.168.1.1;;proto=https;;");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesQuotedValues() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=\"192.168.1.1\";proto=https");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesForWithoutValue() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=;proto=https");
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesIncompleteKeyValue() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for;proto=https");
    assertNull(Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractForwardedIp_HandlesMultipleDelimiters() {
    when(request.getHeader("Forwarded"))
        .thenReturn("for=192.168.1.1,192.168.1.2,;,;for=10.0.0.1");
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }

  @Test
  void extractRemoteAddr_ReturnsValidRemoteAddress() {
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");
    assertEquals("192.168.1.1", Extractors.extractRemoteAddr(request));
  }

  @Test
  void extractRemoteAddr_ReturnsNull_WhenInvalidIP() {
    when(request.getRemoteAddr()).thenReturn("invalid-ip");
    assertNull(Extractors.extractRemoteAddr(request));
  }

  @Test
  void isValidIp_ReturnsTrueForValidIPv4() {
    assertTrue(Extractors.isValidIp("192.168.1.1"));
    assertTrue(Extractors.isValidIp("10.0.0.0"));
    assertTrue(Extractors.isValidIp("172.16.254.1"));
    assertTrue(Extractors.isValidIp("255.255.255.255"));
  }

  @Test
  void isValidIp_ReturnsTrueForValidIPv6() {
    assertTrue(Extractors.isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
    assertTrue(Extractors.isValidIp("fe80::1ff:fe23:4567:890a"));
    assertTrue(Extractors.isValidIp("2001:db8:85a3:8d3:1319:8a2e:370:7348"));
  }

  @Test
  void isValidIp_ReturnsFalseForInvalidIPs() {
    assertFalse(Extractors.isValidIp("256.1.2.3"));
    assertFalse(Extractors.isValidIp("1.2.3.4.5"));
    assertFalse(Extractors.isValidIp("192.168.1"));
    assertFalse(Extractors.isValidIp("192.168.1.1."));
    assertFalse(Extractors.isValidIp("192.168.1."));
    assertFalse(Extractors.isValidIp("invalid-ip"));
    assertFalse(Extractors.isValidIp(""));
    assertFalse(Extractors.isValidIp(null));
  }

  @Test
  void isValidIp_HandlesIPv6Variations() {
    // Compressed forms
    assertTrue(Extractors.isValidIp("2001:db8::1"));
    assertTrue(Extractors.isValidIp("::"));
    assertTrue(Extractors.isValidIp("::1"));
    assertTrue(Extractors.isValidIp("2001::"));

    // IPv4-mapped IPv6 addresses
    assertTrue(Extractors.isValidIp("::ffff:192.168.1.1"));
    assertTrue(Extractors.isValidIp("::ffff:c0a8:0101"));

    // Link-local addresses
    assertTrue(Extractors.isValidIp("fe80::1"));
    assertTrue(Extractors.isValidIp("fe80::1ff:fe23:4567:890a"));
  }

  @Test
  void isValidIp_HandlesInvalidIPv6() {
    assertFalse(Extractors.isValidIp("2001:db8:85a3:8d3:1319:8a2e:370")); // Too short
    assertFalse(Extractors.isValidIp("2001:db8:85a3:8d3:1319:8a2e:370:7348:1234")); // Too long
    assertFalse(Extractors.isValidIp("2001:db8:85a3:8d3:1319:8a2g:370:7348")); // Invalid hex
    assertFalse(Extractors.isValidIp("2001::db8::1")); // Multiple compression markers
    assertFalse(Extractors.isValidIp("[::1]"));
    assertFalse(Extractors.isValidIp("[::ffff:c0a8:0101]"));
    assertFalse(Extractors.isValidIp("[fe80::1ff:fe23:4567:890a]"));
  }

  @Test
  void isValidIp_HandlesEdgeCasesIPv4() {
    // Valid edge cases
    assertTrue(Extractors.isValidIp("0.0.0.0"));
    assertTrue(Extractors.isValidIp("1.2.3.4"));

    // Invalid edge cases
    assertFalse(Extractors.isValidIp("00.0.0.0"));
    assertFalse(Extractors.isValidIp("01.001.01.1"));
    assertFalse(Extractors.isValidIp("1.2.3.4."));
    assertFalse(Extractors.isValidIp(".1.2.3.4"));
    assertFalse(Extractors.isValidIp("1.2.3.4.5"));
    assertFalse(Extractors.isValidIp("256.256.256.256"));
    assertFalse(Extractors.isValidIp("1.2.3.a"));
  }

  @Test
  void formatIp_HandlesNullAndEmpty() {
    assertNull(Extractors.formatIp(null));
    assertEquals("", Extractors.formatIp(""));
    assertEquals("", Extractors.formatIp("   "));
  }

  @Test
  void formatIp_TrimsWhitespace() {
    assertEquals("192.168.1.1", Extractors.formatIp("  192.168.1.1  "));
    assertEquals("2001:db8::1", Extractors.formatIp("\t2001:db8::1\n"));
  }

  @Test
  void formatIp_RemovesIPv6Brackets() {
    assertEquals("2001:db8::1", Extractors.formatIp("[2001:db8::1]"));
    assertEquals("::1", Extractors.formatIp("[::1]"));
    assertEquals("fe80::1", Extractors.formatIp("[fe80::1]"));
  }

  @Test
  void formatIp_HandlesComplexCases() {
    assertEquals("2001:db8::1", Extractors.formatIp("  [2001:db8::1]  "));
    assertEquals("[malformed", Extractors.formatIp("[malformed"));
    assertEquals("malformed]", Extractors.formatIp("malformed]"));
    assertEquals("192.168.1.1]", Extractors.formatIp("192.168.1.1]"));
    assertEquals("[192.168.1.1", Extractors.formatIp("[192.168.1.1"));
  }

  @Test
  void isValidIp_HandlesIPv6WithIPv4Mapped() {
    assertTrue(Extractors.isValidIp("::ffff:192.0.2.128"));
    assertFalse(Extractors.isValidIp("::ffff:192.0.2.256"));
  }

  @Test
  void extractForwardedIp_HandlesWeirdFormatting() {
    when(request.getHeader("Forwarded"))
        .thenReturn("FoR=192.168.1.1;PROTO=https"); // Mixed case
    assertEquals("192.168.1.1", Extractors.extractForwardedIp("Forwarded", request));
  }
} 
