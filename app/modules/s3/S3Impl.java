package modules.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.inject.Singleton;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.mvc.Http;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

/**
 * Created by arkady on 01/03/16.
 */
@Singleton
public class S3Impl implements S3 {

	public static final String AWS_S3_BUCKET = "aws.s3.bucket";
	public static final String AWS_ACCESS_KEY = "aws.accessKeyId";
	public static final String AWS_SECRET_KEY = "aws.secretKey";
	public static final String AWS_CLOUDFRONT_DOMAIN = "aws.cloudfront.domain";

	public static AmazonS3 amazonS3;

	public static String cloudfrontDomain;
	public static String s3Bucket;

	@Inject
	public S3Impl(ApplicationLifecycle lifecycle, Configuration configuration) {
		String accessKey = configuration.getString(AWS_ACCESS_KEY);
		String secretKey = configuration.getString(AWS_SECRET_KEY);
		cloudfrontDomain = configuration.getString(AWS_CLOUDFRONT_DOMAIN);
		s3Bucket = configuration.getString(AWS_S3_BUCKET);

		if ((accessKey != null) && (secretKey != null)) {
			AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
			amazonS3 = new AmazonS3Client(awsCredentials);
			amazonS3.createBucket(s3Bucket);
			Logger.info("Using S3 Bucket: " + s3Bucket + " and cloudfront domain: " + cloudfrontDomain);
		}
	}

	@Override
	public String uploadToS3(String contentType, InputStream inputStream) throws MimeTypeException {
		if (amazonS3 == null) {
			Logger.error("Could not save because amazonS3 was null");
			throw new RuntimeException("Could not save");
		} else {
			String actualFileName = getActualFileName(contentType);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			PutObjectRequest putObjectRequest = new PutObjectRequest(s3Bucket, actualFileName, inputStream, metadata);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			PutObjectResult result = amazonS3.putObject(putObjectRequest);
			Logger.info(result.toString());
			return actualFileName;
		}
	}

	@Override
	public String getCloudfrontDomain() {
		return cloudfrontDomain;
	}

	@Override
	public String extractAndUploadToS3(Http.MultipartFormData.FilePart picture, int width, int height) throws MimeTypeException, IOException {
		String contentType = picture.getContentType();
		File file = picture.getFile();
		String formatName = getFileExtensionFromMimeType(contentType).replace(".", "");
		BufferedImage originalImage = ImageIO.read(file);
		BufferedImage promoPicture = resizeImage(originalImage);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(promoPicture, formatName, os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		String fileName = uploadToS3(contentType, is);
		Logger.info("Uploaded promotion picture to S3: " + fileName);
		return fileName;
	}

	@Override
	public void deleteFromS3(String fileName) {
		if (amazonS3 == null) {
			Logger.error("Could not delete because amazonS3 was null");
			throw new RuntimeException("Could not delete");
		} else {
			amazonS3.deleteObject(s3Bucket, fileName);
			Logger.info("Deleted from S3: " + fileName);
		}
	}

	@Override
	public String getFileExtensionFromMimeType(String contentType) throws MimeTypeException {
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		MimeType mimeType = allTypes.forName(contentType);
		return mimeType.getExtension();
	}

	@Override
	public BufferedImage resizeImage(BufferedImage originalImage) {
		//TODO implement this method
		BufferedImage resizedImage = new BufferedImage(800, 800, originalImage.getType());
		return resizedImage;
	}

	private String getActualFileName(String contentType) throws MimeTypeException {
		String extension = getFileExtensionFromMimeType(contentType);
		return UUID.randomUUID() + extension;
	}
}
