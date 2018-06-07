package modules.s3;

import org.apache.tika.mime.MimeTypeException;
import play.mvc.Http;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by arkady on 01/03/16.
 */
public interface S3 {
	String extractAndUploadToS3(Http.MultipartFormData.FilePart picture, int width, int height) throws MimeTypeException, IOException;
	void deleteFromS3(String fileName);
	String getFileExtensionFromMimeType(String contentType) throws MimeTypeException;
	BufferedImage resizeImage(BufferedImage originalImage);
	String uploadToS3(String contentType, InputStream inputStream) throws MimeTypeException;
	String getCloudfrontDomain();
}
