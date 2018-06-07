package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64OutputStream;
import play.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 19/04/16.
 */
public class QrCodeHelper {
	private final static String QR_FORMAT = "PNG";
	private final static int QR_SIZE = 400;
	private final static String QR_TEXT_CHARSET = "UTF-8";

	public static String getBase64QrCode(String data) throws IOException, WriterException {
		String result = null;
		try (OutputStream outputStream = new ByteArrayOutputStream();
			Base64OutputStream base64Output = new Base64OutputStream(outputStream)) {
			writeQrCodeToOutputStream(data, base64Output);
			result = outputStream.toString();
		} catch (IOException | WriterException e) {
			Logger.warn("Can't get base64 QR", e);
		}
		return result;
	}

	private static void writeQrCodeToOutputStream(String data, OutputStream stream) throws IOException, WriterException {
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		BitMatrix matrix = new MultiFormatWriter().encode(
				new String(data.getBytes(QR_TEXT_CHARSET), QR_TEXT_CHARSET),
				BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hintMap);
		MatrixToImageWriter.writeToStream(matrix, QR_FORMAT, stream);
	}
}
