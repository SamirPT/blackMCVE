package controllers;

import com.google.inject.Inject;
import models.User;
import modules.s3.S3;
import org.apache.tika.mime.MimeTypeException;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.ErrorMessage;
import to.ResponseMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by arkady on 29/02/16.
 */
public class Images extends UserProfileController<MrBlackUserProfile> {

	private S3 s3;

	@Inject
	public Images(S3 s3) {
		this.s3 = s3;
	}

	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result upload() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart picture = body.getFile("image");

		if (picture == null) {
			Logger.warn("Bad request for uploading file: " + request().body().asText());
			return badRequest(Json.toJson(new ErrorMessage("Missing file")));
		}

		final String fileName;
		try {
			fileName = uploadFileToCDN(picture);
		} catch (IOException | MimeTypeException e) {
			Logger.warn("Couldn't upload image to S3", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		String completeFileName = "http://" + s3.getCloudfrontDomain() + "/" + fileName;
		return ok(Json.toJson(new ResponseMessage(completeFileName)));
	}

	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result uploadAndAttachToUser() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart picture = body.getFile("image");

		if (picture == null) {
			Logger.warn("Bad request for uploading file: " + request().body().asText());
			return badRequest(Json.toJson(new ErrorMessage("Missing file")));
		}

		final String fileName;
		try {
			fileName = uploadFileToCDN(picture);
		} catch (IOException | MimeTypeException e) {
			Logger.warn("Couldn't upload image to S3", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		String completeFileName = "http://" + s3.getCloudfrontDomain() + "/" + fileName;

		try {
			final User user = getUserProfile().getUser();
			user.setUserpic(completeFileName);
			user.save();
		} catch (Exception e) {
			Logger.warn("Can't save userpic into user");
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage(completeFileName)));
	}

	private String uploadFileToCDN(Http.MultipartFormData.FilePart picture) throws IOException, MimeTypeException {
		File file = picture.getFile();
		String contentType = picture.getContentType();
		String formatName = s3.getFileExtensionFromMimeType(contentType).replace(".", "");
		BufferedImage originalImage = ImageIO.read(file);
//		BufferedImage resizedImage = s3.resizeImage(originalImage);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		ImageIO.write(resizedImage, formatName, os);
		ImageIO.write(originalImage, formatName, os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		String fileName = s3.uploadToS3(contentType, is);
		return fileName;
	}
}
