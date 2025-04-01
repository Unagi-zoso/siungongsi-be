package org.bob.siungongsi.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("batch")
@Service
public class ZipFileReader {
  public byte[] readZipFile(byte[] zipData) {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(zipData);
        ZipInputStream zis = new ZipInputStream(bais)) {

      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
          baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read ZIP file", e);
    }
    return new byte[0];
  }
}
